package main.cells.cell;

public class CellNumber {
    private int number;
    private Status status = Status.FREE;

    public void putNumber(int n, Status status) {
        number = n;
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        FIXED, PUT, FREE
    }
}
