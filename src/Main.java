import model.Person;
import service.CsvLoader;
import service.FilterService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Person> allPeople = Collections.synchronizedList(new ArrayList<>());
        String[] files = {
                "MOCK_DATA1.csv",
                "MOCK_DATA2.csv",
                "MOCK_DATA3.csv"
        };

        CountDownLatch latch = new CountDownLatch(files.length);

        for (String file : files) {
            new Thread(new CsvLoader(file, allPeople, latch::countDown)).start();
        }

        // Laukiame kol visi failai bus įkelti
        latch.await();
        System.out.println("Visi failai įkelti. Iš viso įrašų: " + allPeople.size());

        // Filtravimas pagal gimimo datą (pvz. žmonės gimę 1990-2000)
        List<Person> filteredByDate = FilterService.filterByDateRange(
                allPeople,
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2000, 12, 31)
        );

        // Rikiavimas pagal pavardę (mažėjimo tvarka)
        List<Person> sortedByNameDesc = FilterService.sortByName(allPeople, false);

        // Filtravimas pagal ID reikšmę
        List<Person> filteredById = FilterService.filterByIdRange(allPeople, 100, 500);

        // Spausdiname rezultatus lentelėje
        System.out.println("\n Žmonės gimę tarp 1990-01-01 ir 2000-12-31:");
        printAsTable(filteredByDate);

        System.out.println("\n Surikiuota pagal pavardę (Z–A):");
        printAsTable(sortedByNameDesc);

        System.out.println("\n Žmonės kurių ID yra tarp 100 ir 500:");
        printAsTable(filteredById);
    }

    private static void printAsTable(List<Person> people) {
        String format = "| %-5s | %-12s | %-12s | %-25s | %-8s | %-15s | %-15s | %-10s |%n";

        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf(format, "ID", "First Name", "Last Name", "Email", "Gender", "Country", "Domain", "Birth Date");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (Person p : people) {
            System.out.printf(format,
                    p.id, p.firstName, p.lastName, p.email, p.gender, p.country, p.domainName, p.birthDate);
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
    }
}