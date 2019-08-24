package main.cells.groups;

import main.cells.cell.AbstractCell;
import main.cells.cell.Cell;
import main.cells.cell.NumbersChangedListener;

import java.util.Set;

public abstract class AbstractCellGroup implements NumbersChangedListener {
    private Set<AbstractCell> cells;

    public abstract boolean isCellsLayoutValid();
    public abstract void registerCell(Cell cell);

    @Override
    public abstract void numbersChanged();
}
