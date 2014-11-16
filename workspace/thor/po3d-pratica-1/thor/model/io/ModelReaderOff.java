// Copyright 2012 Pedro B. Pascoal
package thor.model.io; 

import java.io.IOException;

import thor.model.BufferedModel;

// Object File Format
class ModelReaderOff extends ModelReader {
	
	public ModelReaderOff(String name, String extension) {
		super(name, extension);
	}
	public BufferedModel read(String filename) throws IOException {
		throw new IOException("PO3D-Pratica-1: file format not recognized");
	}
}