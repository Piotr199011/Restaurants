import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private static ReportService instance;

    private ReportService() {
    }

    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public double calculateDailyIncome() {
        List<Order> allOrders = OrderService.getInstance().getOrders();
        double sum = 0;
        for (Order order : allOrders) {
            order.getOrderDate();
            if (order.getOrderDate().equals(LocalDate.now())) {
                sum += order.totalPrice();
            }
        }
        return sum;
    }

    public double calculateMonthlyIncome() {
        List<Order> allOrders = OrderService.getInstance().getOrders();
        double sum = 0;
        LocalDate now = LocalDate.now();

        for (Order order : allOrders) {
            order.getOrderDate();
            if (order.getOrderDate().getMonth().equals(now.getMonth()) &&
                    order.getOrderDate().getYear() == now.getYear()) {
                sum += order.totalPrice();

            }
        }
        return sum;
    }

    public double calculateYearlyIncome() {
        List<Order> allOrders = OrderService.getInstance().getOrders();
        double sum = 0;
        int date = LocalDate.now().getYear();
        for (Order order : allOrders) {
            order.getOrderDate();
            if (order.getOrderDate().getYear() == date) {
                sum += order.totalPrice();
            }
        }
        return sum;
    }

    public Map<String, Integer> counterDailyReport() {
        Map<String, Integer> dailyMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();
        LocalDate today = LocalDate.now();

        for (Order order : allOrders) {
            if (order.getOrderDate() == null || !order.getOrderDate().equals(today)) continue;

            for (Dish dish : order.getSelectedDishes()) {
                String name = dish.getNameDish();
                dailyMap.put(name, dailyMap.getOrDefault(name, 0) + 1);
            }
        }

        return dailyMap;
    }


    public Map<String, Integer> selectedDailyReport(LocalDate localDate) {
        Map<String, Integer> dailyMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();


        for (Order order : allOrders) {
            if (order.getOrderDate() == null || !order.getOrderDate().equals(localDate)) continue;

            for (Dish dish : order.getSelectedDishes()) {
                String name = dish.getNameDish();
                dailyMap.put(name, dailyMap.getOrDefault(name, 0) + 1);
            }
        }

        return dailyMap;
    }



    public Map<String, Integer> countMonthlyOrders() {
        Map<String, Integer> monthlyMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();

        YearMonth currentMonth = YearMonth.now();

        for (Order order : allOrders) {
            YearMonth orderMonth = YearMonth.from(order.getOrderDate());
            if (!orderMonth.equals(currentMonth)) continue; // tylko bieżący miesiąc

            List<Dish> dishes = order.getSelectedDishes();
            if (dishes == null || dishes.isEmpty()) continue;

            for (Dish dish : dishes) {
                String name = dish.getNameDish();
                monthlyMap.put(name, monthlyMap.getOrDefault(name, 0) + 1);
            }
        }

        return monthlyMap;

    }
    public Map<String, Integer> selectedMonthlyOrders(YearMonth yearMonth) {
        Map<String, Integer> monthlyMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();


        for (Order order : allOrders) {
            YearMonth orderMonth = YearMonth.from(order.getOrderDate());
            if (!orderMonth.equals(yearMonth)) continue; // tylko bieżący miesiąc

            List<Dish> dishes = order.getSelectedDishes();
            if (dishes == null || dishes.isEmpty()) continue;

            for (Dish dish : dishes) {
                String name = dish.getNameDish();
                monthlyMap.put(name, monthlyMap.getOrDefault(name, 0) + 1);
            }
        }

        return monthlyMap;

    }


    public Map<String, Integer> getYearlyOrdersCount() {
        Map<String, Integer> yearsMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();
        Year currentYear = Year.now();

        for (Order order : allOrders) {
            Year orderYear = Year.from(order.getOrderDate());
            if (!orderYear.equals(currentYear)) continue;

            for (Dish dish : order.getSelectedDishes()) {
                String name = dish.getNameDish();
                yearsMap.put(name, yearsMap.getOrDefault(name, 0) + 1);
            }
        }

        return yearsMap;
    }
    public Map<String, Integer> selectedYearlyOrdersCount(Year year) {


        Map<String, Integer> yearsMap = new HashMap<>();
        List<Order> allOrders = OrderService.getInstance().getOrders();


        for (Order order : allOrders) {
            Year orderYear = Year.from(order.getOrderDate());
            if (!orderYear.equals(year)) continue;

            for (Dish dish : order.getSelectedDishes()) {
                String name = dish.getNameDish();
                yearsMap.put(name, yearsMap.getOrDefault(name, 0) + 1);
            }
        }

        return yearsMap;
    }

}
