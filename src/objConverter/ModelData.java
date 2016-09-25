package objConverter;

import org.lwjgl.util.vector.Vector3f;


public class ModelData {

		private float[] vertices;
	    private float[] textureCoords;
	    private float[] normals;
	    private int[] indices;
	    private float furthestPoint;
	    private Vector3f[] extremes; // 0 is maxX, 1 is minX, 2 is maxY, 3 is minY, 4 is maxZ, 5 is minZ
	 
	    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
	            float furthestPoint, Vector3f[] extremes) {
	        this.vertices = vertices;
	        this.textureCoords = textureCoords;
	        this.normals = normals;
	        this.indices = indices;
	        this.furthestPoint = furthestPoint;  
	        this.extremes = extremes;
	    }
	 
	    public float[] getVertices() {
	        return vertices;
	    }
	 
	    public float[] getTextureCoords() {
	        return textureCoords;
	    }
	 
	    public float[] getNormals() {
	        return normals;
	    }
	 
	    public int[] getIndices() {
	        return indices;
	    }
	 
	    public float getFurthestPoint() {
	        return furthestPoint;
	    }
	
	    // Get methods for max and min extreme vertices
	    public Vector3f getMaxX(){
	    	return extremes[0];
	    }
	    public Vector3f getMinX(){
	    	return extremes[1];
	    }
	    public Vector3f getMaxY(){
	    	return extremes[2];
	    }
	    public Vector3f getMinY(){
	    	return extremes[3];
	    }
	    public Vector3f getMaxZ(){
	    	return extremes[4];
	    }
	    public Vector3f getMinZ(){
	    	return extremes[5];
	    }
}
