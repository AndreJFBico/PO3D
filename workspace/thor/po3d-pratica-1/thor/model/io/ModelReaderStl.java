// Copyright 2012 Pedro B. Pascoal
package thor.model.io; 

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import thor.graphics.Point3D;
import thor.model.BufferedModel;
import thor.model.geoset.BufferedMesh;
import thor.model.geoset.Vertex;
import thor.model.geoset.Voxel;
import thor.model.geoset.VoxelMesh;
import thor.model.geoset.customMath;

// STereoLithography
class ModelReaderStl extends ModelReader {
	
	public ModelReaderStl(String name, String extension) {
		super(name, extension);
	}
	public BufferedModel read(String filename) throws IOException {
		
		BufferedModel model = new BufferedModel(filename, ".stl");

		BufferedMesh mesh = new BufferedMesh();
		Map<String, Integer> added_vertices = new HashMap<String, Integer>();
		
		int v_index = 0;
		float n_index = 0;
		
		try (Scanner scanner = new Scanner(new FileReader(filename));)
		{
		  scanner.useLocale(Locale.US);
		  while (scanner.hasNextLine())
		  {
			if(scanner.findInLine("solid") != null )
			{
				scanner.nextLine();
				continue;
			}
			
			if(scanner.findInLine("endsolid") != null || !scanner.hasNext())
			{
				break;
			}
			
			//ignore token facet
			String text = scanner.next();
			if(text.compareTo("facet") == 0)
			{
				//normal
				text = scanner.next();
				float nx = Float.parseFloat(scanner.next());
				float ny = Float.parseFloat(scanner.next());
				float nz = Float.parseFloat(scanner.next());
				mesh.addNormal(nx, ny, nz);
				scanner.nextLine();
				
				if(scanner.findInLine("outer loop") != null)
				{
					//removes outer loop line
					scanner.nextLine();
					List<Integer> v = new ArrayList<Integer>();
					List<Integer> vt = new ArrayList<Integer>();
					List<Float> vn = new ArrayList<Float>();
					
					vn.add(n_index);
					n_index++;
					
					while(scanner.findInLine("vertex") != null)
					{
						double vx = Double.parseDouble(scanner.next());
						double vy = Double.parseDouble(scanner.next());
						double vz = Double.parseDouble(scanner.next());
						String vert = Double.toString(vx) + Double.toString(vy) + Double.toString(vz);
						
						if(!added_vertices.containsKey(vert))
						{
							added_vertices.put(vert, v_index);
							mesh.addVertex(vx, vy, vz);
							v.add(v_index);
							v_index++;
						}
						else
						{
							v.add(added_vertices.get(vert));	
						}
						scanner.nextLine();						
					}
					mesh.addFace(v, vt, vn);
					if(scanner.findInLine("endfacet") != null)
					{
						scanner.nextLine();
					}
				}
			}
		  }
		}
		mesh.calculateNormals();
		model.addMesh(mesh);
		return model;
	}
	/*	mesh.calculateNormals();
		//model.addMesh(mesh);
		
		//Voxel
		//TODO: attention this is where we define the voxel size
		// 3f for mario
		// 0.02f for dino
		// 0.15f for cube
		//
		VoxelMesh test = new VoxelMesh(mesh.getVertices(), 0.1f, mesh.getFaces());
		
		List<Voxel> voxels;
		//this is where we generate the boundary
		test.genBoundary();
		
		//this is where we generate the inside voxels of the mesh
		//List<Voxel> voxels = test.genInsideVoxels();
		
		test.genInsideVoxelsRay();
		
		//this is where we generate the distance transform for the boundary voxels
		test.assign_Boundary_Voxel_Distance_Transform();
		
		test.propagateTheBoundaryInward();
		
		test.getCustomGrid();
		
		voxels = test.volumetricThinning();
		
		
		
		System.out.println("voxel size: " + voxels.size());
		v_index = 0;
		n_index = 0;
		
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
		model.addMesh(voxel_mesh);
		System.out.println("FINISHED");
		return model;
	}*/
	
	/*private void addface(int v_index, int inc0, int inc1, int inc2, int inc3, BufferedMesh mesh, float n_index)
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
	}*/
}