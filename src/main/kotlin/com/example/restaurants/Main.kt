import com.example.restaurants.managerFlow
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
