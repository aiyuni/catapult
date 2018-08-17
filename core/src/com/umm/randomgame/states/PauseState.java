package com.umm.randomgame.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.umm.randomgame.Main;

/**
 * Pause state for catapault.
 * Created by Lena on 2018-08-07.
 */

public class PauseState extends State {
    /*
    * Background image for pause state.
    * */
    private Texture background;
    /*Icon for continue game*/
    private Texture continueButton;
    /*Menu button to go back to main*/
    private Texture menuButton;
    public PauseState(GameStateManager gsm){
        super(gsm);
        background = new Texture("pauseTest.png");
    }
    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Main.HEIGHT, Main.WIDTH);
        sb.end();

    }

    @Override
    public void dispose() {

    }
}
