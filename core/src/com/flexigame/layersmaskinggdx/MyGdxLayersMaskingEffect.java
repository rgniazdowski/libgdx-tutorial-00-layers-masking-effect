package com.flexigame.layersmaskinggdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.flexigame.fg.gfx.LayerInfo;
import com.flexigame.fg.gfx.LayerMaskingEffect;
import com.flexigame.fg.gfx.LayerMaskingEffectDrawer;

public class MyGdxLayersMaskingEffect extends ApplicationAdapter implements InputProcessor {
    protected static final String APP_NAME_ID = "LayersMaskingEffect";
    protected static final String APP_TITLE = "Layers Masking Effect X";
    public static final int BASE_SCREEN_WIDTH = 720;
    public static final int BASE_SCREEN_HEIGHT = 1280;

    public int getBaseScreenWidth() {
        return BASE_SCREEN_WIDTH;
    }

    public int getBaseScreenHeight() {
        return BASE_SCREEN_HEIGHT;
    }

    public float getScreenScaleX() {
        return ((float) this.getWidth()) / ((float) BASE_SCREEN_WIDTH);
    }

    public float getScreenScaleY() {
        return ((float) this.getHeight()) / ((float) BASE_SCREEN_HEIGHT);
    }

    //-------------------------------------------------------------------------

    public int getWidth() {
        return Gdx.app.getGraphics().getWidth();
    }

    public int getHeight() {
        return Gdx.app.getGraphics().getHeight();
    }

    //-------------------------------------------------------------------------

    public static final int NUM_LAYER_EFFECTS = 7;

    protected AssetManager assetManager;
    LayerMaskingEffectDrawer layersDrawer;
    LayerMaskingEffect[] layerEffects;
    SpriteBatch spriteBatch;
    Camera camera;
    Viewport viewport;

    float offsetValue = -1.0f;
    float offsetRatio = 0.0f;
    float offsetDirection = 1.0f;
    float offsetSpeed = 0.25f;

    boolean shouldOffset = true;

    int CURRENT_EFFECT_ID = 0;

    Interpolation interpolation = Interpolation.sine;

    int SELECTED_LAYER_ID = 0;

    public void nextLayerEffect() {
        CURRENT_EFFECT_ID++;
        if (CURRENT_EFFECT_ID >= NUM_LAYER_EFFECTS)
            CURRENT_EFFECT_ID = 0;
        this.layersDrawer.showOnly(CURRENT_EFFECT_ID);
        SELECTED_LAYER_ID = 0;
    }

    public void previousLayerEffect() {
        CURRENT_EFFECT_ID--;
        if (CURRENT_EFFECT_ID < 0) {
            CURRENT_EFFECT_ID = NUM_LAYER_EFFECTS - 1;
        }
        this.layersDrawer.showOnly(CURRENT_EFFECT_ID);
        SELECTED_LAYER_ID = 0;
    }

    //-------------------------------------------------------------------------

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.debug(APP_NAME_ID, "Initializing...");
        layerEffects = new LayerMaskingEffect[10];
        for (int i = 0; i < 10; i++)
            layerEffects[i] = null;

        assetManager = new AssetManager();
        Texture.setAssetManager(assetManager);

        TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
        params.genMipMaps = false;
        params.magFilter = Texture.TextureFilter.Linear;
        params.minFilter = Texture.TextureFilter.Linear;
        params.wrapU = Texture.TextureWrap.MirroredRepeat;
        params.wrapV = Texture.TextureWrap.MirroredRepeat;

        assetManager.load("white.tga", Texture.class); // just white pixels (easier to use)
        assetManager.load("black.tga", Texture.class); // just black pixels

        layersDrawer = new LayerMaskingEffectDrawer(assetManager);

        for (int i = 0; i < NUM_LAYER_EFFECTS; i++) {
            int index = i + 1;
            layerEffects[i] = layersDrawer.createLayerEffect("LayerEffect" + index);
            layerEffects[i].hide();
        }

        spriteBatch = layersDrawer.getSpriteBatch();
        viewport = new ScalingViewport(Scaling.stretch,
                this.getBaseScreenWidth(), getBaseScreenHeight(),
                new OrthographicCamera());
        viewport.update(getWidth(), getHeight(), true);
        camera = viewport.getCamera();

        loadTextures(params);

        initializeLayers();

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
    } // void create()

    public void loadTextures(TextureLoader.TextureParameter params) {
        assetManager.load("layers/2_512.jpg", Texture.class, params);
        assetManager.load("layers/3_512.jpg", Texture.class, params);
        assetManager.load("layers/4_512.jpg", Texture.class, params);

        assetManager.load("layers/6_512.tga", Texture.class, params);
        assetManager.load("layers/7_512.tga", Texture.class, params);
        assetManager.load("layers/8_512.tga", Texture.class, params);
        assetManager.load("layers/9_512.tga", Texture.class, params);

        assetManager.load("layers/10_512.tga", Texture.class, params);
        assetManager.load("layers/11_512.tga", Texture.class, params);
        assetManager.load("layers/12_512.tga", Texture.class, params);

        assetManager.load("layers/13_512.tga", Texture.class, params);
        assetManager.load("layers/14_512.tga", Texture.class, params);

        assetManager.load("layers/15_1024.tga", Texture.class, params);
        assetManager.load("layers/16_1024.tga", Texture.class, params);
        assetManager.load("layers/17_1024.tga", Texture.class, params);

        assetManager.load("masks/text_0.tga", Texture.class, params);
        assetManager.load("masks/earth_0.tga", Texture.class, params);
        assetManager.load("masks/lion_0.tga", Texture.class, params);
        assetManager.load("masks/lion_1.tga", Texture.class, params);

        assetManager.load("masks/line_h_0.tga", Texture.class, params);
        assetManager.load("masks/line_h_1.tga", Texture.class, params);
        assetManager.load("masks/line_h_2.tga", Texture.class, params);

        assetManager.load("masks/ring_0.tga", Texture.class, params);
        assetManager.load("masks/ring_1.tga", Texture.class, params);
        assetManager.load("masks/ring_2.tga", Texture.class, params);
        assetManager.load("masks/ring_3.tga", Texture.class, params);

        assetManager.load("masks/triangle_0.tga", Texture.class, params);
        assetManager.load("masks/triangle_1.tga", Texture.class, params);

        assetManager.finishLoading(); // this will block the screen!

        Gdx.app.debug(APP_NAME_ID, "Finished loading all assets!");
    }

    public void initializeLayers() {
        {
            LayerInfo layerInfo = layerEffects[0].addLayer("layers/3_512.jpg", "masks/line_h_0.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = false;
            layerInfo.speed = 0.50f;

            //layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 1.0f;
            layerInfo.maxScale = 1.0f;
        }

        {
            LayerInfo layerInfo = layerEffects[0].addLayer("layers/3_512.jpg", "masks/line_h_1.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = false;
            layerInfo.speed = 0.50f;

            //layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 1.0f;
            layerInfo.maxScale = 1.0f;
        }

        {
            LayerInfo layerInfo = layerEffects[0].addLayer("layers/3_512.jpg", "masks/line_h_2.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = false;
            layerInfo.speed = 0.50f;

            //layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 1.0f;
            layerInfo.maxScale = 1.0f;
        }

        //-------------------------------------------------------------

        {
            LayerInfo layerInfo = layerEffects[1].addLayer("layers/6_512.tga", "masks/ring_0.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.30f;
            layerInfo.rotationSpeed = -10.0f;

            layerInfo.scaleMode = LayerInfo.Mode.BOTH;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 1.0f;
            layerInfo.maxScale = 1.5f;
        }
        {
            LayerInfo layerInfo = layerEffects[1].addLayer("layers/7_512.tga", "masks/ring_1.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.BOTH;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 0.75f;
            layerInfo.maxScale = 1.15f;
        }
        {
            LayerInfo layerInfo = layerEffects[1].addLayer("layers/8_512.tga", "masks/ring_2.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.30f;
            layerInfo.rotationSpeed = -10.0f;

            layerInfo.scaleMode = LayerInfo.Mode.BOTH;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 0.5f;
            layerInfo.maxScale = 1.25f;
        }
        {
            LayerInfo layerInfo = layerEffects[1].addLayer("layers/9_512.tga", "masks/ring_3.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.BOTH;
            layerInfo.offsetMode = LayerInfo.Mode.BOTH;

            layerInfo.minScale = 0.25f;
            layerInfo.maxScale = 1.0f;
        }

        //-------------------------------------------------------------

        {
            LayerInfo layerInfo = layerEffects[2].addLayer("layers/11_512.tga", "masks/text_0.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            //layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.MASK_ONLY;

            layerInfo.minScale = 0.5f;
            layerInfo.maxScale = 2.5f;
        }

        //-------------------------------------------------------------

        {
            LayerInfo layerInfo = layerEffects[3].addLayer("layers/14_512.tga", "masks/earth_0.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth() / 2.0f;
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            //layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.MASK_ONLY;

            layerInfo.minScale = 0.5f;
            layerInfo.maxScale = 2.0f;
        }

        //-------------------------------------------------------------

        {
            LayerInfo layerInfo = layerEffects[4].addLayer("layers/14_512.tga", "masks/lion_0.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            //layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.MASK_ONLY;

            layerInfo.minScale = 0.5f;
            layerInfo.maxScale = 2.0f;
        }

        //-------------------------------------------------------------

        {
            LayerInfo layerInfo = layerEffects[5].addLayer("layers/14_512.tga", "masks/lion_1.tga");
            layerInfo.visible = true;
            layerInfo.position.x = getBaseScreenWidth() / 2;
            layerInfo.position.y = getBaseScreenHeight() / 2;

            layerInfo.size.x = getBaseScreenWidth();
            layerInfo.size.y = getBaseScreenWidth();
            layerInfo.shouldPulse = true;
            layerInfo.speed = 0.50f;
            //layerInfo.rotationSpeed = 16.0f;

            layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
            layerInfo.offsetMode = LayerInfo.Mode.MASK_ONLY;

            layerInfo.minScale = 0.5f;
            layerInfo.maxScale = 2.0f;
        }

        //-------------------------------------------------------------

        {
            float _centerX = getBaseScreenWidth() / 2;
            float _centerY = getBaseScreenHeight() / 2;
            float _width = getBaseScreenWidth();
            float _height = getBaseScreenHeight();
            float mainTriangleSize = _width / 1.75f;
            //Layer [0]: Position: [360;640]  | Relative: [0   ;0]
            //Layer [1]: Position: [161;943]  | Relative: [-20 ;300]
            //Layer [2]: Position: [281;914]  | Relative: [-80 ;275]
            //Layer [3]: Position: [348;900]  | Relative: [-10 ;260]
            //Layer [4]: Position: [325;1057] | Relative: [-35 ;420]
            //Layer [5]: Position: [478;997]  | Relative: [120 ;360]
            //Layer [6]: Position: [571;790]  | Relative: [210 ;150]
            //Layer [7]: Position: [211;669]  | Relative: [-150;30]
            int[][] _offsets = {
                    {0, 0},
                    {-200, 300},
                    {-80, 275},
                    {-10, 260},
                    {-35, 420},
                    {120, 360},
                    {210, 150},
                    {-150, 30}};
            {
                int _lid = 0;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = mainTriangleSize;
                layerInfo.size.y = mainTriangleSize;
                layerInfo.rotation = 180.0f;
            } // [0] flipped big triangle
            {
                int _lid = 1;
                float _triangleSize = _width / 3.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_1.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize;
                layerInfo.size.y = _triangleSize;
                layerInfo.rotation = 0.0f;
            } // [1] top left medium triangle
            {
                int _lid = 2;
                float _triangleSize = _width / 4.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize;
                layerInfo.size.y = _triangleSize;
                layerInfo.rotation = 180.0f;
            } // [2] top left smaller flipped triangle
            {
                int _lid = 3;
                float _triangleSize = _width / 7.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize;
                layerInfo.size.y = _triangleSize;
                layerInfo.rotation = 0.0f;
            } // [3] top center small triangle
            {
                int _lid = 4;
                float _triangleSize = _width / 8.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize;
                layerInfo.size.y = _triangleSize;
                layerInfo.rotation = 0.0f;
            } // [4] top center smaller triangle
            {
                int _lid = 5;
                float _triangleSize = _width / 6.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize;
                layerInfo.size.y = _triangleSize;
                layerInfo.rotation = 180.0f;
            } // [5] top right smaller triangle flipped
            {
                int _lid = 6;
                float _triangleSize = _width / 5.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_1.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize * 0.8f;
                layerInfo.size.y = _triangleSize * 0.8f;
                layerInfo.rotation = 0.0f;
            } // [6] bottom right smaller triangle
            {
                int _lid = 7;
                float _triangleSize = _width / 4.0f;
                LayerInfo layerInfo = layerEffects[6].addLayer("layers/16_1024.tga",
                        "masks/triangle_0.tga");
                layerInfo.position.x = _centerX + _offsets[_lid][0];
                layerInfo.position.y = _centerY + _offsets[_lid][1];

                layerInfo.size.x = _triangleSize * 0.8f;
                layerInfo.size.y = _triangleSize * 0.8f;
                layerInfo.rotation = 0.0f;
            } // [7] bottom left small triangle
            {
                for (int i = 0; i < layerEffects[6].count(); i++) {
                    LayerInfo layerInfo = layerEffects[6].get(i);
                    layerInfo.shouldPulse = false;
                    layerInfo.speed = 0.0f;
                    layerInfo.visible = true;
                    layerInfo.scaleMode = LayerInfo.Mode.MASK_ONLY;
                    layerInfo.offsetMode = LayerInfo.Mode.MASK_ONLY;
                    layerInfo.setScale(1.0f);
                }
            }
        } // Triangles effect configuration [6]

        Gdx.app.debug(APP_NAME_ID, "Finished initializing all layers!");

        this.layersDrawer.showOnly(CURRENT_EFFECT_ID);
    } // void initializeLayers()

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.35f, 0.4f, 0.4f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        final float delta = Gdx.app.getGraphics().getDeltaTime();

        if (shouldOffset)
            offsetRatio += offsetDirection * offsetSpeed * delta;
        if (offsetRatio > 1.0f) {
            offsetDirection = -1.0f;
            offsetRatio = 1.0f;
        } else if (offsetRatio < 0.0f) {
            offsetDirection = 1.0f;
            offsetRatio = 0.0f;
            //shouldOffset = false;
        }
        offsetValue = interpolation.apply(-1.0f, 1.0f, offsetRatio);

        if (CURRENT_EFFECT_ID == 0) {
            this.layerEffects[0].get(0).setOffset(0, getWidth() * offsetValue / 2.0f);
            this.layerEffects[0].get(1).setOffset(0, -getWidth() * offsetValue / 1.75f);
            this.layerEffects[0].get(2).setOffset(0, getWidth() * offsetValue / 3.5f);
            //shmup.testLayerEffect.setOffset(0, getWidth() * offsetValue / 2.0f);
        }

        layersDrawer.update(delta);
        layersDrawer.draw();

        if (isKeyPressed(Input.Keys.LEFT)) {
            LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
            if (effect != null)
                effect.moveOffsetByPixels(-192.0f * delta, 0.0f);
        }
        if (isKeyPressed(Input.Keys.RIGHT)) {
            LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
            if (effect != null)
                effect.moveOffsetByPixels(192.0f * delta, 0.0f);
        }
        if (isKeyPressed(Input.Keys.UP)) {
            LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
            if (effect != null)
                effect.moveOffsetByPixels(0.0f, -192.0f * delta);
        }
        if (isKeyPressed(Input.Keys.DOWN)) {
            LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
            if (effect != null)
                effect.moveOffsetByPixels(0.0f, 192.0f * delta);
        }

        {
            float _speed = 100.0f;
            if (isKeyPressed(Input.Keys.W)) {
                LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
                if (effect != null) {
                    if (effect.getLayerInfoArray().size > SELECTED_LAYER_ID)
                        effect.get(SELECTED_LAYER_ID).position.y += _speed * delta;
                }
            }
            if (isKeyPressed(Input.Keys.S)) {
                LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
                if (effect != null) {
                    if (effect.getLayerInfoArray().size > SELECTED_LAYER_ID)
                        effect.get(SELECTED_LAYER_ID).position.y -= _speed * delta;
                }
            }
            if (isKeyPressed(Input.Keys.A)) {
                LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
                if (effect != null) {
                    if (effect.getLayerInfoArray().size > SELECTED_LAYER_ID)
                        effect.get(SELECTED_LAYER_ID).position.x -= _speed * delta;
                }
            }
            if (isKeyPressed(Input.Keys.D)) {
                LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
                if (effect != null) {
                    if (effect.getLayerInfoArray().size > SELECTED_LAYER_ID)
                        effect.get(SELECTED_LAYER_ID).position.x += _speed * delta;
                }
            }
        }
    } // void render()

    @Override
    public void dispose() {
        layersDrawer.dispose();
        assetManager.dispose();
    } // void dispose()

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(getWidth(), getHeight(), true);
    }

    //-------------------------------------------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.Z) {
            previousLayerEffect();
        }
        if (keycode == Input.Keys.X) {
            nextLayerEffect();
        }
        if (keycode == Input.Keys.COMMA) {
            //LayerMaskingEffect effect = shmup.layerEffects[CURRENT_EFFECT_ID];
            SELECTED_LAYER_ID--;
            if (SELECTED_LAYER_ID < 0)
                SELECTED_LAYER_ID = 0;
            Gdx.app.debug(APP_NAME_ID, "Selected layer id: " + SELECTED_LAYER_ID);
        }
        if (keycode == Input.Keys.PERIOD) {
            LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
            SELECTED_LAYER_ID++;
            if (SELECTED_LAYER_ID >= effect.getLayerInfoArray().size)
                SELECTED_LAYER_ID = effect.getLayerInfoArray().size - 1;
            Gdx.app.debug(APP_NAME_ID, "Selected layer id: " + SELECTED_LAYER_ID);
        }
        return false;
    } // boolean keyDown(...)

    @Override
    public boolean keyUp(int keycode) {
        return false;
    } // boolean keyUp(...)

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    //-------------------------------------------------------------------------

    public boolean isKeyPressed(int keycode) {
        return Gdx.input.isKeyPressed(keycode);
    }

    public boolean isKeyPressed(String keyname) {
        final int keycode = Input.Keys.valueOf(keyname);
        return Gdx.input.isKeyPressed(keycode);
    }

    public boolean isKeyJustPressed(int keycode) {
        return Gdx.input.isKeyJustPressed(keycode);
    }

    public boolean isKeyJustPressed(String keyname) {
        final int keycode = Input.Keys.valueOf(keyname);
        return Gdx.input.isKeyJustPressed(keycode);
    }

    //-------------------------------------------------------------------------

    boolean wasDragged = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        wasDragged = false;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!wasDragged)
            nextLayerEffect();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        wasDragged = true;
        LayerMaskingEffect effect = this.layerEffects[CURRENT_EFFECT_ID];
        if (effect != null)
            effect.moveOffsetByPixels(Gdx.input.getDeltaX(pointer), Gdx.input.getDeltaY(pointer));
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //-------------------------------------------------------------------------
} // class MyGdxLayersMaskingEffect
