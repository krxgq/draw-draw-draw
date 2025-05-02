import models.Line;
import models.LineCanvas;
import models.Point;
import rasterizers.LineCanvasRasterizer;
import rasterizers.SquareRasterizer;
import rasterizers.CircleRasterizer;
import rasters.RasterBufferedImage;
import Fillers.BasicFiller;
import Fillers.FillOperation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class App {
    private static final int DEFAULT_WIDTH = 1120;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int CLEAR_COLOR = 0xaaaaaa;
    private static final int MIN_BRUSH_SIZE = 1;
    private static final int MAX_BRUSH_SIZE = 15;
    private static final int COLOR_BUTTON_SIZE = 50;
    private static final String FRAME_TITLE = "UHK FIM PGRF";
    private static final Font UI_FONT = new Font("Arial", Font.BOLD, 15);
    private static final Font SLIDER_FONT = new Font("Arial", Font.BOLD, 12);

    private final JFrame frame;
    private final JPanel canvasPanel;
    private final RasterBufferedImage raster;
    private final LineCanvasRasterizer lineRasterizer;
    private final SquareRasterizer squareRasterizer;
    private final CircleRasterizer circleRasterizer;
    private final LineCanvas canvas;
    private final BasicFiller filler;
    private JLabel fillModeLabel;
    private JLabel polygonModeLabel;
    private JLabel rubberModeLabel;
    private final List<FillOperation> fillOperations = new ArrayList<>();
    private Point startPoint;
    private Line closingLine;
    private ArrayList<Point> polygonPoints = new ArrayList<>();
    private Color currentColor = Color.BLUE;
    private int lineWidth = 1;
    private int eraserSize = 1;
    private boolean isCtrlPressed;
    private boolean isShiftPressed;
    private boolean isPolygonMode;
    private boolean isFillMode;
    private boolean isSquareMode;
    private boolean isCircleMode;
    private boolean isRubberMode;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(DEFAULT_WIDTH, DEFAULT_HEIGHT).start());
    }

    public App(int width, int height) {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle(FRAME_TITLE + ": " + getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        filler = new BasicFiller(raster);
        canvasPanel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(raster.getImg(), 0, 0, null);
            }
        };
        canvasPanel.setPreferredSize(new Dimension(width, height));
        canvasPanel.setFocusable(true);

        frame.add(createButtonPanel(), BorderLayout.NORTH);
        frame.add(canvasPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        lineRasterizer = new LineCanvasRasterizer(raster);
        squareRasterizer = new SquareRasterizer(raster);
        circleRasterizer = new CircleRasterizer(raster);
        canvas = new LineCanvas(new ArrayList<>(), new ArrayList<>());

        setupEventListeners();
        canvasPanel.requestFocus();
        canvasPanel.requestFocusInWindow();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton colorButton = new JButton("Choose Color");
        colorButton.setFont(UI_FONT);
        colorButton.addActionListener(e -> {
            showColorPalette();
            canvasPanel.requestFocusInWindow();
        });

        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, MIN_BRUSH_SIZE, MAX_BRUSH_SIZE, 1);
        sizeSlider.setFont(SLIDER_FONT);
        sizeSlider.setMajorTickSpacing(5);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setSnapToTicks(true);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(1, new JLabel("1"));
        labels.put(5, new JLabel("5"));
        labels.put(10, new JLabel("10"));
        labels.put(15, new JLabel("15"));
        sizeSlider.setLabelTable(labels);
        sizeSlider.addChangeListener(e -> {
            lineWidth = sizeSlider.getValue();
            eraserSize = sizeSlider.getValue();
            canvasPanel.requestFocusInWindow();
        });

        JButton squareButton = new JButton("Square");
        squareButton.setFont(UI_FONT);
        squareButton.addActionListener(e -> toggleMode(() -> isSquareMode = !isSquareMode));

        JButton circleButton = new JButton("Circle");
        circleButton.setFont(UI_FONT);
        circleButton.addActionListener(e -> toggleMode(() -> isCircleMode = !isCircleMode));

        JButton rubberButton = new JButton("Rubber");
        rubberButton.setFont(UI_FONT);
        rubberButton.addActionListener(e -> toggleMode(() -> isRubberMode = !isRubberMode));

        fillModeLabel = createModeLabel("Fill: OFF");
        polygonModeLabel = createModeLabel("Polygon: OFF");
        rubberModeLabel = createModeLabel("Rubber: OFF");

        panel.add(new JLabel("Color:"));
        panel.add(colorButton);
        panel.add(new JLabel("Size:"));
        panel.add(sizeSlider);
        panel.add(new JLabel("Tools:"));
        panel.add(squareButton);
        panel.add(circleButton);
        panel.add(rubberButton);
        panel.add(new JLabel("Modes:"));
        panel.add(fillModeLabel);
        panel.add(polygonModeLabel);
        panel.add(rubberModeLabel);

        return panel;
    }

    private JLabel createModeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UI_FONT);
        label.setOpaque(true);
        label.setBackground(Color.RED);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private void showColorPalette() {
        JDialog dialog = new JDialog(frame, "Select Color", true);
        dialog.setLayout(new GridLayout(5, 5, 5, 5));
        dialog.setSize(300, 300);
        dialog.setLocationRelativeTo(frame);

        Color[] colors = {
                Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
                Color.RED, new Color(255, 102, 102), new Color(204, 0, 0), new Color(139, 0, 0), new Color(255, 51, 51),
                Color.GREEN, new Color(102, 255, 102), new Color(0, 204, 0), new Color(0, 139, 0), new Color(51, 255, 51),
                Color.BLUE, new Color(102, 102, 255), new Color(0, 0, 204), new Color(0, 0, 139), new Color(51, 51, 255),
                Color.YELLOW, new Color(255, 255, 102), new Color(204, 204, 0), new Color(139, 139, 0), new Color(255, 255, 51)
        };

        for (Color color : colors) {
            JButton button = new JButton();
            button.setBackground(color);
            button.setPreferredSize(new Dimension(COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE));
            button.addActionListener(e -> {
                currentColor = color;
                dialog.dispose();
                canvasPanel.requestFocusInWindow();
            });
            dialog.add(button);
        }

        dialog.setVisible(true);
    }

    private void toggleMode(Runnable modeToggle) {
        modeToggle.run();
        if (isSquareMode || isCircleMode || isRubberMode) {
            isFillMode = false;
            if (isPolygonMode) {
                finalizePolygon();
            }
            isPolygonMode = false;
        }
        updateModeLabels();
        renderCanvas();
        canvasPanel.requestFocusInWindow();
    }

    private void updateModeLabels() {
        fillModeLabel.setText("Fill: " + (isFillMode ? "ON" : "OFF"));
        fillModeLabel.setBackground(isFillMode ? Color.GREEN : Color.RED);
        polygonModeLabel.setText("Polygon: " + (isPolygonMode ? "ON" : "OFF"));
        polygonModeLabel.setBackground(isPolygonMode ? Color.GREEN : Color.RED);
        rubberModeLabel.setText("Rubber: " + (isRubberMode ? "ON" : "OFF"));
        rubberModeLabel.setBackground(isRubberMode ? Color.GREEN : Color.RED);
    }

    private void finalizePolygon() {
        if (polygonPoints.size() >= 3 && closingLine != null) {
            canvas.add(new Line(closingLine.getPoint1(), closingLine.getPoint2(), closingLine.getColor(), closingLine.getThickness()));
        }
        polygonPoints.clear();
        if (closingLine != null) {
            canvas.removeLine(closingLine);
            closingLine = null;
        }
    }

    private void setupEventListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Point draggedPoint;
            private boolean isRightDragging;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && !isRubberMode) {
                    draggedPoint = findClosestPoint(e.getX(), e.getY());
                    isRightDragging = draggedPoint != null;
                    startPoint = null;
                } else if (e.getButton() == MouseEvent.BUTTON1 && isRubberMode) {
                    splitLine(e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1 && !isPolygonMode && !isFillMode && !isSquareMode && !isCircleMode && !isRubberMode) {
                    startPoint = new Point(e.getX(), e.getY());
                    draggedPoint = null;
                    isRightDragging = false;
                } else if (e.getButton() == MouseEvent.BUTTON1 && isFillMode) {
                    startPoint = new Point(e.getX(), e.getY());
                    List<Point> filledPoints = filler.fill(startPoint, currentColor);
                    if (!filledPoints.isEmpty()) {
                        fillOperations.add(new FillOperation(filledPoints, currentColor));
                    }
                    renderCanvas();
                    draggedPoint = null;
                    isRightDragging = false;
                } else if (e.getButton() == MouseEvent.BUTTON1 && (isSquareMode || isCircleMode)) {
                    startPoint = new Point(e.getX(), e.getY());
                    draggedPoint = null;
                    isRightDragging = false;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isRightDragging && draggedPoint != null) {
                    draggedPoint.setX(e.getX());
                    draggedPoint.setY(e.getY());
                    renderCanvas();
                } else if (isRubberMode) {
                    splitLine(e.getX(), e.getY());
                } else if (isPolygonMode && !polygonPoints.isEmpty()) {
                    Point lastPoint = polygonPoints.get(polygonPoints.size() - 1);
                    Point currentPoint = new Point(e.getX(), e.getY());
                    if (isShiftPressed) {
                        currentPoint = alignPoint(lastPoint, currentPoint);
                    }
                    Line tempLine = new Line(lastPoint, currentPoint, currentColor, lineWidth);
                    prepareCanvasAndDraw(() -> lineRasterizer.rasterizeLine(tempLine));
                } else if (isSquareMode) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    prepareCanvasAndDraw(() -> squareRasterizer.rasterize(startPoint, endPoint, currentColor, lineWidth));
                } else if (isCircleMode) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    prepareCanvasAndDraw(() -> circleRasterizer.rasterize(startPoint, endPoint, currentColor, lineWidth));
                } else if (!isFillMode && !isRubberMode && startPoint != null) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    if (isShiftPressed) {
                        endPoint = alignPoint(startPoint, endPoint);
                    }
                    Line line = new Line(startPoint, endPoint, currentColor, lineWidth);
                    prepareCanvasAndDraw(() -> {
                        if (isCtrlPressed) {
                            lineRasterizer.rasterizeDottedLine(line);
                        } else {
                            lineRasterizer.rasterizeLine(line);
                        }
                    });
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && isRightDragging && draggedPoint != null) {
                    draggedPoint.setX(e.getX());
                    draggedPoint.setY(e.getY());
                    renderCanvas();
                    draggedPoint = null;
                    isRightDragging = false;
                } else if (isPolygonMode) {
                    Point newPoint = new Point(e.getX(), e.getY());
                    if (!polygonPoints.isEmpty() && isShiftPressed) {
                        newPoint = alignPoint(polygonPoints.get(polygonPoints.size() - 1), newPoint);
                    }
                    // Handle polygon point addition and closing line
                    if (polygonPoints.isEmpty()) {
                        polygonPoints.add(newPoint);
                    } else {
                        Point lastPoint = polygonPoints.get(polygonPoints.size() - 1);
                        canvas.add(new Line(lastPoint, newPoint, currentColor, lineWidth));
                        if (closingLine != null) {
                            canvas.removeLine(closingLine);
                        }
                        polygonPoints.add(newPoint);
                        if (polygonPoints.size() >= 3) {
                            closingLine = new Line(newPoint, polygonPoints.get(0), currentColor, lineWidth);
                            canvas.add(closingLine);
                        }
                    }
                    renderCanvas();
                } else if (isSquareMode) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    // Draw square with equal sides
                    int dx = endPoint.getX() - startPoint.getX();
                    int dy = endPoint.getY() - startPoint.getY();
                    int side = Math.max(Math.abs(dx), Math.abs(dy));
                    Point topLeft = new Point(startPoint.getX(), startPoint.getY());
                    Point topRight = new Point(startPoint.getX() + side, startPoint.getY());
                    Point bottomLeft = new Point(startPoint.getX(), startPoint.getY() + side);
                    Point bottomRight = new Point(startPoint.getX() + side, startPoint.getY() + side);
                    if (dx < 0) {
                        topLeft.setX(startPoint.getX() - side);
                        topRight.setX(startPoint.getX());
                        bottomLeft.setX(startPoint.getX() - side);
                        bottomRight.setX(startPoint.getX());
                    }
                    if (dy < 0) {
                        topLeft.setY(startPoint.getY() - side);
                        topRight.setY(startPoint.getY() - side);
                        bottomLeft.setY(startPoint.getY());
                        bottomRight.setY(startPoint.getY());
                    }
                    canvas.add(new Line(topLeft, topRight, currentColor, lineWidth));
                    canvas.add(new Line(topRight, bottomRight, currentColor, lineWidth));
                    canvas.add(new Line(bottomRight, bottomLeft, currentColor, lineWidth));
                    canvas.add(new Line(bottomLeft, topLeft, currentColor, lineWidth));
                    renderCanvas();
                } else if (isCircleMode) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    // Approximate circle with line segments
                    int dx = endPoint.getX() - startPoint.getX();
                    int dy = endPoint.getY() - startPoint.getY();
                    int radius = (int) Math.sqrt(dx * dx + dy * dy);
                    int segments = 36;
                    Point[] points = new Point[segments];
                    for (int i = 0; i < segments; i++) {
                        double angle = 2 * Math.PI * i / segments;
                        int x = startPoint.getX() + (int) (radius * Math.cos(angle));
                        int y = startPoint.getY() + (int) (radius * Math.sin(angle));
                        points[i] = new Point(x, y);
                    }
                    for (int i = 0; i < segments; i++) {
                        canvas.add(new Line(points[i], points[(i + 1) % segments], currentColor, lineWidth));
                    }
                    renderCanvas();
                } else if (!isFillMode && !isRubberMode && startPoint != null) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    if (isShiftPressed) {
                        endPoint = alignPoint(startPoint, endPoint);
                    }
                    Line line = new Line(startPoint, endPoint, currentColor, lineWidth);
                    if (isCtrlPressed) {
                        canvas.addDottedLine(line);
                    } else {
                        canvas.add(line);
                    }
                    renderCanvas();
                    startPoint = null;
                }
            }
        };

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_CONTROL -> isCtrlPressed = true;
                    case KeyEvent.VK_SHIFT -> isShiftPressed = true;
                    case KeyEvent.VK_F -> toggleFillMode();
                    case KeyEvent.VK_P -> togglePolygonMode();
                    case KeyEvent.VK_R -> toggleRubberMode();
                    case KeyEvent.VK_C -> clearCanvas();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) isCtrlPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) isShiftPressed = false;
            }
        };

        canvasPanel.addMouseListener(mouseAdapter);
        canvasPanel.addMouseMotionListener(mouseAdapter);
        canvasPanel.addKeyListener(keyAdapter);
    }

    private void toggleFillMode() {
        isFillMode = !isFillMode;
        if (isFillMode) {
            isSquareMode = false;
            isCircleMode = false;
            isRubberMode = false;
            if (isPolygonMode) {
                finalizePolygon();
            }
            isPolygonMode = false;
        }
        updateModeLabels();
        // Do not call renderCanvas to avoid overwriting existing lines
    }

    private void togglePolygonMode() {
        isPolygonMode = !isPolygonMode;
        if (isPolygonMode) {
            isSquareMode = false;
            isCircleMode = false;
            isRubberMode = false;
            isFillMode = false;
            polygonPoints = new ArrayList<>();
            if (closingLine != null) {
                canvas.removeLine(closingLine);
                closingLine = null;
            }
        } else {
            finalizePolygon();
            renderCanvas();
        }
        updateModeLabels();
    }

    private void toggleRubberMode() {
        isRubberMode = !isRubberMode;
        if (isRubberMode) {
            isFillMode = false;
            isSquareMode = false;
            isCircleMode = false;
            if (isPolygonMode) {
                finalizePolygon();
            }
            isPolygonMode = false;
        }
        updateModeLabels();
        renderCanvas();
    }

    private void clearCanvas() {
        canvas.clearLines();
        canvas.getDottedLines().clear();
        fillOperations.clear();
        polygonPoints.clear();
        if (closingLine != null) {
            canvas.removeLine(closingLine);
            closingLine = null;
        }
        raster.clear();
        canvasPanel.repaint();
    }

    private void splitLine(int mouseX, int mouseY) {
        // Split lines near mouse click
        ArrayList<Line> toRemove = new ArrayList<>();
        ArrayList<Line> toAdd = new ArrayList<>();
        double effectiveSize = Math.max(eraserSize, 3.0);
        ArrayList<Line> allLines = new ArrayList<>(canvas.getLines());
        allLines.addAll(canvas.getDottedLines());

        for (Line line : allLines) {
            Point p1 = line.getPoint1();
            Point p2 = line.getPoint2();
            Point closest = getClosestPointOnLine(mouseX, mouseY, p1.getX(), p1.getY(), p2.getX(), p2.getY());
            double distance = Math.sqrt(Math.pow(mouseX - closest.getX(), 2) + Math.pow(mouseY - closest.getY(), 2));
            double length = Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));

            if (distance <= effectiveSize && length > 2 * eraserSize) {
                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();
                double t = ((mouseX - p1.getX()) * dx + (mouseY - p1.getY()) * dy) / (dx * dx + dy * dy);
                t = Math.max(0, Math.min(1, t));
                double gap = 2 * eraserSize;
                double tGap = gap / length / 2;
                double t1 = t - tGap;
                double t2 = t + tGap;

                if (t1 >= 0 && t2 <= 1) {
                    int x1_new = (int) (p1.getX() + t1 * dx);
                    int y1_new = (int) (p1.getY() + t1 * dy);
                    int x2_new = (int) (p1.getX() + t2 * dx);
                    int y2_new = (int) (p1.getY() + t2 * dy);

                    toRemove.add(line);
                    toAdd.add(new Line(p1, new Point(x1_new, y1_new), line.getColor(), line.getThickness()));
                    toAdd.add(new Line(new Point(x2_new, y2_new), p2, line.getColor(), line.getThickness()));
                }
            }
        }

        for (Line line : toRemove) {
            if (canvas.getLines().contains(line)) {
                canvas.removeLine(line);
                canvas.getLines().addAll(toAdd);
            } else if (canvas.getDottedLines().contains(line)) {
                canvas.removeDottedLine(line);
                canvas.getDottedLines().addAll(toAdd);
            }
        }

        if (!toRemove.isEmpty()) {
            renderCanvas();
        }
    }

    private Point getClosestPointOnLine(int x, int y, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) return new Point(x1, y1);
        double t = ((x - x1) * dx + (y - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        return new Point((int) (x1 + t * dx), (int) (y1 + t * dy));
    }

    private Point alignPoint(Point start, Point end) {
        // Align line to horizontal or vertical
        int dx = end.getX() - start.getX();
        int dy = end.getY() - start.getY();
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        if (Math.max(adx, ady) <= 2 * Math.min(adx, ady)) {
            int magnitude = Math.max(adx, ady);
            return new Point(
                    start.getX() + magnitude * Integer.signum(dx),
                    start.getY() + magnitude * Integer.signum(dy)
            );
        } else if (adx > ady) {
            return new Point(end.getX(), start.getY());
        } else {
            return new Point(start.getX(), end.getY());
        }
    }

    private Point findClosestPoint(int mouseX, int mouseY) {
        int threshold = 10;
        ArrayList<Line> lines = new ArrayList<>(canvas.getLines());
        lines.addAll(canvas.getDottedLines());

        for (Line line : lines) {
            Point p1 = line.getPoint1();
            Point p2 = line.getPoint2();
            double distToP1 = Math.sqrt(Math.pow(mouseX - p1.getX(), 2) + Math.pow(mouseY - p1.getY(), 2));
            double distToP2 = Math.sqrt(Math.pow(mouseX - p2.getX(), 2) + Math.pow(mouseY - p2.getY(), 2));

            if (distToP1 < threshold) return p1;
            if (distToP2 < threshold) return p2;
        }
        return null;
    }

    private void prepareCanvasAndDraw(Runnable drawAction) {
        raster.clear();
        for (FillOperation op : fillOperations) {
            Color color = op.getColor();
            for (Point p : op.getPoints()) {
                if (p.getX() >= 0 && p.getX() < raster.getWidth() &&
                        p.getY() >= 0 && p.getY() < raster.getHeight()) {
                    raster.setPixel(p.getX(), p.getY(), color.getRGB());
                }
            }
        }
        lineRasterizer.rasterizeCanvas(canvas);
        drawAction.run();
        canvasPanel.repaint();
    }

    private void renderCanvas() {
        raster.clear();
        for (FillOperation op : fillOperations) {
            Color color = op.getColor();
            for (Point p : op.getPoints()) {
                if (p.getX() >= 0 && p.getX() < raster.getWidth() &&
                        p.getY() >= 0 && p.getY() < raster.getHeight()) {
                    raster.setPixel(p.getX(), p.getY(), color.getRGB());
                }
            }
        }
        lineRasterizer.rasterizeCanvas(canvas);
        canvasPanel.repaint();
    }

    private void start() {
        raster.setClearColor(CLEAR_COLOR);
        raster.clear();
        canvasPanel.repaint();
    }

    public List<FillOperation> getFillOperations() {
        return fillOperations;
    }
}