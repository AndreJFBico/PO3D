package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class Voxel {

	Point3D _position;
	int _indexX, _indexY, _indexZ;
	private Point3D _min;
	private Point3D _max;
	VoxelType _type;
	float _DT;
	private double _size;
	List<VoxelNeighbor> _neighbors;
	AABB _boundingBox;
	
	public Voxel(Point3D position, int indexX, int indexY, int indexZ, Point3D min, Point3D max, VoxelType type, float DT, double size, List<VoxelNeighbor> neighbors, AABB boundingBox)
	{
		_position = position;
		_indexX = indexX;
		_indexY = indexY;
		_indexZ = indexZ;
		_type = type;
		set_min(min);
		set_max(max);
		_boundingBox = boundingBox;
		set_size(size);
		_neighbors = neighbors;
		_DT = DT;
	}
	
	public Voxel(Point3D position, int indexX, int indexY, int indexZ, Point3D min, Point3D max, double size, List<VoxelNeighbor> neighbors)
	{
		_position = position;
		_indexX = indexX;
		_indexY = indexY;
		_indexZ = indexZ;
		_type = VoxelType.OUTSIDE_VOXEL;
		set_min(min);
		set_max(max);
		_boundingBox = new AABB(get_min(), get_max());
		set_size(size);
		_neighbors = neighbors;
		_DT = Float.MAX_VALUE;
	}
	
	public void setType(VoxelType type)
	{
		_type = type;
	}
	
	public boolean intersect(Face triangle, List<Vertex> vertices, double voxelSize)
	{
		//System.out.println("Intersect Voxel");
		return _boundingBox.intersect(triangle, vertices, voxelSize);
	}
	
	public List<VoxelNeighbor> getNeighbors()
	{
		return _neighbors;
	}

	public Point3D get_min() {
		return _min;
	}

	public void set_min(Point3D _min) {
		this._min = _min;
	}

	public Point3D get_max() {
		return _max;
	}

	public void set_max(Point3D _max) {
		this._max = _max;
	}

	public double get_size() {
		return _size;
	}

	public void set_size(double _size) {
		this._size = _size;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Voxel) {
			Voxel e = (Voxel) o;
			return (_position.getX() == e._position.getX() && 
					_position.getY() == e._position.getY() &&
					_position.getZ() == e._position.getZ());
		} else {
			return false;
		}
		
	}
	
	public Voxel clone()
	{
		Voxel voxel = new Voxel(_position, _indexX, _indexY, _indexZ, _min, _max, _type, _DT, _size, _neighbors, _boundingBox);
		return voxel;
	}
}
