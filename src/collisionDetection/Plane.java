package collisionDetection;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class Plane {

	public float[] equation = new float[4];
	
	public Vector3f origin;
	public Vector3f normal;
	
	public boolean isFacingTo;
	public double signedDistanceTo;
	
	// Constructor that takes in origin and normal
	public Plane(Vector3f origin, Vector3f normal){
		this.normal = normal;
		this.origin = origin;
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
	}
	
	// constructor that finds the information needed from a triangle
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3){
		this.normal = Maths.cross(Maths.sub(p2, p1), Maths.sub(p3, p1));	// Cross product of p2-p1 and p3-p1
		this.normal.normalise();
		origin = p1;
		equation[0] = normal.x;
		equation[1] = normal.y;
		equation[2] = normal.z;
		equation[3] = -(normal.x*origin.x + normal.y*origin.y + normal.z*origin.z);
	}
	
	public boolean isFrontFacingTo(Vector3f direction){
		double dot = Maths.dot(normal, direction);
		return (dot <= 0);	// returns true if the requirement is true
	}
	
	public double signedDistanceTo(Vector3f point){
		return ((Maths.dot(point, normal)) + equation[3]);
	}
	
}
