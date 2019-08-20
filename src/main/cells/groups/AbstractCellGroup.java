package main.cells.groups;

import main.cells.cell.AbstractCell;
import main.cells.cell.Cell;

import java.util.Set;

public abstract class AbstractCellGroup {
    private Set<AbstractCell> cells;

    public abstract boolean isCellsLayoutValid();
    public abstract void registerCell(Cell cell);
}
