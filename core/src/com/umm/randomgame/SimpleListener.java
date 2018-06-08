package com.umm.randomgame;

import com.badlogic.gdx.InputProcessor;

import sun.rmi.runtime.Log;

public class SimpleListener implements InputProcessor {
    public boolean keyDown (int keycode) {
        return false;
    }

    public boolean keyUp (int keycode) {
        return false;
    }

    public boolean keyTyped (char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button) {
        System.out.println("hello you pressed down");
        return false;
    }

    public boolean touchUp (int x, int y, int pointer, int button) {
        System.out.println("hello you pressed up");
        return false;
    }

    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    public boolean mouseMoved (int x, int y) {
        System.out.println("hello mouse moved");
        return false;
    }

    public boolean scrolled (int amount) {
        return false;
    }
}
