import java.util.*

fun clientFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KLIENTA ===")
    val table = reserveTable(scanner) ?: return
    val restaurants = Restaurants()
    val currentOrder = orderService.startNewOrder()
    val excelWriter = Writer() // Tworzymy obiekt ExcelWriter

    while (true) {
        println("\nWybierz typ dania:")
        println("1 - Œniadanie")
        println("2 - Obiad")
        println("3 - Kolacja")
        println("4 - Deser")
        println("0 - Powrót do menu g³ównego")

        val mealType = scanner.nextLine().trim()
        if (mealType == "0") break

        val mealNum = mealType.toIntOrNull()
        if (mealNum == null || mealNum !in 1..4) {
            println("Niepoprawny wybór.")
            continue
        }

        val menuFile = restaurants.choseMeal("pl", mealNum)
        val dishes = ExcelReader().readExcel(menuFile)

        println("\nMenu:")
        dishes.forEach {
            println("${it.id}: ${it.nameDish} sk³ad: ${it.compositionOfDish} - ${it.priceDish} PLN")
        }

        while (true) {
            println("\nPodaj ID dania do zamówienia (0 zakoñczenie, -1 powrót do wyboru typu):")
            val input = scanner.nextLine().trim()
            when (input) {
                "0" -> break
                "-1" -> break
                else -> {
                    val dishId = input.toIntOrNull()
                    val dish = dishes.find { it.id == dishId }
                    if (dish != null) {
                        orderService.addDishToChefList(dish)
                        println("Dodano: ${dish.nameDish}")
                    } else {
                        println("Nie ma dania o takim ID.")
                    }
                }
            }
            if (input == "0") return
        }
    }

    if (currentOrder.selectedDishes.isEmpty()) {
        println("Nie z³o¿ono ¿adnego zamówienia.")
        return
    }

    println("\nTwoje zamówienie #${currentOrder.orderNumber}:")
    currentOrder.selectedDishes.forEach {
        println("${it.nameDish} - ${it.priceDish} PLN")
    }

    println("Potwierdzasz zamówienie? (tak/nie)")
    if (scanner.nextLine().equals("tak", ignoreCase = true)) {
        println("Zamówienie wys³ane do kuchni.")
        orderService.updateOrder()

        // TYLKO PO POTWIERDZENIU ZAPIS DO EXCELA
        excelWriter.writeExcel("orders.xlsx", currentOrder.selectedDishes)
        println("Zamówienie zapisane w pliku Excel.")
    } else {
        currentOrder.selectedDishes.clear()
        println("Zamówienie anulowane.")
    }
}
