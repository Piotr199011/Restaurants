import com.itextpdf.text.DocumentException;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Writer {

    public static void writeCsv(String resourceName, Map<String,Integer>map){
        try {
            FileWriter writer=new FileWriter(resourceName);
            for (Map.Entry<String,Integer>entry:map.entrySet()){
                writer.append('"').append(entry.getKey()).append('"')
                        .append(',')
                        .append(entry.getValue().toString())
                        .append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void csvToPdf(String csvFilePath, String pdfFilePath){
        Document document=new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();
            Files.lines(Paths.get(csvFilePath))
                    .forEach(line -> {
                        document.add(new Paragraph(line));
                    });
            document.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeExcel(String resourceName, List<Dish> selectedDishes) {
        try {

            LocalDate today = LocalDate.now();
            String year = String.valueOf(today.getYear());
            String month = String.format("%02d", today.getMonthValue());

            Path folderPath = Paths.get("src/main/resources/orders", year, month);
            Files.createDirectories(folderPath); //


            String fileName = (resourceName.endsWith(".xlsx") ? resourceName.replace(
                    ".xlsx", "") : resourceName)
                    + "." + today + ".xlsx";

            Path filePath = folderPath.resolve(fileName);
            File file = filePath.toFile();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            if (file.exists()) {
                try (FileInputStream is = new FileInputStream(file);
                     FileOutputStream fos = new FileOutputStream(file)) {

                    Workbook workbook = new XSSFWorkbook(is);
                    Sheet sheet = workbook.getSheetAt(0);
                    int rowIndex = sheet.getLastRowNum() + 1;

                    for (Dish dish : selectedDishes) {
                        XSSFRow rowNext = (XSSFRow) sheet.createRow(rowIndex++);
                        rowNext.createCell(0).setCellValue(LocalDateTime.now().format(dtf));
                        rowNext.createCell(1).setCellValue(dish.getNameDish());
                        rowNext.createCell(2).setCellValue(dish.getPriceDish());
                    }

                    workbook.write(fos);
                    System.out.println("Dane zostały zapisane w istniejącym pliku: " + filePath);
                }
            } else {
                String[] headers = {"Time", "Name Dish", "Price"};

                try (XSSFWorkbook workbook = new XSSFWorkbook();
                     FileOutputStream fos = new FileOutputStream(file)) {

                    XSSFSheet sheet = workbook.createSheet("Arkusz1");
                    XSSFRow headerRow = sheet.createRow(0);
                    for (int i = 0; i < headers.length; i++) {
                        XSSFCell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                    }

                    int rowIndex = 1;
                    for (Dish dish : selectedDishes) {
                        XSSFRow rowNext = sheet.createRow(rowIndex++);
                        rowNext.createCell(0).setCellValue(LocalDateTime.now().format(dtf));
                        rowNext.createCell(1).setCellValue(dish.getNameDish());
                        rowNext.createCell(2).setCellValue(dish.getPriceDish());
                    }

                    workbook.write(fos);
                    System.out.println("Nowy plik został utworzony: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
