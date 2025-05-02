package Fillers;

import java.awt.*;
import models.Point;
import java.util.List;

public class FillOperation {
    private final List<Point> points;
    private final Color color;

    public FillOperation(List<Point> points, Color color) {
        this.points = points;
        this.color = color;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }
}
