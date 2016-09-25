package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture {

	private int texture;
	private Vector2f position;	// represents the center of the quad
	private Vector2f scale;	//represents the x and y size of the quad with respect to the screen
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}
	

}
