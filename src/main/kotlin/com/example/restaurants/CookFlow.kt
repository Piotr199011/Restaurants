import java.util.*

fun cookFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KUCHARZA ===")
    print("Login: ");
    val username = scanner.nextLine().trim()
    print("Has�o: ");
    val password = scanner.nextLine().trim()

    val user = LoginService.getInstance().authenticate("Cook", username, password)
    if (user == null) {
        println("B��dny login lub has�o.")
        return
    }

    val cook = Cook(user.userName)
    val orders = orderService.getOrders()

    if (orders.isEmpty()) {
        println("Brak zam�wie�.")
        return
    }

    orders.forEachIndexed { i, order ->
        println("\nZam�wienie #${order.orderNumber}:")
        order.selectedDishes.forEachIndexed { j, dish ->
            println("${i + 1}:${j + 1} ${dish.nameDish} - ${if (dish.isReady()) "gotowe" else "w trakcie"}")
        }
    }

    while (true) {
        println("\nPodaj numer w formacie zam�wienie:pozycja (0 aby zako�czy�):")
        val input = scanner.nextLine().trim()
        if (input == "0") break

        val parts = input.split(":")
        if (parts.size != 2) {
            println("Z�y format, np. 1:2")
            continue
        }

        val orderIndex = parts[0].toIntOrNull()?.minus(1)
        val dishIndex = parts[1].toIntOrNull()?.minus(1)

        if (orderIndex == null || dishIndex == null ||
            orderIndex !in orders.indices ||
            dishIndex !in orders[orderIndex].selectedDishes.indices
        ) {
            println("Niepoprawny numer.")
            continue
        }

        val dish = orders[orderIndex].selectedDishes[dishIndex]
        if (cook.markDishAsReady(dish)) {
            println("Oznaczono jako gotowe: ${dish.nameDish}")
        } else {
            println("To danie ju� jest gotowe.")
        }
    }
}
