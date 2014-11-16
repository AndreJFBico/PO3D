package thor.model.geoset;

public class GraphEdge {
	GraphNode _origin;
	GraphNode _destination;
	double _weight;
	boolean _undirected;
	
	public GraphEdge(GraphNode origin, GraphNode destination, double weight, boolean undirected)
	{
		_origin = origin;
		_destination = destination;
		_weight = weight;
		_undirected = undirected;
	}
}
