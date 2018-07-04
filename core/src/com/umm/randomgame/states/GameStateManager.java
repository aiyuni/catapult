package com.umm.randomgame.states;

import java.util.Stack;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages the game's state, play/pause.
 * Created by Lena on 2018-06-27.
 */
public class GameStateManager {
    /**
     * Array of states.
     */
    private Stack<State> states;

    /**
     * Constructor, creates stack of states.
     */
    public GameStateManager() {
        states = new Stack<State>();
    }

    /**
     * Pushes a state to the top of the stack.
     * @param state being pushed to top
     */
    public void push(State state) {
        states.push(state);
    }

    /**
     * Takes nothing, removes state at the top of the stack.
     */
    public void pop() {
        states.pop();
    }

    /**
     * Removes state at top of stack and then adds state you're setting it to.
     * @param state state being pushed
     */
    public void set(State state) {
        states.pop();
        states.push(state);
    }

    /**
     * Looks at top object in stack, updates during change in time.
     * @param dt change in time between two renders
     */
    public void update(float dt) {
        states.peek().update(dt);
    }

    /**
     * Renders everything to the screen.
     *
     * @param sb spritebatch in use
     */
    public void render(SpriteBatch sb) {
        states.peek().render(sb);
    }
}
