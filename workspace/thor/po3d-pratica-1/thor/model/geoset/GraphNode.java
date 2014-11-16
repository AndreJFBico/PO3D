package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class GraphNode {
	Point3D _position;
	List<GraphEdge> _neighbors = new ArrayList<GraphEdge>();
	
	public GraphNode(Point3D position, List<GraphEdge> neighbors)
	{
		_position = position;
		_neighbors = neighbors;
	}
	
	List<GraphEdge> getNeighbors()
	{
		return _neighbors;
	}
	
	public boolean crossBoundary(GraphNode n)
	{
		return false;
	}
}
