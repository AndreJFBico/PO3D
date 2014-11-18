package thor.model.geoset;

public class IntersectionValue {
	double _dtObject;
	Face	_triangle;
	
	public IntersectionValue(double dtObject, Face triangle) {
		this._dtObject = dtObject;
		this._triangle = triangle;
	}
}
