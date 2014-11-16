package thor.model.geoset;

import thor.graphics.Point3D;

public class IntersectionValue {
	double _dtObject;
	Face	_triangle;
	
	public IntersectionValue(double dtObject, Face triangle) {
		this._dtObject = dtObject;
		this._triangle = triangle;
	}
}
