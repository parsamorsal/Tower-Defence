package TowDef.Map.MapCells;

public abstract class MapCell {
    protected final int x;
    protected final int y;

    MapCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String myToString() {
        return "C";
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
