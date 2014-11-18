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
}