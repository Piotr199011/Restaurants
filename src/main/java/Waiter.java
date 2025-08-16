public class Waiter extends User {


    public Waiter(String name) {
        super(name);
    }

    public void checkOrderStatus() {
        // Pobranie aktualnego zamówienia z singletona
        Order order = OrderService.getInstance().getCurrentOrder();

        if (order.getSelectedDishes().isEmpty()) {
            System.out.println("Brak dań w zamówieniu.");
            return;
        }

        boolean allReady = true;

        System.out.println("Status zamówienia:");
        for (Dish dish : order.getSelectedDishes()) {
            if (dish.isReady()) {
                System.out.println(dish.getNameDish() + ": Gotowe");
            } else {
                System.out.println(dish.getNameDish() + ": W przygotowaniu");
                allReady = false;
            }
        }

        if (allReady) {
            System.out.println("Zamówienie gotowe do odbioru!");

        } else {
            System.out.println("Zamówienie w trakcie realizacji.");
        }
    }

    public boolean servedDish(Dish dish) {
        if (dish.isReady) {
            dish.setServed(true);
            OrderService.getInstance().updateOrder();
            OrderService.getInstance().removeFromWaiterList(dish);
            return true; // potrawa została oznaczona
        }
        return false; // potrawa już była gotowa
    }
}