// Copyright 2012 Pedro B. Pascoal
package thor.model.io; 

import java.io.IOException;

import thor.model.BufferedModel;

// Polygon File Format
class ModelReaderPly extends ModelReader {
	
	public ModelReaderPly(String name, String extension) {
		super(name, extension);
	}
	public BufferedModel read(String filename) throws IOException {
		/*
		 * ================================================================ 
		 * TODO: PO3D Pratica 1 - Polygon File Format (PLY)
		 * Add here the code to read a PLY file
		 * The file "thor.model.io.ModelReaderPly.java" serves as example of file reading
		 *  
		 * * BufferedModel model = new BufferedModel("model-name", "model-extension");
		 * * 
		 * * BufferedMesh mesh = new BufferedMesh();
		 * * 
		 * * for each vertex:
		 * * * mesh.addVertex(x, y, z);
		 * * 
		 * * for each face:
		 * * * mesh.addFace("list-of-vertices");
		 * * 
		 * * model.addMesh(mesh);
		 * * return model;
		 * ================================================================
		 */
		throw new IOException("PO3D-Pratica-1: file format not recognized");
	}
}