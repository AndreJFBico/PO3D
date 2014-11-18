package thor.model.geoset;


import java.util.List;

import thor.graphics.Point3D;

public class Ray {

	Point3D _origin;
	Point3D _direction;
	Point3D _intersectPoint;
	double _t;
	double _dToObject;
	
	public Ray(Point3D origin, Point3D direction)
	{
		_origin = origin;
		_direction = direction;
	}
	
	public boolean intersectWithTriangle(List<Point3D> vertexes)
	{
		//Calculating normal.
		Point3D normal = customMath.cross(
					customMath.sub(
							vertexes.get(0),
							vertexes.get(1)),
					customMath.sub(
							vertexes.get(1),
							vertexes.get(2)));
		
		
		if (!(normal.getX() == 0.0f && normal.getY() == 0.0f && normal.getZ() == 0.0f))
			normal = customMath.normalize(normal);

		double d =  (
					customMath.dot(
							customMath.sub(
									vertexes.get(1),
									_origin), 
							normal)
					/
					customMath.dot(
							_direction, 
							normal));

		Point3D P = customMath.add(
							_origin,
							customMath.mul(
									_direction,
									d));
		
		if (d<0)
			return false;


		int i0 = 0, i1 = 0, i2 = 0;
		//Calculating indices.
		if (Math.abs(normal.getX()) > Math.abs(normal.getY()) && Math.abs(normal.getX()) > Math.abs(normal.getZ()))
		{
			i0 = 0;
			i1 = 1;
			i2 = 2;
		}
		else if (Math.abs(normal.getY()) > Math.abs(normal.getX()) && Math.abs(normal.getY()) > Math.abs(normal.getZ()))
		{
			i1 = 0;
			i0 = 1;
			i2 = 2;
		}
		else if (Math.abs(normal.getZ()) > Math.abs(normal.getX()) && Math.abs(normal.getZ()) > Math.abs(normal.getY()))
		{
			i1 = 0;
			i2 = 1;
			i0 = 2;
		}

		Point3D O = _origin;
		Point3D D = _direction;
		
		//_vertexes
		
		Point3D u = new Point3D.Double(0.0f, 0.0f, 0.0f), v = new Point3D.Double(0.0f, 0.0f, 0.0f);
		customMath.setX(u, customMath.getXYZ(P, i1) - customMath.getXYZ(vertexes.get(0),i1));
		customMath.setX(v, customMath.getXYZ(P,i2) - customMath.getXYZ(vertexes.get(0),i2));
		customMath.setY(u, customMath.getXYZ(vertexes.get(1),i1) - customMath.getXYZ(vertexes.get(0),i1));
		customMath.setZ(u, customMath.getXYZ(vertexes.get(2),i1) - customMath.getXYZ(vertexes.get(0),i1));
		customMath.setY(v, customMath.getXYZ(vertexes.get(1),i2) - customMath.getXYZ(vertexes.get(0),i2));
		customMath.setZ(v, customMath.getXYZ(vertexes.get(2),i2) - customMath.getXYZ(vertexes.get(0),i2));

		double Beta = 0.0f;
		double Alfa = 0.0f;

		if (u.getY() == 0.0f)
		{
			Beta = u.getX() / u.getZ();
			if (Beta <= 1.0f && Beta >= 0.0f)
			{
				Alfa = (v.getX() - Beta*v.getZ()) / v.getY();
			}
		}
		else {
			Beta = (v.getX()*u.getY() - u.getX()*v.getY()) / (v.getZ()*u.getY()- u.getZ()*v.getY());
			if (Beta <= 1.0f && Beta >= 0.0f)
			{
				Alfa = (u.getX() - Beta*u.getZ()) / u.getY();
			}
		}
		if (Beta >= 0.0f && Alfa >= 0.0f && (Alfa + Beta) <= 1)
		{
			
			_intersectPoint = P;
			_dToObject = customMath.length(
							customMath.sub(
									_intersectPoint,
									_origin));
			_t = d;
			return true;
		}
		return false;
	}
}
