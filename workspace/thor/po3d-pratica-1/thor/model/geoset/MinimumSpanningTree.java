package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class MinimumSpanningTree {

	private VoxelMesh _mesh;
	private List<GraphNode> _allNodes;
	private List<GraphEdge> _allEdges;
	private GraphNode _root;

	public MinimumSpanningTree(List<GraphNode> allNodes,
			List<GraphEdge> allEdges, VoxelMesh mesh) {
		_allNodes = allNodes;
		_allEdges = allEdges;
		_mesh = mesh;
		Prim();
	}
	
	public List<GraphNode> getMinimumSpanningTree()
	{
		return _allNodes;
	}

	public GraphNode get_root() {
		return _root;
	}

	public void set_root(GraphNode _root) {
		this._root = _root;
	}

	private List<GraphNode> Prim() {
		List<GraphNode> allNodes = _allNodes;
		List<GraphEdge> allEdges = _allEdges;
		List<GraphEdge> finalEdges = new ArrayList<GraphEdge>();
		List<GraphNode> finalNodes = new ArrayList<GraphNode>();

		finalNodes.add(allNodes.get(0));
		_root = allNodes.get(0);
		
		int i = 0;
		while (finalNodes.size() != allNodes.size()) {
			FindEdgeStruct lightest = findLightestEdge(allEdges, finalNodes);
			if(lightest._edge == null || lightest._node == null)
				System.err.println("ERROR! NULL  " + i);
			finalEdges.add(lightest._edge);
			finalNodes.add(lightest._node);
			i++;
		}

		for (GraphNode node : finalNodes) {
			node._neighbors.clear();
			for (GraphEdge edge : finalEdges) {
				if (edge._origin == node || edge._destination == node) {
					node._neighbors.add(edge);
				}
			}
		}

		return finalNodes;
	}

	private boolean checkIntersection(List<GraphNode> finalNodes,
			List<GraphNode> allNodes) {
		for (GraphNode n : allNodes) {
			if (finalNodes.contains(n))
				continue;
			else
				return false;
		}

		return true;
	}

	private boolean checkIfAdded(List<GraphNode> finalNodes, GraphNode actual) {
		if (finalNodes.contains(actual))
			return false;
		return true;
	}

	private class FindEdgeStruct {
		public GraphEdge _edge;
		public GraphNode _node;

		public FindEdgeStruct(GraphEdge edge, GraphNode node) {
			_edge = edge;
			_node = node;
		}
	}

	private FindEdgeStruct findLightestEdge(List<GraphEdge> allEdges,
			List<GraphNode> finalNodes) {
		double minWeight = Float.MAX_VALUE;
		GraphEdge lightestEdge = null;
		GraphNode destNode = null;
		GraphNode startNode = null;

		for (GraphNode start : finalNodes) {
			List<GraphEdge> neighbors = start._neighbors;

			for (GraphEdge neighbor : neighbors) {
				if (neighbor._origin.equals(start)) {
					if (finalNodes.contains(neighbor._destination))
						continue;
					if (neighbor._weight <= minWeight && !customMath.pierced(_mesh, neighbor._origin, neighbor._destination)) {
						minWeight = neighbor._weight;
						lightestEdge = neighbor;
						destNode = neighbor._destination;
						startNode = start;
					}
				} else if (neighbor._destination.equals(start)) {
					if (finalNodes.contains(neighbor._origin))
						continue;
					if (neighbor._weight <= minWeight && !customMath.pierced(_mesh, neighbor._origin, neighbor._destination)) {
						minWeight = neighbor._weight;
						lightestEdge = neighbor;
						destNode = neighbor._origin;
						startNode = start;
					}
				}
			}
		}
		FindEdgeStruct toReturn = new FindEdgeStruct(lightestEdge, destNode);

		return toReturn;

	}
}
