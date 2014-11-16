package thor.model.geoset;

import thor.graphics.Point3D;

public class HalfSpace {

	/* final */Point3D normal; // normal to boundary plane
	/* final */double d; // eqn of half space is normal.x - d > 0

	/**
	 * Create a half space
	 */
	public HalfSpace(Point3D a, Point3D b, Point3D c) {
		normal = customMath.normalize(customMath.cross(
				customMath.sub(b, a), customMath.sub(c, a)));
		d = customMath.dot(normal, a);
	}

	/**
	 * Create a half space parallel to z axis
	 */
	public HalfSpace(Point3D a, Point3D b) {
		normal = customMath.normalize(customMath.cross(customMath
				.sub(b, a), new Point3D.Double(0.0f, 0.0f, 1.0f)));
		d = customMath.dot(normal, a);
	}

	public boolean inside(Point3D x) {
		return customMath.dot(normal, x) > d;
	}

	/**
	 * z coordinate of intersection of a vertical line through p and
	 * boundary plane
	 */
	public double zint(Point3D p) {
		return (d - normal.getX() * p.getX() - normal.getY() * p.getY())
				/ normal.getZ();
	}
}
