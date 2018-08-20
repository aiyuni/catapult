package com.umm.randomgame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    private Label textLabel;
    private Label.LabelStyle labelStyle;

private BitmapFont font;
private String text;

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
        pressPlay.setPosition(((Main.WIDTH/2) - (realPlayButton.getWidth()/2)), (Main.HEIGHT/2));
        labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        textLabel = new Label("Catapault", labelStyle);
        stage.addActor(textLabel);
        textLabel.setPosition(((Main.WIDTH/2)- (textLabel.getWidth()/2)), (Main.HEIGHT/3*2));
        Gdx.input.setInputProcessor(stage);



    }
    /*
    * Handles input so that if the button is pressed it will send the user to the play screen
    *
    */
    @Override
    public void handleInput() {
        pressPlay.addListener(new ClickListener(){
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
        sb.begin();
        sb.draw(background, 0,0, Main.WIDTH, Main.HEIGHT);
//        font.draw(sb, text, Main.WIDTH/2,(Main.HEIGHT/2*3));
// sb.draw(playButton, ((Main.WIDTH/2)- (gameTitle.getWidth()/2)), (Main.HEIGHT/3*2));
 //sb.draw(playButton, (Main.WIDTH/2) - (playButton.getWidth()/2), (Main.HEIGHT/2));
        sb.end();


       stage.draw();


    }

    @Override
    public void dispose() {
        background.dispose();
        gameTitle.dispose();
        playButton.dispose();
    }
}
