package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class Skeleton {

	private VoxelMesh _mesh;
	private List<GraphNode> _allNodes = new ArrayList<GraphNode>();
	private List<GraphEdge> _allEdges = new ArrayList<GraphEdge>();
	private MinimumSpanningTree _tree;
	private List<GraphNode> _directedGraph;
	private float _DistanceThreshold;
	private float _clusterSizeThreshold;
	private ShapeDescriptor _shapeDescriptor;
	private int _maximumBranchingFactor;

	public Skeleton(VoxelMesh mesh, float DistanceThreshold, int clusterSizeThreshold)
	{
		_mesh = mesh;
		_DistanceThreshold = DistanceThreshold;
		_clusterSizeThreshold = clusterSizeThreshold;
		_maximumBranchingFactor = 0;
		genBasicGraph();
		genMinimumSpanningTree();
		createRootedDirectedGraph();
		_shapeDescriptor = new ShapeDescriptor(_directedGraph,_tree.get_root(), _maximumBranchingFactor);
	}
	
	/*private void createDirectedGraph()
	{
		List<GraphNode> tree = getMinimumSpanningTree();
		
		for(GraphNode n : tree)
		{
			for(GraphEdge e : n._neighbors)
			{
				if(n.equals(e._origin))
				{
					GraphNode dest = e._destination;
					if(n._dt > dest._dt)
						dest._neighbors.remove(e);
				}
				else if(n.equals(e._destination))
				{
					GraphNode dest = e._origin;

					if(n._dt > dest._dt){
						e._origin = e._destination;
						e._destination = dest;
						dest._neighbors.remove(e);
					}
				}
			}
		}
		
		_directedGraph = tree;
		
	}*/
	
	private void createRootedDirectedGraph()
	{
		List<GraphNode> tree = getMinimumSpanningTree();
		List<GraphNode> successors = new ArrayList<GraphNode>();	
		GraphNode node = _tree.get_root();
		
		successors.add(node);
		
		while(!successors.isEmpty())
		{
			GraphNode n = successors.get(0);
			successors.remove(0);
			if(n._neighbors.size() > _maximumBranchingFactor)
				_maximumBranchingFactor = n._neighbors.size();
			for(GraphEdge e : n._neighbors)
			{
				if(n.equals(e._origin))
				{
					GraphNode dest = e._destination;
					dest._neighbors.remove(e);
					successors.add(dest);
					dest._depth = n._depth+1;
				}
				else if(n.equals(e._destination))
				{
					GraphNode dest = e._origin;
					e._origin = e._destination;
					e._destination = dest;
					dest._neighbors.remove(e);
					successors.add(dest);
					dest._depth = n._depth+1;
				}
			}
			_directedGraph = tree;
		}
	}
	
	private Point3D average(List<GraphNode> list)
	{
		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;
		for(GraphNode n : list)
		{
			sumX += n._position.getX();
			sumY += n._position.getY();
			sumZ += n._position.getZ();
		}
		return new Point3D.Double(sumX / list.size(), sumY / list.size(), sumZ / list.size());
	}
	
	private double averageDT(List<GraphNode> list)
	{
		double sumDT = 0;
		for(GraphNode n : list)
		{
			sumDT += n._dt;
		}
		return sumDT / list.size();
	}
	
	private List<GraphNode> clustering()
	{
		int id = 0;
		List<GraphNode> clusteredGraph = new ArrayList<GraphNode>();
		for(GraphNode n : _allNodes)
		{
			List<GraphNode> clusterNodes = new ArrayList<GraphNode>();
			for(GraphNode e : _allNodes)
			{
				if(n.equals(e))
					continue;
				if(customMath.length(customMath.sub(e._position, n._position)) < _DistanceThreshold && !e.isClustered() && !customMath.pierced(_mesh, n, e))
				{
					clusterNodes.add(e);		
				}
			}
			if(clusterNodes.size() >= _clusterSizeThreshold)
			{
				clusteredGraph.add(new GraphNode(average(clusterNodes), new ArrayList<GraphEdge>(), averageDT(clusterNodes), id));
				id++;
				for(GraphNode e : clusterNodes)
				{
					e.setClustered(true);
				}
			}
		}
		_allNodes = clusteredGraph;
		return clusteredGraph;
	}
	
	private List<GraphNode> genBasicGraph()
	{
		List<Voxel> voxels = _mesh._thinnedVolume;
		List<GraphNode> notProcessedNodes = new ArrayList<GraphNode>();
		
		for(int i = 0; i < voxels.size(); i++)
		{
			Voxel v = voxels.get(i);
			_allNodes.add(new GraphNode(v._position, new ArrayList<GraphEdge>(), v._DT, i));
		}
		clustering();
		notProcessedNodes.addAll(_allNodes);
		
		for(int i = 0; i < _allNodes.size(); i++)
		{
			GraphNode n = _allNodes.get(i);
			notProcessedNodes.remove(n);
			for(int z = 0; z < notProcessedNodes.size(); z++)
			{
				GraphNode destination = notProcessedNodes.get(z);
				List<GraphEdge> neighbors = n.getNeighbors();
				GraphEdge edge = new GraphEdge(n, destination, customMath.length(
										customMath.sub(
												destination._position, 
												n._position)), false);
				neighbors.add(edge);
				destination.getNeighbors().add(edge);
				_allEdges.add(edge);
			}
		}

		return _allNodes;
	}
	
	private List<GraphNode> genMinimumSpanningTree()
	{
		//Creates and generates a minimumSpanningTree using the Prim Algorithm.
		 _tree = new MinimumSpanningTree(_allNodes, _allEdges, _mesh);
		return _tree.getMinimumSpanningTree();
	}
	
	public List<GraphNode> getMinimumSpanningTree()
	{
		return _tree.getMinimumSpanningTree();
	}
	
	public List<GraphNode> get_directedGraph() {
		return _directedGraph;
	}

	public void set_directedGraph(List<GraphNode> _directedGraph) {
		this._directedGraph = _directedGraph;
	}
}
