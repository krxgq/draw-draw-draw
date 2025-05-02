package Fillers;

import models.Point;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicFiller implements Filler {

    private final Raster raster;
    private static final int MAX_FILL_SIZE = 1_000_000; // Prevent runaway fills

    public BasicFiller(Raster raster) {
        this.raster = raster;
    }

    @Override
    public List<Point> fill(Point click, Color fillColor) {
        if (click.getX() < 0 || click.getX() >= raster.getWidth() ||
                click.getY() < 0 || click.getY() >= raster.getHeight()) {
            return new ArrayList<>();
        }

        int baseColor = raster.getPixel(click.getX(), click.getY());
        if (baseColor == fillColor.getRGB()) {
            return new ArrayList<>();
        }

        List<Point> pending = new ArrayList<>();
        List<Point> filledPoints = new ArrayList<>();
        Set<Point> processed = new HashSet<>(); // Track processed pixels efficiently
        pending.add(click);
        processed.add(click);

        int fillCount = 0;

        while (!pending.isEmpty()) {
            Point p = pending.remove(0);
            if (floodFill(p, fillColor, baseColor, pending, filledPoints, processed)) {
                fillCount++;
                if (fillCount > MAX_FILL_SIZE) {
                    System.out.println("Fill operation aborted: exceeded maximum fill size.");
                    break;
                }
            }
        }

        return filledPoints;
    }

    private boolean floodFill(Point p, Color fillColor, int baseColor,
                              List<Point> pending, List<Point> filledPoints, Set<Point> processed) {
        if (p.getX() < 0 || p.getX() >= raster.getWidth() ||
                p.getY() < 0 || p.getY() >= raster.getHeight()) {
            return false;
        }

        int currentColor = raster.getPixel(p.getX(), p.getY());
        if (currentColor != baseColor) {
            return false;
        }

        raster.setPixel(p.getX(), p.getY(), fillColor.getRGB());
        filledPoints.add(p);

        Point[] neighbors = {
                new Point(p.getX() + 1, p.getY()),
                new Point(p.getX() - 1, p.getY()),
                new Point(p.getX(), p.getY() + 1),
                new Point(p.getX(), p.getY() - 1)
        };

        for (Point neighbor : neighbors) {
            if (neighbor.getX() >= 0 && neighbor.getX() < raster.getWidth() &&
                    neighbor.getY() >= 0 && neighbor.getY() < raster.getHeight()) {
                if (!processed.contains(neighbor)) {
                    int neighborColor = raster.getPixel(neighbor.getX(), neighbor.getY());
                    if (neighborColor == baseColor) {
                        pending.add(neighbor);
                        processed.add(neighbor);
                    }
                }
            }
        }

        return true;
    }
}