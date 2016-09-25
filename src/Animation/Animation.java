package Animation;

import objConverter.ModelData;

public class Animation {

	private int speed;
	private int frames;
	
	private int index = 0;
	private int count = 0;
	
	private ModelData[] data;
	private ModelData currentData;
	
	public Animation(int speed, ModelData[] data){
		this.speed = speed;
		this.data = data;
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
	
	public ModelData[] getData(){
		return data;
	}
	
	public ModelData getCurrentData(){
		return currentData;
	}
	
	
	
}
