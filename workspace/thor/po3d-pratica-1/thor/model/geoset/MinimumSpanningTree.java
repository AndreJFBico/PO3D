package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

public class MinimumSpanningTree {

	private List<GraphNode> _allNodes;
	private List<GraphEdge> _allEdges;

	public MinimumSpanningTree(List<GraphNode> allNodes,
			List<GraphEdge> allEdges) {
		_allNodes = allNodes;
		_allEdges = allEdges;
		Prim();
	}
	
	public List<GraphNode> getMinimumSpanningTree()
	{
		return _allNodes;
	}

	private List<GraphNode> Prim() {
		List<GraphNode> allNodes = _allNodes;
		List<GraphEdge> allEdges = _allEdges;
		List<GraphEdge> finalEdges = new ArrayList<GraphEdge>();
		List<GraphNode> finalNodes = new ArrayList<GraphNode>();

		finalNodes.add(allNodes.get(0));

		while (!checkIntersection(finalNodes, allNodes)) {
			FindEdgeStruct lightest = findLightestEdge(allEdges, finalNodes);

			finalEdges.add(lightest._edge);
			finalNodes.add(lightest._node);
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
		double minWeight = 100000;
		GraphEdge lightestEdge = null;
		GraphNode destNode = null;
		GraphNode startNode = null;

		for (GraphNode start : finalNodes) {
			for (GraphEdge neighbor : start._neighbors) {
				if (neighbor._origin == start) {
					if (finalNodes.contains(neighbor._destination))
						continue;
					if (neighbor._weight < minWeight) {
						minWeight = neighbor._weight;
						lightestEdge = neighbor;
						destNode = neighbor._destination;
					}
				} else if (neighbor._destination == start) {
					if (finalNodes.contains(neighbor._origin))
						continue;
					if (neighbor._weight < minWeight) {
						minWeight = neighbor._weight;
						lightestEdge = neighbor;
						destNode = neighbor._origin;
					}
				}
			}
		}

		FindEdgeStruct toReturn = new FindEdgeStruct(lightestEdge, destNode);

		return toReturn;

	}
}