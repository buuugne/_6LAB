package ui;

import model.Person;
import service.CsvLoader;
import service.FilterService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class SwingUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Person> people = Collections.synchronizedList(new ArrayList<>());
    private JTextArea logArea;

    public void start() {
        String[] files = {"MOCK_DATA1.csv", "MOCK_DATA2.csv", "MOCK_DATA3.csv"};
        CountDownLatch latch = new CountDownLatch(files.length);

        // Pirma sukuriame GUI, kad logArea būtų inicializuota
        SwingUtilities.invokeLater(this::createAndShowGUI);

        for (String file : files) {
            String finalFile = file; // reikia dėl lambda
            new Thread(new CsvLoader(file, people, () -> {
                System.out.println("Įkeltas failas: " + finalFile);
                SwingUtilities.invokeLater(() -> logArea.append("Įkeltas: " + finalFile + "\n"));
                latch.countDown();
            })).start();
        }

        new Thread(() -> {
            try {
                latch.await();
                SwingUtilities.invokeLater(() -> {
                    logArea.append("Visi failai įkelti!\n");
                    fillTable(people);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Žmonių duomenys – Swing UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 600);

        // Sukuriame log sritį
        logArea = new JTextArea(5, 80);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        String[] columnNames = {"ID", "Vardas", "Pavardė", "Email", "Lytis", "Šalis", "Domenas", "Gimimo data"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Mygtukai
        JButton sortDescButton = new JButton("Rikiuoti pavardę (Z–A)");
        sortDescButton.addActionListener(e -> {
            List<Person> sorted = FilterService.sortByName(people, false);
            fillTable(sorted);
        });

        JButton sortAscButton = new JButton("Rikiuoti pavardę (A–Z)");
        sortAscButton.addActionListener(e -> {
            List<Person> sorted = FilterService.sortByName(people, true);
            fillTable(sorted);
        });

        // Datos filtravimas
        JTextField dateFromField = new JTextField("1990-01-01", 10);
        JTextField dateToField = new JTextField("2000-12-31", 10);

        JButton filterByDateButton = new JButton("Filtruoti pagal datą");
        filterByDateButton.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(dateFromField.getText());
                LocalDate to = LocalDate.parse(dateToField.getText());
                List<Person> filtered = FilterService.filterByDateRange(people, from, to);
                fillTable(filtered);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Blogas datos formatas. Naudok YYYY-MM-DD");
            }
        });

        // ID filtravimo laukai
        JTextField idFromField = new JTextField("100", 5);
        JTextField idToField = new JTextField("500", 5);

        JButton filterByIdButton = new JButton("Filtruoti pagal ID");
        filterByIdButton.addActionListener(e -> {
            try {
                int from = Integer.parseInt(idFromField.getText());
                int to = Integer.parseInt(idToField.getText());
                List<Person> filtered = FilterService.filterByIdRange(people, from, to);
                fillTable(filtered);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Įveskite teisingus skaičius ID laukuose");
            }
        });

        // Panelis mygtukams
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(sortDescButton);
        panel.add(sortAscButton);
        panel.add(new JLabel("Nuo (YYYY-MM-DD):"));
        panel.add(dateFromField);
        panel.add(new JLabel("Iki:"));
        panel.add(dateToField);
        panel.add(filterByDateButton);
        panel.add(new JLabel("ID nuo:"));
        panel.add(idFromField);
        panel.add(new JLabel("iki:"));
        panel.add(idToField);
        panel.add(filterByIdButton);

        // Sudedam viską į langą
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.getContentPane().add(logScroll, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private void fillTable(List<Person> data) {
        tableModel.setRowCount(0);
        for (Person p : data) {
            tableModel.addRow(new Object[]{
                    p.id, p.firstName, p.lastName, p.email, p.gender, p.country, p.domainName, p.birthDate
            });
        }
    }
}