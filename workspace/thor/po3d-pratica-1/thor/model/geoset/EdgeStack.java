package thor.model.geoset;

import java.util.Enumeration;
import java.util.Stack;

public class EdgeStack {
	  private Stack<Edge3D> data; // contents of the stack
	  	
	  public EdgeStack() {
	    data = new Stack<Edge3D>();
	  }
	  	
	  public boolean isEmpty() {
	    return data.isEmpty();
	  }
	  	
	  public Edge3D get() {
	    return (Edge3D) data.pop();
	  }
	  	
	  public void put(Edge3D e) {
	    data.push(e);
	  }

	  public void put(int a, int b) {
	    put(new Edge3D(a,b));
	  }
	  	
	  public void putp(Edge3D e) {
	    int ind = data.indexOf(e);
	    if (ind == -1) {
	      data.push(e);
	    } else {
	      data.removeElementAt(ind);
	    }
	  }

	  public void putp(int a, int b) {
	    putp(new Edge3D(a,b));
	  }
	  	
	  public void dump() {
	    Enumeration<Edge3D> e = data.elements();
	    System.out.println(data.size());
	    while (e.hasMoreElements()) {
	      System.out.println((Edge3D)e.nextElement());
	    }
	    System.out.println();
	  }
}
