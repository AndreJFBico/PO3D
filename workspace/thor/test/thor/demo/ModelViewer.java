package thor.demo;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import thor.Model;
import thor.demo.modelviewer.global.Session;
import thor.demo.modelviewer.menus.MenuFactory;
import thor.demo.modelviewer.menus.MenuFactory.MenuType;

import com.jogamp.opengl.util.FPSAnimator;

public class ModelViewer implements GLEventListener {
	// Define constants for top-level container
	private static final int FPS = 60; // animator's target frames per second
	
	// Setup OpenGL Graphics Renderer
	private GLU glu; // for the GL Utility
	private JFrame frame = new JFrame("Model Viewer v1.0");
	private JMenuBar menuBar = new JMenuBar();
	//private JMenuItem menuItem;
	
	FPSAnimator _animator;
	
	private float _angleModel = 0.0f;
	private final float _speedRotateModel = 2.0f;

	public ModelViewer(int width, int height, Model model) {
		Session.getInstance().setModel(model);
		Session.getInstance().setGlDrawMode(GL2.GL_LINE);
		
		GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        canvas.setPreferredSize(new Dimension(width, height));
        
        menuBar.add(MenuFactory.createMenu(MenuType.File));
        menuBar.add(MenuFactory.createMenu(MenuType.View));
        menuBar.add(MenuFactory.createMenu(MenuType.Help));
        frame.setJMenuBar(menuBar);
        
        frame.getContentPane().add(canvas);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	_animator.stop();
            	frame.removeAll();
            	frame.dispose();
            }
        });
        frame.pack();
        frame.setVisible(true);
        Session.getInstance().setMainFrame(frame);
        
        canvas.addGLEventListener(this); // for handling GLEvents
        
        // Create a animator that drives canvas' display() at the specified FPS.
        _animator = new FPSAnimator(canvas, FPS, true);
        _animator.start(); // start the animation loop
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		
		gl.glShadeModel(GL2.GL_FLAT);
		gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    gl.glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 2.0f);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		
		glu.gluLookAt(	1.0f, 1.0f, 1.0f,
						0.0f, 0.0f, 0.0f,
						0.0f, 1.0f, 0.0f);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, Session.getInstance().getGlDrawMode());
		
		gl.glPushMatrix();

		gl.glLoadIdentity();
		gl.glRotated(_angleModel, 0.0, 1.0, 0.0);
		
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		if(Session.getInstance().getModel() != null)
			Session.getInstance().getModel().draw(drawable);
		
		gl.glPopMatrix();
		gl.glFlush();
		
		_angleModel += _speedRotateModel;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {	
	}
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}
	
}