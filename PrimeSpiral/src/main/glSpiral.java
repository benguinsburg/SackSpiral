package main;

import java.awt.event.*;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.nativewindow.ScalableSurface;

import java.awt.event.KeyListener;

import javax.swing.JFrame;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class glSpiral extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ActionListener {
    // window variables
    private final GLCanvas canvas;
    private int winW = 1400, winH = 1400;
    
    // mouse control variables
    private int mouseX, mouseY;
    private int mouseButton;
    private boolean mouseClick = false;
    private boolean clickedOnShape = false;
    
    // USER VARIABLES
    private final double inc = 0.0001;

    // object transformation variables
    private float differenceTranslationX = 0.0f, differenceTranslationY = 0.0f, differenceTranslationZ = 0.0f;
    private float currentTranslationX = 0.0f, currentTranslationY = 0.0f, currentTranslationZ = -10.0f;
    private float differenceScale = 0.0f;
    private float currentScale = 0.0026f;	//1.0f;  
    private float differenceAngleX = 0.0f, differenceAngleY = 0.0f, differenceAngleZ = 0.0f;
    FloatBuffer currentRotation = FloatBuffer.allocate(16);

    // gl shading variables
    private boolean drawWireframe = false;
    private float lightPos[] = {-5.0f, 10.0f, 5.0f, 1.0f};

    // a set of shapes (your TODO item)
    private static final int NumShapes = 200;
    // initial shape is a triangle
    private int shape = 0;
    private double u = 0;

    // gl context/variables
    private GL2 gl;
    private final GLU glu = new GLU();
    private final GLUT glut = new GLUT();
    
    // external class
    private static Spiral spiral;

    public static void main(String args[]) {
        new glSpiral();
        spiral = new Spiral();
    }
    
    public void drawSpiral(double u){
    	int prevPrime = 0;
    	int n = 300000;	
    	
    	int center = 128;
    	int width = 127;
    	double frequency = Math.PI*2/n;
    	
    	for (int i=0; i<n; i++){
    		/*
    		float red   = (float) (Math.sin(frequency*i+2) * width + center);
    	    float green = (float) (Math.sin(frequency*i+0) * width + center);
    	    float blue  = (float) (Math.sin(frequency*i+4) * width + center);
    	    gl.glColor3f(red, green, blue);
    	    */
    	    
	    	double x = (-1) * Math.cos(Math.sqrt(i) * 2 * Math.PI) * Math.sqrt(i)/1;
	    	double y = Math.sin(Math.sqrt(i)* 2 *Math.PI) * Math.sqrt(i)/1 ;
	    	double[] arr = calcPos(prevPrime);
	    	//double z = i/(n/1000);
	    	double z = distance(x,y,arr[0],arr[1]);
	    	//double z = Math.sqrt(x*x + y*y);	//z = distance to origin
	    	
	    	if ( spiral.isPrime(i) ){ 		//if point represents a prime number
	    	    gl.glVertex3d(x, y, z);
	    	    prevPrime = i;
	        }
	    }
    }
    
    
    public double distance(double x1, double y1, double x2, double y2) {
		int dx = (int) (x1 - x2);
		int dy = (int) (y1 - y2);
		double distance = Math.sqrt(dx*dx + dy*dy);
		return distance;
	}
    
    double[] calcPos(int i){
    	//double x = (1/u-1)*((-1) * Math.cos(Math.sqrt(i) * u * Math.PI) * Math.sqrt(i)) + (1-1/u)*((-1) * Math.sin(Math.sqrt(i) * u * Math.PI) * Math.sqrt(i));
    	double x = (-1) * Math.cos(Math.sqrt(i) * u * Math.PI) * Math.sqrt(i);
    	double y = Math.sin(Math.sqrt(i)* 2 *Math.PI) * Math.sqrt(i) ;
    	double[] arr = new double[2];
    	arr[0]=x;
    	arr[1]=y;
    	return arr;
    }

    // constructor, setup window with OpenGL capability
    public glSpiral() {
        super("Introduction to Computer Graphics - Assignment 1");
        final GLProfile glprofile = GLProfile.getMaxFixedFunc(true);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        canvas = new GLCanvas(glcapabilities);
        canvas.setSurfaceScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE }); // potential fix for Retina Displays
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        getContentPane().add(canvas);
        setSize(winW, winH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        canvas.requestFocus();
    }

    // OpenGL display function
    public void display(GLAutoDrawable drawable) {
        // if mouse is clicked, we need to detect whether it's clicked on the shape
        if (mouseClick) {
            ByteBuffer pixel = ByteBuffer.allocateDirect(1);

            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glDisable(GL2.GL_LIGHTING);
            drawShape();
            gl.glReadPixels(mouseX, (winH - 1 - mouseY), 1, 1, GL2.GL_RED, GL2.GL_UNSIGNED_BYTE, pixel);

            if (pixel.get(0) == (byte) 255) {
                // mouse clicked on the shape, set clickedOnShape to true
                clickedOnShape = true;
            }
            // set mouseClick to false to avoid detecting again
            mouseClick = false;
        }

        // shade the current shape [don't worry about the details here - we'll cover them later in the course]
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, drawWireframe ? GL2.GL_LINE : GL2.GL_FILL);
        gl.glColor3f(1.0f, 0.3f, 0.1f);
        drawShape();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }


    // draw the current shape
    public void drawShape() {
        // calculate the new rotation based on input angle changes
        gl.glLoadIdentity(); // initialize rotation to identity matrix
        gl.glRotatef(differenceAngleX, 1.0f, 0.0f, 0.0f);        
        // your TODO item  (rotations in two other axes), hint: use glRotatef       
        gl.glRotatef(differenceAngleY, 0.0f, 1.0f, 0.0f);  
        gl.glRotatef(differenceAngleZ, 0.0f, 0.0f, 1.0f);  
        
        // the above instructions calcuated a new rotation (matrix)
        // which now needs to be combined (multiplied) with the current rotation (matrix)        
        // OpenGL does the combination (matrix multiplication) for us in the next line
        // and stores the result internally (MODELVIEW matrix)        
        gl.glMultMatrixf(currentRotation);
        // save the current rotation
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, currentRotation);

        // update current translation and scale
        currentTranslationX += differenceTranslationX;
        currentTranslationY += differenceTranslationY;
        currentTranslationZ += differenceTranslationZ; // this will be always zero in this assignment (it's ok)
        //currentScale += differenceScale;
        currentScale += currentScale * differenceScale;
        
        
        // now that we calculated the current rotation, translation, scaling...
        // we need to apply them to a drawn object so that it is transformed
        // on the screen.
        // Transformations are applied in the following order [the order is important!]
        // 1. Initialize the internal OpenGL matrix to identity matrix 
        gl.glLoadIdentity();
        // 2. Apply the current translation
        gl.glTranslatef(currentTranslationX, currentTranslationY, currentTranslationZ);
        // 3. Apply the current scaling
        gl.glScalef(currentScale, currentScale, currentScale);
        // 4. Apply the current rotation
        gl.glMultMatrixf(currentRotation);

        ///////////////////////////////////////////	DRAWING THE SPIRAL /////////////////////////////////////
        
        gl.glColor3f(1.5f, 1.5f, 1.5f);	//point color
        gl.glPointSize(2.0f);	//point size
        
        System.out.println("u = " + u);
        gl.glBegin(GL2.GL_POINTS);	//draw the point cloud
        drawSpiral(u);
	    gl.glEnd();
        
	    ///////////////////////////////////////////	DRAWING THE SPIRAL /////////////////////////////////////
	    
        // position the light
        gl.glLoadIdentity();
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        
        // everything is drawn, re-initializing all updates to zero
        differenceAngleX = 0.0f;
        differenceAngleY = 0.0f;
        differenceAngleZ = 0.0f;
        differenceTranslationX = 0.0f;
        differenceTranslationY = 0.0f;
        differenceTranslationZ = 0.0f;
        differenceScale = 0.0f;   
    }

    // initialization of OpenGL window / variables
    public void init(GLAutoDrawable drawable) {
        // will discuss these later in the course
        gl = drawable.getGL().getGL2();
        gl.setSwapInterval(1);

        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_DIFFUSE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glCullFace(GL2.GL_BACK);
        gl.glEnable(GL2.GL_CULL_FACE);

        // set clear color: this determines the background color (which is dark gray)
        gl.glClearColor(.3f, .3f, .3f, 1f);
        gl.glClearDepth(1.0f);

        // initialize current rotation to identity matrix
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, currentRotation);
        
        
    }

    // reshape callback function: called when the size of the window changes
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        winW = width;
        winH = height;

        // places camera correctly (will discuss later in the course)
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(30.0f, (float) width / (float) height, 0.01f, 100.0f);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    // mouse pressed callback function
    // gets mouse position / button state
    public void mousePressed(MouseEvent e) {
        mouseClick = true;
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButton = e.getButton();
        canvas.display();
    }

    // mouse released callback function
    public void mouseReleased(MouseEvent e) {
        clickedOnShape = false;
        canvas.display();
    }

    // mouse dragged callback function    
    public void mouseDragged(MouseEvent e) {
        /*if (!clickedOnShape) {
            return;
        }*/

        int x = e.getX();
        int y = e.getY();
        if (mouseButton == MouseEvent.BUTTON3) {
            // right button scales
            differenceScale = (y - mouseY) * 0.01f;
        } else if (mouseButton == MouseEvent.BUTTON2) {
            // middle button translates
            differenceTranslationX = (x - mouseX) * 0.01f;
            differenceTranslationY = -(y - mouseY) * 0.01f;
        } else if (mouseButton == MouseEvent.BUTTON1) {
            // left button + shift button rotates about z
            if (e.isShiftDown()) {
                // your TODO item
            	differenceAngleZ = (y - mouseY);
            } else {
                    differenceAngleX = (y - mouseY);
                    // ... more TODO items ...
                    differenceAngleY = (x - mouseX);
            }
        }
        mouseX = x;
        mouseY = y;
        canvas.display();
    }

    // key pressed callback function
    public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_Q:
            System.exit(0);
            break;
        case KeyEvent.VK_W:
            drawWireframe = !drawWireframe;
            break;
        case KeyEvent.VK_SPACE:
            shape = (shape + 1) % NumShapes;
            break;
		  	
		case KeyEvent.VK_A:
			while(true){
				u += 10*inc;
				canvas.display();
			}
		}
        canvas.display();
    }
    
    // mouse wheel callback function
    public void mouseWheelMoved(MouseWheelEvent e) {
    	int notches = e.getWheelRotation();
    	
    	if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
    		//differenceScale = notches * 0.1f;
    		u += notches * inc;
    		//u = u/(notches * inc);
    	}
    	
    	canvas.display();
    }

    
    // these functions are not used for this assignment
    // but may be useful in the future
    public void dispose(GLAutoDrawable glautodrawable) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
}
