package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

// axis aligned bounding box
public class AABB {

	Point3D _min, _max;
	private Point3D normal;
	
	public AABB(Point3D min, Point3D max)
	{
		_min = min;
		_max = max;
	}

	//error 
	/*void computeBoundingBox()
	{d
		_boundingBox.min = point3dMath.subtract(min, new Point3D.Double(0.001f, 0.001f, 0.001f));
		_boundingBox.max = point3dMath.add(max, new Point3D.Double(0.001f, 0.001f, 0.001f));
	}*/
	
	boolean intersect(Ray r)
	{
		Point3D dirfrac = new Point3D.Double(0.0f, 0.0f, 0.0f);
		// r.dir is unit direction vector of ray
		customMath.setX(dirfrac, 1.0f / r._direction.getX());
		customMath.setY(dirfrac, 1.0f / r._direction.getY());
		customMath.setZ(dirfrac, 1.0f / r._direction.getZ());
		// lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
		// r.org is origin of ray
		double t1 = (_min.getX() - r._origin.getX())*dirfrac.getX();
		double t2 = (_max.getX() - r._origin.getX())*dirfrac.getX();
		double t3 = (_min.getY() - r._origin.getY())*dirfrac.getY();
		double t4 = (_max.getY() - r._origin.getY())*dirfrac.getY();
		double t5 = (_min.getZ() - r._origin.getZ())*dirfrac.getZ();
		double t6 = (_max.getZ() - r._origin.getZ())*dirfrac.getZ();

		double t = 0;

		//definir tmin e tmax
		double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
		double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

		if (tmin < 0 && tmax < 0)
			return false;

		if (tmin > tmax)
			return false;

		if (tmin < 0 && tmax > 0)
			t = tmax;
		else t = tmin;

		/*// if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
		if (tmax < 0)
		{
			t = tmax;
			return false;
		}

		// if tmin > tmax, ray doesn't intersect AABB
		if (tmin > tmax)
		{
			t = tmax;
			return false;
		}

		t = tmin;*/
		
		r._intersectPoint = customMath.mul(customMath.add(r._origin, r._direction), t);
		r._dToObject = customMath.length(customMath.sub(r._intersectPoint, r._origin));

		final float ERR = 0.00001f;

		Point3D center = customMath.add(_min, (customMath.div((customMath.sub(_max, _min)), 2.0f)));
		if (r._intersectPoint.getX() > _min.getX() - ERR && r._intersectPoint.getX() < _min.getX() + ERR)
		{
			normal = new Point3D.Double(-1.0f, 0.0f, 0.0f);
		}
		else if (r._intersectPoint.getX() > _max.getX() - ERR && r._intersectPoint.getX() < _max.getX() + ERR)
		{
			normal = new Point3D.Double(1.0, 0.0, 0.0);
		}
		else if (r._intersectPoint.getY() > _min.getY() - ERR && r._intersectPoint.getY() < _min.getY() + ERR)
		{
			normal = new Point3D.Double(0.0, -1.0, 0.0);
		}
		else if (r._intersectPoint.getY() > _max.getY() - ERR && r._intersectPoint.getY() < _max.getY() + ERR)
		{
			normal = new Point3D.Double(0.0, 1.0, 0.0);
		}
		else if (r._intersectPoint.getZ() > _min.getZ() - ERR && r._intersectPoint.getZ() < _min.getZ() + ERR)
		{
			normal = new Point3D.Double(0.0, 0.0, -1.0);
		}
		else if (r._intersectPoint.getZ() > _max.getZ() - ERR && r._intersectPoint.getZ() < _max.getZ() + ERR)
		{
			normal = new Point3D.Double(0.0, 0.0, 1.0);
		}
		else normal = r._intersectPoint;
		
		return true;
	}

	boolean intersect(Face triangle, List<Vertex> vertices, double voxelSize)
	{
		//System.out.println("intersect AABB");
	    double triangleMin, triangleMax;
	    double boxMin, boxMax;
	    final double error = 0.00000001;
	    
	    //Create array with triangle vertices
	    ArrayList<Point3D> triangleVertices = new ArrayList<Point3D>();
	    for(int vert : triangle.Vertices)
	    {
	    	triangleVertices.add(vertices.get(vert));
	    }
	    
	    //Create an array with Vertices of the box
	    ArrayList<Point3D> boxVertices = new ArrayList<Point3D>();
	    boxVertices.add(_min);
	    boxVertices.add(_max);
	    boxVertices.add(new Point3D.Double(_min.getX() + voxelSize, _min.getY(), _min.getZ()));
	    boxVertices.add(new Point3D.Double(_min.getX(), _min.getY() + voxelSize, _min.getZ()));
	    boxVertices.add(new Point3D.Double(_min.getX() + voxelSize, _min.getY() + voxelSize, _min.getZ()));
	    boxVertices.add(new Point3D.Double(_max.getX() - voxelSize, _max.getY(), _max.getZ()));
	    boxVertices.add(new Point3D.Double(_max.getX(), _max.getY() - voxelSize, _max.getZ()));
	    boxVertices.add(new Point3D.Double(_max.getX() - voxelSize, _max.getY() - voxelSize, _max.getZ()));

	    //Create array with x, y and z coordinates of min and max vertices of the box
	    ArrayList<Double> mincoord = new ArrayList<Double>();
	    mincoord.add(_min.getX());
	    mincoord.add(_min.getY());
	    mincoord.add(_min.getZ());
	    
	    ArrayList<Double> maxcoord = new ArrayList<Double>();
	    maxcoord.add(_max.getX());
	    maxcoord.add(_max.getY());
	    maxcoord.add(_max.getZ());
	    
	    // Test the box normals (x-, y- and z-axes)
	    Point3D[] boxNormals = new Point3D[] {
	        new Point3D.Double(1,0,0),
	        new Point3D.Double(0,1,0),
	        new Point3D.Double(0,0,1)
	    };
	    for (int i = 0; i < 3; i++)
	    {
	        ProjectOut result = Project(triangleVertices, boxNormals[i]);
	        triangleMin = result._min;
	        triangleMax = result._max;
	        if (triangleMax < mincoord.get(i) || triangleMin > maxcoord.get(i))
	        {
	        	//System.out.println("False stop 1");
	            return false; // No intersection possible.
	        }
	    }

	    Point3D triangleNormal = new Point3D.Double(triangle.Normal.getX(), triangle.Normal.getY(), triangle.Normal.getZ());
	    // Test the triangle normal
	    double triangleOffset = customMath.dot(triangleNormal, (Point3D)triangleVertices.get(0));
	    ProjectOut result = Project(boxVertices, triangleNormal);
	    boxMin = result._min;
	    boxMax = result._max;
	    if (boxMax + error < triangleOffset || boxMin - error > triangleOffset)
	    {
        	//System.out.println("False stop 2: boxMax - " + boxMax + " boxMin - " + boxMin + " triangleOffset - " + triangleOffset);
	        return false; // No intersection possible.
	    }
	    
	    Point3D aux1 = triangleVertices.get(0);
	    aux1 = customMath.sub(aux1, triangleVertices.get(1));
	    Point3D aux2 = triangleVertices.get(1);
	    aux2 = customMath.sub(aux2, triangleVertices.get(2));
	    Point3D aux3 = triangleVertices.get(2);
	    aux3 = customMath.sub(aux3, triangleVertices.get(0));
	    
	    // Test the nine edge cross-products
	    Point3D[] triangleEdges = new Point3D[] {
	        aux1,
	        aux2,
	        aux3
	    };
	    
	    for (int i = 0; i < 3; i++)
	    for (int j = 0; j < 3; j++)
	    {
	        // The box normals are the same as it's edge tangents
	        Point3D axis = customMath.cross(triangleEdges[i], boxNormals[j]);
	        ProjectOut res = Project(boxVertices, axis);
	        boxMin = res._min;
	        boxMax = res._max;
	        ProjectOut res2 = Project(triangleVertices, axis);
	        triangleMin = res2._min;
	        triangleMax = res2._max;
	        if (boxMax + error < triangleMin || boxMin - error > triangleMax)
	        {
	        	//System.out.println("False stop 3: boxMax - " + boxMax + " boxMin - " + boxMin + " triangleMin - " + triangleMin + " triangleMax - " + triangleMax);
	            return false; // No intersection possible
	        }
	    }

	    // No separating axis found.
	    return true;
	}

	class ProjectOut{
		public double _min, _max;
		ProjectOut(double min, double max){
			_min = min;
			_max = max;
		}
	}
	
	ProjectOut Project(ArrayList<Point3D> points, Point3D axis)
	{
	    double min = Double.POSITIVE_INFINITY;
	    double max = Double.NEGATIVE_INFINITY;
	    for(Point3D p : points)
	    {
	        double val = customMath.dot(axis, p);
	        if (val < min) min = val;
	        if (val > max) max = val;
	    }
	    return new ProjectOut(min, max);
	}
}
