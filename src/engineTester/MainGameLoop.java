package engineTester;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import input.Keyboard;
import input.Mouse;

import java.util.ArrayList;
import java.util.List;

import objConverter.OBJFileLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Animation.AnimatedEntity;
import Animation.Animation;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;

/*TODO:
 * 1. Player Animation
 * 	-Learn Blender Animation and character modeling
 * 	-Create a player in blender
 * 	-Animate that character in blender
 * 	-Write the OBJ passer to take in the animation file
 * 		-Study up on the improved OBJ passer already in my code
 * 	-Create an Animation package and add Java code that will handle all animations
 * 	-Implement quaternions for rotation (at least player rotation) (POSSIBLY OPTIONAL?)
 * 	-Get the final result of the player walking animation
 * 	-Add a player jumping animation
 * 	-Add a player quickly running animation (for when I press ALT)
 * 	-Add standing animation(s) (OPTIONAL FOR NOW)
 * 	-Add a spell casting animation (to shoot a fireball for now)
 * 
 * 2. Collision Detection
 * 	-Use the GJK algorithm or similar algorithms
 * 	-Bound every entity in a box (or sphere) that covers all of their vertices
 * 	-Once the player enters that box, use the GJK to check if vertices are touching.
 * 
 * 3. Basic Skills and Abilities
 * 	-Start with fireball. Do at least one for each element
 * 	-Will figure out later
 * 
 * 4. SkyBox (To be done after multiple zones are added into the world)
 * 	-Set up the SkyBox to change based on what zone the player is in
 * 		-this will likely require modifying the constructor to take in a certain skybox from the main game loop
 * 		rather than take in the image files in the skyboxRenderer class
 * 	-Make certain skybox's move (with clouds) and others stationary
 * 	-Set up the clearcolor to match each skybox in a clever way
 * 		-perhaps taking in color coordinates with the skybox constructor
 * 
 * 5. Mouse Picking
 * 	-check end of tutorial for more info and updated mousepicker class
 * 	-Make the mouse picker pop up a menu for different entities/other depending what it's on (similar to RS)
 * 
 * 6. Inventory and Equip interfaces
 * 	-On the left and right sides of the action bars. Make kind of RS like. Leave room for other interfaces.
 * 
 * 
 * 
 * LAST HAD OPEN:
 * MainGameLoop
 * Vertex
 * ModelData
 * OBJFileLoader
 * CollisionDetection
 * Entity
 * Player
 * 
 * 
 * LIST2 of things TODO to get Collision working:
 * 1. Clean up Code. 
 * 			-Look at water tutorial and add method to masterrenderer
 * 			-Add methods in order to cut back on how much stuff is in the mainGameLoop class
 * 					-This should also make further implementation of stuff easier
 * 
 * 2. Print out and study Collision Detection paper (Narrow Phase)
 * 			-Take notes on what I need to do to implement it
 * 	
 * 3. Implement Collision Detection in my code (Narrow Phase)
 * 			-Implement it
 * 			-Optimize it
 * 
 * 4. Repeat steps 2 and 3 with Broad Phase Collision Detection
 * 
 */

public class MainGameLoop {
	
	public static void main(String[] args){
		
		// Initialize
		DisplayManager.createDisplay();	// Create the Display
		GLContext.createFromCurrent();	// Critical Line for OpenGL
		Loader loader = new Loader();	//Create Loader
		MasterRenderer renderer = new MasterRenderer(loader);	// create Master Renderer
		
		//************************LIGHTS*******************************
		
		List<Light> lights = new ArrayList<Light>();
		
		Light sun = new Light(new Vector3f(20000, 40000, 20000), new Vector3f(1, 1, 1));	// 20,000; 40,000; 20,000 OR 0, 100, 7000 (for night)
		
		// Add lights to list
		lights.add(sun);
//		lights.add(new Light(new Vector3f(380,15,15), new Vector3f(15,15,5), new Vector3f(1, 0.1f, 0.002f)));
//		lights.add(new Light(new Vector3f(420,15,15), new Vector3f(15,15,5), new Vector3f(1, 0.1f, 0.002f)));
			
		//**********************END LIGHTS*****************************
	
		//TEST FOR PLANE CLASS
//		Plane p = new Plane(new Vector3f(0,0,0), new Vector3f(1,10,5), new Vector3f(3,4,8));
		
		//**********TERRAIN TEXTURE STUFF************
		
		List<Terrain> terrains = new ArrayList<Terrain>();
		
		// The following are terrain packs for certain zones (ORDER: mainBackground, pathSupport, backgroundSupport, mainPath)
		TerrainTexturePack desertPack = new TerrainTexturePack(loader, 
				"Terrain/desertSand", "Terrain/desertRock", "Terrain/desertSandstone", "Terrain/desertPath");	// Desert
		
		TerrainTexturePack plainsPack = new TerrainTexturePack(loader, 
				"Terrain/plainsGrass", "Terrain/plainsDirt", "Terrain/plainsGrass2", "Terrain/plainsPath");	// Plains
		
		TerrainTexturePack junglePack = new TerrainTexturePack(loader, 
				"Terrain/jungleGrass", "Terrain/junglePathSupport", "Terrain/jungleGrassDark", "Terrain/junglePath");	// Jungle
		
		TerrainTexturePack snowPack = new TerrainTexturePack(loader, 
				"Terrain/snowMain", "Terrain/snowPathSupport", "Terrain/snowIce", "Terrain/snowPath");	// Snow
		
		TerrainTexturePack evilPack = new TerrainTexturePack(loader, 
				"Terrain/evilMain", "Terrain/evilMainSupport", "Terrain/evilBlood", "Terrain/evilPath");	// Evil
		
		TerrainTexturePack mountainPack = new TerrainTexturePack(loader, 
				"Terrain/mountainStone", "Terrain/mountainMud", "Terrain/mountainRock", "Terrain/mountainPath");	// Mountains
		
		TerrainTexturePack mineEntrancePack = new TerrainTexturePack(loader,
				"Terrain/mountainStone", "Terrain/mountainMud", "Terrain/mountainRock", "Terrain/mountainPath");

		TerrainTexturePack originCityPack = new TerrainTexturePack(loader,
				"Terrain/plainsGrass", "Terrain/plainsDirt", "Terrain/plainsGrass2", "Terrain/plainsPath");
		
		
		
		// Create the Terrain
		Terrain originCity = new Terrain(0, 0, loader, originCityPack, "Maps/originCityBlendMap", "Maps/originCityHM", 100);	// Origin City
		Terrain mineEntrance = new Terrain(0, 1, loader, mineEntrancePack, "Maps/mineEntranceBlendMap", "Maps/mineEntranceHM", 100); // Mine Entrance
		Terrain plains1 = new Terrain(0, -1, loader, plainsPack, "Maps/plains1BlendMap", "Maps/plains1HM", 100);	// Middle Plains
		Terrain plains2 = new Terrain(-1, -1, loader, plainsPack, "Maps/plains2BlendMap", "Maps/plains2HM", 100);	// Left Plains
		Terrain plains3 = new Terrain(1, -1, loader, plainsPack, "Maps/plains3BlendMap", "Maps/plains3HM", 100);	// Right Plains
		
		Terrain currentTerrain = originCity;	// set currentTerrain
		
		terrains.add(originCity);
		terrains.add(mineEntrance);
		terrains.add(plains1);
		terrains.add(plains2);
		terrains.add(plains3);
	
		//**********END TERRAIN TEXTURE STUFF*********************************
		
		//********************ENTITY STUFF****************************
		
		// Entity arraylist that stores all entities
		List<Entity> entities = new ArrayList<Entity>();

//		entities = setUpEntities(loader);	// Sets up Entities for the game
		
		
		
		
		
		int h = 0; // height of objects at the moment
		
		// LAMP
		Entity lamp = new Entity(loader, "Entity/Lamp/lamp", "Entity/Lamp/lampTexture",
				new Vector3f(100, h, 180), 0, 0, 0, 1);		
		lamp.getModel().getTexture().setShineDamper(40);	// set ShineDamper, Reflectivity, hasFakeLighting, and other Texture stuff
		
		// ROCK 1
		Entity rock1 = new Entity(loader, "Entity/Rock/rock1", "Entity/Rock/texture1",
				new Vector3f(100, h, 200), 0, 0, 0, 5f);
		// ROCK 2
		Entity rock2 = new Entity(loader, "Entity/Rock/rock2", "Entity/Rock/texture2",
				new Vector3f(100, h, 220), 0, 0, 0, 5f);
		// ROCK 3
		Entity rock3 = new Entity(loader, "Entity/Rock/rock3", "Entity/Rock/texture3",
				new Vector3f(100, h, 240), 0, 0, 0, 5f);
		// ROCK 4
		Entity rock4 = new Entity(loader, "Entity/Rock/rock4", "Entity/Rock/texture4",
				new Vector3f(100, h, 260), 0, 0, 0, 5f);
		// ROCK 5
		Entity rock5 = new Entity(loader, "Entity/Rock/rock5", "Entity/Rock/texture5",
				new Vector3f(100, h, 280), 0, 0, 0, 5f);
		// ROCK 6
		Entity rock6 = new Entity(loader, "Entity/Rock/rock6", "Entity/Rock/texture6",
				new Vector3f(100, h, 300), 0, 0, 0, 5f);
		
		// PALM TREE
		Entity palmTree = new Entity(loader, "Entity/Tree/palmTree", "Entity/Tree/palmTreeTexture",
				new Vector3f(100, h, 320), 0, 0, 0, 1);
		
		// TREE 1
		Entity tree1 = new Entity(loader, "Entity/Tree/tree1", "Entity/Tree/tree1",
				new Vector3f(100, h, 340), 0, 0, 0, 5);
		// TREE 2
		Entity tree2 = new Entity(loader, "Entity/Tree/tree2", "Entity/Tree/tree2",
				new Vector3f(100, h, 360), 0, 0, 0, 5);
		// TREE 3
		Entity tree3 = new Entity(loader, "Entity/Tree/tree3", "Entity/Tree/tree3",
				new Vector3f(100, h, 380), 0, 0, 0, 5);
		// TREE 4
		Entity tree4 = new Entity(loader, "Entity/Tree/tree4", "Entity/Tree/tree4",
				new Vector3f(100, h, 400), 0, 0, 0, 5);
		// TREE 5
		Entity tree5 = new Entity(loader, "Entity/Tree/tree5", "Entity/Tree/tree5",
				new Vector3f(100, h, 420), 0, 0, 0, 5);
		// TREE 6
		Entity tree6 = new Entity(loader, "Entity/Tree/tree6", "Entity/Tree/tree6",
				new Vector3f(100, h, 440), 0, 0, 0, 5);
		// TREE 7
		Entity tree7 = new Entity(loader, "Entity/Tree/tree7", "Entity/Tree/tree7",
				new Vector3f(100, h, 460), 0, 0, 0, 5);
		
		// SMALL PLANT
		Entity smallPlant = new Entity(loader, "Entity/Foilage/smallPlant", "Entity/Foilage/smallPlant",
				new Vector3f(100, h, 480), 0, 0, 0, 20);
		
		// FENCE
		Entity fence = new Entity(loader, "Entity/Town/fence", "Entity/Town/fenceTexture",
				new Vector3f(100, h, 500), 0, 0, 0, 3);
			
		
		
		// MINE ENTRANCE ENTITIES
		// Fence Along Border between mines and city
		int fx = 0;
		for(int i=0; i<19; i++){
				entities.add(new Entity(loader, "Entity/Town/fence", "Entity/Town/fenceTexture",
						new Vector3f(fx+50, 0, 800), 0, 90, 0, 3));
			fx+=15;
		}
		fx = 0;
		for(int i=0; i<25; i++){
				entities.add(new Entity(loader, "Entity/Town/fence", "Entity/Town/fenceTexture",
						new Vector3f(fx+390, 0, 800), 0, 90, 0, 3));
			fx+=15;
		}
		
		// Fence Along Other Border
		fx = 0;
		for(int i=0; i<19; i++){
				entities.add(new Entity(loader, "Entity/Town/fence", "Entity/Town/fenceTexture",
						new Vector3f(fx+55, 0, 0), 0, 90, 0, 3));
			fx+=15;
		}
		fx = 0;
		for(int i=0; i<25; i++){
				entities.add(new Entity(loader, "Entity/Town/fence", "Entity/Town/fenceTexture",
						new Vector3f(fx+385, 0, 0), 0, 90, 0, 3));
			fx+=15;
		}
		
		
/*		
		// Dead Trees
		for(int i=0; i<10; i++){
			// Rock1
			float randomX = (float) (Math.random()*800);
			float randomZ = (float) (Math.random()*800);
			float randomRot = (float) (Math.random()*360);
			float randomScale = (float) (Math.random()*50);
			entities.add(new Entity(loader, "Entity/Tree/tree1", "Entity/Tree/tree1",
					new Vector3f(randomX, mineEntrance.getHeightOfTerrain(randomX, randomZ+800), randomZ+800), 
					0, randomRot, 0, randomScale));		
		}
*/

/* TESTING ENTITIES
		// Add entities to a list
		entities.add(lamp);
		entities.add(rock1);
		entities.add(rock2);
		entities.add(rock3);
		entities.add(rock4);
		entities.add(rock5);
		entities.add(rock6);
		entities.add(palmTree);
		entities.add(tree1);
		entities.add(tree2);
		entities.add(tree3);
		entities.add(tree4);
		entities.add(tree5);
		entities.add(tree6);
		entities.add(tree7);
		entities.add(smallPlant);
		entities.add(fence);
*/		
		
		// Animation Example
		// for tree_000001 to tree_xxxxxx (6 x's)
//		Animation animation = new Animation(5, OBJFileLoader.loadAnimatedOBJ("TestAnimation/rock", 3));

		
		AnimatedEntity test = new AnimatedEntity(loader, 3, "TestAnimation/rock", "TestAnimation/texture2",
				new Vector3f(400, 0, 400), 0, 0, 0, 1);
		
		
		// Eventually:
//		Animation animation = new Animation(5, "Animations/Tree/tree");
//		AnimatedEntity = new AnimatedEntity()
		
//		this.data = OBJFileLoader.loadOBJ(dataFile);
		
		
		
		
		//********************ENTITY STUFF END****************************
		
		//******************PLAYER AND CAMERA STUFF*****************************
		
		// Create Player
		Player player = new Player(loader, "Entity/Player/cube", "Entity/Player/charcoalTexture",
				new Vector3f(300, 0, 700), 0, 0, 0, 1);
		
		Camera camera = new Camera(player);	// create Camera
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());	// create mouse picker
		
		//******************PLAYER AND CAMERA STUFF END************************
		
		//******************************GUI STUFF*********************************************
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();	// GUI List
		guis = setUpGUI(loader);	// Method that contains all of the GUI information (CHANGE TO ANOTHER CLASS EVENTUALLY?)
		GuiRenderer guiRenderer = new GuiRenderer(loader);	// create GuiRenderer
		
		//*************************END GUI STUFF*************************************************
		
		// Game Loop
		while(glfwWindowShouldClose(DisplayManager.window) == GL_FALSE){
			// Game Logic
			
			// test to see which terrain the player is on
			for(Terrain terrain:terrains){
				if(player.getCurrentGridX() == terrain.getGridX() && player.getCurrentGridZ() == terrain.getGridZ()){
					currentTerrain = terrain;
				}
			}
			
			player.move(currentTerrain);	// Let the player move given the current terrain	
				
			camera.move(); // Let the Camera move
			
			renderer.processEntity(player);	// render player	
			
			//render all terrains that surround the player, including the one the player is on (so 9 of them)
			for(Terrain terrain:terrains){
				if((player.getCurrentGridX()) == terrain.getGridX() && player.getCurrentGridZ() == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 0, 0
				}
				if((player.getCurrentGridX()+1) == terrain.getGridX() && player.getCurrentGridZ() == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 1, 0
				}
				if((player.getCurrentGridX()-1) == terrain.getGridX() && player.getCurrentGridZ() == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// -1,0
				}
				if(player.getCurrentGridX() == terrain.getGridX() && (player.getCurrentGridZ()+1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 0, 1
				}
				if(player.getCurrentGridX() == terrain.getGridX() && (player.getCurrentGridZ()-1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 0, -1
				}
				if((player.getCurrentGridX()+1) == terrain.getGridX() && (player.getCurrentGridZ()+1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 1, 1
				}
				if((player.getCurrentGridX()-1) == terrain.getGridX() && (player.getCurrentGridZ()-1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// -1, -1
				}
				if((player.getCurrentGridX()+1) == terrain.getGridX() && (player.getCurrentGridZ()-1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// 1, -1
				}
				if((player.getCurrentGridX()-1) == terrain.getGridX() && (player.getCurrentGridZ()+1) == terrain.getGridZ()){
					renderer.processTerrain(terrain);	// -1, 1
				}
			}
			
			//render all entities on the surrounding terrain, including the one the player is on
			for(Entity entity:entities){
				
				//Rotate all Entities
//				entity.increaseRotation(1, 0, 1);	// using matrices to rotate the lamp
				
				if((player.getCurrentGridX()) == entity.getGridX() && player.getCurrentGridZ() == entity.getGridZ()){
					renderer.processEntity(entity);	// 0, 0
				}
				if((player.getCurrentGridX()+1) == entity.getGridX() && player.getCurrentGridZ() == entity.getGridZ()){
					renderer.processEntity(entity);	// 1, 0
				}
				if((player.getCurrentGridX()-1) == entity.getGridX() && player.getCurrentGridZ() == entity.getGridZ()){
					renderer.processEntity(entity);	// -1,0
				}
				if(player.getCurrentGridX() == entity.getGridX() && (player.getCurrentGridZ()+1) == entity.getGridZ()){
					renderer.processEntity(entity);	// 0, 1
				}
				if(player.getCurrentGridX() == entity.getGridX() && (player.getCurrentGridZ()-1) == entity.getGridZ()){
					renderer.processEntity(entity);	// 0, -1
				}
				if((player.getCurrentGridX()+1) == entity.getGridX() && (player.getCurrentGridZ()+1) == entity.getGridZ()){
					renderer.processEntity(entity);	// 1, 1
				}
				if((player.getCurrentGridX()-1) == entity.getGridX() && (player.getCurrentGridZ()-1) == entity.getGridZ()){
					renderer.processEntity(entity);	// -1, -1
				}
				if((player.getCurrentGridX()+1) == entity.getGridX() && (player.getCurrentGridZ()-1) == entity.getGridZ()){
					renderer.processEntity(entity);	// 1, -1
				}
				if((player.getCurrentGridX()-1) == entity.getGridX() && (player.getCurrentGridZ()+1) == entity.getGridZ()){
					renderer.processEntity(entity);	// -1, 1
				}
			}		
			
			renderer.render(lights, camera); // render light and camera	
			
			guiRenderer.render(guis);	// render the GUIs
			
			// Turns on polygon mode if P is pressed
			if(Keyboard.isKeyDown(GLFW_KEY_P)){
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			}
			
			picker.update(); //update the mouse picker
			Mouse.update(); // update the mouse
			DisplayManager.updateDisplay(); // Display Update (Keep at Bottom)
			
		}
		
		// Clean Everything up
		guiRenderer.CleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();		
	}
	
	// Create GUIs and set positions. Then return a filled list for the gui
	public static List<GuiTexture> setUpGUI(Loader loader){
		
		// List for use in this method
		List<GuiTexture> list = new ArrayList<GuiTexture>();
		
		// Variables to be modified depending on the GUI area being set up
		float space = 0f;
		float spaceIncrement = 0.065f;
		float startingPosX = -0.293f;
		float startingPosY = -0.9f;
		
		// ACTION BARS
		GuiTexture abFrame = new GuiTexture(loader.loadTexture("GUI/abFrame"), new Vector2f(0.0f, startingPosY), new Vector2f(0.33f, 0.07f));	
		GuiTexture ab1 = new GuiTexture(loader.loadTexture("GUI/ab1"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab2 = new GuiTexture(loader.loadTexture("GUI/ab2"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab3 = new GuiTexture(loader.loadTexture("GUI/ab3"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab4 = new GuiTexture(loader.loadTexture("GUI/ab4"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab5 = new GuiTexture(loader.loadTexture("GUI/ab5"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab6 = new GuiTexture(loader.loadTexture("GUI/ab6"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab7 = new GuiTexture(loader.loadTexture("GUI/ab7"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab8 = new GuiTexture(loader.loadTexture("GUI/ab8"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab9 = new GuiTexture(loader.loadTexture("GUI/ab9"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture ab0 = new GuiTexture(loader.loadTexture("GUI/ab0"), new Vector2f(startingPosX + space, startingPosY), new Vector2f(0.03f, 0.058f));
		
		// FIREBALL ABILITY EXAMPLE
		GuiTexture abilityExample = new GuiTexture(loader.loadTexture("GUI/fireBallExample"), new Vector2f(-0.293f, startingPosY), new Vector2f(0.03f, 0.058f));
		
		// HEALTH AND MAGIC ENERGY BAR
		GuiTexture healthBar = new GuiTexture(loader.loadTexture("GUI/abHealth"), new Vector2f(0.0f,-0.72f), new Vector2f(0.33f,0.03f));
		GuiTexture energyBar = new GuiTexture(loader.loadTexture("GUI/abEnergy"), new Vector2f(0.0f,-0.79f), new Vector2f(0.33f,0.03f));
		
		// change variables
		space = 0f;
		spaceIncrement = 0.12f;
		startingPosX = 0.95f;
		startingPosY = -0.9f;
		
		// RIGHT SIDE INTERFACE (INVENTORY, ETC)
		GuiTexture rightFrame = new GuiTexture(loader.loadTexture("GUI/fillerFrame"), new Vector2f(startingPosX-0.315f, -0.66f), new Vector2f(0.28f, 0.30f));	
		GuiTexture fillerR1 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerR2 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerR3 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerR4 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerR5 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		
		// change variables
		space = 0f;
		spaceIncrement = 0.12f;
		startingPosX = -0.95f;
		startingPosY = -0.9f;
		
		// LEFT SIDE INTERFACE (INVENTORY, ETC)
		GuiTexture leftFrame = new GuiTexture(loader.loadTexture("GUI/fillerFrame"), new Vector2f(startingPosX+0.315f, -0.66f), new Vector2f(0.28f, 0.30f));
		GuiTexture fillerL1 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerL2 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerL3 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerL4 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		space += spaceIncrement;
		GuiTexture fillerL5 = new GuiTexture(loader.loadTexture("GUI/filler"), new Vector2f(startingPosX, startingPosY + space), new Vector2f(0.03f, 0.058f));
		
		// add to GUI
		list.add(abFrame);
		list.add(abilityExample);	// example of an ability on the action bar
		list.add(ab1);
		list.add(ab2);
		list.add(ab3);
		list.add(ab4);
		list.add(ab5);
		list.add(ab6);
		list.add(ab7);
		list.add(ab8);
		list.add(ab9);
		list.add(ab0);
		
		list.add(healthBar);
		list.add(energyBar);
		
		list.add(rightFrame);
		list.add(fillerR1);
		list.add(fillerR2);
		list.add(fillerR3);
		list.add(fillerR4);
		list.add(fillerR5);
		
		list.add(leftFrame);
		list.add(fillerL1);
		list.add(fillerL2);
		list.add(fillerL3);
		list.add(fillerL4);
		list.add(fillerL5);
		
		return list;		
	}
	
}
