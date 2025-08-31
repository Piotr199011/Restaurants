import java.time.LocalDate
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val orderService = OrderService.getInstance()
    val writer = Writer()
    var running = true
    while (running) {
        println("\n=== MENU GŁÓWNE ===")
        println("Wybierz rolę:")
        println("1. Klient")
        println("2. Kucharz")
        println("3. Kelner (ma też opcje klienta)")
        println("4. Kierownik")
        println("0. Wyjście")

        when (scanner.nextLine().trim()) {
            "1" -> clientFlow(scanner, orderService)
            "2" -> cookFlow(scanner, orderService)
            "3" -> waiterFlow(scanner, orderService)
            "4" -> managerFlow(scanner, ReportService(ExcelReader()), Writer())
            "0" -> {
                println("Do widzenia!")
                running = false
            }
            else -> println("Nieznana opcja, spróbuj ponownie.")
        }
    }
}

fun clientFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KLIENTA ===")
    val table = reserveTable(scanner) ?: return
    val restaurants = Restaurants()
    val currentOrder = orderService.startNewOrder()
    val excelWriter = Writer() // Tworzymy obiekt ExcelWriter

    while (true) {
        println("\nWybierz typ dania:")
        println("1 - Śniadanie")
        println("2 - Obiad")
        println("3 - Kolacja")
        println("4 - Deser")
        println("0 - Powrót do menu głównego")

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
            println("${it.id}: ${it.nameDish} skład: ${it.compositionOfDish} - ${it.priceDish} PLN")
        }

        while (true) {
            println("\nPodaj ID dania do zamówienia (0 zakończenie, -1 powrót do wyboru typu):")
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
        println("Nie złożono żadnego zamówienia.")
        return
    }

    println("\nTwoje zamówienie #${currentOrder.orderNumber}:")
    currentOrder.selectedDishes.forEach {
        println("${it.nameDish} - ${it.priceDish} PLN")
    }

    println("Potwierdzasz zamówienie? (tak/nie)")
    if (scanner.nextLine().equals("tak", ignoreCase = true)) {
        println("Zamówienie wysłane do kuchni.")
        orderService.updateOrder()

        // TYLKO PO POTWIERDZENIU ZAPIS DO EXCELA
        excelWriter.writeExcel("orders.xlsx", currentOrder.selectedDishes)
        println("Zamówienie zapisane w pliku Excel.")
    } else {
        currentOrder.selectedDishes.clear()
        println("Zamówienie anulowane.")
    }
}


fun cookFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KUCHARZA ===")
    print("Login: ");
    val username = scanner.nextLine().trim()
    print("Hasło: ");
    val password = scanner.nextLine().trim()

    val user = LoginService.getInstance().authenticate("Cook", username, password)
    if (user == null) {
        println("Błędny login lub hasło.")
        return
    }

    val cook = Cook(user.userName)
    val orders = orderService.getOrders()

    if (orders.isEmpty()) {
        println("Brak zamówień.")
        return
    }

    orders.forEachIndexed { i, order ->
        println("\nZamówienie #${order.orderNumber}:")
        order.selectedDishes.forEachIndexed { j, dish ->
            println("${i + 1}:${j + 1} ${dish.nameDish} - ${if (dish.isReady()) "gotowe" else "w trakcie"}")
        }
    }

    while (true) {
        println("\nPodaj numer w formacie zamówienie:pozycja (0 aby zakończyć):")
        val input = scanner.nextLine().trim()
        if (input == "0") break

        val parts = input.split(":")
        if (parts.size != 2) {
            println("Zły format, np. 1:2")
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
            println("To danie już jest gotowe.")
        }
    }
}

fun waiterFlow(scanner: Scanner, orderService: OrderService) {
    println("\n=== TRYB KELNERA ===")
    print("Login: ");
    val username = scanner.nextLine().trim()
    print("Hasło: ");
    val password = scanner.nextLine().trim()

    val user = LoginService.getInstance().authenticate("Waiter", username, password)
    if (user == null) {
        println("Błędny login lub hasło.")
        return
    }

    val waiter = Waiter(user.userName)
    var inMenu = true

    while (inMenu) {
        println("\n--- MENU KELNERA ---")
        println("1. Sprawdź status zamówień")
        println("2. Wydaj gotowe danie")
        println("3. Tryb klienta (dodawanie zamówień)")
        println("0. Powrót do menu głównego")

        when (scanner.nextLine().trim()) {
            "1" -> {
                val orders = orderService.getOrders()
                if (orders.isEmpty()) println("Brak zamówień.")
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
                    println("Brak dań do wydania.")
                    continue
                }

                println("Gotowe dania do wydania:")
                readyDishes.forEach { println("${it.id}: ${it.nameDish}") }

                println("Podaj ID dania do wydania (0 aby anulować):")
                val input = scanner.nextLine().trim().toIntOrNull()
                if (input == null || input == 0) continue

                val dish = readyDishes.find { it.id == input }
                if (dish != null && waiter.servedDish(dish)) {
                    println("Danie '${dish.nameDish}' wydane.")
                } else {
                    println("Nie można wydać tego dania.")
                }
            }
            "3" -> clientFlow(scanner, orderService)
            "0" -> inMenu = false
            else -> println("Nieznana opcja.")
        }
    }
}

fun managerFlow(scanner: Scanner, reportService: ReportService, writer: Writer) {
    while (true) {
        println("\n=== TRYB KIEROWNIKA ===")
        println("Wybierz typ raportu:")
        println("1 - Dzienny")
        println("2 - Miesięczny")
        println("3 - Roczny")
        println("0 - Powrót")

        when (scanner.nextLine()) {
            "1" -> { // Raport dzienny
                print("Podaj datę (RRRR-MM-DD): ")
                val date = LocalDate.parse(scanner.nextLine())
                val report = reportService.generateDailyReport(
                    date.year.toString(),
                    String.format("%02d", date.monthValue),
                    String.format("%02d", date.dayOfMonth)
                )
                // reportService.printReport(report, "Raport dzienny ${date}")
                writer.writeReportToPdf(
                    report,
                    "daily_dish_report_${date}.pdf",
                    "Raport dzienny ${date}",
                    date.year,
                    date.monthValue,
                    date.dayOfMonth
                )
            }

            "2" -> { // Raport miesięczny
                print("Podaj miesiąc (RRRR-MM): ")
                val ymInput = scanner.nextLine().split("-")
                val year = ymInput[0].toInt()
                val month = ymInput[1].toInt()
                val report = reportService.generateMonthlyReport(
                    year.toString(),
                    String.format("%02d", month)
                )
                //reportService.printReport(report, "Raport miesięczny ${year}-${String.format("%02d", month)}")
                writer.writeReportToPdf(
                    report,
                    "monthly_dish_report_${year}-${String.format("%02d", month)}.pdf",
                    "Raport miesięczny ${year}-${String.format("%02d", month)}",
                    year,
                    month,
                    null
                )
            }

            "3" -> { // Raport roczny
                print("Podaj rok (RRRR): ")
                val year = scanner.nextLine().toInt()
                val report = reportService.generateYearlyReport(year.toString())

                //    reportService.printReport(report, "Raport roczny ${year}")
                writer.writeReportToPdf(
                    report,
                    "yearly_dish_report_${year}.pdf",
                    "Raport roczny ${year}",
                    year,
                    0,
                    null
                )
            }

            "0" -> return
            else -> println("Niepoprawna opcja, spróbuj ponownie.")
        }
    }
}

fun reserveTable(scanner: Scanner): Table? {
    println("\nPodaj numer stolika (0 aby anulować):")
    val num = scanner.nextLine().trim().toIntOrNull()
    if (num == null || num < 0) {
        println("Błędny numer.")
        return null
    }
    if (num == 0) return null
    val table = Table(num, false)
    table.reserve()
    return table
}
