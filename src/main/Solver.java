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

    public void printAnswer() {
        FieldParser.Printer.printField(rows);
    }

    public void solve() {
        findObviousNumbersForCells();


    }

    private void findObviousNumbersForCells() {
        for (Cell cell : cells) {
            printCellInfo(cell, 1, 2); // TESTING
            printCellInfo(cell, 1, 1);
            printCellInfo(cell, 2, 1);
            printCellInfo(cell, 8, 8);

            if (cell.getPossibleNumbers().size() == 1) {
                System.out.println("wtf? if worked for "+cell.getCoordinates());
                cell.setNumber(cell.getPossibleNumbers().get(0), CellNumber.Status.FIXED);
                //findObviousNumbersForCells();
                break;
            }
        }
    }

    private void printCellInfo(Cell cell) {
        System.out.println(cell.getCellInfo());
    }

    private void printCellInfo(Cell cell, int ifX, int ifY) {
        if (cell.getCoordinates().getX() == ifX && cell.getCoordinates().getY() == ifY)
            printCellInfo(cell);
    }
}
