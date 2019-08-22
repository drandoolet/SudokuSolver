package main;

import main.cells.groups.*;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ParseTest.test();
    }

    private static class ParseTest {
        static void test() {
            //FieldParser.Printer.printEmptyField(9);

            int rowsNum = 9;

            ArrayList<CellRow> rows = new ArrayList<>(rowsNum);
            ArrayList<CellColumn> columns = new ArrayList<>(rowsNum);
            ArrayList<CellGroup> groups = new ArrayList<>(rowsNum);

            for (int i = 0; i < rowsNum; i++) {
                rows.add(new CellRow(i));
                columns.add(new CellColumn(i));
                groups.add(new CellGroup(i));
            }

            try {
                FieldParser.startParsingAndRegistering(rows, columns, groups);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FieldParser.Printer.printField(rows);

            System.out.println("now starts Solver\n");

            Solver solver = new Solver(rows, columns, groups);
            solver.solve();
            solver.printAnswer();
        }
    }
}
