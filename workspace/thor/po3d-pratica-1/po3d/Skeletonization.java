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
import thor.model.geoset.Mesh;
import thor.model.geoset.Vertex;
import thor.model.geoset.Voxel;
import thor.model.geoset.VoxelMesh;
import thor.model.geoset.customMath;
import thor.model.io.ModelIO;
import thor.modelanalysis.utils.Normalize;
import thor.modelanalysis.utils.Scene;

public class Skeletonization {

	public static void main(String args[]) throws IllegalArgumentException, IOException {

		String inputModelFile1 = ".\\model-samples\\mario.stl";
		String inputModelFile2 = ".\\model-samples\\torus.stl";
		String inputModelFile3 = ".\\model-samples\\cube.stl";
		float TP = 0.0f;
		
		// Read the model from the file
		Model model = ModelIO.read(new File(inputModelFile1));

		// Normalize the model, so it fits in the Scene window
		Normalize.translation(model);
		Normalize.scale(model);

		Mesh mesh = model.getMeshes().get(0);

		//Voxelize a Mesh
		//TODO: attention this is where we define the voxel size
		// 3f for mario
		// 0.02f for dino
		// 0.08f for cube with 1.5f thinning parameter
		// 0.04f for torus with 2.5f thinning parameter
		//
		VoxelMesh test = new VoxelMesh(mesh.getVertices(), 0.04f, mesh.getFaces(), TP);
		
		List<Voxel> voxels;

		test.genBoundary();
		
		test.genInsideVoxelsRay();
		
		test.assign_Boundary_Voxel_Distance_Transform();
		
		test.propagateTheBoundaryInward();
		
		voxels = test.getCustomGrid();
		
		test.volumetricThinning();
		
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
		BufferedModel VoxelizedModel = new BufferedModel("voxelization", "none");
		VoxelizedModel.addMesh(voxel_mesh);
		System.out.println("FINISHED");
		
		// Create a new scene to be drawn
		Scene scene = new Scene();
		scene.setCameraPosition(0, 0, 1);
		
		// Add the model of drawables to be drawn on the Scene 
		scene.addDrawable(VoxelizedModel);
		new ModelViewer(512, 512, VoxelizedModel);
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
// a Bone receives a name, Id, it's parent Id (-1 if the bone is the root), the pivot point.
/*Bone root = new Bone("root", 0, -1, new Point3D.Double(0, 0, 0));
model.addBone(root);

Bone legR = new Bone("legR", 1, 0, new Point3D.Double(-0.5, -0.25, 0));
legR.setParent(root);
model.addBone(legR);

Bone legL = new Bone("legL", 3, 0, new Point3D.Double(0.5, -0.25, 0));
legL.setParent(root);
model.addBone(legL);

Bone footR = new Bone("footR", 2, 1, new Point3D.Double(-0.5, -0.5, 0));
footR.setParent(legR);
model.addBone(footR);

Bone footL = new Bone("footL", 4, 3, new Point3D.Double(0.5, -0.5, 0));
footL.setParent(legL);
model.addBone(footL);

Bone torso = new Bone("torso", 4, 3, new Point3D.Double(0, 0.25, 0));
torso.setParent(root);
model.addBone(torso);

Bone armR = new Bone("armR", 1, 0, new Point3D.Double(-0.5, 0.25, 0));
armR.setParent(torso);
model.addBone(armR);

Bone armL = new Bone("armL", 1, 0, new Point3D.Double(0.5, 0.25, 0));
armL.setParent(torso);
model.addBone(armL);

Bone head = new Bone("head", 1, 0, new Point3D.Double(0, 0.5, 0));
head.setParent(torso);
model.addBone(head);*/

// To change the bones Parent (origin), and PivotParent (destiny)
	//bone.setParent(bones.get(parentId));
	//bone.setPivotPoint(pivotPoint.get(boneId));