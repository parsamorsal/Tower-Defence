package TowDef.players.AI;

import TowDef.Game.Game;
import TowDef.Game.TimeLine;
import TowDef.Map.MapCells.PathCell;
import TowDef.Units.Intruders.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

class WaveReader {
    public static final int WAVES_COUNT = 28;
    private static int waveNumber = 0;
    private Scanner sc;
    private int lineCount = 0;

    public void BeginNextWave() {
        if (lineCount == 0 && waveNumber < WAVES_COUNT) {
            waveNumber++;
            try {
                File file = new File(getClass().getResource("/files/settings/waves/" + waveNumber + ".txt").toURI());
                calcNextWaveInfo(new Scanner( new File(getClass().getResource("/files/settings/waves/" + (waveNumber+1) + ".txt").toURI())));
                sc = new Scanner(file);
                lineCount = sc.nextInt();
            } catch (URISyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void calcNextWaveInfo(Scanner sc) {
        String result = new String();
        int lineCount = sc.nextInt();
        int fi = 0, li = 0, tr = 0, da = 0, ca = 0;
        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < 5; j++) {
                switch (sc.next()) {
                    case "Fi":
                        fi++;
                        break;
                    case "Li":
                        li++;
                        break;
                    case "Tr":
                        tr++;
                        break;
                    case "Da":
                        da++;
                        break;
                    case "Ca":
                        ca++;
                        break;
                }
            }
        }
        if (fi != 0) {
            result += fi + " fire intruder" + (fi == 1 ? "\n" : "s\n");
        }
        if (li != 0) {
            result += li + " light intruder" + (li == 1 ? "\n" : "s\n");
        }
        if (tr != 0) {
            result += tr + " tree intruder" + (tr == 1 ? "\n" : "s\n");
        }
        if (da != 0) {
            result += da + " dark intruder" + (da == 1 ? "\n" : "s\n");
        }
        if (ca != 0) {
            result += ca + " casual intruder" + (ca == 1 ? "\n" : "s\n");
        }
        AI.getInstance().setNextWaveInfo(result);
    }

    public void SendNextLine(PathCell[] beginning) {
        if (lineCount > 0) {
            boolean isMapBeginningEmpty = true;
            for (int i = 0; i < 5; i++)
                if (beginning[i].getContent().size() != 0)
                    isMapBeginningEmpty = false;
            if (isMapBeginningEmpty) {
                for (int j = 0; j < 5; j++) {
                    switch (sc.next()) {
                        case "Fi":
                            beginning[j].addContent(new FireIntruder(beginning[j], Game.getInstance().getMap().getPathLength(j)));
                            break;
                        case "Li":
                            beginning[j].addContent(new LightIntruder(beginning[j], Game.getInstance().getMap().getPathLength(j)));
                            break;
                        case "Tr":
                            beginning[j].addContent(new TreeIntruder(beginning[j], Game.getInstance().getMap().getPathLength(j)));
                            break;
                        case "Da":
                            beginning[j].addContent(new DarkIntruder(beginning[j], Game.getInstance().getMap().getPathLength(j)));
                            break;
                        case "Ca":
                            beginning[j].addContent(new CasualIntruder(beginning[j], Game.getInstance().getMap().getPathLength(j)));
                            break;
                        case "null":
                            break;
                    }
                }
                lineCount--;
            }
        }
    }

    public static int getWaveNumber() {
        return waveNumber;
    }
}
