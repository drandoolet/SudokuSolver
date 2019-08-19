import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cell extends AbstractCell {
    private final CellGroup row, column, square;
    private final List<CellGroup> groups = new ArrayList<>(3);
    private PossibleNumbers numbers = new PossibleNumbers();

    public Cell(CellGroup row, CellGroup column, CellGroup square) {
        this.row = row;
        this.column = column;
        this.square = square;

        groups.addAll(Arrays.asList(row, column, square));
        for (CellGroup group : groups) group.registerCell(this);
    }

    public ArrayList<Integer> getPossibleNumbers() {
        return numbers.getNumbers();
    }



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
}
