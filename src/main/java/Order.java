import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Dish> selectedDishes = new ArrayList<>();

    private int orderNumber;

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Dish> getSelectedDishes() {
        return selectedDishes;
    }

    public int totalPrice() {
        int sum = 0;
        for (Dish dish : selectedDishes) {
            sum += dish.priceDish;
        }
        return sum;
    }
}
