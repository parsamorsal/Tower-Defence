package TowDef.Map;

import TowDef.Main;
import TowDef.Map.MapCells.LandCell;
import TowDef.Map.MapCells.MapCell;
import TowDef.Map.MapCells.PathCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * this class requires a map in which there is at least
 * one layer of landCells surrounding the whole map but
 * its beginning and end.
 */
public class MapReader {
    private static MapReader ourInstance = new MapReader();

    public static MapReader getInstance() {
        return ourInstance;
    }

    private MapReader() {
    }

    public Map loadMap(String mapFileAddress) {
        try {
            Map map;
            if ((map = readMap(Main.class.getResource(mapFileAddress).toURI())) == null) {
                System.out.println("map is null!");
                return null;
            }
            System.out.println("map is correct!");
            return linkPath(map);
        } catch (URISyntaxException e) {
            System.out.println("map is inCorrect!");
            return null;
        }
    }

    private Map readMap(URI mapFileAddress) {
        try {
            File file = new File(mapFileAddress);
            Scanner scanner = new Scanner(file);
            final int xSize = scanner.nextInt();
            final int ySize = scanner.nextInt();
            MapCell[][] content = new MapCell[xSize][ySize];
            PathCell[] beginning = new PathCell[5];
            PathCell[] end = new PathCell[5];
            int endCounter = 0, beginningCounter = 0;
            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {
                    String c;
                    if (scanner.hasNext())
                        c = scanner.next();
                    else
                        c = "";
                    switch (c) {
                        case "b":
                            if ((x + 1 != xSize) && y != 0) {
                                content[x][y] = new PathCell(x, y);
                                beginning[beginningCounter] = (PathCell) content[x][y];
                            } else {
                                content[x][y] = new PathCell(x, y);
                                beginning[4 - beginningCounter] = (PathCell) content[x][y];
                            }
                            beginningCounter++;
                            break;
                        case "e":
                            if ((x + 1 != xSize) && y != 0) {
                                content[x][y] = new PathCell(x, y);
                                end[4 - endCounter] = (PathCell) content[x][y];
                            } else {
                                content[x][y] = new PathCell(x, y);
                                end[endCounter] = (PathCell) content[x][y];
                            }
                            endCounter++;
                            break;
                        case "p":
                            content[x][y] = new PathCell(x, y);
                            break;
                        case "l":
                            content[x][y] = new LandCell(x, y);
                            break;
                        case "":
                            throw (new Exception("Err: map file is incomplete!"));
                    }
                }
            }
            System.out.println("map loaded correctly!");
            return new Map(content, beginning, end, null, null);//out and pathLength are set temporary!
        } catch (FileNotFoundException e) {
            System.out.println("Err: No map was found in: " + mapFileAddress);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Err: map size is not correct!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map linkPath(Map map) {
        MapCell[][] content = map.getContent();
        PathCell[] mapBeginning = map.getBeginning();
        PathCell[] mapEnd = map.getEnd();
        PathCell[] currentCell = new PathCell[5];
        int[] pathLength = new int[5];
        int xDir = 0, yDir = 0;
        if (mapBeginning[0].getY() == 0)
            yDir = 1;
        else if (mapBeginning[0].getY() + 1 == map.getXSize())
            yDir = -1;
        else if (mapBeginning[0].getX() == 0)
            xDir = 1;
        else if (mapBeginning[0].getX() + 1 == map.getYSize())
            xDir = -1;
        for (int i = 0; i < 5; i++) {
            mapBeginning[i].setPrev(mapBeginning[i]);
            currentCell[i] = mapBeginning[i].setNext((PathCell) content[mapBeginning[i].getX() + xDir][mapBeginning[i].getY() + yDir]);
            currentCell[i].setPrev(mapBeginning[i]);
        }
        int[] isLineDone;
        int doneLineCount;
        int flag = 0;
        while (flag != 5) {
            doneLineCount = 0;
            isLineDone = new int[]{0, 0, 0, 0, 0};
            while (doneLineCount != 5) {
                PathCell[] lastCell = new PathCell[5];
                System.arraycopy(currentCell, 0, lastCell, 0, 5);
                doneLineCount = 0;
                for (int i = 0; i < 5; i++) {
                    if (currentCell[i] == mapEnd[i] ||
                            content[currentCell[i].getX() + xDir][currentCell[i].getY() + yDir] instanceof LandCell) {
                        isLineDone[i] = 1;
                        doneLineCount += isLineDone[i];
                    }
                    if (isLineDone[i] == 0) {
                        currentCell[i] = currentCell[i].setNext(
                                (PathCell) content[currentCell[i].getX() + xDir][currentCell[i].getY() + yDir]);
                        currentCell[i].setPrev(lastCell[i]);
                    }
                }
            }
            if (currentCell[0] != mapEnd[0] && content[currentCell[0].getX() + yDir][currentCell[0].getY() - xDir] instanceof LandCell) {
                currentCell[1] = currentCell[1].getPrev(1);
                currentCell[2] = currentCell[2].getPrev(2);
                currentCell[3] = currentCell[3].getPrev(3);
                currentCell[4] = currentCell[4].getPrev(4);
                int temp = xDir;
                xDir = -yDir;
                yDir = temp;
            } else if (currentCell[4] != mapEnd[4] && content[currentCell[4].getX() - yDir][currentCell[4].getY() + xDir] instanceof LandCell) {
                currentCell[3] = currentCell[3].getPrev(1);
                currentCell[2] = currentCell[2].getPrev(2);
                currentCell[1] = currentCell[1].getPrev(3);
                currentCell[0] = currentCell[0].getPrev(4);
                int temp = yDir;
                yDir = -xDir;
                xDir = temp;
            }
            flag = 0;
            for (int i = 0; i < 5; i++) {
                if (currentCell[i] != mapEnd[i]) {
                    break;
                }
                flag++;
            }
        }
        PathCell out = new PathCell(1000, 1000);
        for (int i = 0; i < 5; i++) {
            mapBeginning[i].setPrev(mapBeginning[i]);
            mapEnd[i].setNext(out);
        }
        for (int i = 0; i < 5; i++) {
            PathCell here = mapBeginning[i];
            while (here != out) {
                here = here.getNext();
                pathLength[i]++;
            }
        }
        out.setNext(out);
        return new Map(content, mapBeginning, mapEnd, out, pathLength);
    }
}