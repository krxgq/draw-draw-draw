# Drawing Application Documentation

## Overview
This Java-based drawing application enables users to create and manipulate 2D graphics on an 800x600 canvas using Swing for the GUI. It supports drawing lines, polygons, squares, circles, and flood-filling areas with colors. The application uses a raster-based rendering system and provides tools for editing lines (e.g., splitting with an eraser) and adjusting line thickness and color.

## Key Components

### App.java
- **Purpose**: Main application class that initializes the GUI, handles user input, and manages drawing operations.
- **Features**:
    - Canvas (1120x600 pixels, customizable) for drawing.
    - Toolbar with color picker, brush size slider (1-15 pixels), and mode buttons (Square, Circle, Rubber).
    - Modes: Line drawing, Polygon, Fill, Square, Circle, Rubber (eraser).
    - Keyboard shortcuts: `Ctrl` (dotted lines), `Shift` (aligned lines), `F` (fill mode), `P` (polygon mode), `R` (rubber mode), `C` (clear canvas).
    - Mouse interactions: Left-click to draw/fill, right-click to drag points.

### Models
- **Line.java**: Represents a line with two points, color, and thickness.
- **Point.java**: Represents a 2D point with x, y coordinates.
- **LineCanvas.java**: Stores lists of solid and dotted lines for rendering.

### Rasterizers
- **Raster.java**: Interface for raster operations (e.g., set/get pixel, clear).
- **RasterBufferedImage.java**: Implements `Raster` using a `BufferedImage` for pixel manipulation.
- **LineCanvasRasterizer.java**: Renders `LineCanvas` contents (solid and dotted lines).
- **TrivialLineRasterizer.java**: Draws solid lines with thickness support.
- **DottedLineRasterizer.java**: Draws dotted lines with dynamic spacing.
- **SquareRasterizer.java**: Draws squares by rasterizing four lines.
- **CircleRasterizer.java**: Draws circles using a midpoint algorithm with thickness.

### Fillers
- **Filler.java**: Interface for filling operations, defining a `fill` method that takes a click point and color.
- **BasicFiller.java**: Implements flood-fill using a queue-based algorithm. Fills an area of the same color as the clicked pixel with the specified color, respecting canvas boundaries and a maximum fill size (1,000,000 pixels) to prevent runaway fills.
- **FillOperation.java**: Stores a list of filled points and their color for rendering.

## Architecture
- **GUI**: Built with Swing (`JFrame`, `JPanel`, `JButton`, `JSlider`, etc.).
- **Rendering**: Uses `RasterBufferedImage` to manage a pixel buffer, updated via rasterizers and fillers.
- **Event Handling**: Mouse and keyboard listeners handle drawing, mode switching, and editing.
- **State Management**: Tracks modes (e.g., `isPolygonMode`, `isFillMode`), current color, brush size, and fill operations.

## Functionality
- **Drawing**: Lines (solid/dotted), polygons (with closing line), squares, circles.
- **Editing**: Drag points with right-click, split lines with rubber tool.
- **Filling**: Flood-fill areas with the selected color, replacing pixels of the same color as the clicked point.
- **Modes**: Toggle between drawing, filling, and erasing; align lines with `Shift`.
- **Persistence**: Lines and fill operations persist until cleared (`C`).

## Extensibility
- Add new rasterizers for additional shapes (e.g., triangles).
- Enhance `BasicFiller` for pattern-based or gradient fills.
- Expand UI with undo/redo, save/load, or additional tools.
