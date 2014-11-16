// Copyright 2013 Pedro B. Pascoal
package thor.model.geoset;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import thor.graphics.Vector3D;
import thor.graphics.Point3D;
import thor.graphics.Point3D.Double;

/**
 * The abstract class Mesh is the superclass of all classes that represent
 * polygon mesh. The mesh must be obtained in a platform-specific manner.
 * 
 * @author Pedro B. Pascoal
 */
public abstract class Mesh extends Object {
	protected List<Vertex> _vertices = new ArrayList<Vertex>(); // list of
																// vertices
	protected List<Point2D> _textCoord = new ArrayList<Point2D>(); // list of
																	// texture
																	// coordinates
	protected List<Vector3D> _normals = new ArrayList<Vector3D>(); // list of
																	// normals
	protected List<Face> _faces = new ArrayList<Face>(); // list of faces
															// (triangles)

	protected Point3D _maxVertex;
	protected Point3D _minVertex;
	Manifold mani;

	protected boolean _baryCenterGenerated;
	protected Point3D _baryCenter;
	protected boolean _triangulated;
	protected boolean _convexHullGenerated;
	protected ConvexHull _convexHull;

	public Mesh() {
		_maxVertex = new Point3D.Float(-9999, -9999, -9999);
		_minVertex = new Point3D.Float(9999, 9999, 9999);
		_baryCenterGenerated = false;
		_triangulated = false;
		_convexHullGenerated = false;
	}

	/**
	 * @return A list of Vertex with all the vertices of the mesh.
	 */
	public List<Vertex> getVertices() {
		return _vertices;
	}

	/**
	 * @return A list of Point2D with the texture coordinates for each of the
	 *         vertices of the mesh.
	 */
	public List<Point2D> getTextCoord() {
		return _textCoord;
	}

	/**
	 * @return A list of Vector3D that represent the normals of each of the
	 *         vertices of the mesh.
	 */
	public List<Vector3D> getNormals() {
		return _normals;
	}

	/**
	 * @return A list of Face with all the faces of the mesh.
	 */
	public List<Face> getFaces() {
		return _faces;
	}

	/**
	 * @return Return the total number of vertices in the mesh.
	 */
	public int countVertices() {
		return _vertices.size();
	}

	/**
	 * @return Return the total number of faces in the mesh.
	 */
	public int countFaces() {
		return _faces.size();
	}

	/**
	 * Moves every point of the mesh a (x, y, z) distance.
	 * 
	 * @param x
	 *            - the X coordinate to add.
	 * @param y
	 *            - the Y coordinate to add.
	 * @param z
	 *            - the Z coordinate to add.
	 */
	public void translate(double x, double y, double z) {
		for (Vertex vertex : _vertices) {
			vertex.add(new Point3D.Double(x, y, z));
		}
	}

	/**
	 * Change the dimension of object by a scaling factor, i.e. enlarging or
	 * shrinking.
	 * 
	 * @param value
	 *            - the scale factor
	 */
	public void scale(double value) {
		for (Vertex vertex : _vertices) {
			vertex.mul(value);
		}
	}

	public void rotateX(float angle) {
		for (Vertex vertex : _vertices) {
			double rangle = Math.atan(vertex.getY() / vertex.getX())
					* (float) (Math.PI / 180);
			vertex.setLocation((float) Math.cos(angle + rangle),
					(float) Math.asin(angle + rangle), vertex.getZ());
		}
	}

	public void rotateZ(float angle) {
		for (Vertex vertex : _vertices) {
			double rangle = Math.atan(vertex.getY() / vertex.getX())
					* (float) (Math.PI / 180);
			vertex.setLocation((float) Math.cos(angle + rangle),
					(float) Math.asin(angle + rangle), vertex.getZ());
		}
	}

	public void calculateNormals() {
		// MyTODO: ok... kinda of tired, so calculate only for 3 vertices, screw
		// when there are 4...
		// i'll do it later, maybe calculate each face normal

		for (Face face : _faces) {
			Point3D p1 = _vertices.get(face.Vertices.get(0));
			Point3D p2 = _vertices.get(face.Vertices.get(1));
			Point3D p3 = _vertices.get(face.Vertices.get(2));

			Vector3D v1 = Vector3D.sub(p2, p1);
			Vector3D v2 = Vector3D.sub(p3, p1);

			face.Normal = Vector3D.product(v1, v2);
			face.Normal.normalize();

			for (int i : face.Vertices) {
				Vertex v = _vertices.get(i);
				v.Normal.add(face.Normal);
			}
		}
		for (Vertex v : _vertices) {
			v.Normal.normalize();
		}
	}

	public Point3D getMaxVertex() {
		return _maxVertex;
	}

	public Point3D getMinVertex() {
		return _minVertex;
	}

	public Point3D getFaceCenter(Face face) {
		Point3D center = new Point3D.Double();

		for (int i : face.Vertices) {
			Vertex v = _vertices.get(i);
			center.add(v);
		}
		center.div(face.Vertices.size());
		return center;
	}

	public void draw(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		for (Face face : _faces) {
			if (face.Vertices.size() == 4) {
				gl.glBegin(GL2.GL_QUADS);
			} else { // default = 3
				gl.glBegin(GL2.GL_TRIANGLES);
			}
			for (int i = 0; i < face.Vertices.size(); i++) {
				gl.glVertex3d(_vertices.get(face.Vertices.get(i)).getX(),
						_vertices.get(face.Vertices.get(i)).getY(), _vertices
								.get(face.Vertices.get(i)).getZ());
			}
			gl.glEnd();
		}
	}

	/*
	 * This triangulation algorithm only works for faces that are convex and
	 * have no holes in them.
	 */
	private void checkTriangulation() {
		for (int i = 0; i < _faces.size(); i++) {
			if (_faces.get(i).Vertices.size() > 3) {
				triangulate(_faces.get(i));
			}
		}
	}

	private void triangulate(Face face) {

		int p0 = face.Vertices.get(0);
		int p2 = face.Vertices.get(2);

		for (int i = 3; i < face.Vertices.size(); i++) {
			int temp = face.Vertices.get(i);
			Face triangle = new Face();
			triangle.Vertices.add(p0);
			triangle.Vertices.add(p2);
			triangle.Vertices.add(temp);
			_faces.add(triangle);
			p2 = temp;
		}

		// Reutilizes the current face without removing it 
		int tmp0 = face.Vertices.get(1);
		int tmp1 = face.Vertices.get(2);
		face.Vertices.clear();
		face.Vertices.add(p0);
		face.Vertices.add(tmp0);
		face.Vertices.add(tmp1);
	}
		
	/*
	 * NOTE: this function is the first to get called and then centers the model
	 * a simple triangulation check is made on all faces.
	 * Also since this function is the first to get called it returns the first generated barycenter.
	 */
	public Point3D getBarycenter() {
		checkTriangulation();

		if (_baryCenterGenerated) {
			return _baryCenter;
		}

		Point3D sumAR = new Point3D.Double(0.0f, 0.0f, 0.0f);
		Point3D C = new Point3D.Double(0.0f, 0.0f, 0.0f);
		double sumA = 0;

		// Note requires faces to be triangles
		for (int i = 0; i < _faces.size(); i++) {
			Face current = _faces.get(i);

			Point3D vertex0 = _vertices.get(current.Vertices.get(0));
			Point3D vertex1 = _vertices.get(current.Vertices.get(1));
			Point3D vertex2 = _vertices.get(current.Vertices.get(2));

			// R is the Average of the vertices of the i'th face
			Point3D R = new Point3D.Double(0.0f, 0.0f, 0.0f);
			R.add(vertex0);
			R.add(vertex1);
			R.add(vertex2);
			R.div(3);

			double A = 2.0f * calcTriangleArea(vertex0, vertex1, vertex2);

			// A * R
			R.mul(A);

			sumAR.add(R);

			sumA += A;
		}

		C.add(sumAR);
		customMath.dDiv(C, sumA);

		// Rounds the values to 2 case decimal
		customMath.twoCaseRound(C);

		_baryCenterGenerated = true;
		_baryCenter = C;

		return C;
	}

	public boolean isManifold() {

		mani = new Manifold(_vertices, _textCoord, _normals, _faces);

		if (_faces.size() < 2)
			return true;

		if (!mani.createEdges()) {
			System.out.println("failed in creating edges and stuff");
			return false;
		}

		if (!mani.checkEdgeIncidency()) {
			System.out.println("failed in incidency");
			return false;
		}

		if (!mani.checkVertexFan()) {
			System.out.println("failed in fan");
			return false;
		}

		return true;
	}

	private double calcTriangleArea(Point3D vertex0, Point3D vertex1,
			Point3D vertex2) {
		double a = vertex0.distance(vertex1);
		double b = vertex1.distance(vertex2);
		double c = vertex2.distance(vertex0);
		double s = 0.5f * (a + b + c);

		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	/*
	 * Note this function requires triangulated faces as it used the heron formula 
	 * for surface area calculation.
	 */
	public double getSurfaceArea() {

		double returnVal = 0.0f;
		for (int i = 0; i < _faces.size(); i++) {
			Face current = _faces.get(i);

			returnVal += calcTriangleArea(
					_vertices.get(current.Vertices.get(0)),
					_vertices.get(current.Vertices.get(1)),
					_vertices.get(current.Vertices.get(2)));
		}
		return returnVal;
	}

	public Mesh getConvexHull() {
		if (!_convexHullGenerated) {
			_convexHull = new ConvexHull();
			_convexHull.setPoints(_vertices);
			_convexHull.build();
			_convexHullGenerated = true;
		}
		
		
		return _convexHull.getMesh();
	}
}
