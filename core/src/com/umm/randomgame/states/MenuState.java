package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.umm.randomgame.Main;

/**
 * Menu screen for the game.
 * Created by Lena on 2018-06-27.
 */

public class MenuState extends State {
    private Texture background;
    private Texture playButton;
    private Texture gameTitle;
    public MenuState(GameStateManager gsm){
        super(gsm);
        background = new Texture("background.png");
        playButton = new Texture ("playbutton.png");
        gameTitle = new Texture ("title.png");
    }
    @Override
    public void handleInput() {

if(Gdx.input.isTouched()){
gsm.set(new PlayState(gsm, main));
dispose();
}


    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0,0, Main.WIDTH, Main.HEIGHT);
        sb.draw(gameTitle, ((Main.WIDTH/2)- (gameTitle.getWidth()/2)), (Main.HEIGHT/3*2));
        sb.draw(playButton, (Main.WIDTH/2) - (playButton.getWidth()/2), (Main.HEIGHT/2));
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        gameTitle.dispose();
        playButton.dispose();
    }
}
