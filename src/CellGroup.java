import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CellGroup extends AbstractCellGroup {
    private final Set<Cell> cells = new HashSet<>();
    private int cellsCount;

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
            if (cell.getNumber() == i) return true;
        }
        return false;
    }
}
