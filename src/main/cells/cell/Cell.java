package main.cells.cell;

import main.cells.groups.CellColumn;
import main.cells.groups.CellGroup;
import main.cells.groups.CellRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Cell extends AbstractCell {
    private final Coordinates coordinates;
    private final CellGroup row, column, square;
    private PossibleNumbers numbers = new PossibleNumbers();

    public Cell(CellRow row, CellColumn column, CellGroup square) {
        super(new HashSet<>(Arrays.asList(row, column, square)));

        coordinates = new Coordinates(row.getGroupNumber(), column.getGroupNumber());
        this.row = row;
        this.column = column;
        this.square = square;

        List<CellGroup> groups = new ArrayList<>(3);
        groups.addAll(Arrays.asList(row, column, square));
        for (CellGroup group : groups) group.registerCell(this);
    }

    @Override
    public String toString() {
        return String.format("%s contains: %d (%s). PN: %s. R/C/S: %d/%d/%d",
                coordinates,
                getCellNumber().getNumber(),
                getCellNumber().getStatus(),
                numbers.getNumbers(),
                row.getGroupNumber(),
                column.getGroupNumber(),
                square.getGroupNumber());
    }

    public ArrayList<Integer> getPossibleNumbers() {
        //if (getCellNumber().getStatus() != CellNumber.Status.FIXED) return numbers.getNumbers();
        //else return new ArrayList<>(); // TODO heap pollution, fix
        if (getCellNumber().getStatus() == CellNumber.Status.FIXED) return new ArrayList<>();
        else return numbers.getNumbers();
    }

    @Override
    public void setNumber(int n, CellNumber.Status status) {
        super.setNumber(n, status);
        numbers.setChanged();
    }

    @Override
    public void numbersChanged() {
        numbers.setChanged();
    }

    public Coordinates getCoordinates() { return coordinates; }


    private class PossibleNumbers {
        private final List<Integer> nums = new ArrayList<>();
        private volatile boolean isChanged = true;

        private void refresh() {
            synchronized (nums) { // todo optimize if multithreading added
                isChanged = false;
                nums.clear();

                ArrayList<Integer> rowNums = row.getNumbersNeeded();
                ArrayList<Integer> columnNums = column.getNumbersNeeded();
                ArrayList<Integer> squareNums = square.getNumbersNeeded();
                HashSet<Integer> all = new HashSet<>();
                all.addAll(rowNums);
                all.addAll(columnNums);
                all.addAll(squareNums);
/*
                if (coordinates.toString().equals("Cell(0-0)")) {
                    System.out.printf("(Cell.java) Cell(0-0):\nall: %s\nrows: %s\ncolumns: %s\nsquares: %s\n",
                            all.toString(), rowNums.toString(), columnNums.toString(), squareNums.toString());
                } */

                for (Integer i : all)
                    if (rowNums.contains(i) && columnNums.contains(i) && squareNums.contains(i))
                        nums.add(i);
            }
        }

        synchronized void setChanged() {
            isChanged = true;
        }

        ArrayList<Integer> getNumbers() {
            synchronized (nums) {
                if (isChanged) refresh();
            }
            return new ArrayList<>(nums);
        }
    }

    public class Coordinates {
        final int x, y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return String.format("Cell(%d-%d)", x, y);
        }
    }

}
