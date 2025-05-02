package models;

import java.util.ArrayList;

public class LineCanvas {
    private ArrayList<Line> lines;
    private ArrayList<Line> dottedLines;

    public LineCanvas(ArrayList<Line> lines, ArrayList<Line> dottedLines) {
        this.lines = new ArrayList<>(lines);
        this.dottedLines = new ArrayList<>(dottedLines);
    }
    public ArrayList<Line> getLines(){
        return lines;
    }
    public ArrayList<Line> getDottedLines(){
        return dottedLines;
    }
    public void add(Line line) {
        this.lines.add(line);
    }
    public void addDottedLine(Line line) {
        this.dottedLines.add(line);
    }
    public void clearLines() {
        this.lines.clear();
    }
    public void removeLine(Line line) {
        if (this.lines.contains(line)) {
        this.lines.remove(line);
        }
    }
    public void removeDottedLine(Line line) {
        if (this.dottedLines.contains(line)) {
            this.dottedLines.remove(line);
        }
    }
}

