package main;

import main.cells.groups.*;
import main.cells.cell.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Solver {
    private final List<CellRow> rows;
    private final List<CellColumn> columns;
    private final List<CellGroup> squares;
    private final ArrayList<Cell> cells = new ArrayList<>();

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
        long time = System.nanoTime();
        //test();
        findObviousNumbersForCells();
        tryFindNonObviousNumbers(0);
        for (Cell cell: cells) {
            if (cell.getPossibleNumbers().size() != 0) System.out.println(cell);
        }
        System.out.println("TOTAL time elapsed in solve() (sec): "+getSecondsElapsedFrom(time));
    }

    private float getSecondsElapsedFrom(long time) {
        return (float) (TimeUnit.NANOSECONDS.toSeconds((System.nanoTime()-time)*100))/100;
    }

    private void findObviousNumbersForCells() {
        for (Cell cell : cells) {
            ArrayList<Integer> possibleNumbers = cell.getPossibleNumbers();

            if (possibleNumbers.size() != 0) {
                System.out.println(cell); // debug

                if (cell.getPossibleNumbers().size() == 1) {
                    System.out.println("found an obvious num (" + cell.getPossibleNumbers().get(0) + ") for " + cell.getCoordinates());
                    cell.setNumber(cell.getPossibleNumbers().get(0), CellNumber.Status.FIXED);
                    printAnswer();
                    findObviousNumbersForCells();
                    break;
                }
            }
        }
    }

    private boolean tryFindNonObviousNumbers(int startingPoint) {
        System.out.printf("Now starts tryFind(%d)\n", startingPoint);
        if (startingPoint == cells.size()) {
            System.out.printf("tryFind(%d) has reached the end.\n", startingPoint);
            return true;
        }
        Cell cell = null;
        for (Cell cell1: cells) {
            if (cell1.getCellNumber().getStatus() == CellNumber.Status.FREE) {
                cell = cell1;
                break;
            }
        }
        if (cell == null) {
            System.out.printf("tryFind(%d) has found no PN in cells. Solution found?\n", startingPoint);
            return true;
        }

        ArrayList<Integer> possibleNumbers = cell.getPossibleNumbers();
        for (Integer i : possibleNumbers) {
            cell.setNumber(i, CellNumber.Status.PUT);
            if (tryFindNonObviousNumbers(startingPoint+1)) return true;
        }
        System.out.printf("tryFind(%d) has found no solution.\n", startingPoint);
        cell.setNumber(0, CellNumber.Status.FREE);
        return false;
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
