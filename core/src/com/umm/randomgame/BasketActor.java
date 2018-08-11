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
import com.badlogic.gdx.utils.Align;
import com.umm.randomgame.states.PlayState;

/**This class defines the basket sprite.
 * The basket is what the user drags to shoot the cat.
 * TODO: don't let the basket move to the exact position of the user touch, make it move a minimal amount but still show the direction of the drag*/
public class BasketActor extends Actor {

    /**Initializes global variables.*/

    private final PlayState game;
    private Sprite sprite = new Sprite(new Texture("basket.png"));

    private float newX; //newX and initialX should be the same
    private float newY;
    private float downX = 0;
    private float downY = 0;

    ///this makes sure certain event listeners only registers if the user touches within a certain range of the basket,
    //aka prevents the cat from flying extreme amounts due to the user dragging the basket across the screen */
    private boolean outOfRange = false;

    private float initialX;
    private float initialY;

    private float xAngle;
    private float yAngle;
    private float xDifference;
    private float yDifference;
    private float diagonal;

    /**Constructor that takes in the game class reference, and the world location of the basket*/
    public BasketActor(PlayState playState, float startingX, float startingY) {
        game = playState;
        initialX = startingX;
        initialY = startingY;
        newX = initialX - sprite.getWidth()/2; //adjust initial position by half the sprite's width/height to make up for body to sprite coordinate conversion
        newY = initialY - sprite.getHeight()/2;
        System.out.println("Starting position for basket is: " + initialX + ", " + initialY );

        setTouchable(Touchable.enabled); //allows the sprite to respond to touch events

        sprite.setPosition(newX, newY); //sets the sprite to the new adjusted position
        sprite.setScale(1f); //change this to resize the sprite if needed

        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight()); //sets the sprite boundaries
        System.out.println("bounds: " + sprite.getX() + ", " + sprite.getY() + ", width: " + sprite.getWidth());

        /**Event listener for touch events */
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            /**Takes in 3 parameters: x is the touch x coordinate, y is the touch y coordinate, pointer is useless */
            public boolean touchDragged (int x, int y, int pointer) {

                //this if statement restricts the drag so that the basket only moves within a certain range.
                //however, the drag will STILL TAKE PLACE AS NORMAL (based on touchDown/touchUp) to calculate the cat speed; the basket won't update it's position to reflect
                //how much the user dragged after a certain range
                if (Math.abs(x - initialX) < 100 && Math.abs(1800 - y - initialY) <100) {

                    //newX and newY are adjusted coordinates calculated to use for draw().
                    newX = x - sprite.getWidth() / 2;
                    newY = 1800 - y - sprite.getHeight() / 2;  //1800 - y because libGDX coordinate system and box2D coordinate system in the y-direction are reversed
                    System.out.println("NewY while dragging is: " + newY);

                    xDifference = x - initialX;
                    yDifference = 1800 - y - initialY;

                    float oppOverAdj = yDifference / xDifference;

                    xAngle = (float) Math.atan(oppOverAdj);
                    System.out.println("xDifference is: " + xDifference);
                    System.out.println("Ydifference is: " + yDifference);
                    System.out.println("xAngle is: " + xAngle);

                    /**This rotates the bottom portion of the basket based on its center (origin) */
                   /* if (xAngle > 0) {
                        game.getInitialBasket().setTransform(game.getInitialBasket().getPosition(), xAngle + 3 * (float) Math.PI / 2);
                    } else if (xAngle < 0) {
                        game.getInitialBasket().setTransform(game.getInitialBasket().getPosition(), xAngle + (float) Math.PI / 2);
                    }

                    //this is for collision detection purposes for now
                    game.getInitialBasketBottom().setTransform(game.getInitialBasketBottom().getPosition(), xAngle + (float) Math.PI / 2);

                    System.out.println("angle of initial basket bottom is: " + game.getInitialBasket().getAngle());
                    */
                }

                game.render(game.getSpriteBatch());

                return false;
            }

            /**This method simply keeps track of the where the user touched, and whether or not the touch is out of range */
            @Override
            public boolean touchDown (int x, int y, int pointer, int button){
                game.getCatBody().getFixture().setRestitution(0.5f);

                System.out.println("Coordinate of pressing down is: " + x + ", " + y);
                if (Math.abs(x - initialX) < 100 && Math.abs( 1800 - y - initialY) < 100) {
                    System.out.println("Coordinate of pressing down is: " + x + ", " + y);
                    //System.out.println("pointer is: " + pointer);
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

            /**Force is applied once the user releases their touch */
            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {

                //these are shadowed variables
                float xDifference;
                float yDifference;

                xDifference = x - downX;
                yDifference =  y - downY; //right equation!, not 1800 - y - downY

                System.out.println("RELEASE x, flSED: Coordinate of pressing UP is: " + x + ", " + y);
                System.out.println("pointer is: " + pointer);
                System.out.println("Difference between release and click is: " + xDifference + ", " + yDifference );

                /**If the user drag is within range, apply force based on the drag distance */
                System.out.println("body's linear velocity in y is: " +  game.getCatBody().getBody().getLinearVelocity().y);
                if (Math.abs(xDifference) < 300 && Math.abs(yDifference) < 400 && !outOfRange /*&& Math.abs(game.getCatBody().getBody().getLinearVelocity().y) < 1*/) {
                    Body body = game.getCatBody().getBody();
                    float mass = body.getMass(); //mass = density * area,  impulse / mass = velocity

                    //change these values to affect the force of the ball
                    float impulseX = -xDifference * 1.2f;
                    float impulseY = -yDifference * 1.5f;

                    //applies the force to the cat
                    game.getCatBody().getBody().applyLinearImpulse(impulseX, impulseY, game.getCatBody().getBody().getPosition().x,
                            game.getCatBody().getBody().getPosition().y, true);

                    game.render(game.getSpriteBatch()); //this must be called to update the dynamic shape!
                    System.out.println("impulse is: " + impulseX + ", " + impulseY);
                }
                else {
                    System.out.println("dragged too far");
                }

                newX = initialX - sprite.getWidth()/2;
                newY = initialY - sprite.getHeight()/2;

                System.out.println("newX and Y after touchUp is:"  + newX + " , " + newY);

                return false;
            }
        });
    }


    /*This method is called from Main Render so that the basket Actor can update its position */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(newX, newY);
        //sprite.setCenterX(newX);
        //sprite.setCenterY(newY);
        sprite.draw(batch);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

}
