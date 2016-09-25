package entities;

import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import terrains.Terrain;
import textures.ModelTexture;
import models.RawModel;
import models.TexturedModel;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private ModelData data;
	private Vector3f centerPos = new Vector3f();	// Center of Object plus the position of the object
	
	// Constructor for premade TexturedModel and ModelData
	public Entity(ModelData data, TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.data = data;
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		calcCenter();
	}
	
	// Constructor that takes in the filePath for the OBJ and Texture files for the model
	public Entity(Loader loader, String dataFile, String textureFile, Vector3f position, float rotX, float rotY, float rotZ, float scale){
		this.data = OBJFileLoader.loadOBJ(dataFile);		
		this.model = new TexturedModel(loader.loadToVAO(data.getVertices(), 
				data.getTextureCoords(), data.getNormals(), data.getIndices()), 
				new ModelTexture(loader.loadTexture(textureFile)));
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public void increasePosition(float dx, float dy, float dz){
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz){
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	
	public int getGridX(){
		float x = this.getPosition().x;
		int gridX = 0;
		
		if(x > Terrain.SIZE){
			while(x > Terrain.SIZE){
				x = x - Terrain.SIZE;
				gridX++;
			}
		} else if(x < 0){
			while(x < 0){
				x = x + Terrain.SIZE;
				gridX--;
			}
		}
		
		return gridX;
	}
	
	public int getGridZ(){
		float z = this.getPosition().z;
		int gridZ = 0;
		
		if(z > Terrain.SIZE){
			while(z > Terrain.SIZE){
				z = z - Terrain.SIZE;
				gridZ++;
			}
		} else if(z < 0){
			while(z < 0){
				z = z + Terrain.SIZE;
				gridZ--;
			}
		}
		
		return gridZ;
	}
	
	// calculates the center of the object based on extreme vertices. Checks for any objects that will screw up the calculation
	// Makes it where X and Z are already centered, Y needs to be 0.
	// Also checks for if Y is centered, but that will still throw it off for now.
	public void calcCenter(){
		
		// Will calculate the center of the object
		Vector3f center = new Vector3f();
		
		// Center X
		if(Math.abs(this.data.getMaxX().x) == Math.abs(this.data.getMinX().x)){
			center.x = 0;
		} else {
			System.out.println("The X coordinate is not symmetrical for this Entity");
		}
		
		// Center Y
		// This first statement is if the object is already centered on y
		if(Math.abs(this.data.getMaxY().y) == Math.abs(this.data.getMinY().y)){
			center.y = 0;
			System.out.println("The Y coordinate is centered for this object");
		// This statement checks to see if y is positioned at 0
		} else if(this.data.getMinY().y == 0){
			center.y = (this.data.getMaxY().y)/2;
		} else if(this.data.getMinY().y > 0){
			System.out.println("The Y coordinate is greater than 0 for this Entity");
		} else if(this.data.getMinY().y < 0){
			System.out.println("The Y coordinate is less than 0 for this Entity");
		}
		
		// Center Z
		if(Math.abs(this.data.getMaxZ().z) == Math.abs(this.data.getMinZ().z)){
			center.z = 0;
		} else {
			System.out.println("The Z coordinate is not symmetrical for this Entity");
		}
		
		// Add the center of the object to the current position of the object (should only be Y, but X and Z just in case)
		this.centerPos.x = center.x + this.position.x;
		this.centerPos.y = center.y + this.position.y;
		this.centerPos.z = center.z + this.position.z;

		System.out.println(center);	// Print the Center	
		System.out.println(centerPos); // Pring the Center Position
		
		// The center relative to the position of the player is:
		// The Position + half of Y (AKA the position vector + the center Vector)
		// Then from there, the eSpace vector will act as distance from the center point
		
	}
	
	public Vector3f getCenterPos(){
		return centerPos;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public ModelData getData(){
		return data;
	}
	
	
	
	
}
