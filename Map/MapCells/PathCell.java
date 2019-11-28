package TowDef.Map.MapCells;

import java.util.LinkedList;

import TowDef.Units.Castle;
import TowDef.Units.Intruders.Intruder;
import TowDef.Units.*;
import TowDef.Units.Intruders.*;

public class PathCell extends MapCell {
    LinkedList<Intruder> content = new LinkedList<>();
    Castle castle = null;
    private PathCell prev;
    private PathCell next;

    public PathCell(int x, int y) {
        super(x, y);
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Castle getCastle() {
        return castle;
    }

    public void removeContent() {
        if (!content.isEmpty()) {
            content.remove();
        }
    }

    public void addContent(Intruder intr) {
        content.add(intr);
    }

    public PathCell setPrev(PathCell prev) {
        return this.prev = prev;
    }

    public PathCell getPrev() {
        return prev;
    }

    public PathCell getPrev(int step) {
        PathCell result = this;
        for (int i = 0; i < step; i++) {
            result = result.prev;
        }
        return result;
    }

    public PathCell setNext(PathCell next) {
        return this.next = next;
    }

    public PathCell getNext() {
        return next;
    }

    public LinkedList<Intruder> getContent() {
        return content;
    }

    public void setContent(LinkedList<Intruder> content) {
        this.content = content;
    }

    public PathCell getNext(int step) {
        PathCell result = this;
        for (int i = 0; i < step; i++) {
            result = result.next;
        }
        return result;
    }

    /*public int getPathCellDistance() {return cellDistance;}*/
    /*public void setPathCellDistance(int cellDistance) {this.cellDistance = cellDistance;}*/
    public int xDir() {
        if (this.next != null)
            return this.next.getX() - this.x;
        return this.x - this.prev.getX();
    }

    public int yDir() {
        if (this.next != null)
            return this.next.getY() - this.y;
        return this.y - this.prev.getY();
    }

    @Override
    public String myToString() {
        String result = "P";
        if (this.getPrev() != null)
            result += "p";
        if (this.getNext() != null)
            result += "n";
        return result;
    }
}


