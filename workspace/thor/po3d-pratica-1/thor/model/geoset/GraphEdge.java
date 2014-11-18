package thor.model.geoset;

public class GraphEdge {
	GraphNode _origin;
	GraphNode _destination;
	double _weight;
	boolean _undirected;
	boolean processed = false;
	
	public GraphEdge(GraphNode origin, GraphNode destination, double weight, boolean undirected)
	{
		_origin = origin;
		_destination = destination;
		_weight = weight;
		_undirected = undirected;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public GraphNode get_origin() {
		return _origin;
	}

	public void set_origin(GraphNode _origin) {
		this._origin = _origin;
	}

	public GraphNode get_destination() {
		return _destination;
	}

	public void set_destination(GraphNode _destination) {
		this._destination = _destination;
	}

	public double get_weight() {
		return _weight;
	}

	public void set_weight(double _weight) {
		this._weight = _weight;
	}

	public boolean is_undirected() {
		return _undirected;
	}

	public void set_undirected(boolean _undirected) {
		this._undirected = _undirected;
	}
}
