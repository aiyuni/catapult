package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.umm.randomgame.BasketActor;
import com.umm.randomgame.CatBody;
import com.umm.randomgame.Main;

import java.awt.Color;
import java.util.Random;

/**
 * This is the main gameplay screen.
 * TODO: .remove() method for Actor does not work at all.  Currently the sprites are constantly being updated per frame, instead of destroying and remaking a new sprite.
 */

public class PlayState extends State  {

    /** width of screen **/
    public static final int WIDTH = 1050;
    /**height of screen**/
    public static final int HEIGHT = 1800;

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

    public Stage stage;
    private Texture background;
    private BasketActor basket1;
    private BasketActor basket2;

    private CatBody catBody;

    private Body initialBasketBottom;
    //private Body initialBasketLeft;
    //private Body initialBasketRight;

    private Body targetBasketBottom;
    //private Body targetBasketLeft;
    //private Body targetBasketRight;

    private Body initialBasket;
    private Body targetBasket;

    private boolean touchedGround = false;

    private Main main;

    private Sprite cat;

    private static Preferences prefs = Gdx.app.getPreferences("HighScores");;
    private int score;
    private String scoreString;
    BitmapFont scoreDisplay;

    private TextButton pauseButton;
    private TextButton.TextButtonStyle pauseButtonStyle;
    public boolean isPaused = false;

    public PlayState(GameStateManager gsm, Main main) {
        super(gsm);
        this.main = main;
        create();

    }

    public void create () {

        ScreenViewport viewport = new ScreenViewport();  //sets up the display
        stage = new Stage(viewport);

        //Gdx.input.setInputProcessor(stage); //enables LibGDX input listeners
        Box2D.init(); //enables Box2D physics

        batch = new SpriteBatch();
        background = new Texture("background.png");
        cat = new Sprite(new Texture("shiny.png"));
        cat.setScale(0.2f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH / PPM, HEIGHT / PPM);  //this is the typical android device resolution

        world = new World(new Vector2(0, -50), true); //gravity is -50 m/s^2
        debugRenderer = new Box2DDebugRenderer();

        /**Initializes the score stuff*/
        score = 0;
        scoreString = "Score: " + score;
        scoreDisplay = new BitmapFont();
        scoreDisplay.getData().setScale(5);

        /**Creates pause button text*/
        pauseButtonStyle = new TextButton.TextButtonStyle();
        pauseButtonStyle.font = new BitmapFont(); //???
        pauseButton = new TextButton("Pause", pauseButtonStyle);
        pauseButton.setPosition(900, 1700);
        pauseButton.getLabel().setFontScale(5f);

        /**Add event listener to pauseButton*/
        pauseButton.addListener(new ChangeListener(){

            public void changed(ChangeEvent event, Actor actor){
//                System.out.println("change event triggered");
            }

            @Override
            public boolean handle (Event event){
                if (isPaused == false){
//                    System.out.println("changing pause to true");
                    isPaused = true;
                }
                else {
//                    System.out.println("changing pause to false");
                    isPaused = false;
                }
                return true;
            }

        });


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
        leftWallBox.setAsBox(0, 5000); //(0, camera.viewportHeight)
        leftWall.createFixture(leftWallBox, 0);
        leftWallBox.dispose();

        /**Creates the RIGHT WALL static body so the cat can bounce off walls. */
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(new Vector2(1050 / PPM, 0));
        Body rightWall = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0, 5000);
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
        catBody = new CatBody(world, catBodyDef, cat);

        //Initializes the cat sprite in the same position as the cat body.
        cat.setCenter(catBody.getX() * PPM, catBody.getY() * PPM);
//        System.out.println("Ball position on create is: " + catBody.getX() + ", " + catBody.getY());


        //Create the initial basket sprite
        basket1 = new BasketActor(this, this.getInitialBasketBottomPositionX() * 30, this.getInitialBasketBottomPositionY() * 30); //multiply by PPM cuz actor doesnt use PPM

        //Create the target basket sprite
        basket2 = new BasketActor(this, this.targetBasketBottom.getPosition().x * 30, this.targetBasketBottom.getPosition().y * 30, true);

        //Add the basket Actor to the stage
        stage.addActor(basket1);
        stage.addActor(basket2);
        stage.addActor(pauseButton);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(basket1.getBasketInputAdapter());
        //multiplexer.addProcessor(basket2.getBasketInputAdapter());
        Gdx.input.setInputProcessor(multiplexer); //enables LibGDX input listeners

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

                /**If the cat touched the ground, add the cat body to bodiesToDestroy so it can be destroyed in render method.
                 * Render method will also spawn new cat object.
                 */
                if (fixtureA.getBody() == groundBody && fixtureB.getBody() == catBody.getBody()){
//                    System.out.println("catbody touched ground.");
                    touchedGround = true;
                    //catBody.getBody().setTransform(targetBasketBottom.getPosition().x/PPM, targetBasketBottom.getPosition().y/PPM, 0);
                    bodiesToDestroy = new Body[1];
                    bodiesToDestroy[0] = catBody.getBody();


                }

                /**If the cat touches the basket's base, set bounce to 0, move the basket down, destroy the lower basket, and spawn a new basket */
                if (fixtureA.getBody() == targetBasketBottom && fixtureB.getBody() == catBody.getBody() && catBody.getBody().getLinearVelocity().y < 0) {

                    //Modify score
                    score++;
                    scoreString = "Score: " + score;
                    if (score > prefs.getInteger("MaxScore")){
                        prefs.putInteger("MaxScore", score);
                        prefs.flush();
                    }

                    catBody.getFixture().setRestitution(0);
                    //catBody.getFixture().setFriction(); //new

                    bodiesToDestroy = new Body[2];
                    bodiesToDestroy[0] = initialBasketBottom;
                    bodiesToDestroy[1] = initialBasket;

                    //System.out.println("The new initial basket's bottom's position is: " + initialBasketBottom.getPosition().x
                    //+ " , " + initialBasketBottom.getPosition().y);

                    initialBasketBottom = targetBasketBottom;
                    initialBasket = targetBasket;

                    basket1.remove();
                    basket2.remove();

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

        if (isPaused){
            stage.act(0);
            stage.draw();

            batch.begin();
            //draw background
            batch.draw(background,0,0, WIDTH, HEIGHT);
            basket1.draw(batch, 0);
            basket2.draw(batch, 0);
            cat.setCenter(catBody.getX()* PPM, catBody.getY() * PPM); //redraw the cat sprite whereever the cat body is
            cat.draw(batch);
            scoreDisplay.setColor(0.5f, 0.7f, 0.1f, 1.0f);
            scoreDisplay.draw(batch, scoreString, 100, 1600);
            batch.end();
            return;
        }

        else {
            stage.act(Gdx.graphics.getDeltaTime());
        }
        stage.draw();

        batch.begin();
        basket1.draw(batch, 0);
        basket2.draw(batch, 0);
        cat.setCenter(catBody.getX()* PPM, catBody.getY() * PPM); //redraw the cat sprite whereever the cat body is
        cat.draw(batch);
        scoreDisplay.setColor(0.5f, 0.7f, 0.1f, 1.0f);
        scoreDisplay.draw(batch, scoreString, 100, 1600);

        //prefs.putInteger("Score", score);
        //score = prefs.getInteger("Score" );
        //prefs.flush();

        batch.end();

        debugRenderer.render(world, camera.combined);
        world.step(1/60f, 6, 2);

        if (catBody.getBody().getLinearVelocity().x > 0.75 && Math.abs(catBody.getBody().getLinearVelocity().y) > 0.5){
            cat.rotate(-10);
        }

        else if (catBody.getBody().getLinearVelocity().x < -0.75 && Math.abs(catBody.getBody().getLinearVelocity().y) > 0.5){
            cat.rotate(10);
        }

        /**If the cat is in the target basket, spawn new basket. */
        if (scored == true){
            for (int i =0; i<bodiesToDestroy.length; i++){
                world.destroyBody(bodiesToDestroy[i]);
            }
            //drawInitialBasket((int)(targetBasketBottom.getPosition().x * PPM), initialY);
			/*for (int i = 2; i < bodiesToDestroy.length; i++){
				world.destroyBody(bodiesToDestroy[i]);
			} */

            //basket2.remove();

            int[] randomPosition = randomizePosition(initialX, targetY);
            drawBasketShape(randomPosition[0], randomPosition[1]);

            //catBody.getFixture().setRestitution(0.5f);  do this in touchUp instead
            scored = false;
        }

        /**If the target basket is falling, continue to update the basket's position every frame */
        if (initialBasketBottom.getLinearVelocity().y != 0){
//            System.out.println("linear velocity not 0");
            cat.setRotation(0);
            //basket2.remove();
            //basket1.remove();
            basket2 = new BasketActor(this, this.targetBasketBottom.getPosition().x * 30, this.targetBasketBottom.getPosition().y * 30);
            basket1 = new BasketActor(this, this.getInitialBasketBottomPositionX() * 30, this.getInitialBasketBottomPositionY() * 30); //multiply by PPM cuz actor doesnt use PPM
        }
        /**If the upper basket moves the lower basket's spot, stop its velocity */
        if (initialBasketBottom.getPosition().y * PPM < initialY && initialBasketBottom.getLinearVelocity().y != 0){
//			System.out.println("basket reached initial position!");
			initialBasketBottom.setLinearVelocity(0,0);
			initialBasket.setLinearVelocity(0, 0);
            catBody.getFixture().setRestitution(0.5f);
            cat.setRotation(0);
            //catBody.getFixture().setFriction(0); //new


           // basket1.remove();
            basket1 = new BasketActor(this, this.getInitialBasketBottomPositionX() * 30, this.getInitialBasketBottomPositionY() * 30); //multiply by PPM cuz actor doesnt use PPM
//            System.out.println("Target basket position is: " + targetBasketBottom.getPosition().x * 30 + ", " + targetBasketBottom.getPosition().y *30);
            //basket2 = new BasketActor(this, this.targetBasketBottom.getPosition().x * 30, this.targetBasketBottom.getPosition().y * 30, true);

        }

		/** If the cat touched the ground, delete the cat and spawn a new cat where the lower basket is. */
		if (touchedGround == true){
            world.destroyBody(bodiesToDestroy[0]);

            BodyDef catBodyDef = new BodyDef();
            catBodyDef.type = BodyDef.BodyType.DynamicBody;
            catBodyDef.position.set(initialBasketBottom.getPosition().x, initialBasketBottom.getPosition().y);
            catBody = new CatBody(world, catBodyDef, cat);

            basket1.setSpriteRotation(0);
            cat.setRotation(0);
            touchedGround = false;

            gsm.set(new EndState(gsm));
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
//        System.out.println("angle of initial basket bottom is: " + initialBasketBottom.getAngle());
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

//        System.out.println("Drawing target basket at: " + x + ", " + y + ", after PPM adjustion is: " + x/PPM + ", " + y/PPM);

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
            position[0] = rand.nextInt(400)  + 600;
        }
        else if (x < 600) {
            position[0] = rand.nextInt(100) + 100;
        }
        else {
            position[0] = rand.nextInt(300) + 100;
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
