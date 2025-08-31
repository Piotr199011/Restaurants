import java.util.List;
import java.util.stream.Collectors;

public class Cook extends User implements OrderListener {

    public Cook(String name) {
        super(name);
        OrderService.getInstance().addListener(this);
    }

    @Override
    public void onOrderUpdated(Order order) {
        System.out.println("\n[Cook] Aktualizacja zamówienia #" + order.getOrderNumber());
        List<String> dishStatuses = order.getSelectedDishes().stream()
                .map(d -> d.getNameDish() + " - " + (d.isReady() ? "gotowe" : "w trakcie"))
                .collect(Collectors.toList());
        dishStatuses.forEach(System.out::println);
    }


    public boolean markDishAsReady(Dish dish) {
        if (!dish.isReady()) {
            dish.setReady(true);
            OrderService.getInstance().updateOrder();
            OrderService.getInstance().addToWaiterList(dish);
            OrderService.getInstance().removeDishFromChefList(dish);
            return true; // potrawa została oznaczona
        }
        return false; // potrawa już była gotowa
    }
}
