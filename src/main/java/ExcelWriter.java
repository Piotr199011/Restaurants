import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExcelWriter {
    public void writeExcel(String resourceName, List<Dish> selectedDishes) {

        File file = new File(resourceName);
        if (file.exists()) {
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

                    rowNext.createCell(0).setCellValue(LocalDateTime.now().toString());

                    rowNext.createCell(1).setCellValue(dish.getNameDish());

                    rowNext.createCell(2).setCellValue(dish.getPriceDish());
                    rowIndex++;
                }
                FileOutputStream outputStream = new FileOutputStream(
                        resourceName + "_" + LocalDate.now().toString() + ".xlsx");
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}