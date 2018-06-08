package com.umm.randomgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class Main extends ApplicationAdapter implements GestureDetector.GestureListener{
	SpriteBatch batch;
	Texture img;

	private Texture dropImage;
	private Texture bucketImage;
	private Rectangle bucket;
	private OrthographicCamera camera;

	World world;
	Box2DDebugRenderer debugRenderer;

	Stage stage;
	CatActor cat;

	@Override
	public void create () {

		ScreenViewport viewport = new ScreenViewport();
		stage = new Stage(viewport);

		Gdx.input.setInputProcessor(stage);

		cat = new CatActor();
		stage.addActor(cat);
		//stage.setKeyboardFocus(actor);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		dropImage = new Texture("droplet.png");
		bucketImage = new Texture("bucket.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 1800);

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 100 / 2;
		bucket.y = 20;
		bucket.width = 100;
		bucket.height = 100;

		Box2D.init();

		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();

		// Create our body definition
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyDef.BodyType.KinematicBody;
// Set its world position
		groundBodyDef.position.set(new Vector2(100, 200));

// Create a body from the defintion and add it to the world
		Body groundBody = world.createBody(groundBodyDef);

// Create a polygon shape
		PolygonShape groundBox = new PolygonShape();
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
		groundBox.setAsBox(camera.viewportWidth, 10.0f);
// Create a fixture from our polygon shape and add it to our ground body
		groundBody.createFixture(groundBox, 0.0f);
// Clean up after ourselves
		groundBox.dispose();


		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
		bodyDef.type = BodyDef.BodyType.DynamicBody;
// Set our body's starting position in the world
		bodyDef.position.set(200, 1000);

// Create our body in the world using our body definition
		Body body = world.createBody(bodyDef);

// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(30f);

// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f; // Make it bounce a little bit

// Create our fixture and attach it to the body
		Fixture fixture = body.createFixture(fixtureDef);

// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
		circle.dispose();


	}

	@Override
	public void render () {

		//this is for handling advanced events
		//Gdx.input.setInputProcessor(new GestureDetector(this));

		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();



		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		cat.draw(batch, 0);
		batch.end();

		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			//camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
			System.out.println("bucket is at : " + touchPos.x + ", " + touchPos.y);
			bucket.y = 1800 - touchPos.y - 20; //screen is 1800 pixel tall
			//bucket.y = touchPos.y;
		}
		debugRenderer.render(world, camera.combined);

		world.step(1/60f, 6, 2);


	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {

		System.out.println("flinged!!!!!!");
		System.out.println("velocity: " + velocityX + velocityY);
		System.out.println("button: " + button);
		return false;
	}


	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}



	////ADVANCED EVENT HANDLERS

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {

		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {

		return false;
	}

	@Override
	public boolean longPress(float x, float y) {

		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {

		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {

		return false;
	}

	@Override
	public boolean zoom (float originalDistance, float currentDistance){

		return false;
	}

	@Override
	public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){

		return false;
	}
	@Override
	public void pinchStop () {
	}


}
