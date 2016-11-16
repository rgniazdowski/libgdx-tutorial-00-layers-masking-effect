package com.flexigame.fg.gfx;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 *
 */
public class LayerMaskingEffect {

    /* External asset manager - for getting textures */
    private AssetManager assetManager;
    /* All currently owned layers for this effect */
    private Array<LayerInfo> layerInfoArray;
    /* Is active/visible? */
    private boolean active;
    /* Current Layer id name */
    String name;

    //-------------------------------------------------------------------------

    public LayerMaskingEffect(AssetManager assetManager) {
        if (assetManager == null)
            throw new RuntimeException("AssetManager passed into constructor cannot be NULL!");
        this.assetManager = assetManager;
        this.layerInfoArray = new Array<LayerInfo>();
        this.active = true; // active by default
        this.name = "";
    }

    public LayerMaskingEffect(AssetManager assetManager, String name) {
        if (assetManager == null)
            throw new RuntimeException("AssetManager passed into constructor cannot be NULL!");
        this.assetManager = assetManager;
        this.layerInfoArray = new Array<LayerInfo>();
        this.active = true; // active by default
        this.name = name;
    }

    public void dispose() {
        int n = this.count();
        for (int i = 0; i < n; i++) {
            this.layerInfoArray.items[i].dispose(false);
        }
        this.layerInfoArray.clear();
    }

    //-------------------------------------------------------------------------

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Array<LayerInfo> getLayerInfoArray() {
        return layerInfoArray;
    }

    public int count() {
        return layerInfoArray.size;
    }

    public int getSize() {
        return layerInfoArray.size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    //-------------------------------------------------------------------------

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean toggle) {
        this.active = toggle;
    }

    public void show() {
        this.active = true;
    }

    public void hide() {
        this.active = false;
    }

    //-------------------------------------------------------------------------

    public LayerInfo get(int index) {
        return layerInfoArray.get(index);
    }

    public LayerInfo get(Texture texture) {
        int index = this.indexOf(texture);
        if (index < 0)
            return null;
        return layerInfoArray.get(index);
    }

    public LayerInfo get(String textureFile) {
        int index = this.indexOf(textureFile);
        if (index < 0)
            return null;
        return layerInfoArray.get(index);
    }

    //-------------------------------------------------------------------------

    public int indexOf(LayerInfo layerInfo) {
        return layerInfoArray.indexOf(layerInfo, true);
    }

    public int indexOf(Texture texture) {
        final int n = this.count();
        int index = -1;
        for (int i = 0; i < n; i++) {
            if (layerInfoArray.get(i).getTexture() == texture) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int indexOf(String textureFile) {
        if (this.assetManager == null)
            return -1; // no exception here
        Texture texture = this.assetManager.get(textureFile, Texture.class);
        return indexOf(texture);
    }
    //-------------------------------------------------------------------------

    public void resetOffset() {
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            layerInfoArray.get(i).resetOffset();
        }
    }

    public void setOffset(int x, int y) {
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            layerInfoArray.get(i).setOffset(x, y);
        }
    }

    public void moveOffsetByPixels(int x, int y) {
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            layerInfoArray.get(i).moveOffsetByPixels(x, y);
        }
    }

    public void setOffset(float x, float y) {
        setOffset((int) x, (int) y);
    }

    public void moveOffsetByPixels(float x, float y) {
        moveOffsetByPixels((int) x, (int) y);
    }

    //-------------------------------------------------------------------------

    public LayerInfo addLayer(String textureFile) {
        if (this.assetManager == null)
            throw new RuntimeException("AssetManager cannot be NULL!");
        return this.addLayer(this.assetManager.get(textureFile, Texture.class));
    }

    public LayerInfo addLayer(Texture texture) {
        if (texture == null)
            return null; // no exception;
        LayerInfo layerInfo = new LayerInfo(texture);
        layerInfo.setIndex(this.count());
        this.layerInfoArray.add(layerInfo);
        return layerInfo;
    }

    public LayerInfo addLayer(String textureFile, String maskFile) {
        if (this.assetManager == null)
            throw new RuntimeException("AssetManager cannot be NULL!");
        return this.addLayer(this.assetManager.get(textureFile, Texture.class),
                this.assetManager.get(maskFile, Texture.class));
    }

    public LayerInfo addLayer(Texture texture, Texture maskTexture) {
        if (texture == null || maskTexture == null)
            return null; // no exception;
        LayerInfo layerInfo = new LayerInfo(texture, maskTexture);
        layerInfo.setIndex(this.count());
        this.layerInfoArray.add(layerInfo);
        return layerInfo;
    }

    public LayerInfo addLayer(String textureFile, float x, float y) {
        if (this.assetManager == null)
            throw new RuntimeException("AssetManager cannot be NULL!");
        return this.addLayer(this.assetManager.get(textureFile, Texture.class),
                x, y);
    }

    public LayerInfo addLayer(Texture texture, float x, float y) {
        if (texture == null)
            return null; // no exception!
        LayerInfo layerInfo = new LayerInfo(texture);
        layerInfo.position.x = x;
        layerInfo.position.y = y;
        layerInfo.setIndex(this.count());
        this.layerInfoArray.add(layerInfo);
        return layerInfo;
    }

    public LayerInfo addLayer(String textureFile, String maskFile,
                              float x, float y,
                              boolean shouldPulse, float speed,
                              float minScale, float maxScale) {
        if (this.assetManager == null)
            throw new RuntimeException("AssetManager cannot be NULL!");
        return this.addLayer(this.assetManager.get(textureFile, Texture.class),
                this.assetManager.get(maskFile, Texture.class),
                x, y, shouldPulse, speed, minScale, maxScale);
    }

    public LayerInfo addLayer(Texture texture, Texture maskTexture,
                              float x, float y,
                              boolean shouldPulse, float speed,
                              float minScale, float maxScale) {
        if (texture == null || maskTexture == null)
            return null; // no exception!
        LayerInfo layerInfo = new LayerInfo(texture, maskTexture);
        layerInfo.position.x = x;
        layerInfo.position.y = y;
        layerInfo.shouldPulse = shouldPulse;
        layerInfo.speed = speed;
        layerInfo.minScale = minScale;
        layerInfo.maxScale = maxScale;
        layerInfo.setIndex(this.count());
        this.layerInfoArray.add(layerInfo);
        return layerInfo;
    }

    //-------------------------------------------------------------------------

    private void refreshLayerIndexes() {
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            LayerInfo layerInfo = this.layerInfoArray.items[i];
            if (layerInfo != null)
                layerInfo.setIndex(i);
        }
    }

    //-------------------------------------------------------------------------

    public int deleteLayer(Texture texture) {
        int n = 0;
        while (true) {
            int index = this.indexOf(texture);
            if (index == -1)
                break;
            deleteLayer(index);
            n++;
        }
        return n;
    }

    public int deleteLayer(String textureFile) {
        int n = 0;
        while (true) {
            int index = this.indexOf(textureFile);
            if (index == -1)
                break;
            deleteLayer(index);
            n++;
        }
        return n;
    }

    public void deleteLayer(int index) {
        LayerInfo layerInfo = this.layerInfoArray.removeIndex(index);
        if (layerInfo != null) {
            layerInfo.dispose(false);
            layerInfo = null;
        }
        this.refreshLayerIndexes();
    }

    //-------------------------------------------------------------------------

    void update(float delta) {
        if (active == false)
            return;
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            this.layerInfoArray.get(i).update(delta);
        }
    }

    void draw(SpriteBatch batch) {
        if (batch == null || active == false)
            return;
        final int n = this.count();
        for (int i = 0; i < n; i++) {
            this.layerInfoArray.get(i).draw(batch);
        }
    }

    //-------------------------------------------------------------------------
} // class LayerMaskingEffect
