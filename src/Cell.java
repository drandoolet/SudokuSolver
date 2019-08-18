import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cell extends AbstractCell {
    private final AbstractCellGroup row, column, square;
    private final List<AbstractCellGroup> groups = new ArrayList<>(3);

    public Cell(AbstractCellGroup row, AbstractCellGroup column, AbstractCellGroup square) {
        this.row = row;
        this.column = column;
        this.square = square;

        groups.addAll(Arrays.asList(row, column, square));
        for (AbstractCellGroup group : groups) group.registerCell(this);
    }


}
