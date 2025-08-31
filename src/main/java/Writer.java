


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Writer {
    private static final String REPORTS_DIR = "src/main/resources/reports/";

    public void writeReportToPdf(Map<String, int[]> report, String fileName,
                                 String title, int year, int month, Integer day) {
        try {
            // bazowy folder reports
            File reportsDir = new File("src/main/resources/reports");
            if (!reportsDir.exists()) reportsDir.mkdirs();

            // Folder roku
            File yearDir = new File(reportsDir, String.valueOf(year));
            if (!yearDir.exists()) yearDir.mkdirs();

            // folder docelowy
            File targetDir;
            if (day != null) { // Raport dzienny
                File monthDir = new File(yearDir, String.format("%02d", month));
                if (!monthDir.exists()) monthDir.mkdirs();
                targetDir = monthDir;
            } else if (month > 0) { // Raport miesięczny
                targetDir = yearDir;
            } else { // Raport roczny
                targetDir = reportsDir;
            }

            if (!targetDir.exists()) targetDir.mkdirs();

            File file = new File(targetDir, fileName);

            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Tytuł
            Paragraph titleParagraph = new Paragraph(title).setBold().setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(10);
            document.add(titleParagraph);

            // Tabela
            Table table = new Table(new float[]{4, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Cell().add(new Paragraph("Danie"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Ilosc"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Przychód"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));

            int totalRevenue = 0;
            for (Map.Entry<String, int[]> entry : report.entrySet()) {
                String dishName = entry.getKey();
                int quantity = entry.getValue()[0];
                int revenue = entry.getValue()[1];
                table.addCell(new Cell().add(new Paragraph(dishName)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(quantity)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(revenue)))
                        .setTextAlignment(TextAlignment.CENTER));
                totalRevenue += revenue;
            }

            table.addCell(new Cell(1, 2).add(
                            new Paragraph("Laczny przychód"))
                    .setBold().setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalRevenue + " Pln")))
                    .setBold().setTextAlignment(TextAlignment.CENTER));

            document.add(table);
            document.close();

            System.out.println("Raport zapisany w PDF: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==== Zapis do Excela ====
    public void writeExcel(String resourceName, List<Dish> selectedDishes) {
        try {
            LocalDate today = LocalDate.now();
            String year = String.valueOf(today.getYear());
            String month = String.format("%02d", today.getMonthValue());

            Path folderPath = Paths.get("src/main/resources/orders", year, month);
            Files.createDirectories(folderPath);

            String fileName = (resourceName.endsWith(".xlsx") ? resourceName.replace(".xlsx", "") : resourceName)
                    + "." + today + ".xlsx";

            Path filePath = folderPath.resolve(fileName);
            File file = filePath.toFile();

            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
                sheet = workbook.getSheetAt(0);
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Arkusz1");

                // Nagłówki dla nowego pliku
                String[] headers = {"Time", "Name Dish", "Price"};
                XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }
            }

            // Dopisanie nowych wierszy
            int rowIndex = sheet.getLastRowNum() + 1;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (Dish dish : selectedDishes) {
                XSSFRow rowNext = sheet.createRow(rowIndex++);
                rowNext.createCell(0).setCellValue(LocalDateTime.now().format(dtf));
                rowNext.createCell(1).setCellValue(dish.getNameDish());
                rowNext.createCell(2).setCellValue(dish.getPriceDish());
            }

            // Zapis do pliku
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();

            System.out.println("Dane zapisane w pliku Excel: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
