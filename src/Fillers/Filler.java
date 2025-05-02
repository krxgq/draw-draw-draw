package Fillers;

import models.Point;

import java.awt.*;
import java.util.List;

public interface Filler {

    public List<Point> fill(
            Point click,
            Color fillColor
    );
}
