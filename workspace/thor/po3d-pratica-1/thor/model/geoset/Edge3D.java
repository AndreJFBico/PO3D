package thor.model.geoset;

public class Edge3D {
	int _start; // end points
	int _end;

	public Edge3D(int start, int end) {
		this._start = start;
		this._end = end;
	}

	public boolean equals(Object o) {
		if (o instanceof Edge3D) {
			Edge3D e = (Edge3D) o;
			return (_start == e._end && _end == e._start)
					|| (_end == e._end && _start == e._start);
		} else {
			return false;
		}
	}
}
