package main;

import main.cells.cell.*;
import main.cells.groups.CellColumn;
import main.cells.groups.CellGroup;
import main.cells.groups.CellRow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class FieldParser {

    static SudokuField parseField(String parse, int squareXSize, int squareYSize) {
        List<List<Integer>> lists = convertStringFieldIntoIntList(parse);

        class Helper {
            private int getSquareNum(int rowNumber, int columnNumber, int squareSizeX, int squareSizeY) {
                int row = squareSizeX * (rowNumber / squareSizeX);
                int column = columnNumber / squareSizeY;
                return row + column;
            }
        }
        Helper helper = new Helper();

        testParseString(lists);

        SudokuField.Builder builder = new SudokuField.Builder();
        builder.initializeLists(lists.size());

        for (int rowNum = 0; rowNum < lists.size(); rowNum++) {
            List<Integer> row = lists.get(rowNum);
            for (int columnNum = 0; columnNum < row.size(); columnNum++) {
                int cellNum = row.get(columnNum);
                registerCell(cellNum, rowNum, columnNum,
                        helper.getSquareNum(rowNum, columnNum, squareXSize, squareYSize),
                        builder);
            }
        }

        return builder.build();
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
                Cell cell = Cell.CellFactory.newCell(builder.getCellRow(rowNum), builder.getCellColumn(j),
                        builder.getCellSquare(helper.getSquareNum(rowNum, j, squareSize)));
                if (n == 0) cell.setNumber(0, CellNumber.Status.FREE);
                else cell.setNumber(n, CellNumber.Status.FIXED);
            }
        }

        return builder.build();
    }

    private static boolean testParseString(List<List<Integer>> lists) {
        int y = lists.size();
        for (List<Integer> list : lists) {
            if (list.size() != y) throw new IllegalArgumentException("Not a square!");
        }

        return true;
    }

    private static boolean testParseString(String... strings) {
        //if (Math.sqrt(strings[0].length()) != (int) Math.sqrt(strings[0].length())) return false;
        //if (strings[0].length() % 3 != 0) return false;
        for (int i = 1; i < strings.length; i++) {
            if (strings[i-1].length() != strings[i].length()) return false;
        }
        return true;
    }

    private static void registerRow(char[] chars,
                                    int rowNumber,
                                    ArrayList<CellRow> rows,
                                    ArrayList<CellColumn> columns,
                                    ArrayList<CellGroup> groups) {

    }

    private static void registerCell(int number, int rowIdx, int columnIdx, int groupIdx,
                                     ArrayList<CellRow> rows,
                                     ArrayList<CellColumn> columns,
                                     ArrayList<CellGroup> groups) {
        Cell cell = Cell.CellFactory.newCell(rows.get(rowIdx), columns.get(columnIdx), groups.get(groupIdx));
        if (number == 0) cell.setNumber(0, CellNumber.Status.FREE);
        else cell.setNumber(number, CellNumber.Status.FIXED);
    }

    private static void registerCell(int number, int rowIdx, int columnIdx, int groupIdx,
                                     SudokuField.Builder builder) {
        Cell cell = Cell.CellFactory.newCell(builder.getCellRow(rowIdx),
                builder.getCellColumn(columnIdx),
                builder.getCellSquare(groupIdx));
        if (number == 0) cell.setNumber(0, CellNumber.Status.FREE);
        else cell.setNumber(number, CellNumber.Status.FIXED);
    }


    public static List<List<Integer>> convertStringFieldIntoIntList(String parse) {
        Scanner scanner = new Scanner(parse).useDelimiter(" ");
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();

        String s = null;
        while (true) {
            try {
                s = scanner.next();
            } catch (NoSuchElementException e) {
                break;
            }

            if (s.contains("\n")) {
                String[] ss = s.split("\n");
                list.add(Integer.parseInt(ss[0]));

                ArrayList<Integer> list2 = new ArrayList<>(list);
                //Collections.copy(list2, list);
                result.add(list2);
                list.clear();

                if (ss.length != 2) break;

                s = ss[1];
            }
            list.add(Integer.parseInt(s));
        }

        for (int i=0; i<result.size(); i++) {
            System.out.print("Line "+i+": { ");
            List<Integer> arrayList = result.get(i);
            for (Integer integer : arrayList) {
                System.out.printf("[%d]", integer);
            }
            System.out.println(" }");
        }

        return result;
    }

    private static void parseAndRegisterRow(String s,
                                            int rowNumber,
                                            ArrayList<CellRow> rows,
                                            ArrayList<CellColumn> columns,
                                            ArrayList<CellGroup> groups) throws NumberFormatException {
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int n = Integer.parseInt(String.valueOf(chars[i]));
            registerCell(n, rowNumber, i, (3 * (rowNumber / 3) + i / 3),
                    rows, columns, groups);
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

    static class JsonParser {
        private JsonParser() {
        }

        enum Tokens {
            ROW("row"),
            COLUMN("column"),
            GROUP("group"),
            NUMBER("number"),
            STATUS("status");
            final String pattern;

            Tokens(String pattern) {
                this.pattern = pattern;
            }

            enum Main {
                CELLS("cells"),
                FIELD_SIZE("field_size"),
                SQUARE_X_SIZE("square_x_size"),
                SQUARE_Y_SIZE("square_y_size");
                final String pattern;

                Main(String pattern) {
                    this.pattern = pattern;
                }
            }
        }

        private static void addCell(JSONObject where, Cell cell) {
            JSONObject object = new JSONObject();
            object.put(Tokens.ROW.pattern, cell.getCoordinates().getX());
            object.put(Tokens.COLUMN.pattern, cell.getCoordinates().getY());
            object.put(Tokens.GROUP.pattern, cell.getCoordinates().getS());
            object.put(Tokens.NUMBER.pattern, cell.getCellNumber().getNumber());
            object.put(Tokens.STATUS.pattern, cell.getCellNumber().getStatus().toString());
            JSONArray array = (JSONArray) where.get(Tokens.Main.CELLS.pattern);
            array.add(object);
        }

        public static SudokuField parseField(String jsonString) throws ParseException {
            JSONObject main = (JSONObject) new JSONParser().parse(jsonString);
            JSONArray array = (JSONArray) main.get(Tokens.Main.CELLS.pattern);

            SudokuField.Builder builder = new SudokuField.Builder();
            builder.initializeLists(getIntFromLongObject(main.get(Tokens.Main.FIELD_SIZE.pattern)));

            for (int i = 0; i < array.size(); i++) {
                JSONCellObject object = JSONCellObject.newJsonCellObject((JSONObject) array.get(i));
                registerCell(object.getInt(Tokens.NUMBER),
                        object.getInt(Tokens.ROW),
                        object.getInt(Tokens.COLUMN),
                        object.getInt(Tokens.GROUP),
                        builder);
            }

            return builder.build();
        }

        public static String getJsonString(SudokuField field) {
            JSONObject main = new JSONObject();
            main.put(Tokens.Main.CELLS.pattern, new JSONArray());
            main.put(Tokens.Main.FIELD_SIZE.pattern, field.getRows().size());
            //main.put(Tokens.Main.SQUARE_X_SIZE.pattern, );
            //main.put(Tokens.Main.SQUARE_Y_SIZE.pattern, );

            for (Cell cell: field.getCells())
                addCell(main, cell);

            return main.toJSONString();
        }

        private static int getIntFromLongObject(Object o) {
            return ((Long) o).intValue();
        }

        private static class JSONCellObject {
            private final JSONObject object;

            private JSONCellObject(JSONObject object) {
                this.object = object;
            }

            static JSONCellObject newJsonCellObject(JSONObject object) {
                return new JSONCellObject(object);
            }

            int getInt(Tokens token) {
                return getIntFromLongObject(object.get(token.pattern));
            }
        }
    }
}
