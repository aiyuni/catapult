package com.umm.randomgame;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;

public class CatActor extends Actor {

    Sprite sprite = new Sprite(new Texture("droplet.png"));
    int newX = 800;
    int newY = 800;

    public CatActor(){
        sprite.setX(500);
        sprite.setY(500);
        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        //System.out.println("bounds: " + sprite.getX() + ", " + sprite.getY() + ", width: " + sprite.getWidth());
        setTouchable(Touchable.enabled);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDragged (int x, int y, int pointer) {
                System.out.println("hello inside input processor");
                newX = x;
                newY = 1800 - y;


                return false;
            }

            @Override
            public boolean touchDown (int x, int y, int pointer, int button){

                System.out.println("Coordinate of pressing down is: " + x + ", " + y);
                System.out.println("pointer is: " + pointer);
                return true;
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                System.out.println("RELEA x, flSED: Coordinate of pressing up is: " + x + ", " + y);
                System.out.println("pointer is: " + pointer);


                return false;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setX(newX);
        sprite.setY(newY);
        sprite.draw(batch);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

}
