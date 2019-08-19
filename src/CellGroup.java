import java.util.*;

public class CellGroup extends AbstractCellGroup {
    private final Set<Cell> cells = new HashSet<>();
    private int cellsCount;
    private NumbersNeeded numbersNeeded = new NumbersNeeded();

    public CellGroup(Cell ...cells) {
        this.cells.addAll(Arrays.asList(cells));
        cellsCount = cells.length;
    }

    public CellGroup() {} // TODO unsafe

    @Override
    public void registerCell(Cell cell) {
        cells.add(cell);
    }

    @Override
    public boolean isCellsLayoutValid() {
        for (int i = 1; i <= cellsCount; i++) {
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

    public ArrayList<Integer> getNumbersNeeded() {
        return numbersNeeded.getNumbers();
    }

    private class NumbersNeeded {
        private final List<Integer> nums = new ArrayList<>();
        private volatile boolean isChanged = false;

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
                for (int i = 1; i <= cellsCount; i++) {
                    nums.add(i);
                }

                // checking if cells have a num, if -> delete
                for (int i = 1; i <= cellsCount; i++) {
                    for (Cell cell : cells) {
                        if (cell.getCellNumber().getNumber() == i) nums.remove(i);
                    }
                }
            }
        }
    }
}
