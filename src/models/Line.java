package models;
import java.awt.*;

public class Line {
	
	private Point point1;
	private Point point2;
	private Color color;
	private int thickness;

	public Line(Point point1, Point point2, Color color, int thickness) {
		this.point1 = point1;
		this.point2 = point2;
		this.color = color;
		this.thickness = thickness;
	}

	public Point getPoint1() {
		return point1;
	}
	public Point getPoint2(){
		return point2;
	}
	public Color getColor(){
		return color;
	}
	public int getThickness(){
		return thickness;
	}

}
