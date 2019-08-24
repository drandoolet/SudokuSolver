package main.cells.cell;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCell implements NumbersChangedListener {
    private CellNumber number = new CellNumber();
    private Set<NumbersChangedListener> listeners;

     AbstractCell(Set<NumbersChangedListener> listeners) {
        this.listeners = listeners;
    }

    public void setNumber(int n, CellNumber.Status status) {
        number.putNumber(n, status);
        listeners.forEach(NumbersChangedListener::numbersChanged);
    }

    public CellNumber getCellNumber() {
        return number;
    }

    @Override
    public abstract void numbersChanged();
}
