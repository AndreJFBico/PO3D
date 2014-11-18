package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class GraphNode {

	Point3D _position;
	int _id;
	double _dt;
	int _depth;

	List<GraphEdge> _neighbors = new ArrayList<GraphEdge>();
	TSV _localTSV;
	
	Bone _bone;
	boolean clustered;

	public GraphNode(Point3D position, List<GraphEdge> neighbors, double dt,
			int id) {
		_position = position;
		_neighbors = neighbors;
		_dt = dt;
		_id = id;
		_depth = 1;
		_localTSV = new TSV(0);
	}

	public int get_depth() {
		return _depth;
	}

	public void set_depth(int _depth) {
		this._depth = _depth;
	}

	List<GraphEdge> getNeighbors() {
		return _neighbors;
	}

	public void setBone(Bone b) {
		_bone = b;
	}

	public Bone getBone() {
		return _bone;
	}

	public boolean crossBoundary(GraphNode n) {
		return false;
	}

	public Point3D getPosition() {
		return _position;
	}

	public List<GraphEdge> getEdges() {
		return _neighbors;
	}

	public boolean equals(Object o) {
		if (o instanceof GraphNode) {
			GraphNode e = (GraphNode) o;
			return (_id == e._id);
		} else {
			return false;
		}
	}

	public boolean isClustered() {
		return clustered;
	}

	public void setClustered(boolean clustered) {
		this.clustered = clustered;
	}
}
