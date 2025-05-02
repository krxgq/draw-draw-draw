package rasterizers;

import models.Line;
import models.Point;
import rasters.Raster;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class SquareRasterizer {
    private Raster raster;

    public SquareRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Point p1, Point p2, Color color, int thickness) {
        int dx = p2.getX() - p1.getX();
        int dy = p2.getY() - p1.getY();
        int side = Math.max(Math.abs(dx), Math.abs(dy));

        // Determine the four corners of the square
        Point topLeft = new Point(p1.getX(), p1.getY());
        Point topRight = new Point(p1.getX() + side, p1.getY());
        Point bottomLeft = new Point(p1.getX(), p1.getY() + side);
        Point bottomRight = new Point(p1.getX() + side, p1.getY() + side);

        // If dx is negative, adjust points to the left
        if (dx < 0) {
            topLeft.setX(p1.getX() - side);
            topRight.setX(p1.getX());
            bottomLeft.setX(p1.getX() - side);
            bottomRight.setX(p1.getX());
        }

        // If dy is negative, adjust points upwards
        if (dy < 0) {
            topLeft.setY(p1.getY() - side);
            topRight.setY(p1.getY() - side);
            bottomLeft.setY(p1.getY());
            bottomRight.setY(p1.getY());
        }

        // Create lines for the four sides of the square
        Line top = new Line(topLeft, topRight, color, thickness);
        Line right = new Line(topRight, bottomRight, color, thickness);
        Line bottom = new Line(bottomRight, bottomLeft, color, thickness);
        Line left = new Line(bottomLeft, topLeft, color, thickness);

        // Rasterize each side
        TrivialLineRasterizer lineRasterizer = new TrivialLineRasterizer(raster);
        lineRasterizer.rasterize(top);
        lineRasterizer.rasterize(right);
        lineRasterizer.rasterize(bottom);
        lineRasterizer.rasterize(left);
    }
}


