package textures;

import renderEngine.Loader;

public class TerrainTexturePack {

	private TerrainTexture backgroundTexture;
	private TerrainTexture rTexture;
	private TerrainTexture gTexture;
	private TerrainTexture bTexture;
	
	// Constructor (that takes in TerrainTextures
	public TerrainTexturePack(TerrainTexture backgroundTexture,
			TerrainTexture rTexture, TerrainTexture gTexture,
			TerrainTexture bTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}
	
	// Constructor (that takes in the file path names)
	public TerrainTexturePack(Loader loader, String background, String red, String green, String blue){
		this.backgroundTexture = new TerrainTexture(loader.loadTexture(background));
		this.rTexture = new TerrainTexture(loader.loadTexture(red));
		this.gTexture = new TerrainTexture(loader.loadTexture(green));
		this.bTexture = new TerrainTexture(loader.loadTexture(blue));	
	}

	public TerrainTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public TerrainTexture getrTexture() {
		return rTexture;
	}

	public TerrainTexture getgTexture() {
		return gTexture;
	}

	public TerrainTexture getbTexture() {
		return bTexture;
	}
	
	
}
