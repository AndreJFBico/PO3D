package thor.model.geoset;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import thor.graphics.Vector3D;
import thor.model.geoset.Face;
import thor.model.geoset.Vertex;

public class Manifold {
	protected List<Vertex> _vertices = new ArrayList<Vertex>();		// list of vertices
	protected List<Point2D> _textCoord = new ArrayList<Point2D>();	// list of texture coordinates
	protected List<Vector3D> _normals = new ArrayList<Vector3D>();	// list of normals
	protected List<Face> _faces = new ArrayList<Face>(); 			// list of faces (triangles)
	protected ArrayList<Pair<Integer, Integer>> _edges = new ArrayList<Pair<Integer, Integer>>();
	protected Hashtable<Integer, int[]> _faceEdges = new Hashtable<Integer, int[]>();
	protected Hashtable<Integer, ArrayList<Integer>> _vertexFaces = new Hashtable<Integer, ArrayList<Integer>>();
	
	public Manifold(List<Vertex> vertices, List<Point2D> textCoord, List<Vector3D> normals, List<Face> faces)
	{
		_vertices = vertices;
		_textCoord = textCoord;
		_normals = normals;
		_faces = faces;
		
	}
	
	protected boolean createEdges()
	{
		
		//######### 1 - CREATES AN ARRAY WITH ALL EDGES
		//######### 2 - CREATES A MAP WITH FACES AS KEYS AND A SIMPLE ARRAY OF EDGES AS VALUE
		//######### 3 - CREATES A MAP WITH VERTICES AS KEYS AND FACES AS VALUE
		
		int facenum = 0;
		int edgenum = 0;
		
		//for each face
		for(Face f : _faces)
		{			
			if(f.Vertices.size() < 3)
				continue;
			if(f.Vertices.size() > 3)
				System.out.println("WARNiNG!?!?!?!!?!?!?!");
			
			int first = f.Vertices.get(0);
			int prev = f.Vertices.get(0);
			
			int[] edges = new int[3];
			
			//for each vertex of that face
			for(int i = 1; i < f.Vertices.size(); i++)
			{				
				int u = f.Vertices.get(i);
				
				Pair<Integer, Integer> edge = new Pair<Integer, Integer>(prev, u);
				_edges.add(edge);
				edges[i-1] = edgenum;
				
				edgenum++;
				prev = f.Vertices.get(i);
			}
			
			Pair<Integer, Integer> edge = new Pair<Integer, Integer>(prev, first);
			_edges.add(edge);
			edges[2] = edgenum;
			_faceEdges.put(facenum, edges);
			edgenum++;
			facenum++;
		}
		
		facenum = 0;
		for(Face f: _faces)
		{
			for(Integer i : f.Vertices)
			{
				if(!_vertexFaces.containsKey(i))
				{
					_vertexFaces.put(i, new ArrayList<Integer>());
				}
				
				ArrayList<Integer> faces = _vertexFaces.get(i);
				faces.add(facenum);
				_vertexFaces.put(i, faces);
			}
			facenum++;
		}
		
		
		if(_edges.isEmpty()){
			System.out.println("Edges are null");
			return false;	
		}
		return true;
//		for(int i = 0 ; i < _vertexFaces.size(); i++)
//		{
//			System.out.println("Vertex number: " + i);
//			System.out.println("Faces of Vertex:");
//			for(int j = 0; j < _vertexFaces.get(i).size(); j++)
//			{
//				System.out.println("	- " + _vertexFaces.get(i).get(j));				
//			}
//		}
	}
	
	protected boolean checkEdgeIncidency()
	{	

		//######### 1 - CHECK IF EDGES BELONG TO AT LEAST A FACE ((and no more than 2))
		
		int Left;
		int Right;
		int incidency;

//		try{
//		int l = 0;
//		PrintWriter writer = new PrintWriter("debug.txt", "UTF-8");
//		for(Pair<Integer, Integer> k : _edges)
//			{
//				writer.println("	Edge: " + l);	
//				writer.println("[" + k.getL() + ", " + k.getR() + "]");	
//				writer.println("    #######    ");
//				l++;
//			}
//		writer.close();
//		}catch(Exception e){}
//		int l = 0;
//		for(Pair<Integer, Integer> k : _edges)
//		{
//			System.out.println("	Edge: " + l);	
//			System.out.println("[" + k.getL() + ", " + k.getR() + "]");	
//			System.out.println("    #######    ");
//			l++;
//		}
//		l = 0;
		
		// For each edge
		for(Pair<Integer, Integer> p1 : _edges)
		{
			Left = p1.getL();
			Right = p1.getR();
			
			incidency = 0;
			
			int p2L = 0;
			int p2R = 0;
//			int k = 0;
			
			// For each face
			Set<Integer> keys = _faceEdges.keySet();
			for(Integer key : keys)
			{
				//get face edges
				int[] edges = _faceEdges.get(key);
				
				//for each edge of that face
				for(int i : edges)
				{
					Pair<Integer, Integer> e = _edges.get(i);
					p2L = e.getL();
					p2R = e.getR();
	
//					System.out.println("##### Edge: " + k + " ######");
//					System.out.println("[" + Left + ", " + Right + "]");
//					System.out.println("[" + p2L + ", " + p2R + "]");
//					System.out.println("##################### faces ---> " + _faces.size());
//					k++;
					if((Left == p2L && Right == p2R) || (Left == p2R && Right == p2L))
					{
						incidency++;
					}
				}
			}
//			incidency--; // because it will always find the same edge
//			l++;
			if(incidency > 0 && incidency < 3)
				continue;
			else
			{
				System.out.println("failed in: with incidency: " + incidency);			
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean checkPathToVertex(int start, int end, int skip)
	{
		//get all edges connected to this vertex
		//get all 
		//for each
		return true;
	}
	
	protected boolean checkVertexFanAlternative()
	{
		for(int i = 0; i < _vertices.size(); i++ )
		{
			//get all edges connected to this vertice
		}
		
		return true;
	}
	
	protected boolean checkVertexFan()
	{

		// for each vertex in vertexFaces
		// check if its faces are connected to others (n>2 - 2 edges; side faces have 1 edge)
		// check if they are connected as a whole [1->2->3->4(->1)]
		
		for(int i = 0; i < _vertices.size(); i++)
		{
			ArrayList<Integer> faces = null;
			faces = _vertexFaces.get(i);
			
			if(faces.size() < 2)
				continue;
			
			// 1 - Create Array for amount of edges common between faces and auxiliary arrays
			
			Hashtable<Integer, Integer> numOverlapEdges = new Hashtable<Integer, Integer>();
			Hashtable<Integer, ArrayList<Integer>> overlapedEdges = new Hashtable<Integer, ArrayList<Integer>>();
			Hashtable<Integer, ArrayList<Integer>> adjFaces = new Hashtable<Integer, ArrayList<Integer>>();
			//Array to store all faces edges
			Hashtable<Integer, ArrayList<Integer>> allEdges = new Hashtable<Integer, ArrayList<Integer>>(faces.size());

			//for each face
			for(Integer j : faces)
			{
				numOverlapEdges.put(j, 0);
				adjFaces.put(j, new ArrayList<Integer>());
				allEdges.put(j, new ArrayList<Integer>());
				overlapedEdges.put(j, new ArrayList<Integer>());
				
				//for each edge of that face
				for(int m : _faceEdges.get(j))
				{
					//Store edge on array
					ArrayList<Integer> temp = allEdges.get(j);
					temp.add(m);
					allEdges.put(j, temp);
				}
			}
			
			// 2 - Fill the array with correct values
			Hashtable<Integer, ArrayList<Integer>> aux = new Hashtable<Integer, ArrayList<Integer>>();
			aux.putAll(allEdges);
			
			//for each face
			for(Integer j : faces)
			{
				aux.remove(j);
				
				//for each edge of the face
				for(int m : allEdges.get(j))
				{
					Pair<Integer, Integer> p = _edges.get(m);
					
					Set<Integer> keys = aux.keySet();
					// for each face except the being analyzed
					for(Integer k : keys)
					{
						//for each edge of these faces
						for(int n : aux.get(k))
						{
							Pair<Integer, Integer> other = _edges.get(n);
							int pLeft = p.getL();
							int pRight = p.getR();
							int otherLeft = other.getL();
							int otherRight = other.getR();
							if(( pLeft == otherLeft && pRight == otherRight) || (pLeft == otherRight && pRight == otherLeft))
							{
								int num = numOverlapEdges.get(j) + 1;
								numOverlapEdges.put(j, num);
								num = numOverlapEdges.get(k) + 1;
								numOverlapEdges.put(k, num);
								
								ArrayList<Integer> temp = adjFaces.get(j);
								temp.add(k);
								adjFaces.put(j, temp);
							
								ArrayList<Integer> newList = overlapedEdges.get(j);
								newList.add(n);
								overlapedEdges.put(j, newList);
								
								newList = overlapedEdges.get(k);
								newList.add(n);
								overlapedEdges.put(k, newList);
							}
						}
					}
				}
			}
			
			//TODO -> check the values in numOverlapEdges array.
			
			int limit1 = -1;
			int limit2 = -1;
			ArrayList<Integer> middles = new ArrayList<Integer>();
			
			Set<Integer> keys = adjFaces.keySet();
			for(Integer k : keys)
			{
				if(numOverlapEdges.get(k) < 1 && _faces.size() > 1)
					return false;
				if(numOverlapEdges.get(k) == 1)
					if(limit1 != -1){
						if(limit2 != -1){
							System.out.println("An error occured -> checkVertexFan");
							return false;
						}
						limit2 = k; 
					}
					else
					{
						limit1 = k;
					}
				else
				{
					middles.add(k);
				}
			}
			
			if(middles.size() == 0 && limit1 == -1 && limit2 == -1)
				return false;
			
			//######### IF CLOSED FAN
			if(limit1 == -1 && limit2 == -1)
			{
//				try{
//				System.out.println("STUFF");
//				System.out.println(middles.get(0));
//				System.out.println(overlapedEdges.get(middles.get(0)).size());
//				System.out.println(middles.size());
//				System.out.println(overlapedEdges.size());
//				System.out.println(_edges.size());
				Pair<Integer, Integer> first = _edges.get(overlapedEdges.get(middles.get(0)).get(0));
//				}catch(IndexOutOfBoundsException e)
//				{
//					PrintWriter writer = new PrintWriter("debug2.txt", "UTF-8");
//					for (Enumeration<ArrayList<Integer>> u = overlapedEdges.elements(); u.hasMoreElements();)
//						for(Integer o : u.nextElement())
//						{
//					       System.out.println(o);
//						}
//						writer.println("	OverlapedEdge: " + k);	
//						writer.println("[" + overlapedEdges.get() + ", " + k.getR() + "]");	
//						writer.println("    #######    ");
//					}
//					writer.close();
//				}
				ArrayList<Integer> copy = new ArrayList<Integer>();
				copy.addAll(middles);
				copy.remove(0);
				
				//for each face
				for(int l = 0; l < middles.size(); l++)
				{		
					//for each face except the one being tracked		
					outerloop:
					for(Integer k : copy)
					{
						//get array of edges
						ArrayList<Integer> OE = overlapedEdges.get(k);
						if(OE.size() > 2)
						{
							System.out.println("OE size is not good");
							return false;
						}
						//if(OE.size() == 1)
						//	return true;
						
						int index = 0;
						//for each edge
						for(int b : OE)
						{
							Pair<Integer, Integer> p = _edges.get(b);
							//if edge is equal to one being tracked
							if((p.getL() == first.getL() && p.getR() == first.getR()) || (p.getL() == first.getR() && p.getR() == first.getL()))
							{
								//remove from overlaped edges list
								ArrayList<Integer> newList = new ArrayList<Integer>();
								newList.addAll(OE);
								newList.remove(index);
								overlapedEdges.put(k, newList);
								//set the face being tracked to the one which the edge belonged
								first = _edges.get(overlapedEdges.get(k).get(0));
								//remove the new edge being tracked from the copied vector
								copy.remove(k);
								//restart the checking
								break outerloop;
							}
							index++;
						}
					}
				}
				
				Pair<Integer, Integer> newfirst = _edges.get(overlapedEdges.get(middles.get(0)).get(1));
				
				if( ((newfirst.getL() == first.getL() && newfirst.getR() == first.getR()) ||
					(newfirst.getL() == first.getR() && newfirst.getR() == first.getL())) &&
					(copy.size() == 0))
				{
					continue;			
				}
				else 
				{
					System.out.println("the end was not right");
					return false;
				}
			}
			else if(limit1 == -1 || limit2 == -1)
			{
				System.out.println("Either limit was not filled");
				return false;
			}
			//######### IF OPEN FAN
			else
			{
				Pair<Integer, Integer> first = _edges.get(overlapedEdges.get(limit1).get(0));
				boolean isMiddle = false;

				for(int k = 0 ; k < faces.size(); k++)
				{
					//for each middle check if overlapping edge
					//if true( remove that from that face)
					// compare the other to rest of middles or other limit
					//if true for other limit return true
					if(middles.size() == 0)
					{
						Pair<Integer, Integer> second = _edges.get(overlapedEdges.get(limit2).get(0));
	
						if((second.getL() == first.getL() && second.getR() == first.getR()) || (second.getL() == first.getR() && second.getR() == first.getL()))
						{
							break;
						}
						
					}

					outerloop:
					for(Integer l : middles)
					{
						ArrayList<Integer> OE = overlapedEdges.get(l);
						if(OE.size() > 2)
						{
							System.out.println("OE size is not good");
							return false;
						}
						//if(OE.size() == 1)
						//	return true;
						
						int index = 0;
						for(int b : OE)
						{
							Pair<Integer, Integer> p = _edges.get(b);
							if((p.getL() == first.getL() && p.getR() == first.getR()) || (p.getL() == first.getR() && p.getR() == first.getL()))
							{
								ArrayList<Integer> newList = OE;
								newList.remove(index);
								overlapedEdges.put(l, newList);
								if(!isMiddle)
									isMiddle = true;
								first = _edges.get(overlapedEdges.get(l).get(0));
								middles.remove(l);
								break outerloop;
							}
							index++;
						}
					}
				
				}
			}
			
		}
		return true;
	}	
	
}
