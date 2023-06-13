package com.phazejeff.mcgpt.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Utility class for reading and writing the API key to a file.
 * Uses Text File I/O
 * 
 * @author phazejeff
 * @version 1.0
 */
public class Key {
    private static final String FILE_PATH = "config\\MinecraftGPT.txt";

    /**
     * Reads the API key from the file.
     *
     * @return The API key if it exists in the file, or null if the file does not exist or an error occurs.
     */
    public static String read() {
        File f = new File(FILE_PATH);

        // Creates a new file if it doesn't exist
        // This shouldn't throw any errors unless for whatever reason it doesn't have permission to create files in the config folder
        // Which for Minecraft, should never happen
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

        // Read data in file
        String data = scanner.nextLine();
        scanner.close();
        return data;
    }

    /**
     * Writes the API key to the file.
     *
     * @param key The API key to be written.
     */
    public static void write(String key) {
        FileWriter writer;
        try {
            // Write to file
            writer = new FileWriter(FILE_PATH);
            writer.write(key);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
