package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

/*
 * The algorithm used is based on gift wrapping in 3D.
 * A basic explanation of how the algorithm works can be found in 
 * "http://www.cse.unsw.edu.au/~lambert/java/3d/giftwrap.html"
 * */

public class ConvexHull {

	protected EdgeStack _openEdges = new EdgeStack();
	protected List<String> _createdEdges = new ArrayList<String>();

	protected Vertex[] _pts;
	protected BufferedMesh _mesh = new BufferedMesh();

	public ConvexHull() {
	}

	public void setPoints(List<Vertex> vertices) {
		_pts = vertices.toArray(new Vertex[vertices.size()]);
	}

	private boolean edgeExists(int p1, int p2) {
		String edge = Integer.toString(p1) + Integer.toString(p2);
		return _createdEdges.contains(edge);
	}

	private void addEdge(int p1, int p2) {
		String edge = Integer.toString(p1) + Integer.toString(p2);
		if (!_createdEdges.contains(edge)) {
			_createdEdges.add(edge);
		}
		if (!edgeExists(p2, p1)) {
			_openEdges.put(new Edge3D(p2, p1));
		}
	}

	public Mesh getMesh() {
		return _mesh;
	}
	
	/*
	 * 3D version that instead of using a line made from the bottom point
	 * uses a plane and an edge as the axis.
	 */
	protected int search(Edge3D e) {
		int i;
		for (i = 0; i == e._start || i == e._end; i++) {
			/* nothing */
		}
		Point3D cand = _pts[i];
		int icand = i;
		HalfSpace candh = new HalfSpace(_pts[e._start], _pts[e._end], cand);
		for (i = i + 1; i < _pts.length; i++) {
			if (i != e._start && i != e._end && candh.inside(_pts[i])) {
				cand = _pts[i];
				icand = i;
				candh = new HalfSpace(_pts[e._start], _pts[e._end], cand);
			}
		}
		return icand;
	}

	// Search based on gift wrapping in 2D or know as jarvis march.
	protected int search2d(Point3D p) {
		int i;
		i = _pts[0] == p ? 1 : 0;
		Point3D cand = _pts[i];
		int candid = i;
		HalfSpace candh = new HalfSpace(p, cand);
		for (i = i + 1; i < _pts.length; i++) {
			if (_pts[i] != p && candh.inside(_pts[i])) {
				cand = _pts[i];
				candid = i;
				candh = new HalfSpace(p, cand);
			}
		}
		return candid;
	}

	/* bottom point */
	private int bottom() {
		Point3D bot = _pts[0];
		int ibot = 0;
		for (int i = 1; i < _pts.length; i++) {
			if (_pts[i].getY() < bot.getY()) {
				bot = _pts[i];
				ibot = i;
			}
		}
		return ibot;
	}

	/*
	 * This function calculates the convex hull based of a point cloud.
	 */
	public Mesh build() {
		/*
		 * Get bottom most point and runs the algorithm in 2D to get an edge
		 * that we know for certain belongs to the convex hull.
		 */
		int index1 = bottom();
		int index2 = search2d(_pts[index1]);

		addEdge(index2, index1);

		int v_index = 0;
		
		while (!_openEdges.isEmpty()) {
			Edge3D edge = _openEdges.get();
			index1 = edge._start;
			index2 = edge._end;

			if (!edgeExists(index1, index2)) {
				int index3 = search(new Edge3D(index1, index2));

				// create a polygon
				_mesh.addVertex(_pts[index1]);
				_mesh.addVertex(_pts[index2]);
				_mesh.addVertex(_pts[index3]);

				List<Integer> face = new ArrayList<Integer>();
				face.add(v_index);
				face.add(v_index + 1);
				face.add(v_index + 2);

				_mesh.addFace(face);
				v_index += 3;
				//

				addEdge(index1, index2);
				addEdge(index2, index3);
				addEdge(index3, index1);
			}
		}
		return _mesh;
	}

}
