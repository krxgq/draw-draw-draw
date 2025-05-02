package rasterizers;

import models.Line;
import rasters.Raster;

import java.util.ArrayList;

public class DottedLineRasterizer implements Rasterizer {

    private Raster raster;

    public DottedLineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        int thickness = line.getThickness();
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();
        int spacing = thickness + 5; // Dynamic spacing: base of 5 pixels plus line thickness

        int halfThickness = Math.max(0, (thickness - 1) / 2);

        if (x1 == x2) {
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int y = y1; y <= y2; y += spacing) {
                drawThickPoint(x1, y, halfThickness, line.getColor().getRGB());
            }
            return;
        }

        float k = (float) (y2 - y1) / (x2 - x1);
        float q = y1 - (k * x1);

        if (Math.abs(k) < 1) {
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            for (int x = x1; x <= x2; x += spacing) {
                int y = Math.round(k * x + q);
                drawThickPoint(x, y, halfThickness, line.getColor().getRGB());
            }
        } else {
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            for (int y = y1; y <= y2; y += spacing) {
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