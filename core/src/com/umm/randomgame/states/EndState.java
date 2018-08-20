package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Collections;

/**
 * Created by Lena on 2018-07-07.
 */

public class EndState extends State {
    private Texture background;
    private Texture newGameTexture;
    private Sprite newGameSprite;

    private int score;
    private String scoreString;
    BitmapFont scoreDisplay;

    private static Preferences prefs = Gdx.app.getPreferences("HighScores");

    public EndState(GameStateManager gsm) {
        super(gsm);
        background = new Texture("background.png");
        newGameTexture= new Texture ("shiny.png");
        newGameSprite = new Sprite(newGameTexture);
        this.create();


    }


    @Override
    public void handleInput() {

        //Restart the game for now if you touch anywhere
        if (Gdx.input.isTouched()) {
            gsm.set(new PlayState(gsm, main));
            System.out.println("Touched whole screen");
            dispose();
        }
    }


    public void create(){
        score = prefs.getInteger("MaxScore");
        System.out.println("Your Max Score is: " + score);
        scoreString = "Score: " + score;
        scoreDisplay = new BitmapFont();
        scoreDisplay.getData().setScale(5);

        newGameSprite.setBounds(500, 500, 300, 300);

        Gdx.input.setInputProcessor(new InputAdapter(){

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                //testing stuff
                if(newGameSprite.getBoundingRectangle().contains(screenX, screenY))
                    System.out.println("Image Clicked");

                return true;
            }

        });
    }


    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        scoreDisplay.setColor(0.5f, 0.7f, 0.1f, 1.0f);
        scoreDisplay.draw(sb, scoreString, 100, 1600);
        newGameSprite.draw(sb);
       // System.out.println("inside endstate!");
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
        newGameTexture.dispose();
        background.dispose();
    }
}
