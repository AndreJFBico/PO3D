package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

public class Skeleton {

	private VoxelMesh _mesh;
	private List<GraphNode> _allNodes = new ArrayList<GraphNode>();
	private List<GraphEdge> _allEdges = new ArrayList<GraphEdge>();

	public Skeleton(VoxelMesh mesh)
	{
		_mesh = mesh;
	}
	
	public List<GraphNode> genBasicGraph()
	{
		List<Voxel> voxels = _mesh._thinnedVolume;
		List<GraphNode> notProcessedNodes = new ArrayList<GraphNode>();
		
		for(int i = 0; i < voxels.size(); i++)
		{
			Voxel v = voxels.get(i);
			_allNodes.add(new GraphNode(v._position, new ArrayList<GraphEdge>()));
		}
		
		notProcessedNodes.addAll(_allNodes);
		
		for(int i = 0; i < _allNodes.size(); i++)
		{
			GraphNode n = _allNodes.get(i);
			notProcessedNodes.remove(n);
			for(int z = 0; z < notProcessedNodes.size(); z++)
			{
				GraphNode destination = _allNodes.get(z);
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
	
	public List<GraphNode> genMinimumSpanningTree()
	{
		//Creates and generates a minimumSpanningTree using the Prim Algorithm.
		MinimumSpanningTree tree = new MinimumSpanningTree(_allNodes, _allEdges);
		return tree.getMinimumSpanningTree();
	}
}
