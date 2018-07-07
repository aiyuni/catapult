package com.umm.randomgame.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.umm.randomgame.Main;

/**
 * Game States.
 * Created by Lena on 2018-06-27.
 */

public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse;
    protected GameStateManager gsm;
    protected Main main;
    /**Constructs a game state.**/
    public State(GameStateManager gsm) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    public State(GameStateManager gam, Main main){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
        this.main = main;

    }
    /** Place input handlers within this. **/
    public abstract void handleInput();
    /** Action taken when game updates, put handleInput() into this. **/
    public abstract void update(float dt);
    /** Images are drawn but you have to start the spritebatch and then end it when you're done. **/
    public abstract void render(SpriteBatch sb);
    /** Disposal of images used to reduce memory leaks after each state. **/
    public abstract void dispose();

}
