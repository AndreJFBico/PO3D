package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class ShapeDescriptor {
	List<GraphNode> _DAG;
	GraphNode _root;
	List<List<GraphNode>> _subgraphs = new ArrayList<List<GraphNode>>();
	List<Matrix> _adjacencyMatrixes = new ArrayList<Matrix>();
	int _maximumBranchingFactor;
	
	public ShapeDescriptor(List<GraphNode> DAG, GraphNode root, int maximumBranchingFactor)
	{
		_DAG = DAG;
		_root = root;
		_maximumBranchingFactor = maximumBranchingFactor;
		genSubGraphs();
		System.out.println("FINISHED SHAPE DESCRIPTOR GENERATION");
	}
	
	private List<GraphNode> genSubGraph(GraphNode beginNode)
	{
		List<GraphNode> successors = new ArrayList<GraphNode>();
		successors.add(beginNode);
		
		List<GraphNode> subGraph = new ArrayList<GraphNode>();
		subGraph.add(beginNode);
		
		while(!successors.isEmpty())
		{
			GraphNode n = successors.get(0);
			successors.remove(0);
			for(GraphEdge e : n._neighbors)
			{
				subGraph.add(e._destination);
				successors.add(e._destination);
			}
		}
		return subGraph;
	}
	
	private void genSubGraphs()
	{
		for(GraphNode n : _DAG)
		{
			for(GraphEdge e : n._neighbors)
			{
				List<GraphNode> subGraph = genSubGraph(e._destination);
				Matrix adjacencyMatrix = genAdjacencyMatrix(subGraph);
				_adjacencyMatrixes.add(adjacencyMatrix);
				_subgraphs.add(subGraph);
				
				// create a symmetric positive definite matrix
				Matrix A = adjacencyMatrix;
				
				// compute the spectral decomposition
				EigenvalueDecomposition eigenDecomposition = A.eig();
				double[] eigenValues = eigenDecomposition.getRealEigenvalues();
				List<Double> sortedEigenValues = new ArrayList<Double>();
				sortedEigenValues.add(eigenValues[0]);
				
				for(double eigenv : eigenValues)
				{
					int index = 0;
					for(Double d : sortedEigenValues)
					{
						if(eigenv >= d)
						{
							sortedEigenValues.add(index, eigenv);
							break;
						}
						index++;
					}
				}
				
				int depth = e._destination._depth;
				
				double S = 0;
				//TODO: depth -1 and sortedEigenValues
				for(int i = 0; i < depth - 1 && i < sortedEigenValues.size(); i++)
				{
					S+= sortedEigenValues.get(i);
				}
				
				e._destination._localTSV._S = S;
				n._localTSV._nextTSVs.add(e._destination._localTSV);
			}
			
			if(n._localTSV._nextTSVs.size() < _maximumBranchingFactor)
			{
				for(int i = n._localTSV._nextTSVs.size(); i < _maximumBranchingFactor; i++)
				{
					n._localTSV._nextTSVs.add(new TSV(0));
				}
			}
		}
	}
	
	//first line first node
	private Matrix genAdjacencyMatrix(List<GraphNode> graph)
	{
		double[][] data = new double[graph.size()][graph.size()];
		
		for(int i = 0; i < graph.size(); i++)
		{
			GraphNode node = graph.get(i);
			List<GraphEdge> neighbors = node.getNeighbors();
			for(int z = 0; z < graph.size(); z++)
			{
				boolean found = false;
				for(int g = 0; g < neighbors.size(); g++)
				{
					if(neighbors.get(g)._destination._id == z)
					{
						data[i][z] = 1;
						found = true;
					}	
				}
				if(!found)
				{
					data[i][z] = 0;
				}
			}
		}
		return new Matrix(data);
	}
}
