package com.umm.randomgame.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Lena on 2018-07-07.
 */

public class EndState extends State {
    private Texture background;
    private Texture fallingCat;
    public EndState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("insertBackground");
        fallingCat = new Texture ("insertCatImage");
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
//draw Textures and Sprites here
        //need image for background
        //need image for cat with parachute?
        //show score
        //submit score button (updates to database)
        //menu button
        sb.end();
    }

    @Override
    public void dispose() {
        fallingCat.dispose();
        background.dispose();
    }
}
