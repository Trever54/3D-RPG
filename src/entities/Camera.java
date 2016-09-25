package entities;

import input.Mouse;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private final float EXTRA_VERTICAL_DISTANCE = 3;
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 10;
	private float yaw = 0;
	private float roll;
	
	private Player player;
	
	public Camera(Player player){
		this.player = player;
	}
	
	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance + EXTRA_VERTICAL_DISTANCE;
		
	}
	
	// Change to make zooming happen with the scroll wheel
	private void calculateZoom(){
		distanceFromPlayer -= Mouse.getScrollOffset()*2;
		Mouse.setScrollOffset(0);
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(0)){
			float pitchChange = (float) (Mouse.getDY() * 0.1f);
			pitch += pitchChange;
		}
	}

	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(0)){
			float angleChange = (float) (Mouse.getDX() * 0.3f);
			angleAroundPlayer -= angleChange;
		}
	}
	
}
