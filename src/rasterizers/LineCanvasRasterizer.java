package rasterizers;

import models.Line;
import models.LineCanvas;
import rasters.Raster;

public class LineCanvasRasterizer {
    private Raster raster;
    private Rasterizer lineRasterizer;
    private Rasterizer dottedLineRasterizer;

    public LineCanvasRasterizer(Raster raster) {
        this.raster = raster;
        lineRasterizer = new TrivialLineRasterizer(raster);
        dottedLineRasterizer = new DottedLineRasterizer(raster);
    }

    public void rasterizeCanvas(LineCanvas canvas) {
        ((TrivialLineRasterizer) lineRasterizer).rasterizeArray(canvas.getLines());
        ((DottedLineRasterizer) dottedLineRasterizer).rasterizeArray(canvas.getDottedLines());
    }

    public void rasterizeLine(Line line) {
        ((TrivialLineRasterizer) lineRasterizer).rasterize(line);
    }

    public void rasterizeDottedLine(Line line) {
        ((DottedLineRasterizer) dottedLineRasterizer).rasterize(line);
    }
}