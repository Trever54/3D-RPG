package entities;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import input.Keyboard;
import models.TexturedModel;
import objConverter.ModelData;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import terrains.Terrain;

public class Player extends Entity{

	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;	// typically -50
	private static final float JUMP_POWER = 20;
	
	private static float RUN_SPEED; // typically 20
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;
	
	// Default Constructor
	public Player(ModelData data, TexturedModel model, Vector3f position, float rotX,
			float rotY, float rotZ, float scale) {
		super(data, model, position, rotX, rotY, rotZ, scale);
	}
	
	// Constructor that Takes in Strings
	public Player(Loader loader, String dataFile, String textureFile, Vector3f position, float rotX, float rotY, float rotZ, float scale){
		super(loader, dataFile, textureFile, position, rotX, rotY, rotZ, scale);
	}
	
	public int getCurrentGridX(){
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
	
	public int getCurrentGridZ(){
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
	
	// returns the X coordinate of the palyer in respect to that terrain tile
	public float getRelativeX(){	
		float x = this.getPosition().x;
		
		if(x > Terrain.SIZE){
			while(x > Terrain.SIZE){
				x = x - Terrain.SIZE;
			}
		} else if(x < 0){
			while(x < 0){
				x = x + Terrain.SIZE;
			}
		}
		
		return x;	
	}
	
	// returns the Z coordinate of the palyer in respect to that terrain tile
	public float getRelativeZ(){
		float z = this.getPosition().z;
		
		if(z > Terrain.SIZE){
			while(z > Terrain.SIZE){
				z = z - Terrain.SIZE;
			}
		} else if(z < 0){
			while(z < 0){
				z = z + Terrain.SIZE;
			}
		}
		return z;
	}
	
	public void move(Terrain terrain){
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		
		if(Keyboard.isKeyDown(GLFW_KEY_LEFT_ALT) || Keyboard.isKeyDown(GLFW_KEY_RIGHT_ALT)){	
			RUN_SPEED = 200;	// increase run speed if Alt key is pressed
		} else {
			RUN_SPEED = 20;
		}
		
		
		// Jumping Code
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		// Collision detection Code (with terrain)
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y < terrainHeight){
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	private void jump(){
		if(!isInAir){
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(GLFW_KEY_W)){
			this.currentSpeed = RUN_SPEED;
		} else if(Keyboard.isKeyDown(GLFW_KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(GLFW_KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		} else if(Keyboard.isKeyDown(GLFW_KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(GLFW_KEY_SPACE)){
//			super.increasePosition(0, 2, 0);	// delete later (and change Gravity back to -50)
			jump();
		}
		if(Keyboard.isKeyDown(GLFW_KEY_X)){		// delete later
//			super.increasePosition(0, -2, 0);	// delete later
		}										// delete later
	}

}
