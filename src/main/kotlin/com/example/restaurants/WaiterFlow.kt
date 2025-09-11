import java.util.*

fun waiterFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KELNERA ===")
    print("Login: ");
    val username = scanner.nextLine().trim()
    print("Has�o: ");
    val password = scanner.nextLine().trim()

    val user = LoginService.getInstance().authenticate("Waiter", username, password)
    if (user == null) {
        println("B��dny login lub has�o.")
        return
    }

    val waiter = Waiter(user.userName)
    var inMenu = true

    while (inMenu) {
        println("\n--- MENU KELNERA ---")
        println("1. Sprawd� status zam�wie�")
        println("2. Wydaj gotowe danie")
        println("3. Tryb klienta (dodawanie zam�wie�)")
        println("0. Powr�t do menu g��wnego")

        when (scanner.nextLine().trim()) {
            "1" -> {
                val orders = orderService.getOrders()
                if (orders.isEmpty()) println("Brak zam�wie�.")
                else orders.forEach { order ->
                    println("\nZam�wienie #${order.orderNumber}:")
                    order.selectedDishes.forEach { dish ->
                        println(
                            "${dish.id}: ${dish.nameDish} - ${
                                if
                                        (dish.isReady()) "gotowe" else "w trakcie"
                            }"
                        )
                    }
                }
            }
            "2" -> {
                val readyDishes = orderService.getReadyToDishes()
                if (readyDishes.isEmpty()) {
                    println("Brak da� do wydania.")
                    continue
                }

                println("Gotowe dania do wydania:")
                readyDishes.forEach { println("${it.id}: ${it.nameDish}") }

                println("Podaj ID dania do wydania (0 aby anulowa�):")
                val input = scanner.nextLine().trim().toIntOrNull()
                if (input == null || input == 0) continue

                val dish = readyDishes.find { it.id == input }
                if (dish != null && waiter.servedDish(dish)) {
                    println("Danie '${dish.nameDish}' wydane.")
                } else {
                    println("Nie mo�na wyda� tego dania.")
                }
            }
            "3" -> clientFlow(scanner, orderService)
            "0" -> inMenu = false
            else -> println("Nieznana opcja.")
        }
    }
}
