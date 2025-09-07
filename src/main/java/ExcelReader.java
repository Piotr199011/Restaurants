import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ExcelReader {


    public ArrayList<Manager> readExcelManager(String year, String month, String day) {
        if(day.length()==1){
            day="0"+day;
        }
        if(month.length()==1){
            month="0"+month;
        }
            ArrayList<Manager> daneManager = new ArrayList<>();
            String path = "src/main/resources/orders/" + year + "/" + month +
                    "/orders." + year + "-" + month + "-" + day + ".xlsx";

            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("Plik " + path + " nie został znaleziony!");
            }

        try (InputStream is = new FileInputStream(file)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Odczyt z arkusza: " + sheet.getSheetName());

            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // pomijamy nagłówek
                }

                //  kolumna 0 = data, kolumna 1 = nazwa dania, kolumna 2 = cena, kolumna 3 = ilość
                String date = row.getCell(0).getStringCellValue();
                String dishName = row.getCell(1).getStringCellValue();
                Cell priceCell = row.getCell(2);
                int price;

                if (priceCell.getCellType() == CellType.NUMERIC) {
                    price = (int) priceCell.getNumericCellValue();
                } else {
                    price = (int) Double.parseDouble(priceCell.getStringCellValue());
                }

                int quantity = 1; // domyślna ilość
                if (row.getLastCellNum() > 3 && row.getCell(3) != null) {
                    Cell qtyCell = row.getCell(3);
                    if (qtyCell.getCellType() == CellType.NUMERIC) {
                        quantity = (int) qtyCell.getNumericCellValue();
                    } else {
                        quantity = Integer.parseInt(qtyCell.getStringCellValue());
                    }
                }

                daneManager.add(new Manager(date, dishName, price, quantity));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return daneManager;
    }

    // Odczyt listy dań
    public ArrayList<Dish> readExcel(String resourceName) {
        ArrayList<Dish> dishes = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalArgumentException("Plik " + resourceName +
                        " nie został znaleziony w resources!");
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println("Odczyt z arkusza: " + sheet.getSheetName());

            boolean isFirstRow = true;
            int counter = 1;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                int id = row.getCell(0).getRowIndex();
                String name = row.getCell(1).getStringCellValue();
                String composition = row.getCell(2).getStringCellValue();

                Cell priceCell = row.getCell(3);
                int price;
                if (priceCell.getCellType() == CellType.NUMERIC) {
                    price = (int) priceCell.getNumericCellValue();
                } else {
                    price = (int) Double.parseDouble(priceCell.getStringCellValue());
                }

                boolean isReady = false;
                dishes.add(new Dish(counter, id, name, composition, price, isReady));
                counter++;
            }

        } catch (Exception e) {
            System.err.println("Błąd podczas odczytu pliku Excel: " + e.getMessage());
        }
        return dishes;
    }

    // Odczyt wszystkich plików w katalogu i podkatalogach
    public void readAllExcelFilesRecursively(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Nieprawidłowa ścieżka katalogu: " + directoryPath);
            return;
        }
        traverseAndRead(dir);
    }

    private void traverseAndRead(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                traverseAndRead(file);
            } else if (file.getName().toLowerCase().endsWith(".xls") || file.getName()
                    .toLowerCase().endsWith(".xlsx")) {
                System.out.println("=== Odczyt pliku: " + file.getAbsolutePath() + " ===");
                readExcelManager("2025", "08", "30"); // przykład: podać rok/miesiąc/dzień lub parametryzować
                System.out.println();
            }
        }
    }

    // Odczyt loginów z arkusza
    public Map<String, String> readLoginsFromSheet(String resourceName, int sheetIndex) {
        Map<String, String> logowanie = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalArgumentException("Plik " + resourceName
                        + " nie został znaleziony w resources!");
            }
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            boolean isFirstRow = true;
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                String login = getCellStringValue(row.getCell(0));
                String password = getCellStringValue(row.getCell(1));
                if (!login.isEmpty()) {
                    logowanie.put(login, password);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas odczytu pliku Excel: " + e.getMessage());
        }
        return logowanie;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}


//    public ArrayList<Dish> readExcel(String resourceName) {
//        ArrayList<Dish> dishes = new ArrayList<>();
//        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
//            if (is == null) {
//                throw new IllegalArgumentException("Plik " + resourceName + " nie został znaleziony w resources!");
//            }
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheetAt(0);
//            System.out.println("Odczyt z arkusza: " + sheet.getSheetName());
//            boolean isFirstRow = true;
//            int counter = 1;
//            for (Row row : sheet) {
//                if (isFirstRow) {
//                    isFirstRow = false;
//                }
//                int id = row.getCell(0).getRowIndex();
//                String name = row.getCell(1).getStringCellValue();
//                String composition = row.getCell(2).getStringCellValue();
//                Cell priceCell = row.getCell(3);
//                int price;
//                if (priceCell.getCellType() == CellType.NUMERIC) {
//                    price = (int) priceCell.getNumericCellValue();
//                } else {
//                    price = (int) Double.parseDouble(priceCell.getStringCellValue());
//                }
//                boolean isReady = false;
//                Dish dish = new Dish(counter, id, name, composition, price, isReady);
//                dishes.add(dish);
//                counter++;
//            }
//            workbook.close();
//        } catch (Exception e) {
//            System.err.println("Błąd podczas odczytu pliku Excel: " + e.getMessage());
//        }
//        return dishes;
//    }
//
//    public void readAllExcelFilesRecursively(String directoryPath) {
//        File dir = new File(directoryPath);
//        if (!dir.exists() || !dir.isDirectory()) {
//            System.err.println("Nieprawidłowa ścieżka katalogu: " + directoryPath);
//            return;
//        }
//        traverseAndRead(dir);
//    }
//
//    private void traverseAndRead(File dir) {
//        File[] files = dir.listFiles();
//        if (files == null) return;
//        for (File file : files) {
//            if (file.isDirectory()) {
//                traverseAndRead(file);
//            } else if (file.getName().toLowerCase().endsWith(".xls")) {
//                System.out.println("=== Odczyt pliku: " + file.getAbsolutePath() + " ===");
//                readExcel(file.getAbsolutePath());
//                System.out.println();
//            }
//        }
//    }
//
//    public Map<String, String> readLoginsFromSheet(String resourceName, int sheetIndex) {
//        Map<String, String> logowanie = new HashMap<>();
//        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
//            if (is == null) {
//                throw new IllegalArgumentException("Plik " + resourceName + " nie został znaleziony w resources!");
//            }
//            Workbook workbook = new XSSFWorkbook(is);
//            int numberOfSheets = workbook.getNumberOfSheets();
//            System.out.println("Zakładki w pliku: ");
//            for (int i = 0; i < numberOfSheets; i++) {
//                System.out.println(" - " + workbook.getSheetName(i));
//            }
//            Sheet sheet = workbook.getSheetAt(sheetIndex);
//            System.out.println("Odczyt z arkusza: " + sheet.getSheetName());
//            boolean isFirstRow = true;
//            for (Row row : sheet) {
//                if (isFirstRow) {
//                    isFirstRow = false;
//                }
//                String login = getCellStringValue(row.getCell(0));
//                String password = getCellStringValue(row.getCell(1));
//                if (!login.isEmpty()) {
//                    logowanie.put(login, password);
//                }
//            }
//            workbook.close();
//        } catch (Exception e) {
//            System.err.println("Błąd podczas odczytu pliku Excel: " + e.getMessage());
//        }
//        return logowanie;
//    }
//
//    private String getCellStringValue(Cell cell) {
//        if (cell == null) return "";
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                return String.valueOf((int) cell.getNumericCellValue());
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            default:
//                return "";
//        }
//    }
//}