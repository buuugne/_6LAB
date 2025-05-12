package service;

import model.Person;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

public class CsvLoader implements Runnable {
    private final String filePath;
    private final List<Person> people;
    private final Runnable onFinish;

    public CsvLoader(String filePath, List<Person> people, Runnable onFinish) {
        this.filePath = filePath;
        this.people = people;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        try (Stream<String> lines = Files.lines(Paths.get(filePath)).skip(1)) {
            lines.map(line -> line.split(","))
                    .map(values -> new Person(
                            Integer.parseInt(values[0]),
                            values[1], values[2], values[3], values[4],
                            values[5], values[6], LocalDate.parse(values[7]) // bird_date
                    ))
                    .forEach(people::add);
            System.out.println("Įkeltas failas: " + filePath);
        } catch (IOException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Klaida įkeliant failą: " + filePath + " - " + e.getMessage());
        } finally {
            onFinish.run();
        }
    }
}