package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

import thor.graphics.Point3D;

public class VoxelMesh {

	protected List<Voxel> _boundary = new ArrayList<Voxel>();
	protected List<Voxel> _inside = new ArrayList<Voxel>();
	protected Voxel[] _voxelGrid;
	protected double _voxelSize;
	protected int numVoxelX, numVoxelY, numVoxelZ;
	protected AABB _boundingBox;
	protected List<Vertex> _vertices;
	protected List<Face> _faces;
	protected List<Voxel> _thinnedVolume;
	protected float _TP;
	protected Point3D _gridCenter;

	private boolean dtRModified = false;

	public VoxelMesh(List<Vertex> vertices, double voxelSize, List<Face> faces,
			float TP) {
		_vertices = vertices;
		_faces = faces;
		_TP = TP;
		double min_x, max_x, min_y, max_y, min_z, max_z;
		min_x = max_x = vertices.get(0).getX();
		min_y = max_y = vertices.get(0).getY();
		min_z = max_z = vertices.get(0).getZ();
		for (int i = 0; i < vertices.size(); i++) {
			if (vertices.get(i).getX() < min_x)
				min_x = vertices.get(i).getX();
			if (vertices.get(i).getX() > max_x)
				max_x = vertices.get(i).getX();
			if (vertices.get(i).getY() < min_y)
				min_y = vertices.get(i).getY();
			if (vertices.get(i).getY() > max_y)
				max_y = vertices.get(i).getY();
			if (vertices.get(i).getZ() < min_z)
				min_z = vertices.get(i).getZ();
			if (vertices.get(i).getZ() > max_z)
				max_z = vertices.get(i).getZ();
		}

		int increase = 3;
		min_x -= voxelSize * increase;
		min_y -= voxelSize * increase;
		min_z -= voxelSize * increase;
		max_x += voxelSize * increase;
		max_y += voxelSize * increase;
		max_z += voxelSize * increase;

		System.out.println("running");
		// bounding box of the mesh
		_boundingBox = new AABB(new Point3D.Double(min_x, min_y, min_z),
				new Point3D.Double(max_x, max_y, max_z));

		_voxelSize = voxelSize;

		double lengthX = max_x - min_x;
		double lengthY = max_y - min_y;
		double lengthZ = max_z - min_z;
		numVoxelX = (int) Math.round(lengthX / voxelSize);
		numVoxelY = (int) Math.round(lengthY / voxelSize);
		numVoxelZ = (int) Math.round(lengthZ / voxelSize);

		_gridCenter = new Point3D.Double(numVoxelX * voxelSize/2f, numVoxelY * voxelSize/2f, numVoxelZ * voxelSize/2f);
		_gridCenter = customMath.add(_gridCenter, new Point3D.Double(min_x, min_y, min_z));
		
		// System.out.println(numVoxelX + " " + numVoxelY + " " + numVoxelZ);
		// System.out.println(lengthX + " " + lengthY + " " + lengthZ);

		_voxelGrid = new Voxel[numVoxelX * numVoxelY * numVoxelZ];
		System.out.println(numVoxelX * numVoxelY * numVoxelZ);
		double current_min_x = min_x;
		double current_min_y = min_y;
		double current_min_z = min_z;

		double current_max_x = min_x + _voxelSize;
		double current_max_y = min_y + _voxelSize;
		double current_max_z = min_z + _voxelSize;

		for (int x = 0; x < numVoxelX; x++, current_min_x += _voxelSize, current_max_x += _voxelSize) {
			for (int y = 0; y < numVoxelY; y++, current_min_y += _voxelSize, current_max_y += _voxelSize) {
				for (int z = 0; z < numVoxelZ; z++, current_min_z += _voxelSize, current_max_z += _voxelSize) {
					_voxelGrid[x * numVoxelY * numVoxelZ + y * numVoxelZ + z] = new Voxel(
							new Point3D.Double(
									(current_min_x + current_max_x) / 2.0f,
									(current_min_y + current_max_y) / 2.0f,
									(current_min_z + current_max_z) / 2.0f), x,
							y, z, new Point3D.Double(current_min_x,
									current_min_y, current_min_z),
							new Point3D.Double(current_max_x, current_max_y,
									current_max_z), _voxelSize, null);
					System.out.println(x * numVoxelY * numVoxelZ + y
							* numVoxelZ + z);
				}
				current_min_z = min_z;
				current_max_z = min_z + _voxelSize;
			}
			current_min_y = min_y;
			current_max_y = min_y + _voxelSize;
		}
		System.out.println(numVoxelX * numVoxelY * numVoxelZ);
		
		
		
		// done initializing the voxels

		// possible optimization where instead of iterating all the voxels once
		// and then a second one for the neighbors
		// iterate once and while generating the neighbors create the voxels if
		// they arent already created.

		// for simplification purposes we iterate again to generate the
		// neighbors and assign specific types such as V, E and F.
		// generate the voxel neighbors
		for (int x = 0; x < numVoxelX; x++) {
			for (int y = 0; y < numVoxelY; y++) {
				for (int z = 0; z < numVoxelZ; z++) {
					Voxel tmpV = getVoxel(x, y, z);
					tmpV._neighbors = genVoxelNeighbors(x, y, z);
				}
			}
		}
	}

	public Point3D get_gridCenter() {
		return _gridCenter;
	}

	public void set_gridCenter(Point3D _gridCenter) {
		this._gridCenter = _gridCenter;
	}

	private void addNToV(List<VoxelNeighbor> nghbrs, int tmpx, int tmpy,
			int tmpz, VoxelNeighborType type) {
		// if its an invalid neighbor as in present outside of a grid, it
		// doesn`t add it
		if (tmpx < 0 || tmpy < 0 || tmpz < 0 || tmpx > numVoxelX - 1
				|| tmpy > numVoxelY - 1 || tmpz > numVoxelZ - 1)
			return;
		VoxelNeighbor tmp_voxel = new VoxelNeighbor(getVoxel(tmpx, tmpy, tmpz),
				type);
		nghbrs.add(tmp_voxel);
	}

	// Function that generates a voxels 26 neighbors.
	private List<VoxelNeighbor> genVoxelNeighbors(int x, int y, int z) {
		List<VoxelNeighbor> neighbors = new ArrayList<VoxelNeighbor>();

		// 9 x+1
		addNToV(neighbors, x + 1, y, z, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x + 1, y + 1, z, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x + 1, y - 1, z, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x + 1, y, z + 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x + 1, y, z - 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x + 1, y + 1, z + 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x + 1, y + 1, z - 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x + 1, y - 1, z + 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x + 1, y - 1, z - 1, VoxelNeighborType.V_NEIGHBOR);

		// 9 x-1
		addNToV(neighbors, x - 1, y, z, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x - 1, y + 1, z, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x - 1, y - 1, z, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x - 1, y, z + 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x - 1, y, z - 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x - 1, y + 1, z + 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x - 1, y + 1, z - 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x - 1, y - 1, z + 1, VoxelNeighborType.V_NEIGHBOR);
		addNToV(neighbors, x - 1, y - 1, z - 1, VoxelNeighborType.V_NEIGHBOR);

		// 8 x == 0
		addNToV(neighbors, x, y + 1, z, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x, y - 1, z, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x, y, z + 1, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x, y, z - 1, VoxelNeighborType.F_NEIGHBOR);
		addNToV(neighbors, x, y + 1, z + 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x, y + 1, z - 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x, y - 1, z + 1, VoxelNeighborType.E_NEIGHBOR);
		addNToV(neighbors, x, y - 1, z - 1, VoxelNeighborType.E_NEIGHBOR);

		return neighbors;
	}

	// returns inside voxels per line ray tracing
	private List<Voxel> getVoxelsPerZLineRayTracing(Ray ray, int x, int y) {
		List<Voxel> foundInsideVoxels = new ArrayList<Voxel>();
		List<IntersectionValue> intersectedValues = new ArrayList<IntersectionValue>();
		boolean intersected = false;
		for (int i = 0; i < _faces.size(); i++) {
			List<Integer> indexes = _faces.get(i).Vertices;
			List<Point3D> vertexes = new ArrayList<Point3D>();
			for (int z = 0; z < indexes.size(); z++) {
				vertexes.add(_vertices.get(indexes.get(z)));
			}
			intersected = ray.intersectWithTriangle(vertexes);

			boolean inserted = false;
			if (intersected) {
				// System.out.println("intersected: " + ray._dToObject);
				for (int z = 0; z < intersectedValues.size(); z++) {
					if (ray._dToObject < intersectedValues.get(z)._dtObject) {
						intersectedValues.add(z, new IntersectionValue(
								ray._dToObject, _faces.get(i)));
						inserted = true;
						// System.out.println("INSERTED");
						break;
					}
				}
				if (!inserted) {
					// System.out.println("INSERTED");
					intersectedValues.add(new IntersectionValue(ray._dToObject,
							_faces.get(i)));
				}
			}
			// else System.out.println("not intersected");
		}
		// System.out.println("finished intersection test");
		while (!intersectedValues.isEmpty()) {
			// System.out.println(intersectedValues.size());
			Voxel voxel = null, voxel2 = null;
			int i = 0;
			boolean found = false, found2 = false;
			// we have a list of triangles with an order of whos closest to the
			// ray origin.
			for (; i < intersectedValues.size(); i++) {
				// face is towards the ray direction
				if (customMath.dot(ray._direction,
						intersectedValues.get(i)._triangle.Normal) < 0) {
					// System.out.println("towards me!");
					int dtObject = (int) Math
							.ceil(intersectedValues.get(i)._dtObject
									/ _voxelSize) - 1;
					if (dtObject > numVoxelZ - 1) {
						dtObject -= 1;
					}

					// get boundary Voxel on triangle if it isnt a boundary
					// voxel check its Z neighbors for a boundary voxel.

					voxel = getVoxel(x, y, dtObject);
					if (voxel._type != VoxelType.BOUNDARY_VOXEL) {
						if (dtObject - 1 >= 0) {
							voxel = getVoxel(x, y, dtObject - 1);
							found = true;
							break;
						} else
							System.err
									.println("We are trying to acess a non existent voxel - negative index");
						System.err
								.println("We are getting a non boundary voxel");
					} else {
						found = true;
						break;
					}
				}
			}
			if (found) {
				for (int j = 0; j <= i && j < intersectedValues.size(); j++)
					intersectedValues.remove(j);
				for (i = 0; i < intersectedValues.size(); i++) {
					// face is towards the ray direction
					if (customMath.dot(ray._direction,
							intersectedValues.get(i)._triangle.Normal) > 0) {
						// System.out.println("against me!");
						int dtObject = (int) Math
								.ceil(intersectedValues.get(i)._dtObject
										/ _voxelSize) - 1;
						if (dtObject > numVoxelZ - 1) {
							dtObject -= 1;
						}
						// get boundary Voxel on triangle if it isnt a boundary
						// voxel check its Z neighbors for a boundary voxel.

						voxel2 = getVoxel(x, y, dtObject);
						if (voxel2._type != VoxelType.BOUNDARY_VOXEL) {
							if (dtObject - 1 >= 0) {
								voxel2 = getVoxel(x, y, dtObject - 1);
								found2 = true;
								break;
							} else
								System.err
										.println("We are trying to acess a non existent voxel - negative index");
							System.err
									.println("We are getting a non boundary voxel");
						} else {
							found2 = true;
							break;
						}
					}
				}
				if (found2) {
					for (int j = 0; j <= i && j < intersectedValues.size(); j++)
						intersectedValues.remove(j);
					i = 0;
					for (int iz = voxel._indexZ + 1; iz < voxel2._indexZ; iz++) {
						Voxel tmpVoxel = getVoxel(x, y, iz);
						if (tmpVoxel._type != VoxelType.BOUNDARY_VOXEL) {
							Voxel newInside = getVoxel(x, y, iz);
							newInside._type = VoxelType.INSIDE_VOXEL;
							foundInsideVoxels.add(newInside);

						}
					}
				} else
					break;
			} else
				break;
		}
		return foundInsideVoxels;
	}

	//TODO: check normalization of direction
	public List<Voxel> genInsideVoxelsRay() {
		for (int x = 0; x < numVoxelX; x++) {
			for (int y = 0; y < numVoxelY; y++) {
				Voxel voxel = getVoxel(x, y, 0);
				Ray ray = new Ray(customMath.sub(voxel._position,
						new Point3D.Double(0.0f, 0.0f, _voxelSize)),
						customMath.normalize(customMath.sub(customMath.add(voxel._position,
								new Point3D.Double(0.0f, 0.0f, 1.0f)),
								voxel._position)));
				_inside.addAll(getVoxelsPerZLineRayTracing(ray, x, y));
			}
		}
		return _inside;
	}

	// Generates inside voxels and the distance transform of each voxel to the
	// boundary
	public List<Voxel> genInsideVoxels() {
		boolean foundBoundary = false;
		int numBoundaryVoxels = 0;
		List<Voxel> _probableInsideVoxels = new ArrayList<Voxel>();
		for (int x = 0; x < numVoxelX; x++) {
			for (int y = 0; y < numVoxelY; y++) {
				for (int z = 0; z < numVoxelZ; z++) {
					Voxel voxel = getVoxel(x, y, z);

					if (voxel._type == VoxelType.BOUNDARY_VOXEL) {
						if (!foundBoundary) {
							foundBoundary = true;
						} else {
							// foundBoundary = false;

							// We are sure that the probable inside voxels are
							// inside
							for (int i = 0; i < _probableInsideVoxels.size(); i++) {
								_probableInsideVoxels.get(i)._type = VoxelType.PROBABLE_INSIDE_VOXEL;
								_inside.add(_probableInsideVoxels.get(i));
							}
							_probableInsideVoxels.clear();
						}
						numBoundaryVoxels++;
					} else {
						if (foundBoundary) {
							_probableInsideVoxels.add(voxel);
						}
					}
				}
				_probableInsideVoxels.clear();
				// reset check
				foundBoundary = false;
				System.out.println(numBoundaryVoxels);
				numBoundaryVoxels = 0;
			}
		}
		List<Voxel> removeVoxels = new ArrayList<Voxel>();
		boolean removed = false;
		do {
			removed = false;
			for (int i = 0; i < _inside.size(); i++) {
				List<VoxelNeighbor> tmpList = _inside.get(i)._neighbors;
				for (int z = 0; z < tmpList.size(); z++) {
					VoxelNeighbor tmpV = tmpList.get(z);
					if (tmpV._voxel._type == VoxelType.OUTSIDE_VOXEL
							&& tmpV._type == VoxelNeighborType.F_NEIGHBOR) {
						_inside.get(i)._type = VoxelType.OUTSIDE_VOXEL;
						removeVoxels.add(_inside.get(i));
						removed = true;
					}
				}
			}
		} while (removed);

		for (int i = 0; i < removeVoxels.size(); i++) {
			_inside.remove(removeVoxels.get(i));
		}

		for (int i = 0; i < _inside.size(); i++) {
			_inside.get(i)._type = VoxelType.INSIDE_VOXEL;
		}
		_probableInsideVoxels.clear();
		return _inside;
	}

	// First pass of the distance transform calculation algorithm.
	// This function is used when calculating the distance transform for
	// boundary voxels.
	public void assign_Boundary_Voxel_Distance_Transform() {
		for (int i = 0; i < _boundary.size(); i++) {
			Voxel voxel = _boundary.get(i);
			List<VoxelNeighbor> tmp_neighbors = voxel.getNeighbors();
			for (int z = 0; z < tmp_neighbors.size(); z++) {
				VoxelNeighbor v = tmp_neighbors.get(z);
				if (v._voxel._type == VoxelType.OUTSIDE_VOXEL) {
					if (v._type == VoxelNeighborType.F_NEIGHBOR) {
						voxel._DT = 3;
					} else if (v._type == VoxelNeighborType.E_NEIGHBOR) {
						voxel._DT = 4;
					} else if (v._type == VoxelNeighborType.V_NEIGHBOR) {
						voxel._DT = 5;
					}
				}
			}
		}
	}

	private List<Voxel> iterate_and_assign_dts(List<Voxel> BV) {
		// TODO - this is not a copy and so we will iterate and fix everything
		List<Voxel> newBV = new ArrayList<Voxel>();
		for (int i = 0; i < BV.size(); i++) {
			Voxel p = BV.get(i);
			List<VoxelNeighbor> tmp_neighbors = p.getNeighbors();
			for (int z = 0; z < tmp_neighbors.size(); z++) {
				VoxelNeighbor r = tmp_neighbors.get(z);
				if (r._voxel._type == VoxelType.INSIDE_VOXEL) {
					if (r._type == VoxelNeighborType.F_NEIGHBOR) {
						if (r._voxel._DT > p._DT + 3)
						{
							dtRModified = true;
							newBV.add(r._voxel);
						}							
						r._voxel._DT = Math.min(r._voxel._DT, p._DT + 3);
						// _voxelGrid[r._indexX * numVoxelY * numVoxelZ +
						// r._indexY * numVoxelZ + r._indexZ]._DT = r._DT;
					} else if (r._type == VoxelNeighborType.E_NEIGHBOR) {
						if (r._voxel._DT > p._DT + 4)
						{
							dtRModified = true;
							newBV.add(r._voxel);
						}	
						r._voxel._DT = Math.min(r._voxel._DT, p._DT + 4);
						// _voxelGrid[r._indexX * numVoxelY * numVoxelZ +
						// r._indexY * numVoxelZ + r._indexZ]._DT = r._DT;
					} else if (r._type == VoxelNeighborType.V_NEIGHBOR) {
						if (r._voxel._DT > p._DT + 5)
						{
							dtRModified = true;
							newBV.add(r._voxel);
						}	
						r._voxel._DT = Math.min(r._voxel._DT, p._DT + 5);
						// _voxelGrid[r._indexX * numVoxelY * numVoxelZ +
						// r._indexY * numVoxelZ + r._indexZ]._DT = r._DT;
					}
					
				}
			}
			p._type = VoxelType.OUTSIDE_VOXEL;
		}
		for (Voxel v : newBV) {
			v._type = VoxelType.BOUNDARY_VOXEL;
		}

		if (!dtRModified)
			return BV;
		return newBV;
	}

	public List<Voxel> propagateTheBoundaryInward() {
		// TODO: check if this returns a true copy i believe it does ->convex
		// hull example

		List<Voxel> BV = _boundary;

		// int stop = 2;
		// for(int i = 0; i < stop; i++)
		// {
		// dtRModified = false;
		// BV = iterate_and_assign_dts(BV);
		// System.out.println("Iteration: "+BV.size());
		//
		// }
		do {
			dtRModified = false;
			BV = iterate_and_assign_dts(BV);
			System.out.println("Iteration: " + BV.size());
		} while (dtRModified);

		for (Voxel v : _boundary) {
			v._type = VoxelType.BOUNDARY_VOXEL;
		}
		for (Voxel v : _inside) {
			v._type = VoxelType.INSIDE_VOXEL;
		}

		return BV;
	}

	// Volumetric thinning
	public List<Voxel> volumetricThinning() {

		List<Voxel> thinnedVolume = new ArrayList<Voxel>();
		for (int x = 0; x < numVoxelX; x++) {
			for (int y = 0; y < numVoxelY; y++) {
				for (int z = 0; z < numVoxelZ; z++) {

					float sumDT = 0;
					float numberOfVoxels = 0f;
					Voxel tmpV = getVoxel(x, y, z);

					if (tmpV._type == VoxelType.OUTSIDE_VOXEL
							|| tmpV._type == VoxelType.BOUNDARY_VOXEL)
						continue;

					List<VoxelNeighbor> neighbors = tmpV._neighbors;
					for (int i = 0; i < neighbors.size(); i++) {
						if (neighbors.get(i)._voxel._type != VoxelType.OUTSIDE_VOXEL ) {
							sumDT += neighbors.get(i)._voxel._DT;
							numberOfVoxels++;
						}
					}
					/*
					 * if(numberOfVoxels != 26) continue;
					 */
					float MNT = sumDT / numberOfVoxels;
					if (MNT < tmpV._DT - _TP) {
						thinnedVolume.add(tmpV);
					}
				}
			}
		}
		_thinnedVolume = thinnedVolume;
		return thinnedVolume;
	}

	public List<Voxel> genBoundary() {
		for (int i = 0; i < _faces.size(); i++) {
			// calculates AABB of a triangle
			Face f = _faces.get(i);
			Point3D vertex = _vertices.get(f.Vertices.get(0));
			double min_x = vertex.getX(), min_y = vertex.getY(), min_z = vertex
					.getZ(), max_x = vertex.getX(), max_y = vertex.getY(), max_z = vertex
					.getZ();
			for (int v = 1; v < f.Vertices.size(); v++) {
				vertex = _vertices.get(f.Vertices.get(v));
				if (vertex.getX() < min_x)
					min_x = vertex.getX();
				if (vertex.getX() > max_x)
					max_x = vertex.getX();
				if (vertex.getY() < min_y)
					min_y = vertex.getY();
				if (vertex.getY() > max_y)
					max_y = vertex.getY();
				if (vertex.getZ() < min_z)
					min_z = vertex.getZ();
				if (vertex.getZ() > max_z)
					max_z = vertex.getZ();
			}

			// Obtain a list of voxels accordingly to the triangle bounding box
			int lowx = (int) Math.floor((min_x - _boundingBox._min.getX())
					/ _voxelSize);
			int lowy = (int) Math.floor((min_y - _boundingBox._min.getY())
					/ _voxelSize);
			int lowz = (int) Math.floor((min_z - _boundingBox._min.getZ())
					/ _voxelSize);

			int highx = (int) Math.round((max_x - _boundingBox._min.getX())
					/ _voxelSize);
			int highy = (int) Math.round((max_y - _boundingBox._min.getY())
					/ _voxelSize);
			int highz = (int) Math.round((max_z - _boundingBox._min.getZ())
					/ _voxelSize);

			if (highx > numVoxelX - 1) {
				highx = numVoxelX - 1;
			}
			if (highy > numVoxelY - 1) {
				highy = numVoxelY - 1;
			}
			if (highz > numVoxelZ - 1) {
				highz = numVoxelZ - 1;
			}

			// number of voxels width on x, y and z
			//int xnum_voxels = highx - lowx;
			//int ynum_voxels = highy - lowy;
			//int znum_voxels = highz - lowz;

			for (int x = lowx; x <= highx; x++) {
				for (int y = lowy; y <= highy; y++) {
					for (int z = lowz; z <= highz; z++) {
						Voxel voxel = getVoxel(x, y, z);
						if (voxel.intersect(f, _vertices, _voxelSize)) {
							// System.out.println("intersected: face- " + i +
							// "// XYZ- " + x + " " + y + " " + z);
							if (!(_boundary.contains(voxel))) {
								voxel.setType(VoxelType.BOUNDARY_VOXEL);
								_boundary.add(voxel);
							}
						} else {
							// System.out.println("not intersected: face- " + i
							// + "// XYZ- " + x + " " + y + " " + z);
						}
					}
				}
			}
		}
		return _boundary;
	}

	public List<Voxel> getBoundary() {
		return _boundary;
	}

	public Voxel[] getGrid() {
		return _voxelGrid;
	}

	// not tested!!!!
	private Voxel getVoxel(int x, int y, int z) {
		return _voxelGrid[x * numVoxelY * numVoxelZ + y * numVoxelZ + z];
	}

	public List<Voxel> getCustomGrid(int min, int max) {
		List<Voxel> custom = new ArrayList<Voxel>();
		int maxValue = 0;
		for (Voxel v : _voxelGrid) {
			int val = Math.round(v._DT);
			if (val > maxValue && v._DT < 10000) {
				maxValue = val;
			}

			if (val <= max && val > min) {
				custom.add(v);
			}
		}

		System.out.println("Highest DT: " + maxValue);

		return custom;
	}
}
