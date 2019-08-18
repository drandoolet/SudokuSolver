import java.util.Set;

public abstract class AbstractCellGroup {
    private Set<AbstractCell> cells;

    public abstract boolean isCellsLayoutValid();
    public abstract void registerCell(Cell cell);
}
