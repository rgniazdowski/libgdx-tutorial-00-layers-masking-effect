package com.flexigame.fg.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * This class is for rendering special layer effects (with texture masking).
 * Uses custom shader. Sprite batch will flush quite often because of second texture unit.
 *
 * This needs LayerMaskingEffect instances to draw something. LayerMaskingEffect
 * describes internal parameters of the effect.
 */
public class LayerMaskingEffectDrawer {
    private SpriteBatch spriteBatch;
    private ShaderProgram customShaderProgram;
    private AssetManager assetManager;

    private Array<LayerMaskingEffect> layerMaskingEffects;

    private String vertexShader;
    private String fragmentShader;

    //-------------------------------------------------------------------------

    public LayerMaskingEffectDrawer(AssetManager assetManager) {
        if (assetManager == null)
            throw new RuntimeException("AssetManager passed into constructor cannot be NULL!");
        this.assetManager = assetManager;

        this.spriteBatch = new SpriteBatch(); // important!

        this.layerMaskingEffects = new Array<LayerMaskingEffect>();

        // Create custom shader program - important!
        this.vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                + "uniform mat4 u_projTrans;\n"
                + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n"
                + "\n"
                + "void main()\n"
                + "{\n"
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                + "   v_color.a = v_color.a * (255.0/254.0);\n"
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                + "}\n";

        // -1 * (sc - 1.0)/2.0 + (sc) * uv
        // " vec2 newCoords = -1.0 * (u_maskScale - 1.0)/2.0 + (u_maskScale * v_texCoords);\n" +

        this.fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform sampler2D u_texture2;\n" +
                "uniform float u_maskScale;\n" +
                "uniform float u_useMask;\n" +
                "uniform vec2 u_maskOffset;\n" +
                "void main()                                  \n" +
                "{                                            \n" +
                " vec2 newCoords = -1.0 * (u_maskScale - 1.0)/2.0 + (u_maskScale * v_texCoords) + u_maskOffset;\n" +
                " vec4 mask = vec4(1.0, 1.0, 1.0, 1.0); \nif(u_useMask > 0.5) \n\tmask = texture2D(u_texture2, v_texCoords);\n" +
                " vec4 color = v_color * texture2D(u_texture, newCoords);\n" +
                "  gl_FragColor = vec4(color.rgb, color.a * mask.r);\n" +
                "}";
        if (this.customShaderProgram == null)
            customShaderProgram = new ShaderProgram(vertexShader, fragmentShader);

        if (!customShaderProgram.isCompiled()) {
            Gdx.app.debug("LayerMaskingEffectDrawer", "Custom shader did not compile:\n " + customShaderProgram.getLog());
        } else {
            Gdx.app.debug("LayerMaskingEffectDrawer", "Custom shader compiled successfully. " + customShaderProgram.getLog());
        }

        if (this.customShaderProgram.isCompiled()) {
            customShaderProgram.begin();
            customShaderProgram.setUniformi("u_texture", 0);
            customShaderProgram.setUniformi("u_texture2", 1);
            customShaderProgram.setUniformf("u_useMask", 1.0f);
            customShaderProgram.setUniformf("u_maskScale", 1.0f);
            customShaderProgram.setUniformf("u_maskOffset", 0.0f, 0.0f);
            this.spriteBatch.setShader(customShaderProgram); // use custom shader !
        }

    } // LayerMaskingEffectDrawer(...)

    public void dispose() {
        this.spriteBatch.setShader(null);
        this.spriteBatch.dispose();
        this.customShaderProgram.dispose();
        final int n = 0;
        for (int i = 0; i < n; i++)
            this.layerMaskingEffects.items[i].dispose();
        this.layerMaskingEffects.clear();
    }

    //-------------------------------------------------------------------------

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public ShaderProgram getCustomShaderProgram() {
        return customShaderProgram;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public String getVertexShader() {
        return vertexShader;
    }

    public String getFragmentShader() {
        return fragmentShader;
    }

    //-------------------------------------------------------------------------

    public Array<LayerMaskingEffect> getEffects() {
        return layerMaskingEffects;
    }

    public int count() {
        return layerMaskingEffects.size;
    }

    public int getSize() {
        return layerMaskingEffects.size;
    }

    public LayerMaskingEffect get(int index) {
        return layerMaskingEffects.get(index);
    }

    public LayerMaskingEffect get(String name) {
        if(name == null)
            return null;
        if(name.isEmpty())
            return null;
        for(int index = 0;index < layerMaskingEffects.size; index++) {
            LayerMaskingEffect effect = layerMaskingEffects.get(index);
            if(effect.getName() == null)
                continue;
            if(effect.getName().compareTo(name) == 0) {
                return effect;
            }
        } // for each layer masking effect
        return null;
    }

    public LayerMaskingEffect peek() {
        return layerMaskingEffects.peek();
    }

    public int indexOf(LayerMaskingEffect effect) {
        return layerMaskingEffects.indexOf(effect, true);
    }

    //-------------------------------------------------------------------------

    public void hideAll() {
        for(int index = 0; index < layerMaskingEffects.size; index++) {
            layerMaskingEffects.get(index).hide();
        } // for each layer masking effect
    }

    public void showAll() {
        for(int index = 0; index < layerMaskingEffects.size; index++) {
            layerMaskingEffects.get(index).show();
        } // for each layer masking effect
    }

    public void show(String name) {
        // show selected one
        LayerMaskingEffect effect = get(name);
        if(effect != null)
            effect.show();
    }

    public void show(int index) {
        // show selected one
        if(index > 0 && index < layerMaskingEffects.size)
            layerMaskingEffects.get(index).show();
    }

    public void hide(String name) {
        // hide selected one
        LayerMaskingEffect effect = get(name);
        if(effect != null)
            effect.hide();
    }

    public void hide(int index) {
        // hide selected one
        if(index > 0 && index < layerMaskingEffects.size)
            layerMaskingEffects.get(index).hide();
    }

    public void showOnly(String name) {
        // this will show only selected one and hide the rest
        LayerMaskingEffect effect = get(name);
        if(effect == null)
            return;
        hideAll();
        effect.show();
    }

    public void showOnly(int index) {
        // this will show only selected one and hide the rest
        if(index < 0 || index >= layerMaskingEffects.size)
            return;
        LayerMaskingEffect effect = get(index);
        hideAll();
        effect.show();
    }

    public void hideOnly(String name) {
        // this will hide only selected one and show the rest
        LayerMaskingEffect effect = get(name);
        if(effect == null)
            return;
        showAll();
        effect.hide();
    }

    public void hideOnly(int index) {
        // this will hide only selected one and show the rest
        if(index < 0 || index >= layerMaskingEffects.size)
            return;
        LayerMaskingEffect effect = get(index);
        hideAll();
        effect.hide();
    }

    //-------------------------------------------------------------------------

    public LayerMaskingEffect getFirstInactive() {
        LayerMaskingEffect result = null;
        for (int index = 0; index < layerMaskingEffects.size; index++) {
            result = layerMaskingEffects.get(index);
            if (!result.isActive())
                break;
            result = null;
        } // for each layer masking effect
        return result;
    }

    public LayerMaskingEffect getFirstActive() {
        LayerMaskingEffect result = null;
        for (int index = 0; index < layerMaskingEffects.size; index++) {
            result = layerMaskingEffects.get(index);
            if (result.isActive()) // is active/visible?
                break;
            result = null;
        } // for each layer masking effect
        return result;
    }

    public LayerMaskingEffect getLastInactive() {
        LayerMaskingEffect result = null;
        for (int index = layerMaskingEffects.size - 1; index >= 0; index--) {
            result = layerMaskingEffects.get(index);
            if (!result.isActive()) // is inactive?
                break;
            result = null;
        } // for each layer masking effect
        return result;
    }

    public LayerMaskingEffect getLastActive() {
        LayerMaskingEffect result = null;
        for (int index = layerMaskingEffects.size - 1; index >= 0; index--) {
            result = layerMaskingEffects.get(index);
            if (result.isActive()) // is active/visible?
                break;
            result = null;
        } // for each layer masking effect
        return result;
    }

    //-------------------------------------------------------------------------

    public void refreshScreenDimensions() {
        if (Gdx.app == null)
            return;
        final float width = Gdx.app.getGraphics().getWidth();
        final float height = Gdx.app.getGraphics().getHeight();
        this.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    public void refreshScreenDimensions(int width, int height) {
        this.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    //-------------------------------------------------------------------------

    public LayerMaskingEffect createLayerEffect() {
        LayerMaskingEffect layerMaskingEffect = new LayerMaskingEffect(this.assetManager);
        this.layerMaskingEffects.add(layerMaskingEffect);
        return layerMaskingEffect;
    }

    public LayerMaskingEffect createLayerEffect(String name) {
        LayerMaskingEffect layerMaskingEffect = new LayerMaskingEffect(this.assetManager, name);
        this.layerMaskingEffects.add(layerMaskingEffect);
        return layerMaskingEffect;
    }

    //-------------------------------------------------------------------------

    public void update(float delta) {
        final int n = this.count();
        LayerMaskingEffect effect;
        for (int i = 0; i < n; i++) {
            effect = this.layerMaskingEffects.get(i);
            if (!effect.isActive())
                continue;
            effect.update(delta);
        }
    } // void update(...)

    public void draw() {
        final int n = this.count();
        this.spriteBatch.begin();
        this.spriteBatch.setColor(Color.WHITE);
        LayerMaskingEffect effect;
        for (int i = 0; i < n; i++) {
            effect = this.layerMaskingEffects.get(i);
            if (!effect.isActive())
                continue;
            effect.draw(this.spriteBatch);
        }
        this.spriteBatch.end();
        customShaderProgram.begin();
        customShaderProgram.setUniformf("u_useMask", 0.0f); // no masking
        customShaderProgram.setUniformf("u_maskScale", 1.0f);
        customShaderProgram.setUniformf("u_maskOffset", 0.0f, 0.0f);
        customShaderProgram.end();
    } // void draw()

    //-------------------------------------------------------------------------

} // class LayerMaskingEffectDrawer
