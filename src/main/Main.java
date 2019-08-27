package main;

import main.cells.groups.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ParseTest.test();
    }

    private static class ParseTest {
        static void test() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                StringBuilder builder = new StringBuilder();
                while (true) {
                    String s = br.readLine();
                    if (s.equals("")) break;

                    builder.append(s).append('\n');
                }
                Solver solver = new Solver(FieldParser.parseField(builder.toString()));
                FieldParser.Printer.printFieldToConsole(solver.solve());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
