package com.umm.randomgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.umm.randomgame.states.GameStateManager;
import com.umm.randomgame.states.MenuState;

import java.util.Random;


/** ToDo: the diagonal lines that make up the basket body needs a proper rotation based on user drag.
  * In order to do that, need to set the diagonal lines based on the updated bottom line's position whenever it updates,
 * rather than just the original bottom line's position.
 * This means the edgeShape set() method's vector needs to be updated based on the bottom line's angle, but the angle is returning the same value for 2 different
 * positions.
 * ToDo:
 */
public class Main extends ApplicationAdapter {
	/** width of screen **/
public static final int WIDTH = 1050;
/**height of screen**/
public static final int HEIGHT = 1800;
/**title of game**/
public static final String TITLE= "Cat-apault";
/** game state manager class from states **/
private GameStateManager gsm;
	/**Pixels per meter constant. This constant is to relate Box2D's physics with LibGDX sprites. */
	public static final float PPM = 30;

	/**This keeps track if the cat lands in the basket. */
	public boolean scored = false;

	/**Values for the initial (lower) basket and target (upper) basket.*/
	public int initialX = 200;  //this value will change over time to spawn baskets at random location
	public int initialY =500;
	public int targetX = 200; //this is the same as initialX by design of how randomizePosition is defined.
	public int targetY = 1000;

	/**Initializes all the global variables used in the program.*/
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Body[] bodiesToDestroy;

	private Stage stage;
	private BasketActor basket1;
	private BasketActor basket2; //remove later, rename basket1

	private CatBody catBody;

	private Body initialBasketBottom;
	private Body initialBasketLeft;
	private Body initialBasketRight;

	private Body targetBasketBottom;
	private Body targetBasketLeft;
	private Body targetBasketRight;

	private Sprite cat;


	@Override
	public void create () {

		ScreenViewport viewport = new ScreenViewport();  //sets up the display
		stage = new Stage(viewport);

		Gdx.input.setInputProcessor(stage); //enables LibGDX input listeners
		Box2D.init(); //enables Box2D physics

		cat = new Sprite(new Texture("shiny.png"));
		cat.setScale(0.2f);

		batch = new SpriteBatch();
		gsm = new GameStateManager();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		gsm.push(new MenuState(gsm));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH/PPM, HEIGHT/PPM);  //this is the typical android device resolution

		world = new World(new Vector2(0, -50), true); //gravity is -50 m/s^2
		debugRenderer = new Box2DDebugRenderer();

		/** Creates the game ground for testing purposes (will remove later so the cat can fall out of the screen)*/
		//Creates the ground BodyDef object: this determines the type and position of the ground
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyDef.BodyType.KinematicBody;
		groundBodyDef.position.set(new Vector2(100/PPM, 1/PPM));
		// Create a body from the BodyDef and add it to the world
		Body groundBody = world.createBody(groundBodyDef);
		// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
		// Set the polygon shape as a box which is twice the size of our view port and 20 high (setAsBox takes half-width and half-height as arguments!)
		groundBox.setAsBox(camera.viewportWidth, 1.0f); // the ground will take up the entire width of the screen, and 1 pixel high
		// Create a fixture from our polygon shape and add it the body. Fixtures are responsible for collision.
		groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();

		/**Creates the LEFT WALL static body so the cat can bounce off walls. */
		BodyDef leftWallDef = new BodyDef();
		leftWallDef.type = BodyDef.BodyType.StaticBody;
		leftWallDef.position.set(new Vector2(0, 0));
		Body leftWall = world.createBody(leftWallDef); //CREATES THE ACTUAL BODY SO YOU CAN ADD THE BODY IN RENDER
		PolygonShape leftWallBox = new PolygonShape();
		leftWallBox.setAsBox(0, camera.viewportHeight);
		leftWall.createFixture(leftWallBox, 0);
		leftWallBox.dispose();

		/**Creates the RIGHT WALL static body so the cat can bounce off walls. */
		BodyDef rightWallDef = new BodyDef();
		rightWallDef.type = BodyDef.BodyType.StaticBody;
		rightWallDef.position.set(new Vector2(1050/PPM, 0));
		Body rightWall = world.createBody(rightWallDef); //CREATES THE ACTUAL BODY SO YOU CAN ADD THE BODY IN RENDER
		PolygonShape rightWallBox = new PolygonShape();
		rightWallBox.setAsBox(0, camera.viewportHeight);
		rightWall.createFixture(rightWallBox, 0);
		rightWallBox.dispose();

		/**Draws the initial (lower) basket */
		this.drawInitialBasket(initialX, initialY);

		/**Draws the target (upper) basket after randoming its position */
		int[] randomPosition = this.randomizePosition(targetX, targetY);
		this.drawBasketShape(randomPosition[0], randomPosition[1]);

		/**THIS IS THE BOUNCING CAT*/
		BodyDef catBodyDef = new BodyDef();
		catBodyDef.type = BodyDef.BodyType.DynamicBody;
		catBodyDef.position.set(200/PPM, 1200/PPM);
		catBody = new CatBody(world, catBodyDef);

		//Initializes the cat sprite in the same position as the cat body.
		cat.setCenter(catBody.getX()* PPM, catBody.getY() * PPM);
		System.out.println("Ball position on create is: " + catBody.getX() + ", " + catBody.getY());

		//Create the initial basket sprite
		basket1 = new BasketActor(this, getInitialBasketBottomPositionX() * 30, getInitialBasketBottomPositionY()* 30); //multiply by PPM cuz actor doesnt use PPM

		//Add the basket Actor to the stage
		stage.addActor(basket1);

		/**Lets the world detect collision between its objects */
		world.setContactListener(new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				Gdx.app.log("beginContact", "between " + fixtureA.getBody().toString() + " and " + fixtureB.getBody().toString());
				if (fixtureA.getBody() == catBody.getBody()){
					//System.out.println("fixtureA body = cat");
				}
				if (fixtureB.getBody() == catBody.getBody()){
					//System.out.println("fixtureB body = cat");
				}
				if (fixtureA.getBody() == targetBasketBottom) {
					//System.out.println("fixtureA body = target basket bottom!");
				}

				if (fixtureA.getBody() == targetBasketBottom && fixtureB.getBody() == catBody.getBody()){
					catBody.getFixture().setRestitution(0);
					//System.out.println("Restitution of ball set to 0!");
					//world.destroyBody(initialBasketBottom);
					//world.destroyBody(initialBasketRight);
					//world.destroyBody(initialBasketLeft);

					/*bodiesToDestroy = new Body[3];
					bodiesToDestroy[0] = initialBasketLeft;
					bodiesToDestroy[1] = initialBasketBottom;
					bodiesToDestroy[2] = initialBasketRight; */

					bodiesToDestroy = new Body[6];
					bodiesToDestroy[0] = initialBasketBottom;
					bodiesToDestroy[1] = initialBasketLeft;
					bodiesToDestroy[2] = initialBasketRight;
					bodiesToDestroy[3] = targetBasketBottom;
					bodiesToDestroy[4] = targetBasketRight;
					bodiesToDestroy[5] = targetBasketLeft;

					System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
							+ " , " + initialBasketBottom.getPosition().y);

					initialBasketBottom = targetBasketBottom;
					initialBasketLeft = targetBasketLeft;
					initialBasketRight = targetBasketRight;

					System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
							+ " , " + initialBasketBottom.getPosition().y);

					//targetBasketBottom.setTransform(initialX/PPM, initialY/PPM, 0);

					//initialBasketBottom.setTransform(((BodyDef)targetBasketBottom.getUserData()).position.x / PPM, initialY / PPM, 0);
					//initialBasketLeft.setTransform(((BodyDef)targetBasketLeft.getUserData()).position.x/PPM, initialY/PPM, 0);
					//initialBasketRight.setTransform(((BodyDef)targetBasketRight.getUserData()).position.x/PPM, initialY/PPM, 0);

					System.out.println("User data for new initial basket bottom is: " + initialBasketBottom.getUserData());
					System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
									+ " , " + initialBasketBottom.getPosition().y);
					//System.out.println("BodyDef target bottom x is: " + ((BodyDef)targetBasketBottom.getUserData()).position.x);
					//System.out.println(initialBasketBottom.getPosition().x + ", " + initialBasketBottom.getPosition().y);


					//world.destroyBody(targetBasketBottom);
					//world.destroyBody(targetBasketLeft);
					//world.destroyBody(targetBasketRight);

					scored = true;
					//drawInitialBasket(initialX, initialY);

					//int[] randomPosition = randomizePosition(targetX, targetY);
					//drawBasketShape(randomPosition[0], randomPosition[1]);




				}
			}

			@Override
			public void endContact(Contact contact) {
				//ballBody.fixture.setRestitution(0.5f);  //cant reset restituion here, need to do it after the basket is transposed?
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

		});

	}

	/**This method is called every frame */
	@Override
	public void render () {


		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		batch.begin();
		basket1.draw(batch, 0);
		cat.setCenter(catBody.getX()* PPM, catBody.getY() * PPM); //redraw the cat sprite whereever the cat body is
		cat.draw(batch);
		batch.end();

		debugRenderer.render(world, camera.combined);

		/**If the cat is in the target basket, spawn new basket. Still needs work... */
		if (scored == true){
			for (int i =0; i<bodiesToDestroy.length - 3; i++){
				world.destroyBody(bodiesToDestroy[i]);
			}
			drawInitialBasket((int)(targetBasketBottom.getPosition().x * PPM), initialY);
			for (int i = 3; i < bodiesToDestroy.length; i++){
				world.destroyBody(bodiesToDestroy[i]);
			}
			int[] randomPosition = randomizePosition(initialX, targetY);
			drawBasketShape(randomPosition[0], randomPosition[1]);

			catBody.getFixture().setRestitution(0.5f);
			scored = false;
		}

		world.step(1/60f, 6, 2);

	}

	/**Draws the initial (lower) basket*/
	public void drawInitialBasket(int x, int y){

		//Horizontal Line for basket//
		BodyDef basketBottomDef = new BodyDef();
		basketBottomDef.type = BodyDef.BodyType.KinematicBody;  //BodyDef sets the position of the line
		basketBottomDef.position.set(new Vector2(x/PPM, y/PPM));
		initialBasketBottom = world.createBody(basketBottomDef);  //Body is a class that lets us add BodyDef to world
		initialBasketBottom.setUserData(basketBottomDef); //stores the BodyDef for future use (use when creating a new body with this bodydef)
		EdgeShape initialBasketBottomBox = new EdgeShape(); //PolygonShape sets how long the line is
		initialBasketBottomBox.set(new Vector2(-1,0), new Vector2(1, 0));
		System.out.println("angle of initial basket bottom is: " + initialBasketBottom.getAngle());
		initialBasketBottom.createFixture(initialBasketBottomBox, 0);
		initialBasketBottomBox.dispose();

		//Right Diagonal Line for basket
		BodyDef basketRightDef = new BodyDef();
		basketRightDef.type = BodyDef.BodyType.KinematicBody;
		basketRightDef.position.set(new Vector2((x + 50)/PPM, y/PPM));
		initialBasketRight= world.createBody(basketRightDef);
		initialBasketRight.setUserData(basketRightDef);
		EdgeShape basketRightBox = new EdgeShape();
		basketRightBox.set(new Vector2(-0,0), new Vector2(2,3));
		initialBasketRight.createFixture(basketRightBox, 0);
		basketRightBox.dispose();

		//Left Diagonal Line for basket //
		BodyDef basketLeftDef = new BodyDef();
		basketLeftDef.type = BodyDef.BodyType.KinematicBody;
		basketLeftDef.position.set(new Vector2((x - 50)/PPM, y/PPM));
		initialBasketLeft = world.createBody(basketLeftDef);
		initialBasketLeft.setUserData(basketLeftDef);
		EdgeShape basketLeftBox = new EdgeShape();
		basketLeftBox.set(new Vector2(0,0), new Vector2(-2,3));
		initialBasketLeft.createFixture(basketLeftBox, 0);
		basketLeftBox.dispose();


	}

	/** Draws the target (upper) basket */
	public void drawBasketShape(int x, int y) {

		System.out.println("Drawing target basket at: " + x + ", " + y + ", after PPM adjustion is: " + x/PPM + ", " + y/PPM);
		//Draw the bottom line
		BodyDef targetBasketBottomDef = new BodyDef();
		targetBasketBottomDef.type = BodyDef.BodyType.KinematicBody;  //BodyDef sets the position of the line
		targetBasketBottomDef.position.set(new Vector2(x/PPM, y/PPM));
		targetBasketBottom = world.createBody(targetBasketBottomDef);  //Body is a class that lets us add BodyDef to world
		targetBasketBottom.setUserData(targetBasketBottomDef);
		EdgeShape targetBasketBottomBox = new EdgeShape(); //PolygonShape sets how long the line is
		targetBasketBottomBox.set(new Vector2(-1,0), new Vector2(1, 0));
		targetBasketBottom.createFixture(targetBasketBottomBox, 0);
		targetBasketBottomBox.dispose();

		//Right Diagonal Line for basket//
		BodyDef targetBasketRightDef = new BodyDef();
		targetBasketRightDef.type = BodyDef.BodyType.KinematicBody;
		targetBasketRightDef.position.set(new Vector2((x + 50)/PPM, y/PPM));
		targetBasketRight = world.createBody(targetBasketRightDef);
		targetBasketRight.setUserData(targetBasketRightDef);
		EdgeShape targetBasketRightBox = new EdgeShape();
		targetBasketRightBox.set(new Vector2(-0,0), new Vector2(2,3));
		targetBasketRight.createFixture(targetBasketRightBox, 0);
		targetBasketRightBox.dispose();

		//Left Diagonal Line for basket//
		BodyDef targetBasketLeftDef = new BodyDef();
		targetBasketLeftDef.type = BodyDef.BodyType.KinematicBody;
		targetBasketLeftDef.position.set(new Vector2((x - 50)/PPM, y/PPM));
		targetBasketLeft = world.createBody(targetBasketLeftDef);
		targetBasketLeft.setUserData(targetBasketLeftDef);
		EdgeShape targetBasketLeftBox = new EdgeShape();
		targetBasketLeftBox.set(new Vector2(-0,0), new Vector2(-2,3));
		targetBasketLeft.createFixture(targetBasketLeftBox, 0);
		targetBasketLeftBox.dispose();

		initialX = x; //so that randomizePosition() work correctly

	}

	/**Returns a randomized x and y value */
	public int[] randomizePosition(int x, int y){  //y will be a constant here

		int[] position = new int[2];
		position[0] = 0;
		position[1] = 0;

		Random rand = new Random();

		if (x < 400) {
			position[0] = rand.nextInt(400)  + 500;
		}
		else {
			position[0] = rand.nextInt(400);
		}

		position[1] = y + rand.nextInt(100) - 50;

		return position;

	}

	public float getInitialBasketBottomPositionX(){
		return initialBasketBottom.getPosition().x;
	}

	public float getInitialBasketBottomPositionY(){
		return initialBasketBottom.getPosition().y;
	}

	public Body getInitialBasketBottom(){
		return initialBasketBottom;
	}

	public Body getInitialBasketLeft() {
		return initialBasketLeft;
	}

	public Body getInitialBasketRight() {

		return initialBasketRight;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public Body getBody() {

		Body body = catBody.getBody();
		return body;
	}

}
