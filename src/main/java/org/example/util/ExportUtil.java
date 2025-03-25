package org.example.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class ExportUtil {


    public static boolean exportToCSV(Object[][] data, String[] headers, String hotelName, String dateString, String filePrefix) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvare fișier CSV");

        // Set default file name
        String defaultFileName = filePrefix + "_" + hotelName.replaceAll("\\s+", "_") + "_" + dateString.replaceAll("[\\s:]+", "_") + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Add .csv extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write CSV header
                for (int i = 0; i < headers.length; i++) {
                    writer.append(headers[i]);
                    if (i < headers.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");

                // Write data rows
                for (Object[] row : data) {
                    for (int i = 0; i < row.length; i++) {
                        String value = row[i] != null ? row[i].toString() : "";
                        // Escape commas in values
                        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                            writer.append("\"").append(value.replace("\"", "\"\"")).append("\"");
                        } else {
                            writer.append(value);
                        }

                        if (i < row.length - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }

                return true;
            } catch (IOException e) {
                System.err.println("Eroare la exportul în CSV: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public static boolean exportToDOC(Object[][] data, String[] headers, String hotelName, String dateString, String title, String filePrefix) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvare fișier DOC");

        // Set default file name
        String defaultFileName = filePrefix + "_" + hotelName.replaceAll("\\s+", "_") + "_" + dateString.replaceAll("[\\s:]+", "_") + ".docx";
        fileChooser.setSelectedFile(new File(defaultFileName));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Word Documents (*.docx)", "docx");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Add .docx extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".docx")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".docx");
            }

            try (XWPFDocument document = new XWPFDocument();
                 FileOutputStream out = new FileOutputStream(fileToSave)) {

                // Add title
                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText(title);
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                // Add a blank line
                document.createParagraph();

                // Create table
                XWPFTable table = document.createTable();

                // Create header row
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < headers.length; i++) {
                    if (i == 0) {
                        headerRow.getCell(0).setText(headers[i]);
                    } else {
                        headerRow.addNewTableCell().setText(headers[i]);
                    }
                }

                // Add data rows
                for (Object[] row : data) {
                    XWPFTableRow dataRow = table.createRow();
                    for (int i = 0; i < row.length; i++) {
                        dataRow.getCell(i).setText(row[i] != null ? row[i].toString() : "");
                    }
                }

                // Write document to file
                document.write(out);

                return true;
            } catch (IOException e) {
                System.err.println("Eroare la exportul în DOC: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
}