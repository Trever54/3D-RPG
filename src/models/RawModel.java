package models;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import objConverter.ModelData;
import renderEngine.Loader;

public class RawModel {

	private int vaoID;
	private int vertexCount;
	
	// constructor
	public RawModel(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public void changeModelData(ModelData data){
/*
		buffer = BufferUtils.createIntBuffer(data.length);
		buffer.clear();
		buffer.put(data);
*/		
		
/*
		int vaoID = createVAO(); // create empty vao and get the ID of it
		bindIndicesBuffer(indices); //bind indices
		storeDataInAttributeList(0, 3, positions);	// Store vertex data in an attribute list
		storeDataInAttributeList(1, 2, textureCoords);	// store texture data in an attribute list
		storeDataInAttributeList(2, 3, normals);
		unbindVAO(); // unbind vao
	
*/
	
	
	}
	
}
