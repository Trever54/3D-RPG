package Animation;

import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Loader;
import entities.Entity;

public class AnimatedEntity extends Entity {
	
	private int speed;
	private int frames;
	
	private int index = 0;
	private int count = 0;
	
	private ModelData[] data;
	private ModelData currentData;
	
	public AnimatedEntity(Loader loader, int speed, String dataFile, String textureFile,
			Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		
		// Load up the first of the dataFiles as the starting point of the Entity
		super(loader, dataFile+"_000001", textureFile, position, rotX, rotY, rotZ, scale);
		
		// set animation variables
		this.speed = speed;
		this.data = OBJFileLoader.loadAnimatedOBJ(dataFile, 3);
		this.currentData = data[0];		
		frames = data.length;
	}	
	
	public void runAnimation(){
		index++;
		if(index > speed){
			index = 0;
			nextFrame();
		}
	}
	
	private void nextFrame(){
		for(int i=0; i < frames; i++){
			if(count == i){
				currentData = data[i];
			}
		}		
		count++;		
		if(count > frames){
			count = 0;
		}
	}
	
	public ModelData getCurrentData(){
		return this.currentData;
	}
	
	
	
	
	
}
