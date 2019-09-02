package main;

import main.cells.cell.CellNumber;
import main.cells.groups.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static main.CalcHelper.getTimeCounterHelper;

public class Main {
    public static void main(String[] args) {
        PerformanceTest.test(
                FieldParser.parseField(
                        ParseTest.getStringFieldFromConsole()),
                50, 50);
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
            CalcHelper.TimeCounterHelper time = CalcHelper.getTimeCounterHelper(times);

            for (int i = 0; i < times; i++) {
                solver = new Solver(randomizeField(field));
                System.out.printf("Iteration #%d...\n", i);
                long l = System.nanoTime();
                //FieldParser.Printer.printFieldToConsole(solver.solve());
                solver.solve();

                time.update(System.nanoTime() - l);
            }
            time.finish();
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
    }
}
