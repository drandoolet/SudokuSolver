package main;

import main.cells.cell.CellNumber;
import org.json.simple.parser.ParseException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;



public class Main {
    public static void main(String[] args) {

        //PerformanceTest.test(ParseTest.getStringFieldFromConsole(), 3, 3, 1, 1);

        FieldParser.SudokuField field = FieldParser.parseField(ParseTest.getStringFieldFromConsole(),3,3);

        String jsonField = FieldParser.JsonParser.getJsonString(field);
        System.out.println("jsonField: "+jsonField);

        try {
            System.out.println("parsed back:");
            FieldParser.Printer.printFieldToConsole(FieldParser.JsonParser.parseField(jsonField));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        jsonField = FieldParser.JsonParser.getJsonString(new Solver(field).solve());
        System.out.println("solved: ");
        try {
            FieldParser.Printer.printFieldToConsole(FieldParser.JsonParser.parseField(jsonField));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static class ParseTest {
        static String getStringFieldFromConsole() {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    String s = br.readLine();
                    if (s.equals("")) break;

                    builder.append(s).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        static void test(String s) {
            Solver solver = new Solver(FieldParser.parseField(s));
            FieldParser.Printer.printFieldToConsole(solver.solve());
        }
    }

    private static class PerformanceTest {
        static void test(FieldParser.SudokuField field, int times, int preheat) {
            System.out.println("Preheating...");
            Solver solver = new Solver(field);
            for (int i = 0; i < preheat; i++) {
                solver.solve();
            }
            System.out.println("Performance test starts now.");
            CalcHelper.TimeCounterHelper time = CalcHelper.getTimeCounterHelper();

            for (int i = 0; i < times; i++) {
                solver = new Solver(randomizeField(field));
                System.out.printf("Iteration #%d...\n", i);
                long l = System.nanoTime();
                FieldParser.Printer.printFieldToConsole(solver.solve());
                //solver.solve();

                time.update(System.nanoTime() - l);
            }
            System.out.println(time);
        }

        static void test(String field, int squareXSize, int squareYSize, int times, int preheat) {
            System.out.println("let's test new m");
            FieldParser.convertStringFieldIntoIntList(field);

            System.out.println("Preheating...");
            Solver solver = new Solver(FieldParser.parseField(field, squareXSize, squareYSize));
            for (int i = 0; i < preheat; i++) {
                solver.solve();
            }
            System.out.println("Performance test starts now.");
            CalcHelper.TimeCounterHelper time = CalcHelper.getTimeCounterHelper();

            for (int i = 0; i < times; i++) {
                solver = new Solver(FieldParser.parseField(randomizeField(field), squareXSize, squareYSize));
                System.out.printf("Iteration #%d...\n", i);
                long l = System.nanoTime();
                FieldParser.Printer.printFieldToConsole(solver.solve());
                //solver.solve();

                time.update(System.nanoTime() - l);
            }
            System.out.println(time);
        }

        private static FieldParser.SudokuField randomizeField(FieldParser.SudokuField field) {
            Random random = new Random();
            int x = field.getRows().size();
            int y = field.getRows().get(0).getCells().size();
            for (int i = 1; i < 10; i++) {
                field.getRows().get(random.nextInt(x)).getCells().get(random.nextInt(y)).setNumber(i, CellNumber.Status.PUT);
            }
            return field;
        }

        private static String randomizeField(String field) { // NOTE: inf loop prone if field is not 0-only
            Random random = new Random();
            List<List<Integer>> lists = FieldParser.convertStringFieldIntoIntList(field);
            for (int i = 0; i < lists.size(); i++) {
                List<Integer> list = lists.get(i);
                int r = random.nextInt(list.size() - 1);
                list.remove(r);
                list.add(r, i);
            }
            StringBuilder builder = new StringBuilder();

            for (List<Integer> list: lists) {
                for (Integer integer: list) {
                    builder.append(integer).append(" ");
                }
                builder.replace(builder.length()-2, builder.length()-1, "\n");
            }

            System.out.println("Randomized field:");
            System.out.println(builder.toString());

            return builder.toString();
        }
    }
}
