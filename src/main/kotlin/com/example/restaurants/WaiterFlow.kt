import java.util.*

fun waiterFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KELNERA ===")
    print("Login: ");
    val username = scanner.nextLine().trim()
    print("Has³o: ");
    val password = scanner.nextLine().trim()

    val user = LoginService.getInstance().authenticate("Waiter", username, password)
    if (user == null) {
        println("B³êdny login lub has³o.")
        return
    }

    val waiter = Waiter(user.userName)
    var inMenu = true

    while (inMenu) {
        println("\n--- MENU KELNERA ---")
        println("1. SprawdŸ status zamówieñ")
        println("2. Wydaj gotowe danie")
        println("3. Tryb klienta (dodawanie zamówieñ)")
        println("0. Powrót do menu g³ównego")

        when (scanner.nextLine().trim()) {
            "1" -> {
                val orders = orderService.getOrders()
                if (orders.isEmpty()) println("Brak zamówieñ.")
                else orders.forEach { order ->
                    println("\nZamówienie #${order.orderNumber}:")
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
                    println("Brak dañ do wydania.")
                    continue
                }

                println("Gotowe dania do wydania:")
                readyDishes.forEach { println("${it.id}: ${it.nameDish}") }

                println("Podaj ID dania do wydania (0 aby anulowaæ):")
                val input = scanner.nextLine().trim().toIntOrNull()
                if (input == null || input == 0) continue

                val dish = readyDishes.find { it.id == input }
                if (dish != null && waiter.servedDish(dish)) {
                    println("Danie '${dish.nameDish}' wydane.")
                } else {
                    println("Nie mo¿na wydaæ tego dania.")
                }
            }
            "3" -> clientFlow(scanner, orderService)
            "0" -> inMenu = false
            else -> println("Nieznana opcja.")
        }
    }
}
