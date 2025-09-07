package com.example.restaurants

import ReportService
import Writer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

fun managerFlow(scanner: Scanner, reportService: ReportService, writer: Writer) {
    while (true) {
        println("\n=== TRYB KIEROWNIKA ===")
        println("Wybierz typ raportu:")
        println("1 - Dzienny")
        println("2 - Miesiêczny")
        println("3 - Roczny")
        println("0 - Powrót")

        when (scanner.nextLine()) {
            "1" -> { // Raport dzienny
                print("Podaj datê (RRRR-MM-DD): ")
                val input = scanner.nextLine().trim()

                val date = try {
                    // Parsujemy datê w formacie dopuszczaj¹cym pojedyncze cyfry
                    LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-M-d"))
                } catch (e: DateTimeParseException) {
                    println("Niepoprawny format daty. Podaj w formacie RRRR-MM-DD.")
                    return
                }

                try {     // Generujemy raport u¿ywaj¹c normalizowanych wartoœci
                    val report = reportService.generateDailyReport(
                        date.year.toString(),
                        String.format("%02d", date.monthValue),
                        String.format("%02d", date.dayOfMonth)
                    )

                    writer.writeReportToPdf(
                        report,
                        "daily_dish_report_${
                            String.format(
                                "%04d-%02d-%02d",
                                date.year,
                                date.monthValue,
                                date.dayOfMonth
                            )
                        }.pdf",
                        "Raport dzienny ${
                            String.format(
                                "%04d-%02d-%02d",
                                date.year,
                                date.monthValue,
                                date.dayOfMonth
                            )
                        }",
                        date.year,
                        date.monthValue,
                        date.dayOfMonth
                    )
                    print("Raport wygenerowany pomyœlnie")
                } catch (e: IllegalArgumentException) {
                    println("Nie znaleziono pliku z danymi dla podanej daty.")
                }
            }

            "2" -> { // Raport miesiêczny
                print("Podaj miesi¹c (RRRR-MM): ")
                val input = scanner.nextLine().trim()

                val yearMonth = try {
                    // Parsujemy zarówno 2025-8 jak i 2025-08
                    YearMonth.parse(input, DateTimeFormatter.ofPattern("yyyy-M"))
                } catch (e: DateTimeParseException) {
                    println("Niepoprawny format miesi¹ca. Podaj w formacie RRRR-MM.")
                    return
                }

                val year = yearMonth.year
                val month = yearMonth.monthValue
                try {

                    val report = reportService.generateMonthlyReport(
                        year.toString(),
                        String.format("%02d", month)
                    )

                    writer.writeReportToPdf(
                        report,
                        "monthly_dish_report_${String.format("%04d-%02d", year, month)}.pdf",
                        "Raport miesiêczny ${String.format("%04d-%02d", year, month)}",
                        year,
                        month,
                        null
                    )


                    print("Raport wygenerowany pomyœlnie")
                } catch (e: IllegalArgumentException) {
                    println("Nie znaleziono pliku z danymi dla podanej daty.")
                }
            }

            "3" -> { // Raport roczny
                print("Podaj rok (RRRR): ")
                val input = scanner.nextLine().trim()

                val year = try {
                    input.toInt().also {
                        if (input.length != 4) throw NumberFormatException()
                    }
                } catch (e: NumberFormatException) {
                    println("Niepoprawny format roku. Podaj w formacie RRRR.")
                    return
                }
                try {


                    val report = reportService.generateYearlyReport(year.toString())

                    writer.writeReportToPdf(
                        report,
                        "yearly_dish_report_${year}.pdf",
                        "Raport roczny $year",
                        year,
                        0,
                        null
                    )

                    print("Raport wygenerowany pomyœlnie")
                } catch (e: IllegalArgumentException) {
                    println("Nie znaleziono pliku z danymi dla podanej daty.")
                }
            }
            "0" -> return
            else -> println("Niepoprawna opcja, spróbuj ponownie.")
        }
    }
}
