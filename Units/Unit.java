package TowDef.Units;

import TowDef.Active;
import TowDef.Map.MapCells.MapCell;

public abstract class Unit implements Active {
    protected MapCell cell = null;

    public Unit(MapCell cell) {
        this.cell = cell;
    }

    public Unit() {
    }
}
