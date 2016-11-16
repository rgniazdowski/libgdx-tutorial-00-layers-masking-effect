package com.flexigame.fg.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 *
 */
public class LayerInfo {
    public enum Mode {
        LAYER_ONLY,
        MASK_ONLY,
        BOTH
    }

    /* Current layer index - does not determine order */
    private int index;
    /* Whether or not should layer pulse */
    public boolean shouldPulse;
    /* Is layer visible? */
    public boolean visible;
    /* What element should be changed with pulsing (scaling)? */
    public Mode scaleMode;
    /* Offset mode is for special texturing effect */
    public Mode offsetMode;
    /* Standard texture (diffuse, index 0) */
    private Texture texture;
    /* Additional masking texture (index 1) */
    private Texture maskTexture;
    /* 2D size of the layer */
    public Vector2 size;
    /* Current position (center) */
    public Vector2 position;
    /* Real position (corner) */
    private Vector2 realPos;
    /* Current mask offset value - in screen pixels */
    private Vector2 offset;
    /* Current offset for mask - UV space */
    private Vector2 maskOffset;
    /* Current mask offset value - in screen pixels - layer only */
    private Vector2 layerOffset;
    /* Speed of the effect (pulsing) */
    public float speed;
    /* Current direction of the effect (grow / shrink) */
    private float direction;
    /* Current rotation for the effect */
    public float rotation;
    /* Rotation speed (degrees per second - can be less than < 0.0) */
    public float rotationSpeed;
    /* Current scale value */
    private float scale;
    /* Current scale value for mask texture */
    private float maskScale;
    /* Current scale value for layer texture */
    private float layerScale;
    /* Minimal scale allowed */
    public float minScale;
    /* Maximal scale allowed */
    public float maxScale;
    /* Interpolation method to use when calculating scale */
    private Interpolation interpolation = Interpolation.sine;
    /* Ratio (between 0.0 and 1.0) to use with interpolation function */
    private float scaleRatio;

    //-------------------------------------------------------------------------

    public LayerInfo() {
        visible = true;
        rotation = 0.0f; // 0 degrees
        rotationSpeed = 0.0f;
        index = 0; // current index
        scaleMode = Mode.BOTH;
        offsetMode = Mode.BOTH; // both as default?
        texture = null;
        maskTexture = null;
        size = new Vector2(0.0f, 0.0f);
        position = new Vector2(0.0f, 0.0f);
        realPos = new Vector2(0.0f, 0.0f);
        offset = new Vector2(0.0f, 0.0f); // pixel space (screen / 2D)
        maskOffset = new Vector2(0.0f, 0.0f); // UV space (0.0 - 1.0)
        layerOffset = new Vector2(0.0f, 0.0f); // pixel space (screen / 2D)
        speed = 0.1f;
        scale = 1.0f;
        maskScale = 1.0f;
        layerScale = 1.0f;
        direction = 1.0f;
        minScale = 0.9f;
        maxScale = 1.1f;
        shouldPulse = false;
        //radiansScale = 0.0f;
        scaleRatio = 0.0f;
    }

    public LayerInfo(Texture texture) {
        this();
        this.setTexture(texture);
    }

    public LayerInfo(Texture texture, Texture maskTexture) {
        this();
        this.setTexture(texture);
        this.setMaskTexture(maskTexture);
    }

    //-------------------------------------------------------------------------

    public void dispose(boolean force) {
        if (this.texture != null) {
            if (!this.texture.isManaged() || force)
                this.texture.dispose();
        }
        this.maskTexture = null;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setTexture(Texture newTexture) {
        this.texture = newTexture;
        if (this.texture != null) {
            size.x = texture.getWidth();
            size.y = texture.getHeight();
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void setMaskTexture(Texture maskTexture) {
        this.maskTexture = maskTexture;
    }

    public Texture getMaskTexture() {
        return maskTexture;
    }

    public void setScale(float _scale) {
        this.scale = _scale; // should check if < 0.0f ?
    }

    public float getScale() {
        return scale;
    }

    public float getMaskScale() {
        return maskScale;
    }

    public float getLayerScale() {
        return layerScale;
    }
    
    public Interpolation getInterpolationMethod() {
        return this.interpolation;
    }

    public void setInterpolationMethod(Interpolation interpolation) {
        if(interpolation != null)
            this.interpolation = interpolation;
    }

    public void resetOffset() {
        this.offset.x = 0.0f;
        this.offset.y = 0.0f;
        this.maskOffset.x = 0.0f;
        this.maskOffset.y = 0.0f;
        this.layerOffset.x = 0.0f;
        this.layerOffset.y = 0.0f;
    }

    public void getOffset(Vector2 out) {
        out.x = offset.x;
        out.y = offset.y;
    }

    public void getLayerOffset(Vector2 out) {
        out.x = layerOffset.x;
        out.y = layerOffset.y;
    }

    public void getMaskOffset(Vector2 out) {
        out.x = maskOffset.x;
        out.y = maskOffset.y;
    }

    public void setOffset(int x, int y) {
        float diffX = x - offset.x;
        float diffY = y - offset.y;

        moveOffsetByPixels((int) diffX, (int) diffY);
        if (x == 0 && y == 0) {
            resetOffset();
        }
    }

    public void setOffset(float x, float y) {
        setOffset((int) x, (int) y);
    }

    public void moveOffsetByPixels(int x, int y) {
        float maskOffsetScale = 1.0f;
        if (scaleMode == Mode.LAYER_ONLY) {
            maskOffsetScale = 1.0f / scale;
        } else if (scaleMode == Mode.MASK_ONLY) {
            maskOffsetScale = scale;
        }
        if (offsetMode != Mode.BOTH)
            maskOffsetScale = 1.0f;

        offset.x += (float) x;
        offset.y += (float) y;

        if (offsetMode != Mode.LAYER_ONLY) {
            maskOffset.x = offset.x / size.x * maskOffsetScale;
            maskOffset.y = offset.y / size.y * maskOffsetScale;
        }
        // Layer offset is now in screen space so can use it directly
        if (offsetMode != Mode.MASK_ONLY) {
            layerOffset.x += (float) x;
            layerOffset.y += (float) y;
        }
    } // void moveOffsetByPixels(...)

    public void moveOffsetByPixels(float x, float y) {
        moveOffsetByPixels((int) x, (int) y);
    }

    public void moveOffsetByPixels(Vector2 pixels) {
        moveOffsetByPixels((int) pixels.x, (int) pixels.y);
    }

    //-------------------------------------------------------------------------

    public void update(float delta) {
        if (shouldPulse) {
            scaleRatio += direction * delta * speed;
            if(scaleRatio >= 1.0f) {
                scaleRatio = 1.0f;
                direction = -1.0f;
            } else if(scaleRatio < 0.0f) {
                scaleRatio = 0.0f;
                direction = 1.0f;
            }
            scale = interpolation.apply(minScale, maxScale, scaleRatio);
        }

        float maskOffsetScale = 1.0f;
        if (scaleMode == Mode.BOTH) {
            maskScale = scale;
            layerScale = scale;
        } else if (scaleMode == Mode.LAYER_ONLY) {
            layerScale = scale;
            maskScale = 1.0f;
            maskOffsetScale = 1.0f / scale;
        } else if (scaleMode == Mode.MASK_ONLY) {
            layerScale = 1.0f;
            maskScale = scale;
            maskOffsetScale = scale;
        }

        rotation += rotationSpeed * delta;

        if (rotation <= 0.0f) {
            rotation = 360.0f - rotation;
        } else if (rotation > 360.0f) {
            rotation = rotation - 360.0f;
        }

        // center !
        realPos.x = position.x - size.x * layerScale / 2.0f + layerOffset.x;
        realPos.y = position.y - size.y * layerScale / 2.0f - layerOffset.y;
    }
    //-------------------------------------------------------------------------

    public void draw(SpriteBatch batch) {
        if (this.texture == null || batch == null || visible == false)
            return;
        ShaderProgram shader = batch.getShader();
        if (this.maskTexture != null) {
            shader.setUniformf("u_maskScale", this.maskScale);
            shader.setUniformf("u_useMask", 1.0f);
            // u_maskOffset ?
            if (shader.hasUniform("u_maskOffset"))
                shader.setUniformf("u_maskOffset", this.maskOffset.x, this.maskOffset.y);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
            this.maskTexture.bind(1);
        } else {
            shader.setUniformf("u_maskScale", 1.0f);
            shader.setUniformf("u_useMask", 0.0f);
            if (shader.hasUniform("u_maskOffset"))
                shader.setUniformf("u_maskOffset", 0.0f, 0.0f); // no offset !
        }

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        batch.draw(this.texture,
                realPos.x,
                realPos.y,
                size.x * layerScale / 2.0f,
                size.y * layerScale / 2.0f,
                size.x * layerScale,
                size.y * layerScale,
                1.0f,
                1.0f,
                rotation,
                0, 0,
                this.texture.getWidth(),
                this.texture.getHeight(),
                false, false);

        batch.flush(); // need to flush when using additional texture (sic!)
    } // void draw(...)

    //-------------------------------------------------------------------------
} // class LayerInfo
