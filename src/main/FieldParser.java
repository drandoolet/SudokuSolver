package main;

import main.cells.cell.*;

public class FieldParser {
    public static final String FIELD_WALL = "|";
    public static final String FIELD_CELL = ":%n:";

    public static void printEmptyField(int cellsPerRow) {
        for (int j = 0; j < cellsPerRow-1; j++) {
            System.out.print(".-.-");
        }
        System.out.println();

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

            for (int j = 0; j < cellsPerRow-1; j++) {
                System.out.print(".-.-");
            }
            System.out.println();
        }
    }
}
