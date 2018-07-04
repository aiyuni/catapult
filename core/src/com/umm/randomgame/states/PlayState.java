package com.umm.randomgame.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This should be the playstate. The previous one wouldn't work so I'm currently trying to move your code from Main to play.
 * Created by Lena on 2018-07-04.
 */

public class PlayState extends State{
    private Texture test;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        test= new Texture("test.png");

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
        sb.draw(test, 0, 0);
sb.end();

    }

    @Override
    public void dispose() {

    }
}
