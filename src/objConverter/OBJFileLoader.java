package objConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/* This class, along with the other two in this package are part of a better OBJ loader
 * It allows for seams to be loaded more effeciently, but it's not much different than the old loader
 */

public class OBJFileLoader {

	 private static final String RES_LOC = "res/";
	 
	    public static ModelData loadOBJ(String objFileName) {
	        FileReader isr = null;
	        File objFile = new File(RES_LOC + objFileName + ".obj");
	        try {
	            isr = new FileReader(objFile);
	        } catch (FileNotFoundException e) {
	            System.err.println("File not found in res; don't use any extention");
	        }
	        BufferedReader reader = new BufferedReader(isr);
	        String line;
	        List<Vertex> vertices = new ArrayList<Vertex>();
	        List<Vector2f> textures = new ArrayList<Vector2f>();
	        List<Vector3f> normals = new ArrayList<Vector3f>();
	        List<Integer> indices = new ArrayList<Integer>();     
	        
	    	// Min and Max vertices locations for Collision Detection purposes
	        Vector3f[] extremes = new Vector3f[6];	// 0 is maxX, 1 is minX, 2 is maxY, 3 is minY, 4 is maxZ, 5 is minZ     
	        for(int i = 0; i<extremes.length;i++){
	        	extremes[i] = new Vector3f();
	        }
	        // ----------------------------------------------------
	        
	        try {
	            while (true) {
	                line = reader.readLine();
	                if (line.startsWith("v ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]),
	                            (float) Float.valueOf(currentLine[3]));
	                    Vertex newVertex = new Vertex(vertices.size(), vertex);	                    
	                    extremes = testForExtreme(vertex, extremes);	// Tests for Maximum and minimum Vertices values
	                    vertices.add(newVertex);
	 
	                } else if (line.startsWith("vt ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]));
	                    textures.add(texture);
	                } else if (line.startsWith("vn ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]),
	                            (float) Float.valueOf(currentLine[3]));
	                    normals.add(normal);
	                } else if (line.startsWith("f ")) {
	                    break;
	                }
	            }
	            while (line != null && line.startsWith("f ")) {
	                String[] currentLine = line.split(" ");
	                String[] vertex1 = currentLine[1].split("/");
	                String[] vertex2 = currentLine[2].split("/");
	                String[] vertex3 = currentLine[3].split("/");
	                processVertex(vertex1, vertices, indices);
	                processVertex(vertex2, vertices, indices);
	                processVertex(vertex3, vertices, indices);
	                line = reader.readLine();
	            }
	            reader.close();
	        } catch (IOException e) {
	            System.err.println("Error reading the file");
	        }
	        removeUnusedVertices(vertices);
	        float[] verticesArray = new float[vertices.size() * 3];
	        float[] texturesArray = new float[vertices.size() * 2];
	        float[] normalsArray = new float[vertices.size() * 3];
	        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
	                texturesArray, normalsArray);
	        int[] indicesArray = convertIndicesListToArray(indices);
	        ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
	                furthest, extremes);
	        return data;
	    }
	 
	    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
	        int index = Integer.parseInt(vertex[0]) - 1;
	        Vertex currentVertex = vertices.get(index);
	        int textureIndex = Integer.parseInt(vertex[1]) - 1;
	        int normalIndex = Integer.parseInt(vertex[2]) - 1;
	        if (!currentVertex.isSet()) {
	            currentVertex.setTextureIndex(textureIndex);
	            currentVertex.setNormalIndex(normalIndex);
	            indices.add(index);
	        } else {
	            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
	                    vertices);
	        }
	    }
	 
	    private static int[] convertIndicesListToArray(List<Integer> indices) {
	        int[] indicesArray = new int[indices.size()];
	        for (int i = 0; i < indicesArray.length; i++) {
	            indicesArray[i] = indices.get(i);
	        }
	        return indicesArray;
	    }
	 
	    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
	            List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
	            float[] normalsArray) {
	        float furthestPoint = 0;
	        for (int i = 0; i < vertices.size(); i++) {
	            Vertex currentVertex = vertices.get(i);
	            if (currentVertex.getLength() > furthestPoint) {
	                furthestPoint = currentVertex.getLength();
	            }
	            Vector3f position = currentVertex.getPosition();
	            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
	            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
	            verticesArray[i * 3] = position.x;
	            verticesArray[i * 3 + 1] = position.y;
	            verticesArray[i * 3 + 2] = position.z;
	            texturesArray[i * 2] = textureCoord.x;
	            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
	            normalsArray[i * 3] = normalVector.x;
	            normalsArray[i * 3 + 1] = normalVector.y;
	            normalsArray[i * 3 + 2] = normalVector.z;
	        }
	        return furthestPoint;
	    }
	 
	    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
	            int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
	        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
	            indices.add(previousVertex.getIndex());
	        } else {
	            Vertex anotherVertex = previousVertex.getDuplicateVertex();
	            if (anotherVertex != null) {
	                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
	                        indices, vertices);
	            } else {
	                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
	                duplicateVertex.setTextureIndex(newTextureIndex);
	                duplicateVertex.setNormalIndex(newNormalIndex);
	                previousVertex.setDuplicateVertex(duplicateVertex);
	                vertices.add(duplicateVertex);
	                indices.add(duplicateVertex.getIndex());
	            }
	 
	        }
	    }
	     
	    private static void removeUnusedVertices(List<Vertex> vertices){
	        for(Vertex vertex:vertices){
	            if(!vertex.isSet()){
	                vertex.setTextureIndex(0);
	                vertex.setNormalIndex(0);
	            }
	        }
	    }
	    
	    // For loading OBJ files with Animations. "num" is how many animation files there are
	    public static ModelData[] loadAnimatedOBJ(String path, int num){
	    	
	    	// The path will lead to the file name, but it will be followed
	    	// by an underscore and 6 numbers.
	    	
	    	// Use a string separator/reader to find out how many files there are
	    	// Use that to create an appropriately sized array of modelData objects
	    	
	    	// Use a string separator to read in and add each ModelData to the array
	    	// also call the loadOBJ method to load in each easier
	    	
	    	// Return the array of ModelData
	    	
	    	// ONCE RETURNED (IN THE ANIMATION CLASS)
	    	// cycle through, somehow changing the ModelData for an Entity
	    	// every so many frames.
	    	
	    	
	    	ModelData[] animation = new ModelData[num-1];
	    	
	    	int fileNumber = 1;	// The file number	    	
	    	for(int i=0; i<animation.length; i++){
	    		if(fileNumber < 10){
	    			animation[i] = loadOBJ(path + "_00000" + fileNumber);
	    		}
	    		if(fileNumber >= 10 && fileNumber < 100){
	    			animation[i] = loadOBJ(path + "_0000" + fileNumber);
	    		}
	    		if(fileNumber >= 100 && fileNumber < 1000){
	    			animation[i] = loadOBJ(path + "_000" + fileNumber);
	    		}
	    		if(fileNumber >= 1000 && fileNumber < 10000){
	    			animation[i] = loadOBJ(path + "_00" + fileNumber);
	    		}
	    		if(fileNumber >= 10000 && fileNumber < 100000){
	    			animation[i] = loadOBJ(path + "_0" + fileNumber);
	    		}
	    		if(fileNumber >= 100000 && fileNumber < 1000000){
	    			animation[i] = loadOBJ(path + "_" + fileNumber);
	    		}
	    		if(fileNumber >= 1000000){
	    			System.out.println("Animation Loading Error: Too Many Files");
	    		}	    		
	    		fileNumber++;    		
	    	}	
	    	
	    	return animation;
	    	
	    }
	    
	    // Tests for Max and Min vertices (for Collision Detection)
	    private static Vector3f[] testForExtreme(Vector3f vertex, Vector3f[] extremes){ 	
	    	if(vertex.x > extremes[0].x){
	    		extremes[0] = vertex;	// max X
	    	} else if(vertex.x < extremes[1].x){
	    		extremes[1] = vertex;	// min X
	    	}
	    	
	    	if(vertex.y > extremes[2].y){
	    		extremes[2] = vertex;	// max Y
	    	} else if(vertex.y < extremes[3].y){
	    		extremes[3] = vertex;	// min Y
	    	}
	    	
	    	if(vertex.z > extremes[4].z){
	    		extremes[4] = vertex;	// max Z
	    	} else if(vertex.z < extremes[5].z){
	    		extremes[5] = vertex;	// min Z
	    	} 	
	    	
	    	return extremes;
	    }
	
}
