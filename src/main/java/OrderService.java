import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderService {
    private static OrderService instance;
    private List<Order> orders = new ArrayList<>();
    private static int lastOrderNumber = 0;
    private List<OrderListener> listeners = new ArrayList<>();
    private List<Dish> readyToSend = new ArrayList<>();

    public List<Dish> getReadyToDishes() {
        return Collections.unmodifiableList(readyToSend);
    }



    private OrderService() {
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public Order startNewOrder() {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderNumber(lastOrderNumber);
        orders.add(order);
        notifyListeners(order);
        System.out.println("Nowe zamówienie #" + order.getOrderNumber());
        return order;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Order getCurrentOrder() {
        return orders.isEmpty() ? null : orders.get(orders.size() - 1);
    }

    public void addDishToChefList(Dish dish) {
        Order current = getCurrentOrder();
        if (current != null) {
            current.getSelectedDishes().add(dish);
            notifyListeners(current);
        }
    }
    public void removeDishFromChefList(Dish dish) {
        Order current = getCurrentOrder();
        if (current != null) {
            current.getSelectedDishes().remove(dish);
            notifyListeners(current);
        }
    }

    public void addToWaiterList(Dish dish) {
        Order current = getCurrentOrder();
        if (!readyToSend.contains(dish)) {
            readyToSend.add(dish);
            notifyListeners(current);
        }
    }

    public void removeFromWaiterList(Dish dish) {
        Order current = getCurrentOrder();
        if (readyToSend.contains(dish)) {
            readyToSend.remove(dish);
            notifyListeners(current);
        }
    }

    public void addListener(OrderListener listener) {
        if (!listeners.contains(listener)) { // zabezpieczenie przed powieleniem
            listeners.add(listener);
        }
    }

    public void removeListener(OrderListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Order order) {
        for (OrderListener listener : listeners) {
            listener.onOrderUpdated(order);
        }
    }

    public void updateOrder() {
        Order current = getCurrentOrder();
        if (current != null) {
            notifyListeners(current);
        }
    }

    public List<Order> getPendingOrders() {
        List<Order> pending = new ArrayList<>();
        for (Order o : orders) {
            boolean hasPendingDish = o.getSelectedDishes().stream().anyMatch(d -> !d.isReady());
            if (hasPendingDish) pending.add(o);
        }
        return pending;
    }
}
