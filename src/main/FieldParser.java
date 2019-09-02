package main;

import main.cells.cell.*;
import main.cells.groups.CellColumn;
import main.cells.groups.CellGroup;
import main.cells.groups.CellRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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

    static SudokuField parseField(String parse) {
        class Helper {
            int getSquareNum(int rowNumber, int columnNumber, int squareSize) {
                int row = squareSize * (rowNumber / squareSize);
                int column = columnNumber / squareSize;
                return row + column;
            }
        }
        Helper helper = new Helper();
        String[] strings = parse.split("\n");
        if (!testParseString(strings)) throw new IllegalArgumentException();

        SudokuField.Builder builder = new SudokuField.Builder();
        builder.initializeLists(strings[0].length());
        int squareSize = (int) Math.sqrt(strings[0].length());

        for (int rowNum = 0; rowNum < strings.length; rowNum++) {
            char[] chars = strings[rowNum].toCharArray(); // TODO what if num is 2 digit ?
            for (int j = 0; j < chars.length; j++) {
                int n = Integer.parseInt(String.valueOf(chars[j]));
                Cell cell = new Cell(builder.getCellRow(rowNum), builder.getCellColumn(j),
                        builder.getCellSquare(helper.getSquareNum(rowNum, j, squareSize)));
                if (n == 0) cell.setNumber(0, CellNumber.Status.FREE);
                else cell.setNumber(n, CellNumber.Status.FIXED);
            }
        }

        return builder.build();
    }

    private static boolean testParseString(String... strings) {
        if (Math.sqrt(strings[0].length()) != (int) Math.sqrt(strings[0].length())) return false;
        //if (strings[0].length() % 3 != 0) return false;
        for (int i = 1; i < strings.length; i++) {
            if (strings[i-1].length() != strings[i].length()) return false;
        }
        return true;
    }

    private static void parseAndRegisterRow(String s,
                                            int rowNumber,
                                            ArrayList<CellRow> rows,
                                            ArrayList<CellColumn> columns,
                                            ArrayList<CellGroup> groups) throws NumberFormatException {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int n = Integer.parseInt(String.valueOf(chars[i]));
            Cell cell = new Cell(rows.get(rowNumber), columns.get(i), groups.get(3 * (rowNumber / 3) + i / 3));
            if (n == 0) cell.setNumber(0, CellNumber.Status.FREE);
            else cell.setNumber(n, CellNumber.Status.FIXED);
        }
    }

    static class Printer {

        private static final String FIELD_WALL = "|";
        private static final String FIELD_CELL = ":%n:";
        private static final String FIELD_FLOOR = "....";
        private static final String FIELD_MASK = ":%d:";

        static void printEmptyField(int cellsPerRow) {
            printFloor(cellsPerRow);

            for (int i = 0; i < cellsPerRow / 3; i++) {
                for (int j = 0; j < cellsPerRow / 3; j++) {
                    System.out.print(FIELD_WALL);
                    for (int e = 0; e < cellsPerRow / 3; e++) {
                        for (int k = 0; k < cellsPerRow / 3; k++) {
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
            for (int i = 0; i < size - 1; i++) {
                System.out.print(FIELD_FLOOR);
            }
            System.out.println();
        }

        private static void printCell(Cell cell) {
            System.out.printf(FIELD_MASK, cell.getCellNumber().getNumber());
        }

        private static void printRow(CellRow row) {
            int i = 0;
            int size = (int) Math.sqrt(row.getCells().size());
            System.out.print(FIELD_WALL);
            for (Cell cell : row.getCells()) {
                if (i % size == 0) System.out.print(FIELD_WALL);
                printCell(cell);
                i++;
            }
            System.out.println(FIELD_WALL);
        }

        @Deprecated
        static void printField(List<CellRow> rows) {
            int i = 0;
            int size = rows.size() / 3;
            for (CellRow row : rows) {
                if (i % size == 0) printFloor(rows.size());
                printRow(row);
                i++;
            }
            printFloor(rows.size());
        }

        static void printFieldToConsole(SudokuField field) {
            int i = 0;
            int size = (int) Math.sqrt(field.getRows().size());
            for (CellRow row : field.getRows()) {
                if (i % size == 0) printFloor(field.getRows().size());
                printRow(row);
                i++;
            }
            printFloor(field.getRows().size());
        }
    }

    static final class SudokuField {
        private final List<CellRow> rows;
        private final List<CellColumn> columns;
        private final List<CellGroup> squares;

        private SudokuField(ArrayList<CellRow> rows, ArrayList<CellColumn> columns, ArrayList<CellGroup> squares) {
            this.rows = Collections.unmodifiableList(rows);
            this.columns = Collections.unmodifiableList(columns);
            this.squares = Collections.unmodifiableList(squares);
        }

        public List<CellRow> getRows() {
            return rows;
        }

        public List<CellColumn> getColumns() {
            return columns;
        }

        public List<CellGroup> getSquares() {
            return squares;
        }

        public List<Cell> getCells() {
            ArrayList<Cell> cells = new ArrayList<>();
            /*for (CellRow row : rows)
                cells.addAll(row.getCells());
            return cells; */
            for (CellGroup group : squares)
                cells.addAll(group.getCells());
            return cells;
        }

        static class Builder {
            private final ArrayList<CellRow> rows;
            private final ArrayList<CellColumn> columns;
            private final ArrayList<CellGroup> squares;

            private Builder(ArrayList<CellRow> rows, ArrayList<CellColumn> columns, ArrayList<CellGroup> squares) {
                this.rows = rows;
                this.columns = columns;
                this.squares = squares;
            }

            Builder() {
                rows = new ArrayList<>();
                columns = new ArrayList<>();
                squares = new ArrayList<>();
            }

            void initializeLists(int size) {
                for (int i = 0; i < size; i++) {
                    rows.add(new CellRow(i));
                    columns.add(new CellColumn(i));
                    squares.add(new CellGroup(i));
                }
            }

             CellRow getCellRow(int idx) {
                return rows.get(idx);
            }

             CellColumn getCellColumn(int idx) {
                return columns.get(idx);
            }

             CellGroup getCellSquare(int idx) {
                return squares.get(idx);
            }

            SudokuField build() {
                 return new SudokuField(rows, columns, squares);
            }
        }
    }
}
