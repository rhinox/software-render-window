package rhx.gfx.render.renderer;

import rhx.gfx.render.Drawable;

import java.awt.*;
import java.awt.image.DataBufferInt;
import java.io.IOException;

/**
 * Tunnel effect renderer
 * Created by rhinox on 2014-10-05.
 */
public class TunnelRenderer extends ImageRenderer {
    private double movement = 0.1;
    private double animation = 0;

    private int scrWidth;
    private int scrHeight;
    private int shiftX, shiftY, shiftLookX, shiftLookY;

    private int angles[][];
    private int distances[][];

    public TunnelRenderer(String imageFileName) throws IOException {
        super(imageFileName);
    }

    @Override
    public Renderer init(Drawable drawable) {
        Dimension dimension = drawable.getDimension();
        scrWidth = dimension.width;
        scrHeight = dimension.height;

        distances = new int[scrWidth * 2][scrHeight * 2];
        angles = new int[scrWidth * 2][scrHeight * 2];

        for (int x = 0; x < scrWidth * 2; x++) {
            for (int y = 0; y < scrHeight * 2; y++) {
                distances[x][y] = (int) ((30.0 * texHeight / Math.sqrt((x - scrWidth / 2) * (x - scrWidth / 2) + (y - scrHeight / 2) * (y - scrHeight / 2))) % texHeight);
                   angles[x][y] = (int) (0.5 * texWidth * Math.atan2(y - scrHeight / 2, x - scrWidth / 2) / Math.PI);
            }
        }
        return this;
    }

    @Override
    public Renderer drawOn(Drawable drawable) {
        DataBufferInt dataBuffer = (DataBufferInt) drawable.getDrawableRaster().getDataBuffer();
        int[] offScreenRaster = dataBuffer.getData();
        animation += 1;
        movement  += 1;

        shiftX = (int) (texWidth + animation);
        shiftY = (int) (texHeight + movement);

        shiftLookX = texWidth / 4 + (int) (texWidth / 4 * Math.sin(animation * 0.1));
        shiftLookY = texHeight / 4 + (int) (texHeight / 4 * Math.sin(animation * 0.2));


        for (int y = 0, cursor = 0; y < scrHeight; y++) {
            for (int x = 0; x < scrWidth; x++, cursor++) {
                int texX = (distances[x + shiftLookX][y + shiftLookY] + shiftX) % texWidth;
                int texY = ((angles[x + shiftLookX][y + shiftLookY] + shiftY) % texHeight) * texWidth;
                offScreenRaster[x + y * scrWidth] = texture[texX + texY];
            }
        }
        return this;
    }
}
