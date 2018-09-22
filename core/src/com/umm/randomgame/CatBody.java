package com.umm.randomgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;


/**Defines the Cat Body. */
public class CatBody extends Actor{

    public static final int PPM = 30;

    private Body catBody;
    private World world;
    private BodyDef bodyDef;
    private Fixture fixture;
    private CircleShape circle;
    private Sprite cat;

    /**Constructs the cat body using its bodyDef parameter. */
    public CatBody(World world, BodyDef bodyDef, Sprite cat) {

        this.world = world;
        this.bodyDef = bodyDef;
        this.cat = cat;

        catBody = world.createBody(bodyDef);

        circle = new CircleShape();
        circle.setRadius(30f/PPM);

        // Create a fixture definition to describe the cat.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.5f; // this affects the cat's bounce

        fixture = catBody.createFixture(fixtureDef);

        circle.dispose();

    }

    public float getX(){

        return catBody.getPosition().x;

    }

    public float getY(){
        return catBody.getPosition().y;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public Body getBody() {
        return catBody;
    }


}

