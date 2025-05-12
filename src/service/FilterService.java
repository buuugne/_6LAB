package service;

import model.Person;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FilterService {
    public static List<Person> filterByDateRange(List<Person> people, LocalDate from, LocalDate to) {
        return people.stream()
                .filter(p -> !p.birthDate.isBefore(from) && !p.birthDate.isAfter(to))
                .collect(Collectors.toList());
    }

    public static List<Person> sortByName(List<Person> people, boolean ascending) {
        Comparator<Person> comparator = Comparator.comparing(p -> p.lastName);
        if (!ascending) comparator = comparator.reversed();

        return people.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public static List<Person> filterByIdRange(List<Person> people, int min, int max) {
        return people.stream()
                .filter(p -> p.id >= min && p.id <= max)
                .collect(Collectors.toList());
    }
}