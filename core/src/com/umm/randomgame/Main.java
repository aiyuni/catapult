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


/** This is the class that gets called when the app starts.
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

	/**Initializes the sprite batch and stage objects*/
	private SpriteBatch batch;
	private Stage stage;

	@Override
	public void create () {

		ScreenViewport viewport = new ScreenViewport();  //sets up the display
		stage = new Stage(viewport);

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

	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}
