package po3d;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thor.Model;
import thor.demo.ModelViewer;
import thor.graphics.Point3D;
import thor.model.BufferedModel;
import thor.model.geoset.Bone;
import thor.model.geoset.BufferedMesh;
import thor.model.geoset.GraphEdge;
import thor.model.geoset.GraphNode;
import thor.model.geoset.Mesh;
import thor.model.geoset.Skeleton;
import thor.model.geoset.Vertex;
import thor.model.geoset.Voxel;
import thor.model.geoset.VoxelMesh;
import thor.model.geoset.customMath;
import thor.model.io.ModelIO;
import thor.modelanalysis.utils.Normalize;
import thor.modelanalysis.utils.Scene;

public class Skeletonization {

	public static void main(String args[]) throws IllegalArgumentException, IOException {
		// Parameters
		// 0.02f for mario and 1.6f thinning parameter
		// 0.02f for dino and 1.5f thinning parameter
		// 0.03f for cube with 1.5f thinning parameter
		// 0.03f for torus with 2.5f thinning parameter
		// 0.025 for cylinder with 1.5f thinning parameter
		// clustering test - torus with voxel size 0.03f tp = 1.5f distancethreshold = 0.1 and clustersize = 3
		
		String inputModelFile1 = ".\\model-samples\\mario.stl";
		String inputModelFile2 = ".\\model-samples\\torus.stl";
		String inputModelFile3 = ".\\model-samples\\cube.stl";
		String inputModelFile4 = ".\\model-samples\\dino.stl";
		String inputModelFile5 = ".\\model-samples\\monkey.stl";
		String inputModelFile6 = ".\\model-samples\\cylinder.stl";
		/*float TP = 1.5f;
		float voxelSize = 0.02f;
		float distanceThreshold = 0.3f;
		int clusterSize = 3;*/
		float TP = 1.8f;
		float voxelSize = 0.02f;
		float distanceThreshold = 0.1f;
		int clusterSize =3;
		
		// Read the model from the file
		Model model = ModelIO.read(new File(inputModelFile1));

		// Normalize the model, so it fits in the Scene window
		Normalize.translation(model);
		Normalize.scale(model);

		Mesh mesh = model.getMeshes().get(0);

		VoxelMesh test = new VoxelMesh(mesh.getVertices(), voxelSize, mesh.getFaces(), TP);
		
		Point3D barycenter = mesh.getBarycenter();
		Point3D gridCenter = test.get_gridCenter();
		
//		model.translate(0.1, 0.1, 0.1);
		
		BufferedMesh voxel_mesh = generateThinnedVoxelizedMesh(test, mesh, TP);
		
		BufferedModel VoxelizedModel = new BufferedModel("voxelization", "none");
		VoxelizedModel.addMesh(voxel_mesh);
		System.out.println("FINISHED THINNING PROCESS");
		
		//new ModelViewer(512, 512, VoxelizedModel);
		
		BufferedModel skeleton = generateSkeleton(test, distanceThreshold, clusterSize);
		System.out.println("FINISHED GRAPH GENERATION");
		skeleton.addMesh(voxel_mesh);
		new ModelViewer(512, 512, skeleton);
	}

	private static  BufferedModel generateSkeleton(VoxelMesh test, float distanceThreshold, int clusterSize)
	{
		BufferedModel model = new BufferedModel("sk", "none");
		Skeleton skeleton = new Skeleton(test, distanceThreshold, clusterSize);
		List<GraphNode> nodes = skeleton.get_directedGraph();	
		List<GraphNode> sucessors = new ArrayList<GraphNode>();
		
		int nodeId = 0;
		String name = "";
		for(GraphNode n : nodes)
		{
			//-1  means parent is undefined
			Bone b = new Bone(name + nodeId, nodeId, -2, n.getPosition());		
			n.setBone(b);
			nodeId++;
		}
		
		for(GraphNode k : nodes)
		{
			sucessors.add(k);
			while(!sucessors.isEmpty())
			{
				GraphNode n = sucessors.get(0);
				sucessors.remove(0);
				List<GraphEdge> edges = n.getEdges();
				for(GraphEdge e : edges)
				{
					if(!e.isProcessed())
					{
						if(n.equals(e.get_origin()))
						{
							GraphNode destination = e.get_destination();
							destination.getBone().setParent(n.getBone());
							sucessors.add(destination);
						}
						else if (n.equals(e.get_destination()))
						{
							GraphNode origin = e.get_origin();
							origin.getBone().setParent(n.getBone());	
							sucessors.add(origin);
						}
						e.setProcessed(true);
					}
				}			
			}
		}

		for(GraphNode n : nodes)
		{
			model.addBone(n.getBone());
		}
		
		return model;
	}
	
	private static BufferedMesh generateThinnedVoxelizedMesh(VoxelMesh test,Mesh mesh, float TP)
	{
		List<Voxel> voxels;

		test.genBoundary();
		
		test.genInsideVoxelsRay();
		
		test.assign_Boundary_Voxel_Distance_Transform();
		
		test.propagateTheBoundaryInward();
		
		test.getCustomGrid(14, 25);
		
		voxels = test.volumetricThinning();
		
		
		System.out.println("voxel size: " + voxels.size());
		int v_index = 0;
		int n_index = 0;
		
		BufferedMesh voxel_mesh = new BufferedMesh();
		for(int i = 0; i < voxels.size(); i++)
		{
			Voxel v = voxels.get(i);
		    //Create an array with Vertices of the box
		    voxel_mesh.addVertex(v.get_min().getX(), v.get_min().getY(), v.get_min().getZ());
		    voxel_mesh.addVertex(v.get_max().getX(), v.get_max().getY(), v.get_max().getZ());
		    voxel_mesh.addVertex(new Vertex(v.get_min().getX() + v.get_size(), v.get_min().getY(), v.get_min().getZ()));
		    voxel_mesh.addVertex(new Vertex(v.get_min().getX(), v.get_min().getY() + v.get_size(), v.get_min().getZ()));
		    voxel_mesh.addVertex(new Vertex(v.get_min().getX() + v.get_size(), v.get_min().getY() + v.get_size(), v.get_min().getZ()));
		    voxel_mesh.addVertex(new Vertex(v.get_max().getX() - v.get_size(), v.get_max().getY(), v.get_max().getZ()));
		    voxel_mesh.addVertex(new Vertex(v.get_max().getX(), v.get_max().getY() - v.get_size(), v.get_max().getZ()));
		    voxel_mesh.addVertex(new Vertex(v.get_max().getX() - v.get_size(), v.get_max().getY() - v.get_size(), v.get_max().getZ()));	
		    
		    addface(v_index, 0, 3, 4, 2, voxel_mesh, n_index);
		    n_index += 1;
		    addface(v_index, 0, 7, 5, 3, voxel_mesh, n_index);
		    n_index += 1;
		    addface(v_index, 0, 2, 6, 7, voxel_mesh, n_index);
		    n_index += 1;
		    addface(v_index, 2, 4, 1, 6, voxel_mesh, n_index);
		    n_index += 1;
		    addface(v_index, 4, 3, 5, 1, voxel_mesh, n_index);
		    n_index += 1;
		    addface(v_index, 1, 5, 7, 6, voxel_mesh, n_index);
		    n_index += 1;

			v_index += 8;
		}
		return voxel_mesh;
	}
	
	private static void addface(int v_index, int inc0, int inc1, int inc2, int inc3, BufferedMesh mesh, float n_index)
	{
		List<Integer> ver = new ArrayList<Integer>();
		List<Integer> vert = new ArrayList<Integer>();
		List<Float> vern = new ArrayList<Float>();
	
		//Face 0
		ver.add(v_index + inc0);
		ver.add(v_index + inc1);
		ver.add(v_index + inc2);
		ver.add(v_index + inc3);
		
		Point3D normal = 
		customMath.cross(
				customMath.sub(
						mesh.getVertices().get(v_index + inc1), 
						mesh.getVertices().get(v_index + inc0)),
				customMath.sub(
						mesh.getVertices().get(v_index + inc2), 
						mesh.getVertices().get(v_index + inc1)));
		
		mesh.addNormal((float)normal.getX(), (float)normal.getY(), (float)normal.getZ());
		vern.add(n_index);
		
		mesh.addFace(ver, vert, vern);
	}
	
}
