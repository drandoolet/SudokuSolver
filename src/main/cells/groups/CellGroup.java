package main.cells.groups;

import main.cells.cell.Cell;

import java.util.*;

public class CellGroup extends AbstractCellGroup {
    private final ArrayList<Cell> cells = new ArrayList<>();
    private int groupNumber;
    private NumbersNeeded numbersNeeded = new NumbersNeeded();

    public CellGroup(Cell ...cells) {
        this.cells.addAll(Arrays.asList(cells));
    }

    @Override
    public String toString() {
        return String.format("CellGroup (%s %d): NN: %s. Cells: %s. Layout valid: %b",
                this.getClass().toString(),
                groupNumber,
                getNumbersNeeded().toString(),
                getCellsNumbers().toString(),
                isCellsLayoutValid());
    }

    public CellGroup(int groupNumber) {
        this.groupNumber = groupNumber;
    } // TODO unsafe

    public int getGroupNumber() {
        return groupNumber;
    }

    public ArrayList<Cell> getCells() { return cells; }

    @Override
    public void registerCell(Cell cell) {
        cells.add(cell);
    }

    @Override
    public boolean isCellsLayoutValid() {
        for (int i = 1; i <= cells.size(); i++) {
            if (!cellsHaveValue(i)) return false;
        }
        return true;
    }

    private boolean cellsHaveValue(int i) {
        for (Cell cell : cells) {
            if (cell.getCellNumber().getNumber() == i) return true;
        }
        return false;
    }

    private ArrayList<Integer> getCellsNumbers() {
        ArrayList<Integer> list = new ArrayList<>(cells.size());
        for (Cell cell: cells) list.add(cell.getCellNumber().getNumber());
        return list;
    }

    public ArrayList<Integer> getNumbersNeeded() {
        return numbersNeeded.getNumbers();
    }

    private class NumbersNeeded {
        private final List<Integer> nums = new ArrayList<>();
        private volatile boolean isChanged = true;

        void numbersChanged() { isChanged = true; }

        private ArrayList<Integer> getNumbers() {
            synchronized (nums) {
                if (isChanged) refresh();
            }
            return new ArrayList<>(nums);
        }

        private NumbersNeeded() {}

        private void refresh() {
            synchronized (nums) {
                // set changed to false (nums are up-to-date)
                isChanged = false;
                // starting point - group needs every num
                nums.clear();
                for (int i = 1; i <= cells.size(); i++) {
                    nums.add(i);
                }

                // checking if cells have a num, if -> delete
                for (int i = 1; i <= cells.size(); i++) {
                    for (Cell cell : cells) {
                        StringBuilder s = new StringBuilder();
                        if (cell.getCoordinates().toString().equals("Cell(0-0)")) {
                            s.append(String.format("%s contains: %d. PN: %s",
                                    cell.getCoordinates(),
                                    cell.getCellNumber().getNumber(),
                                    cell.getPossibleNumbers().toString()));
                            s.append(String.format("CellGroup now tests %s for having %d : ", cell.getCoordinates(), i));

                            if (cell.getCellNumber().getNumber() == i) {
                                s.append("true!");
                                nums.remove((Integer) i);
                            } else s.append("false!");
                            //System.out.println(s.toString());
                        } else if (cell.getCellNumber().getNumber() == i) {
                            nums.remove((Integer) i);
                        }
                    }
                }
            }
        }
    }
}
