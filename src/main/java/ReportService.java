import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.YearMonth;

public class ReportService {

    private ExcelReader excelReader;

    public ReportService(ExcelReader excelReader) {
        this.excelReader = excelReader;
    }

    // ================= RAPORT DZIENNY =================
    public Map<String, int[]> generateDailyReport(String year, String month, String day) {
        ArrayList<Manager> orders = excelReader.readExcelManager(year, month, day);
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("Brak danych dla dnia: " + year + "-" + month + "-" + day);
        }
        return generateReportFromOrders(orders);
    }

    // ================= RAPORT MIESIĘCZNY =================
    public Map<String, int[]> generateMonthlyReport(String year, String month) {
        Map<String, int[]> report = new HashMap<>();
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int daysInMonth = yearMonth.lengthOfMonth();
        boolean anyDayFound = false;

        for (int day = 1; day <= daysInMonth; day++) {
            String dayStr = String.format("%02d", day);
            File file = new File("src/main/resources/orders/" + year
                    + "/" + month + "/orders." + year + "-" + month + "-" + dayStr + ".xlsx");
            if (!file.exists()) continue;

            ArrayList<Manager> orders = excelReader.readExcelManager(year, month, dayStr);
            if (orders != null && !orders.isEmpty()) {
                anyDayFound = true;
                mergeReport(report, generateReportFromOrders(orders));
            }
        }

        if (!anyDayFound) {
            throw new IllegalArgumentException("Brak danych dziennych w miesiącu: " + year + "-" + month);
        }

        return report;
    }

    // ================= RAPORT ROCZNY =================
    public Map<String, int[]> generateYearlyReport(String year) {
        Map<String, int[]> report = new HashMap<>();
        boolean anyMonthFound = false;

        for (int month = 1; month <= 12; month++) {
            String monthStr = String.format("%02d", month);
            try {
                Map<String, int[]> monthlyReport = generateMonthlyReport(year, monthStr);
                mergeReport(report, monthlyReport);
                anyMonthFound = true;
            } catch (IllegalArgumentException e) {
                // brak danych w danym miesiącu, pomijamy
            }
        }

        if (!anyMonthFound) {
            throw new IllegalArgumentException("Brak danych w roku: " + year);
        }

        return report;
    }

    // ================= GENEROWANIE RAPORTU Z LISTY ZAMÓWIEŃ =================
    private Map<String, int[]> generateReportFromOrders(ArrayList<Manager> orders) {
        Map<String, int[]> report = new HashMap<>();
        for (Manager m : orders) {
            String dishName = m.getName();
            int quantity = m.getQuantity();
            int price = m.getPrice();
            if (!report.containsKey(dishName)) {
                report.put(dishName, new int[]{quantity, quantity * price});
            } else {
                int[] current = report.get(dishName);
                current[0] += quantity;
                current[1] += quantity * price;
            }
        }
        return report;
    }

    // ================= MERGOWANIE RAPORTÓW =================
    private void mergeReport(Map<String, int[]> main, Map<String, int[]> toMerge) {
        for (Map.Entry<String, int[]> entry : toMerge.entrySet()) {
            String dish = entry.getKey();
            int[] value = entry.getValue();
            if (!main.containsKey(dish)) {
                main.put(dish, value);
            } else {
                int[] current = main.get(dish);
                current[0] += value[0];
                current[1] += value[1];
            }
        }
    }

    // ================= WYŚWIETLANIE RAPORTU =================
    public void printReport(Map<String, int[]> report, String title) {
        System.out.println("=== " + title + " ===");
        System.out.println("Danie\tIlość\tPrzychód");
        for (Map.Entry<String, int[]> entry : report.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue()[0] + "\t" + entry.getValue()[1]);
        }
        System.out.println();
    }
}
