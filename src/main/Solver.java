package main;

import main.cells.groups.*;
import main.cells.cell.*;
import main.FieldParser.SudokuField;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Solver {
    private final List<Cell> cells;
    private final SudokuField field;

     Solver(SudokuField field) {
        cells = field.getCells();
        this.field = field;
    }

    private CellGroup getCellGroupByClassAndNumber(Groups group, int number) {
        switch (group) {
            case ROW: return field.getRows().get(number);
            case COLUMN: return field.getColumns().get(number);
            case SQUARE: return field.getSquares().get(number);
            default: throw new IllegalArgumentException("No such CellGroup");
        }
    }

    public SudokuField solve() {
         //FieldParser.Printer.printFieldToConsole(field);
        //long time = System.nanoTime();
        //test();
        findObviousNumbersForCells();
        tryFindNonObviousNumbers(0);
        for (Cell cell: cells) {
            if (cell.getPossibleNumbers().size() != 0) System.out.println(cell);
        }
        //System.out.println("TOTAL time elapsed in solve() (sec): "+getSecondsElapsedFrom(time));
        return field;
    }

    private void findObviousNumbersForCells() {
        for (Cell cell : cells) {
            ArrayList<Integer> possibleNumbers = cell.getPossibleNumbers();

            if (possibleNumbers.size() != 0) {
                if (cell.getPossibleNumbers().size() == 1) {
                    //System.out.println("found an obvious num (" + cell.getPossibleNumbers().get(0) + ") for " + cell.getCoordinates());
                    cell.setNumber(cell.getPossibleNumbers().get(0), CellNumber.Status.FIXED);
                    findObviousNumbersForCells();
                    break;
                }
            }
        }
    }

    private boolean tryFindNonObviousNumbers(int startingPoint) {
        System.out.printf("Now starts tryFind(%d)\n", startingPoint);
        if (startingPoint == cells.size()) {
            //System.out.printf("tryFind(%d) has reached the end.\n", startingPoint);
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
            //System.out.printf("tryFind(%d) has found no PN in cells. Solution found?\n", startingPoint);
            return true;
        }

        ArrayList<Integer> possibleNumbers = cell.getPossibleNumbers();
        for (Integer i : possibleNumbers) {
            cell.setNumber(i, CellNumber.Status.PUT);
            if (tryFindNonObviousNumbers(startingPoint+1)) return true;
        }
        //FieldParser.Printer.printFieldToConsole(field);
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
        FieldParser.Printer.printFieldToConsole(field);

        System.out.println(findCell(1, 11));
        System.out.println(getCellGroupByClassAndNumber(Groups.ROW, 2));
        System.out.println(getCellGroupByClassAndNumber(Groups.COLUMN, 7));
        System.out.println(getCellGroupByClassAndNumber(Groups.SQUARE, 1));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
