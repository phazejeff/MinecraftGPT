package com.phazejeff.mcgpt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Key {
    private static final String FILE_PATH = "config\\MinecraftGPT.txt";

    public static String read() {
        File f = new File(FILE_PATH);

        try {
            if (f.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Scanner scanner;
        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found even though it should have been created.");
            return null;
        }

        String data = scanner.nextLine();
        scanner.close();
        return data;
    }

    public static void write(String key) {
        FileWriter writer;
        try {
            writer = new FileWriter(FILE_PATH);
            writer.write(key);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
