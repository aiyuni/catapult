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


/** ToDo:
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
	public int initialX = 200;  //this value will change over time to spawn baskets at a random location
	public int initialY =500; //the starting basket will ALWAYS be adjusted to this value.  Treat this as a constant for now.
	public int targetX = 200; //this is the same as initialX by design of how randomizePosition is defined.
	public int targetY = 1000; //this affects the base starting height of the target basket.

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
	//private Body initialBasketLeft;
	//private Body initialBasketRight;

	private Body targetBasketBottom;
	//private Body targetBasketLeft;
	//private Body targetBasketRight;

	private Body initialBasket;
	private Body targetBasket;
	private Body tempBasket;

	private Sprite cat;


	@Override
	public void create () {

		ScreenViewport viewport = new ScreenViewport();  //sets up the display
		stage = new Stage(viewport);

		Gdx.input.setInputProcessor(stage); //enables LibGDX input listeners
		Box2D.init(); //enables Box2D physics

		batch = new SpriteBatch();
		gsm = new GameStateManager();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		gsm.push(new MenuState(gsm));




	}

	/**This method is called every frame */
	@Override
	public void render () {


		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);


		/**If the cat is in the target basket, spawn new basket. */
		if (scored == true){
			for (int i =0; i<bodiesToDestroy.length; i++){
				world.destroyBody(bodiesToDestroy[i]);
			}
			//drawInitialBasket((int)(targetBasketBottom.getPosition().x * PPM), initialY);
			/*for (int i = 2; i < bodiesToDestroy.length; i++){
				world.destroyBody(bodiesToDestroy[i]);
			} */
			int[] randomPosition = randomizePosition(initialX, targetY);
			drawBasketShape(randomPosition[0], randomPosition[1]);

			//catBody.getFixture().setRestitution(0.5f);  do this in touchUp instead
			scored = false;
		}

		/**If the upper basket moves the lower basket's spot, stop its velocity */

		/*if (initialBasketBottom.getPosition().y * PPM < initialY){
			System.out.println("basket reached initial position!");
			initialBasketBottom.setLinearVelocity(0,0);
			initialBasket.setLinearVelocity(0, 0);
		} */
		//world.step(1/60f, 6, 2);

	}

	/**Draws the initial (lower) basket*/
	public void drawInitialBasket(int x, int y){

		//Draws the entire basket body
		BodyDef basketDef = new BodyDef();
		basketDef.type = BodyDef.BodyType.KinematicBody;
		basketDef.position.set(new Vector2(x/PPM, y/PPM));
		initialBasket = world.createBody(basketDef);
		initialBasket.setUserData(basketDef);
		EdgeShape basketBase = new EdgeShape(); //PolygonShape sets how long the line is
		basketBase.set(new Vector2(-1,0), new Vector2(1, 0));
		EdgeShape basketLeftArm = new EdgeShape();
		basketLeftArm.set(new Vector2(-1, 0), new Vector2(-3,3));
		EdgeShape basketRightArm = new EdgeShape();
		basketRightArm.set(new Vector2(1, 0), new Vector2(3, 3));
		initialBasket.createFixture(basketBase,0);
		initialBasket.createFixture(basketRightArm,0);
		initialBasket.createFixture(basketLeftArm, 0);
		basketBase.dispose();
		basketLeftArm.dispose();
		basketRightArm.dispose();


		//Horizontal Line for basket for collision purposes//
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


		/*

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

        */
	}

	/** Draws the target (upper) basket */
	public void drawBasketShape(int x, int y) {

		System.out.println("Drawing target basket at: " + x + ", " + y + ", after PPM adjustion is: " + x/PPM + ", " + y/PPM);

		/**This is the entire basket body */
		BodyDef basketDef = new BodyDef();
		basketDef.type = BodyDef.BodyType.KinematicBody;
		basketDef.position.set(new Vector2(x/PPM, y/PPM));
		targetBasket = world.createBody(basketDef);
		targetBasket.setUserData(basketDef);
		EdgeShape basketBase = new EdgeShape(); //PolygonShape sets how long the line is
		basketBase.set(new Vector2(-1,0), new Vector2(1, 0));
		EdgeShape basketLeftArm = new EdgeShape();
		basketLeftArm.set(new Vector2(-1, 0), new Vector2(-3,3));
		EdgeShape basketRightArm = new EdgeShape();
		basketRightArm.set(new Vector2(1, 0), new Vector2(3, 3));
		targetBasket.createFixture(basketBase,0);
		targetBasket.createFixture(basketRightArm,0);
		targetBasket.createFixture(basketLeftArm, 0);
		basketBase.dispose();
		basketLeftArm.dispose();
		basketRightArm.dispose();


		//Draw the bottom line for collision detection purposes
		BodyDef targetBasketBottomDef = new BodyDef();
		targetBasketBottomDef.type = BodyDef.BodyType.KinematicBody;  //BodyDef sets the position of the line
		targetBasketBottomDef.position.set(new Vector2(x/PPM, y/PPM));
		targetBasketBottom = world.createBody(targetBasketBottomDef);  //Body is a class that lets us add BodyDef to world
		targetBasketBottom.setUserData(targetBasketBottomDef);
		EdgeShape targetBasketBottomBox = new EdgeShape(); //PolygonShape sets how long the line is
		targetBasketBottomBox.set(new Vector2(-1,0), new Vector2(1, 0));
		targetBasketBottom.createFixture(targetBasketBottomBox, 0);
		targetBasketBottomBox.dispose();


		/*
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

		*/

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


	public Body getInitialBasket(){
		return initialBasket;
	}
	@Override
	public void dispose () {
		batch.dispose();
	}

	public CatBody getCatBody() {

		return catBody;
	}

}
