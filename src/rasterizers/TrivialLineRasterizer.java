package rasterizers;

import models.Line;
import rasters.Raster;

import java.util.ArrayList;

public class TrivialLineRasterizer implements Rasterizer {
    private Raster raster;

    public TrivialLineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        int thickness = line.getThickness();
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();

        // Handle thickness <= 1 as single pixel
        int halfThickness = Math.max(0, (thickness - 1) / 2);

        // Handle vertical lines (x1 == x2)
        if (x1 == x2) {
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int y = y1; y <= y2; y++) {
                drawThickPoint(x1, y, halfThickness, line.getColor().getRGB());
            }
            return;
        }

        // Handle horizontal lines (y1 == y2)
        if (y1 == y2) {
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            for (int x = x1; x <= x2; x++) {
                drawThickPoint(x, y1, halfThickness, line.getColor().getRGB());
            }
            return;
        }

        // Handle diagonal lines (slope-based approach)
        float k = (float) (y2 - y1) / (float) (x2 - x1);
        float q = y1 - (k * x1);

        if (Math.abs(k) < 1) {
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);
                drawThickPoint(x, y, halfThickness, line.getColor().getRGB());
            }
        } else {
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int y = y1; y <= y2; y++) {
                int x = Math.round((y - q) / k);
                drawThickPoint(x, y, halfThickness, line.getColor().getRGB());
            }
        }
    }

    private void drawThickPoint(int x, int y, int halfThickness, int color) {
        for (int dx = -halfThickness; dx <= halfThickness; dx++) {
            for (int dy = -halfThickness; dy <= halfThickness; dy++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color);
                }
            }
        }
    }

    public void rasterizeArray(ArrayList<Line> lines) {
        for (Line line : lines) {
            rasterize(line);
        }
    }
}