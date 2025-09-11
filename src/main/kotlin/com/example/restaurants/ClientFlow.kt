import java.util.*

fun clientFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KLIENTA ===")
    val table = reserveTable(scanner) ?: return
    val restaurants = Restaurants()
    val currentOrder = orderService.startNewOrder()
    val excelWriter = Writer() // Tworzymy obiekt ExcelWriter

    while (true) {
        println("\nWybierz typ dania:")
        println("1 - �niadanie")
        println("2 - Obiad")
        println("3 - Kolacja")
        println("4 - Deser")
        println("0 - Powr�t do menu g��wnego")

        val mealType = scanner.nextLine().trim()
        if (mealType == "0") break

        val mealNum = mealType.toIntOrNull()
        if (mealNum == null || mealNum !in 1..4) {
            println("Niepoprawny wyb�r.")
            continue
        }

        val menuFile = restaurants.choseMeal("pl", mealNum)
        val dishes = ExcelReader().readExcel(menuFile)

        println("\nMenu:")
        dishes.forEach {
            println("${it.id}: ${it.nameDish} sk�ad: ${it.compositionOfDish} - ${it.priceDish} PLN")
        }

        while (true) {
            println("\nPodaj ID dania do zam�wienia (0 zako�czenie, -1 powr�t do wyboru typu):")
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
        println("Nie z�o�ono �adnego zam�wienia.")
        return
    }

    println("\nTwoje zam�wienie #${currentOrder.orderNumber}:")
    currentOrder.selectedDishes.forEach {
        println("${it.nameDish} - ${it.priceDish} PLN")
    }

    println("Potwierdzasz zam�wienie? (tak/nie)")
    if (scanner.nextLine().equals("tak", ignoreCase = true)) {
        println("Zam�wienie wys�ane do kuchni.")
        orderService.updateOrder()

        // TYLKO PO POTWIERDZENIU ZAPIS DO EXCELA
        excelWriter.writeExcel("orders.xlsx", currentOrder.selectedDishes)
        println("Zam�wienie zapisane w pliku Excel.")
    } else {
        currentOrder.selectedDishes.clear()
        println("Zam�wienie anulowane.")
    }
}
