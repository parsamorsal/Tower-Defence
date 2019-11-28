package TowDef.Map.MapCells;

import TowDef.Units.Towers.Tower;
import TowDef.Units.Towers.*;

public class LandCell extends MapCell {
    private Tower content;

    public LandCell(int x, int y) {
        super(x, y);
    }

    public Tower getContent() {
        return content;
    }

    public void setContent(Tower content) {
        this.content = content;
    }

    @Override
    public String myToString() {
        return "l";
    }
}