package rhx.gfx.render;

import com.google.common.base.Preconditions;

import java.awt.*;
import java.awt.image.*;

/**
 * This implementation of {@link rhx.gfx.render.PrimitiveDrawable} is based on and underlying int {@link java.awt.image.Raster}.
 * Created by rhinox on 2014-07-15.
 */
public class PrimitiveDrawableOnWritableRaster implements PrimitiveDrawable {


    private final int width;
    private final int height;
    private final int[] rasterDataBuffer;

    public PrimitiveDrawableOnWritableRaster(Raster writableRaster) {
        int rasterNumberOfBands = writableRaster.getNumBands();
        Preconditions.checkArgument(rasterNumberOfBands == 1, "Number of bands expected 1, actual " + rasterNumberOfBands);

        DataBuffer dataBuffer = writableRaster.getDataBuffer();
        if (DataBuffer.TYPE_INT == dataBuffer.getDataType()) {
            rasterDataBuffer = ((DataBufferInt)dataBuffer).getData();
            width = writableRaster.getWidth();
            height = writableRaster.getHeight();
        } else {
            throw new IllegalArgumentException(
                    "Provided raster of type: " +
                            writableRaster.getClass().getCanonicalName() +
                    " is not supported.");
        }
    }

    private void DrwDrawPixel(final int x, final int y, Color color) {
        rasterDataBuffer[x + y * width] = color.getRGB();
    }

    @Override
    public PrimitiveDrawable drawLine(int sx, int sy, int ex, int ey, Color color) {
        int dx, dy, dp, dd,  d0, ix, iy, di, i;

        if (sx > ex) {
            dx = sx;
            sx = ex;
            ex = dx;

            dy = sy;
            sy = ey;
            ey = dy;
        }

        if (sy < ey) {
            if ( (ex - sx) > (ey - sy)) {
                dx = ex - sx;
                dy = ey - sy;
                ix = sx;
                iy = sy;
                dp = 2 * dy;
                dd = 2 * (dy - dx);
                d0 = 2 * dy - dx;
                di = d0;

                DrwDrawPixel(sx, sy, color);

                for (i = 0; i < dx; ++i) {
                    ix += 1;
                    if (di >= 0) {
                        di += dd;
                        iy += 1;
                    } else  {
                        di += dp;
                    }
                    DrwDrawPixel(ix, iy, color);
                }
            } else {
                dx = ex - sx;
                dy = ey - sy;
                ix = sx;
                iy = sy;
                dp = 2 * dx;
                dd = 2 * (dx - dy);
                d0 = 2 * dx - dy;
                di = d0;

                DrwDrawPixel(sx, sy, color);
                for (i = 0; i < dy; ++i) {
                    iy += 1;
                    if (di >= 0) {
                        di += dd;
                        ix += 1;
                    } else {
                        di += dp;
                    }
                    DrwDrawPixel(ix, iy, color);
                }
            }
        } else {
            if ( (ex - sx) > (sy - ey)) {
                dx = ex - sx;
                dy = sy - ey;
                ix = sx;
                iy = sy;
                dp = - 2 * dy;
                dd = - 2 * (dy - dx);
                d0 = - 2 * dy + dx;
                di = d0;

                DrwDrawPixel(sx, sy, color);
                for (i = 0; i < dx; ++i) {
                    ix += 1;
                    if (di <= 0) {
                        di += dd;
                        iy -= 1;
                    } else {
                        di += dp;
                    }
                    DrwDrawPixel(ix, iy, color);
                }
            } else {
                dx = ex - sx;
                dy = sy - ey;
                ix = sx;
                iy = sy;
                dp = - 2 * dx;
                dd = - 2 * (dx - dy);
                d0 = - 2 * dx + dy;
                di = d0;

                DrwDrawPixel(sx, sy, color);
                for (i = 0; i < dy; ++i) {
                    iy -= 1;
                    if (di <= 0) {
                        di += dd;
                        ix += 1;
                    } else {
                        di += dp;
                    }
                    DrwDrawPixel(ix, iy, color);
                }
            }
        }
        return this;
    }
}
