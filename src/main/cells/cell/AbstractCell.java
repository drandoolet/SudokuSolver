package main.cells.cell;

public abstract class AbstractCell {
    private CellNumber number = new CellNumber();

    public void setNumber(int n, CellNumber.Status status) { number.putNumber(n, status); }

    public CellNumber getCellNumber() {
        return number;
    }
}
