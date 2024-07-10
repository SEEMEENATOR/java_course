package ru.clevertec.check;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionHandler {
    public static void handle(String message) {
        System.out.println(message);
        try (PrintWriter writer = new PrintWriter(new FileWriter("result.csv"))) {
            writer.println("ERROR");
            writer.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
