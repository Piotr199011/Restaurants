import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelWriter {
    public void writeExcel(String resourceName, List<Dish> selectedDishes) {

        File file = new File(
                "src/main/resources/orders",
                (resourceName.endsWith(".xlsx") ? resourceName.replace(
                        ".xlsx", "") : resourceName)
                        + "." + LocalDate.now() + ".xlsx"
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = LocalDateTime.now().format(formatter);
        if (file.exists()) {
            try (FileInputStream is = new FileInputStream(file)) {

                Workbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                int rowIndex = sheet.getLastRowNum() + 1;
                for (Dish dish : selectedDishes) {
                    XSSFRow rowNext = (XSSFRow) sheet.createRow(rowIndex);
                    rowNext.createCell(0).setCellValue(formattedDate);
                    rowNext.createCell(1).setCellValue(dish.getNameDish());
                    rowNext.createCell(2).setCellValue(dish.getPriceDish());
                    rowIndex++;
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                workbook.close();

                System.out.println("Dane zostały dopisane");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String[] tab = {"Time", "Name Dish", "Price"};

            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Arkusz1");
                XSSFRow row = sheet.createRow(0);

                for (int i = 0; i < tab.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(tab[i]);
                }

                int rowIndex = 1;

                for (Dish dish : selectedDishes) {
                    XSSFRow rowNext = sheet.createRow(rowIndex);
                    rowNext.createCell(0).setCellValue(formattedDate);
                    rowNext.createCell(1).setCellValue(dish.getNameDish());
                    rowNext.createCell(2).setCellValue(dish.getPriceDish());
                    rowIndex++;
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                workbook.close();

                System.out.println("Nowy plik został utworzony");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}