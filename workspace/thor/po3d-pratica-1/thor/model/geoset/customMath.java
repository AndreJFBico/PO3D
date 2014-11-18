package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class customMath {
	
	public static Point3D k = new Point3D.Double(0.0f, 0.0f, 1.0f);

	public static void setXYZ(Point3D v, int index, double x)
	{
		if(index == 0)
			setX(v, x);
		else if (index == 1)
			setY(v, x);
		else if (index == 2)
			setZ(v, x);
	}
	
	public static void setX(Point3D v, double x) {
		v.setLocation(x, v.getY(), v.getZ());
	}

	public static void setY(Point3D v, double y) {
		v.setLocation(v.getX(), y, v.getZ());
	}
	
	public static void setZ(Point3D v, double z) {
		v.setLocation(v.getX(), v.getY(), z);
	}
	
	public static double getXYZ(Point3D v, int index)
	{
		if(index == 0)
			return v.getX();
		else if (index == 1)
			return v.getY();
		else if (index == 2)
			return v.getZ();
		System.err.println("invalid index acess to Point3D vector");
		return -1;
	}
	
	public static Point3D scale(Point3D v, double x) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);
		a.setLocation(v.getX() * x, v.getY() * x, v.getZ() * x);
		return a;
	}

	public static double length(Point3D v) {
		return Math.sqrt(dot(v, v));
	}

	public static Point3D normalize(Point3D v) {
		return scale(v, 1 / length(v));
	}

	public static Point3D mul(Point3D v, double x) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);

		a.setLocation(v.getX() * x, v.getY() * x,
				v.getZ() * x);

		return a;
	}

	public static Point3D div(Point3D point, double val) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);
		
		a.setLocation(point.getX() / val, point.getY() / val, point.getZ()
				/ val);
		
		return a;
	}

	public static Point3D add(Point3D v, double x) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);

		a.setLocation(v.getX() + x, v.getY() + x,
				v.getZ() + x);

		return a;
	}
	
	public static Point3D add(Point3D v, Point3D x) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);

		a.setLocation(v.getX() + x.getX(), v.getY() + x.getY(),
				v.getZ() + x.getZ());

		return a;
	}
	
	public static Point3D sub(Point3D v, Point3D x) {
		Point3D a = new Point3D.Double(0.0f, 0.0f, 0.0f);

		a.setLocation(v.getX() - x.getX(), v.getY() - x.getY(),
				v.getZ() - x.getZ());

		return a;
	}
	
	public static Point3D projectOver(Point3D self, Point3D v)
	{
		double length = dot(self, v);
		return new Point3D.Double(length * v.getX(), length * v.getY(), length * v.getZ());
	}

	public static double dot(Point3D v, Point3D x) {
		double d = 0;

		d += v.getX() * x.getX();
		d += v.getY() * x.getY();
		d += v.getZ() * x.getZ();

		return d;
	}

	public static Point3D cross(Point3D v, Point3D x) {
		return new Point3D.Double(v.getY() * x.getZ() - x.getY() * v.getZ(),
				v.getZ() * x.getX() - x.getZ() * v.getX(), v.getX() * x.getY()
						- x.getX() * v.getY());
	}
	
	/*
	 * Function that rounds a Point3D.
	 */
	public static void twoCaseRound(Point3D point) {
		point.setLocation(Math.round(point.getX() * 100.0) / 100.0,
				Math.round(point.getY() * 100.0) / 100.0,
				Math.round(point.getZ() * 100.0) / 100.0);
	}

	/*
	 * Function that divides a Point3D with a double.
	 */
	public static void dDiv(Point3D point, double val) {
		point.setLocation(point.getX() / val, point.getY() / val, point.getZ()
				/ val);
	}
	
	public static boolean pierced(VoxelMesh mesh,GraphNode n, GraphNode e)
	{
		Point3D direction = customMath.sub(e._position, n._position);
		Ray r = new Ray(n._position, customMath.normalize(direction));
		List<Face> faces = mesh._faces;
		List<Vertex> vertices = mesh._vertices;
		double minorDistance = Float.MAX_VALUE;
		for(Face f : faces)
		{
			List<Point3D> triangle = new ArrayList<Point3D>();
			List<Integer> faceVs = f.Vertices;
			for(Integer i : faceVs)
			{
				triangle.add(vertices.get(i));
			}
			if(r.intersectWithTriangle(triangle))
			{
				if(r._dToObject < minorDistance)
				{
					minorDistance = r._dToObject;
				}
			}
		}
		if(minorDistance > customMath.length(direction))
		{
			return false;
		}
		else return true;
	}
}
