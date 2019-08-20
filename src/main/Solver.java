package main;

import main.cells.groups.*;
import main.cells.cell.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver {
    private final List<CellRow> rows;
    private final List<CellColumn> columns;
    private final List<CellGroup> squares;
    private final Set<Cell> cells = new HashSet<>();

    public Solver(List<CellRow> rows, List<CellColumn> columns, List<CellGroup> squares) {
        this.rows = new ArrayList<>(rows);
        this.columns = new ArrayList<>(columns);
        this.squares = new ArrayList<>(squares);

        for (CellGroup group : rows) cells.addAll(group.getCells());
    }

    public void solve() {
        findObviousNumbersForCells();


    }

    private void findObviousNumbersForCells() {
        for (Cell cell : cells) {
            if (cell.getPossibleNumbers().size() == 1) {
                cell.setNumber(cell.getPossibleNumbers().get(0), CellNumber.Status.FIXED);
                findObviousNumbersForCells();
                break;
            }
        }
    }
}
