package main;

import main.cells.cell.*;
import main.cells.groups.CellColumn;
import main.cells.groups.CellGroup;
import main.cells.groups.CellRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

 class FieldParser {

    static void startParsingAndRegistering(
            ArrayList<CellRow> rows,
            ArrayList<CellColumn> columns,
            ArrayList<CellGroup> groups) throws IOException {
        System.out.println("Please enter a row (e.g. 102000503 for a 9-digit-per-row field).");
        System.out.println("Be sure to enter 9 digits.");
        System.out.println("Input ends automatically when you create a square or throws an exception otherwise.");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String s = bufferedReader.readLine();
        int length = s.length();
        if (length != 9) throw new IllegalArgumentException("You need to enter 9 digits!");

        parseAndRegisterRow(s, 0, rows, columns, groups);

        for (int i = 1; i < length; i++) {
            parseAndRegisterRow(bufferedReader.readLine(), i, rows, columns, groups);
        }
    }

    private static void parseAndRegisterRow(String s,
                                            int rowNumber,
                                            ArrayList<CellRow> rows,
                                            ArrayList<CellColumn> columns,
                                            ArrayList<CellGroup> groups) throws NumberFormatException
    {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int n = Integer.parseInt(String.valueOf(chars[i]));
            Cell cell = new Cell(i, rowNumber, rows.get(rowNumber), columns.get(i), groups.get(i%3));
            cell.setNumber(n, CellNumber.Status.FIXED);
        }
    }

    static class Printer {

        private static final String FIELD_WALL = "|";
        private static final String FIELD_CELL = ":%n:";
        private static final String FIELD_FLOOR = "....";
        private static final String FIELD_MASK = ":%d:";

        static void printEmptyField(int cellsPerRow) {
            printFloor(cellsPerRow);

            for (int i = 0; i < cellsPerRow/3; i++) {
                for (int j = 0; j < cellsPerRow/3; j++) {
                    System.out.print(FIELD_WALL);
                    for (int e = 0; e < cellsPerRow/3; e++) {
                        for (int k = 0; k < cellsPerRow/3; k++) {
                            System.out.printf(":%d:", i);
                        }
                        System.out.print(FIELD_WALL);
                    }
                    System.out.println();
                }

                printFloor(cellsPerRow);
            }
        }

        private static void printFloor(int size) {
            for (int i = 0; i < size-1; i++) {
                System.out.print(FIELD_FLOOR);
            }
            System.out.println();
        }

        private static void printCell(Cell cell) {
            System.out.printf(FIELD_MASK, cell.getCellNumber().getNumber());
        }

        private static void printRow(CellRow row) {
            int i = 0;
            System.out.print(FIELD_WALL);
            for (Cell cell : row.getCells()) {
                if (i%3==0) System.out.print(FIELD_WALL);
                printCell(cell);
                i++;
            }
            System.out.println(FIELD_WALL);
        }

        static void printField(List<CellRow> rows) {
            int i = 0;
            for (CellRow row : rows) {
                if (i%3==0) printFloor(rows.size());
                printRow(row);
                i++;
            }
            printFloor(rows.size());
        }
    }
}
