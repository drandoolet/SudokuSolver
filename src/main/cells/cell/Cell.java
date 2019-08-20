package main.cells.cell;

import main.cells.groups.CellGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cell extends AbstractCell {
    private final Coordinates coordinates;
    private final CellGroup row, column, square;
    private PossibleNumbers numbers = new PossibleNumbers();

    public Cell(int x, int y, CellGroup row, CellGroup column, CellGroup square) {
        coordinates = new Coordinates(x, y);
        this.row = row;
        this.column = column;
        this.square = square;

        List<CellGroup> groups = new ArrayList<>(3);
        groups.addAll(Arrays.asList(row, column, square));
        for (CellGroup group : groups) group.registerCell(this);
    }

    public ArrayList<Integer> getPossibleNumbers() {
        return numbers.getNumbers();
    }

    public Coordinates getCoordinates() { return coordinates; }


    private class PossibleNumbers {
        private final List<Integer> nums = new ArrayList<>();
        private volatile boolean isChanged = false;

        private void refresh() {
            synchronized (nums) { // todo optimize if multithreading added
                isChanged = false;
                nums.clear();

                ArrayList<Integer> rowNums = row.getNumbersNeeded();
                ArrayList<Integer> columnNums = column.getNumbersNeeded();
                ArrayList<Integer> squareNums = square.getNumbersNeeded();
                ArrayList<Integer> all = new ArrayList<>();
                all.addAll(rowNums);
                all.addAll(columnNums);
                all.addAll(squareNums);

                for (Integer i : all)
                    if (rowNums.contains(i) && columnNums.contains(i) && squareNums.contains(i))
                        nums.add(i);
            }
        }

        void setChanged(boolean changed) {
            isChanged = changed;
        }

        ArrayList<Integer> getNumbers() {
            synchronized (nums) {
                if (isChanged) refresh();
            }
            return new ArrayList<>(nums);
        }
    }

    private class Coordinates {
        final int x, y;

        Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }

}
