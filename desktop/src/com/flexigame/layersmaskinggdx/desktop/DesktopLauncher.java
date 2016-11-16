package com.flexigame.layersmaskinggdx.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.flexigame.layersmaskinggdx.MyGdxLayersMaskingEffect;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 506;
        config.height = 900;
        config.title = "Layers Masking Effect X - flexigame.com - Technical Blog";
        new LwjglApplication(new MyGdxLayersMaskingEffect(), config);
    }
} // class DesktopLauncher
