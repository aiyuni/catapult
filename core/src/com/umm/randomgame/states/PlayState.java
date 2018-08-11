package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
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
import com.umm.randomgame.BasketActor;
import com.umm.randomgame.CatBody;
import com.umm.randomgame.Main;

import java.util.Random;

/**
 * This should be the playstate. The previous one wouldn't work so I'm currently trying to move your code from Main to play.
 * Created by Lena on 2018-07-04.
 */

public class PlayState extends State  {

    /** width of screen **/
    public static final int WIDTH = 1050;
    /**height of screen**/
    public static final int HEIGHT = 1800;

    private Texture test;

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

    private Main main;

    private Sprite cat;
    public PlayState(GameStateManager gsm, Main main) {
        super(gsm);
        this.main = main;
        create();

    }

    public void create () {

        ScreenViewport viewport = new ScreenViewport();  //sets up the display
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage); //enables LibGDX input listeners
        Box2D.init(); //enables Box2D physics

        batch = new SpriteBatch();

        cat = new Sprite(new Texture("shiny.png"));
        cat.setScale(0.2f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH / PPM, HEIGHT / PPM);  //this is the typical android device resolution

        world = new World(new Vector2(0, -50), true); //gravity is -50 m/s^2
        debugRenderer = new Box2DDebugRenderer();

        /** Creates the game ground for testing purposes (will remove later so the cat can fall out of the screen)*/
        //Creates the ground BodyDef object: this determines the type and position of the ground
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.KinematicBody;
        groundBodyDef.position.set(new Vector2(100 / PPM, 1 / PPM));
        // Create a body from the BodyDef and add it to the world
        final Body groundBody = world.createBody(groundBodyDef);
        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // sets the shape as a box (setAsBox takes half-width and half-height as arguments!)
        groundBox.setAsBox(camera.viewportWidth, 1.0f); // the ground will take up the entire width of the screen, and 1 pixel high
        // Create a fixture from our polygon shape and add it the body. Fixtures are responsible for collision.
        groundBody.createFixture(groundBox, 0.0f);
        groundBox.dispose();

        /**Creates the LEFT WALL static body so the cat can bounce off walls. */
        BodyDef leftWallDef = new BodyDef();
        leftWallDef.type = BodyDef.BodyType.StaticBody;
        leftWallDef.position.set(new Vector2(0, 0));
        Body leftWall = world.createBody(leftWallDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(0, camera.viewportHeight);
        leftWall.createFixture(leftWallBox, 0);
        leftWallBox.dispose();

        /**Creates the RIGHT WALL static body so the cat can bounce off walls. */
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(new Vector2(1050 / PPM, 0));
        Body rightWall = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0, camera.viewportHeight);
        rightWall.createFixture(rightWallBox, 0);
        rightWallBox.dispose();

        /**Draws the initial (lower) basket */
        this.drawInitialBasket(initialX, initialY);

        /**Draws the target (upper) basket after randoming its position */
        int[] randomPosition = this.randomizePosition(targetX, targetY);
        this.drawBasketShape(randomPosition[0], randomPosition[1]);

        /**THIS CREATES THE BOUNCING CAT*/
        BodyDef catBodyDef = new BodyDef();
        catBodyDef.type = BodyDef.BodyType.DynamicBody;
        catBodyDef.position.set(200 / PPM, 1200 / PPM);
        catBody = new CatBody(world, catBodyDef);

        //Initializes the cat sprite in the same position as the cat body.
        cat.setCenter(catBody.getX() * PPM, catBody.getY() * PPM);
        System.out.println("Ball position on create is: " + catBody.getX() + ", " + catBody.getY());

        //Create the initial basket sprite
        basket1 = new BasketActor(this, this.getInitialBasketBottomPositionX() * 30, this.getInitialBasketBottomPositionY() * 30); //multiply by PPM cuz actor doesnt use PPM

        //Add the basket Actor to the stage
        stage.addActor(basket1);

        /**Lets the world detect collision between its objects. */
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA(); //this gets the first fixture of the collision
                Fixture fixtureB = contact.getFixtureB(); //this gets the other fixture
                Gdx.app.log("beginContact", "between " + fixtureA.getBody().toString() + " and " + fixtureB.getBody().toString());
                if (fixtureA.getBody() == catBody.getBody()) {
                    //System.out.println("fixtureA body = cat");
                }
                if (fixtureB.getBody() == catBody.getBody()) {
                    //System.out.println("fixtureB body = cat");
                }
                if (fixtureA.getBody() == targetBasketBottom) {
                    //System.out.println("fixtureA body = target basket bottom!");
                }

                if (fixtureA.getBody() == groundBody && fixtureB.getBody() == catBody.getBody()){
                    System.out.println("catbody touched ground.");
                    catBody.getBody().setTransform(targetBasketBottom.getPosition().x/PPM, targetBasketBottom.getPosition().y/PPM, 0);

                }

                /**If the cat touches the basket's base, set bounce to 0, move the basket down, destroy the lower basket, and spawn a new basket */
                if (fixtureA.getBody() == targetBasketBottom && fixtureB.getBody() == catBody.getBody()) {

                    catBody.getFixture().setRestitution(0);
                    //catBody.getFixture().setFriction(); //new

                    bodiesToDestroy = new Body[2];
                    bodiesToDestroy[0] = initialBasketBottom;
                    bodiesToDestroy[1] = initialBasket;

                    //System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
                    //+ " , " + initialBasketBottom.getPosition().y);

                    initialBasketBottom = targetBasketBottom;
                    initialBasket = targetBasket;

                    //System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
                    //+ " , " + initialBasketBottom.getPosition().y);

                    targetBasketBottom.setLinearVelocity(0, -12f); //change this value to affect how fast the canopy falls
                    targetBasket.setLinearVelocity(0, -12f);

                    //System.out.println("User data for new initial basket bottom is: " + initialBasketBottom.getUserData());
                    //System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
                    //+ " , " + initialBasketBottom.getPosition().y);

                    scored = true;

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

    @Override
    public void handleInput() {
/*
if(conditionToEnd){
gsm.set(new EndState(gsm));
dipose();
}
 */
    }

    @Override
    public void update(float dt) {
handleInput();
    }

    public void render(SpriteBatch batch){
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        batch.begin();
        basket1.draw(batch, 0);
        cat.setCenter(catBody.getX()* PPM, catBody.getY() * PPM); //redraw the cat sprite whereever the cat body is
        cat.draw(batch);
        batch.end();

        debugRenderer.render(world, camera.combined);
        world.step(1/60f, 6, 2);

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
        if (initialBasketBottom.getPosition().y * PPM < initialY){
			System.out.println("basket reached initial position!");
			initialBasketBottom.setLinearVelocity(0,0);
			initialBasket.setLinearVelocity(0, 0);
            catBody.getFixture().setRestitution(0.5f);
            //catBody.getFixture().setFriction(0); //new
		}

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
        basketLeftArm.set(new Vector2(-1, 0), new Vector2(-3,2));
        EdgeShape basketRightArm = new EdgeShape();
        basketRightArm.set(new Vector2(1, 0), new Vector2(3, 2));
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
        basketLeftArm.set(new Vector2(-1, 0), new Vector2(-3,2));
        EdgeShape basketRightArm = new EdgeShape();
        basketRightArm.set(new Vector2(1, 0), new Vector2(3, 2));
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

    public SpriteBatch getSpriteBatch() {
        return batch;
    }
}
