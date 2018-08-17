package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.umm.randomgame.Main;

/**
 * Menu screen for the game.
 * Created by Lena on 2018-06-27.
 */

public class MenuState extends State {
    private Texture background;
    private Texture playButton;
    private Texture gameTitle;
    private ImageButton pressPlay;
    private TextureRegion myTextureRegion;
    private TextureRegionDrawable drawableRegion;
    private Stage stage;
    private Texture realPlayButton;

    public MenuState(GameStateManager gsm){
        super(gsm);
        background = new Texture("background.png");
        playButton = new Texture ("playbutton.png");
        gameTitle = new Texture ("title.png");
        realPlayButton= new Texture("playButton2.png");
        myTextureRegion = new TextureRegion(realPlayButton);
        drawableRegion = new TextureRegionDrawable(myTextureRegion);
        pressPlay = new ImageButton(drawableRegion);
        stage = new Stage(new ScreenViewport());
        stage.addActor(pressPlay);
        Gdx.input.setInputProcessor(stage);

    }
    @Override
    public void handleInput() {
pressPlay.addListener(new EventListener(){
    @Override
    public boolean handle (Event event){
        gsm.set(new PlayState(gsm, main));
        dispose();
        return false;
    }

});
//        if(Gdx.input.isTouched()){
//        gsm.set(new PlayState(gsm, main)    );
//        dispose();
    }




    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
//        sb.begin();
//        sb.draw(background, 0,0, Main.WIDTH, Main.HEIGHT);
//        sb.draw(gameTitle, ((Main.WIDTH/2)- (gameTitle.getWidth()/2)), (Main.HEIGHT/3*2));
//        sb.draw(playButton, (Main.WIDTH/2) - (playButton.getWidth()/2), (Main.HEIGHT/2));
//        sb.end();

    }

    @Override
    public void dispose() {
        background.dispose();
        gameTitle.dispose();
        playButton.dispose();
    }
}
