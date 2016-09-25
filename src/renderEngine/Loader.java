package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import textures.TextureData;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Loader {
	
	//keep track of vaos and vbos (for memory management)
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	// Method that stores a float[] of vertices into a VAO
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals,  int[] indices){
		int vaoID = createVAO(); // create empty vao and get the ID of it
		bindIndicesBuffer(indices); //bind indices
		storeDataInAttributeList(0, 3, positions);	// Store vertex data in an attribute list
		storeDataInAttributeList(1, 2, textureCoords);	// store texture data in an attribute list
		storeDataInAttributeList(2, 3, normals);
		unbindVAO(); // unbind vao
		return new RawModel(vaoID, indices.length); // return the RawModel
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions){
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length/dimensions);
	}
	
	// load up a texture into openGL so we can use it
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
		
	}
	
	//Deletes all vertex arrays in the integer lists
	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}
	
	// takes in the six skybox file names
	public int loadCubeMap(String[] textureFiles){
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for(int i=0;i<textureFiles.length;i++){
			TextureData data = decodeTextureFile("res/Skybox/" + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, 
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		
		//Fixes visible seams on skybox (which only appears on some computers)
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		textures.add(texID);
		return texID;
	}
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
	// Creates a new VAO
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();	// Creates an empty VAO
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);	//Activates the VAO (by binding it)
		return vaoID;	// returns the ID
	}
	
	// Store the data of the VAO in a attribute list (into a VBO)
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();	//Create empty vbo
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);	// Binds the vbo
		FloatBuffer buffer = storeDataInFloatBuffer(data);	// stores data as a float buffer (calls floatbuffer method)
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);	// store the floatbuffer into the vbo
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);	// number of attribute list, length of each vertex, type of data, if data is normalized, distance between each vertices, the offset (should it start at the beggining of the data?)
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);	// unbind the vbo
	}
	
	// unbinds the VAO
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	// load up the indices buffer and bind it to a vao we want to render
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	// Convert int array of indices into an int buffer we can store in the vbo
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	// creates a floatbuffer
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);	// create an empty floatbuffer
		buffer.put(data);	//put the data into the floatbuffer
		buffer.flip();		//prepare the buffer to be read from
		return buffer;		//return the buffer
	}
	
}
