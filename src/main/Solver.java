package main;

import main.cells.groups.*;
import main.cells.cell.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public CellGroup getCellGroupByClassAndNumber(Groups group, int number) {
        switch (group) {
            case ROW: return rows.get(number);
            case COLUMN: return columns.get(number);
            case SQUARE: return squares.get(number);
            default: throw new IllegalArgumentException("No such CellGroup");
        }
    }

    public void printAnswer() {
        FieldParser.Printer.printField(rows);
    }

    public void solve() {
        test();
        findObviousNumbersForCells();

    }

    private void findObviousNumbersForCells() {
        for (Cell cell : cells) {
            if (cell.getPossibleNumbers().size() == 1) {
                System.out.println("found an obvious num ("+cell.getPossibleNumbers().get(0)+") for "+cell.getCoordinates());
                cell.setNumber(cell.getPossibleNumbers().get(0), CellNumber.Status.FIXED);
                //findObviousNumbersForCells();
                break;
            }
        }
    }

    private Cell findCell(int x, int y) {
        for (Cell cell : getCellGroupByClassAndNumber(Groups.ROW, x).getCells()) {
            if (cell.getCoordinates().getY() == y) return cell;
        }
        return null; //TODO create exception cellnotfound or smth like
    }

    private class SolverLogger {
        Logger logger = Logger.getLogger("Solver.java");

        void log(String s) {
            log(s, Level.CONFIG);
        }

        void log(String s, Level level) {
            logger.log(level, s);
        }
    }

    public enum Groups {
        ROW,
        COLUMN,
        SQUARE
    }

    private void test() {
        System.out.println(findCell(0, 0));
        System.out.println(getCellGroupByClassAndNumber(Groups.ROW, 0));
        System.out.println(getCellGroupByClassAndNumber(Groups.COLUMN, 0));
        System.out.println(getCellGroupByClassAndNumber(Groups.SQUARE, 0));

    }
}
