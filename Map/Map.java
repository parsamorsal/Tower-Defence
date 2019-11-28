package TowDef.Map;

import TowDef.Map.MapCells.MapCell;
import TowDef.Map.MapCells.PathCell;
import TowDef.players.AI.AI;

public class Map {
    private MapCell[][] content;
    private final int xSize;
    private final int ySize;
    private final PathCell[] beginning;
    private final PathCell[] end;
    private final int[] pathLength;
    private final PathCell out;

    Map(MapCell[][] content, PathCell[] beginning, PathCell[] end, PathCell out,int[] pathLength) {
        this.content = content;
        this.beginning = beginning;
        this.end = end;
        this.out = out;
        this.pathLength = pathLength;
        this.xSize = content.length;
        this.ySize = content[0].length;
        AI.getInstance().setMapBeginning(this.beginning);
    }

    public MapCell[][] getContent() {
        return content;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public PathCell[] getBeginning() {
        return beginning;
    }

    public PathCell[] getEnd() {
        return end;
    }

    public PathCell getOut() {
        return out;
    }

    public int getPathLength(int i) {
        return pathLength[i];
    }

    public String myToString() {
        String result = "";
        for (int j = 0; j < ySize; j++) {
            for (int i = 0; i < xSize; i++) {
                System.out.print(content[i][j].myToString() + "\t");
            }
            System.out.println();
        }
        return result;
    }

    public String toString() {
        String result = "";
        for (int j = 0; j < xSize; j++) {
            for (int i = 0; i < ySize; i++) {
                result += content[i][j].toString();
            }
            result += "/n";
        }
        return result;
    }
}
