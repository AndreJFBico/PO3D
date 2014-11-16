package thor.model.geoset;

public class VoxelNeighbor {
	Voxel _voxel;
	VoxelNeighborType _type;
	
	public VoxelNeighbor(Voxel voxel, VoxelNeighborType type)
	{
		_voxel = voxel;
		_type = type;
	}
}
