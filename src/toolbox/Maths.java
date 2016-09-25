package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {
	
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	// Shows up Twice in vertexShader, in StaticShader. Rotation used in Entity and Player classes
	// There is a Quaternion class in LWJGL? Look into this and how to use it
	// Look into the Matrix4f class and how all of these transformations work
	// See if Translation and Scaling should be done using a different Method, or how Quaternions work with Matrices
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);	// rotate X
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);	// rotate Y
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);	// rotate Z
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	
	// Vector operations below
	public static Vector3f sub(Vector3f a, Vector3f b){
		Vector3f n = new Vector3f();
		n.x = a.x - b.x;
		n.y = a.y - b.y;
		n.z = a.z - b.z;
		return n;
	}
	
	public static Vector3f add(Vector3f a, Vector3f b){
		Vector3f n = new Vector3f();
		n.x = a.x + b.x;
		n.y = a.y + b.y;
		n.z = a.z + b.z;
		return n;
	}
	
	public static Vector3f cross(Vector3f a, Vector3f b){
		Vector3f n = new Vector3f();
		n.x = (a.y*b.z) - (a.z*b.y);
		n.y = (a.z*b.x) - (a.x*b.z);
		n.z = (a.x*b.y) - (a.y*b.x);
		return n;
	}
	
	public static float dot(Vector3f a, Vector3f b){
		float n;
		n = (a.x*b.x) + (a.y*b.y) + (a.z*b.z);
		return n;
	}
	
	public static Vector3f scale(float s, Vector3f v){
		Vector3f n = new Vector3f();
		n.x = s*v.x;
		n.y = s*v.y;
		n.z = s*v.z;
		return n;
	}
	
	public static Vector3f scale(double s, Vector3f v){
		Vector3f n = new Vector3f();
		n.x = (float) (s*v.x);
		n.y = (float) (s*v.y);
		n.z = (float) (s*v.z);
		return n;
	}
	
	public static float getSquaredLength(Vector3f v){
		return ((v.x*v.x) + (v.y*v.y) + (v.z*v.z));
		
	}
	
	
}
