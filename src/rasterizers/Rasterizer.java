package rasterizers;

import models.Line;
import models.LineCanvas;

import java.awt.*;
import java.util.ArrayList;

public interface Rasterizer {

    void rasterizeArray(ArrayList<Line> lines);

    void rasterize(Line line);
}