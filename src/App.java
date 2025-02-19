import models.Line;
import models.Point;
import rasterizers.Rasterizer;
import rasterizers.TrivialLineRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private MouseAdapter mouseAdapter;
    private Point point;
    private Rasterizer rasterizer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        rasterizer = new TrivialLineRasterizer(raster);

        createAdapters();
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);

        panel.requestFocus();
        panel.requestFocusInWindow();
    }

    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                point = new Point(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point point2 = new Point(e.getX(), e.getY());

                Line line = new Line(point, point2, Color.cyan);

                rasterizer.rasterize(line);

                panel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }
        };
    }

}
