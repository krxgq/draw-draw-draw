package rasterizers;

import models.Point;
import rasters.Raster;

import java.awt.Color;

public class CircleRasterizer {
    private Raster raster;

    public CircleRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Point center, Point edge, Color color, int thickness) {
        // Calculate radius as the distance between center and edge
        int dx = edge.getX() - center.getX();
        int dy = edge.getY() - center.getY();
        int radius = (int) Math.sqrt(dx * dx + dy * dy);

        // Adjust radius for thickness
        int outerRadius = radius + (thickness - 1) / 2;
        int innerRadius = Math.max(0, radius - thickness / 2);

        // Draw filled circles from inner to outer radius to simulate thickness
        for (int r = innerRadius; r <= outerRadius; r++) {
            drawCircle(center.getX(), center.getY(), r, color.getRGB());
        }
    }

    private void drawCircle(int cx, int cy, int radius, int color) {
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            // Draw all eight octants
            setPixel(cx + x, cy + y, color);
            setPixel(cx + y, cy + x, color);
            setPixel(cx - y, cy + x, color);
            setPixel(cx - x, cy + y, color);
            setPixel(cx - x, cy - y, color);
            setPixel(cx - y, cy - x, color);
            setPixel(cx + y, cy - x, color);
            setPixel(cx + x, cy - y, color);

            if (err <= 0) {
                y += 1;
                err += 2 * y + 1;
            }
            if (err > 0) {
                x -= 1;
                err -= 2 * x + 1;
            }
        }
    }

    private void setPixel(int x, int y, int color) {
        if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
            raster.setPixel(x, y, color);
        }
    }
}