package rhx.gfx.render.renderer;

import rhx.gfx.render.Drawable;

import java.awt.*;
import java.awt.image.DataBufferInt;
import java.io.IOException;

/**
 * Water effect renderer.
 * Created by rhinox on 2014-10-10.
 */
public class WaterRenderer extends ImageRenderer {

    private static final int DAMP = 7;
    private static final int PULSE = 512;

    private int scrWidth;
    private int scrHeight;

    private int[] waveMapNow;
    private int[] outBuffer;
    private int[] waveMapBefore;

    public WaterRenderer(String imageFileName) throws IOException{
        super(imageFileName);
    }

    @Override
    public Renderer init(Drawable drawable) {
        Dimension dimension = drawable.getDimension();
        scrWidth = dimension.width;
        scrHeight = dimension.height;

        DataBufferInt dataBuffer = (DataBufferInt) drawable.getDrawableRaster().getDataBuffer();
        int[] offScreenRaster = dataBuffer.getData();
        outBuffer = new int[offScreenRaster.length];
        waveMapNow = new int[offScreenRaster.length];
        waveMapBefore = new int[offScreenRaster.length];
        return this;
    }

    @Override
    public Renderer drawOn(Drawable drawable) {
        updateWaveMap();
        updateOutputBuffer();
        updateScreen(drawable);
        return this;
    }

    private void updateWaveMap() {
        int n;
        for (int y = 1; y < scrHeight - 1; ++y) {
            for (int x = 1; x < scrWidth - 1; ++x) {
                n = ((
                        waveMapNow[x + 1 + y * scrWidth] +
                        waveMapNow[x - 1 + y * scrWidth] +
                        waveMapNow[x + (y + 1) * scrWidth] +
                        waveMapNow[x + (y - 1) * scrWidth]
                ) >> 1) - waveMapBefore[x + y * scrWidth];
                n -= n >> DAMP;
                waveMapBefore[x + y * scrWidth] = n;
            }
        }
        int [] waveMapTmp = waveMapNow;
        waveMapNow = waveMapBefore;
        waveMapBefore = waveMapTmp;
    }

    private void updateOutputBuffer() {
        int xDisplaced, yDisplaced;
        for (int y = 1; y < scrHeight - 1; ++y) {
            for (int x = 1; x < scrWidth - 1; ++x) {
                xDisplaced = ((x - scrWidth) * (1024 - waveMapNow[x + y * scrWidth]) / 1024) + scrWidth;
                yDisplaced = ((y - scrHeight) * (1024 - waveMapNow[x + y * scrWidth]) / 1024) + scrHeight;

                if (xDisplaced < 0) xDisplaced = 0;
                if (xDisplaced > scrWidth) xDisplaced = scrWidth - 1;
                if (yDisplaced < 0) yDisplaced = 0;
                if (yDisplaced > scrHeight) yDisplaced = scrHeight- 1;

                outBuffer[x + y * scrWidth] = texture[(xDisplaced) * texWidth / scrWidth + ((yDisplaced) * texHeight / scrHeight) * texWidth];;
            }
        }
    }

    private void updateScreen(Drawable drawable) {
        DataBufferInt dataBuffer = (DataBufferInt) drawable.getDrawableRaster().getDataBuffer();
        System.arraycopy(outBuffer, 0, dataBuffer.getData(), 0, outBuffer.length);
    }

    public ImageRenderer poke(int x, int y) {
        waveMapNow[x + y * scrWidth] = PULSE;
        return this;
    }
}
