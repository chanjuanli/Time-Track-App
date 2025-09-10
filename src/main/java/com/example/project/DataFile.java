package com.example.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Long.parseLong;

/**
 * The Data file class for reading and writing to a data file.
 */
public class DataFile {

    /**
     * Read records from the data file and populates the provided ArrayList.
     *
     * @param records records the ArrayList to store the read records.
     */
    public static void read(ArrayList<Record> records) {
        File dataFile = new File("data.txt");
        boolean success = true;
        try {
            Scanner reader = new Scanner(dataFile);
            while (reader.hasNext()) {
                String line = reader.nextLine();
                String[] fields = line.split(",");
                //If the line contains exactly four fields,
                // it creates a new Record object rec using the values from the fields
                // then adds it to the records ArrayList.
                if (fields.length == 4) {
                    Record rec = new Record(fields[0], fields[1], fields[2], parseLong(fields[3]));
                    records.add(rec);
                }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            //if the file is not found, set success to false.
            success = false;
        } finally {
            if (success) {
                System.out.println("data file found!");
            } else {
                System.out.println("data file missing!");
            }
        }
    }

    /**
     * Write records from the provided ArrayList to the data file.
     *
     * @param records the records
     */
    public static void write(ArrayList<Record> records) {
        boolean success = true;
        try {
            PrintWriter file = new PrintWriter(new FileOutputStream("data.txt"));
            for (int i = 0; i < records.size(); i++) {
                Record rec = records.get(i);
                //Retrieving each record from the ArrayList and writing its project name, start date, completion date, and duration (in seconds) to the file.
                file.println(rec.getProjName() + "," + rec.getStartDate() + "," + rec.getCompleteDate() + "," + Long.toString(rec.getDurationInSecs()));
            }
            file.close();
        } catch (FileNotFoundException ex) {
            // If file creation fails, set success to false
            success = false;
        } finally {
            if (success) {
                System.out.println("data file created/updated!");
            } else {
                System.out.println("data file cannot be created/updated!");
            }
        }
    }

}
