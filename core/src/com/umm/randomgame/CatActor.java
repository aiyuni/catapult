package com.umm.randomgame;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
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
    int newX = 500;
    int newY = 500;
    int downX = 0;
    int downY = 0;
    int releaseX;
    int releaseY;
    Main mainClassReference;
    boolean outOfRange = false;

    int initialX = 500;
    int initialY = 500;

    public CatActor(Main main) {
        mainClassReference = main;
        sprite.setX(initialX);
        sprite.setY(initialY);
        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        System.out.println("bounds: " + sprite.getX() + ", " + sprite.getY() + ", width: " + sprite.getWidth());
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
                if (Math.abs(x - initialX) < 100 && Math.abs( 1800 - y - initialY) < 100) {
                    //System.out.println("Coordinate of pressing down is: " + x + ", " + y);
                    System.out.println("pointer is: " + pointer);
                    downX = x;
                    downY = y;
                    outOfRange = false;
                    return true;
                }else {
                    System.out.println("touched too far");
                    outOfRange = true;
                    return false;
                }
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {

                float xDifference;
                float yDifference;

                xDifference = x - initialX;
                yDifference = 1800 - y - initialY;

                System.out.println("RELEA x, flSED: Coordinate of pressing up is: " + x + ", " + y);
                System.out.println("pointer is: " + pointer);
                System.out.println("Difference between release and click is: " + xDifference + ", " + yDifference );

                if (Math.abs(xDifference) < 300 && Math.abs(yDifference) < 400 && !outOfRange) {
                    Body body = mainClassReference.getBody();
                    float mass = body.getMass(); //mass = density * area,  impulse / mass = velocity
                    float impulseX = -xDifference * 0.2f;
                    float impulseY = -yDifference * 0.4f;


                    //mainClassReference.getBody().applyForceToCenter(500,500, true);
                    mainClassReference.getBody().applyLinearImpulse(impulseX, impulseY, mainClassReference.getBody().getPosition().x,
                            mainClassReference.getBody().getPosition().y, true);

                    mainClassReference.render(); //call this to update the dynamic shape!
                    System.out.println("impulse is: " + impulseX + ", " + impulseY);
                }
                else {
                    System.out.println("dragged too far");
                }

                newX = initialX;
                newY = initialY;

                return false;
            }
        });
    }

    /*This method is called from Main Render so that the Actor can update its position */
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
