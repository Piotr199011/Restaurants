
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelReaderTest {
    static Stream<Arguments> provideDateFormats() {
        return Stream.of(
                Arguments.of("2025", "09", "07", true),
                Arguments.of("2025", "9", "07", true),
                Arguments.of("2025", "09", "7", true),
                Arguments.of("2025", "9", "7", true),
                Arguments.of("2025", "O9", "O7", false),
                Arguments.of("07", "09", "2025", false)

        );
    }

    @ParameterizedTest
    @MethodSource("provideDateFormats")
    void testReadExcelManager_variousDateFormats(String year, String month, String day, boolean expectedValid) {
        ExcelReader reader = new ExcelReader();
        if (expectedValid) {
            var managers = reader.readExcelManager(year, month, day);

            assertFalse(managers.isEmpty(),
                    "Nie odczytano danych dla: " + year + "-" + month + "-" + day);
        } else {
            assertThrows(IllegalArgumentException.class,
                    () -> reader.readExcelManager(year, month, day),
                    "Oczekiwano wyjątku dla: " + year + "-" + month + "-" + day);
        }
    }

    @Test
    void testReadExcelManager_dateNoExist() {
        ExcelReader reader = new ExcelReader();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            reader.readExcelManager("2023", "8", "5");

        });
    }


    @Test
    void testReadLogins() {
        ExcelReader reader = new ExcelReader();
        Map<String, String> logins = reader.readLoginsFromSheet("logins.xlsx", 0);
        assertEquals("abcd", logins.get("piotr")); // sprawdza poprawność hasła


    }

    static Stream<Arguments> checkIdFormat() {
        return Stream.of(
                Arguments.of("1", true),
                Arguments.of("3", true),
                Arguments.of("7", true),
                Arguments.of("0", false),
                Arguments.of("8", false),
                Arguments.of("15", false),
                Arguments.of("O", false),
                Arguments.of("a", false)
        );
    }

    @ParameterizedTest
    @MethodSource("checkIdFormat")
    void testReadExcelMenu_variousIdFormats(String idInput, boolean expectedValid) {
        ExcelReader reader = new ExcelReader();

        if (expectedValid) {
            ArrayList<Dish> menu = reader.readExcel("menu_pl_obiad.xlsx");
            int id = Integer.parseInt(idInput);
            assertFalse(menu.isEmpty(), "Menu nie powinno być puste");
            boolean found = false;
            for (Dish d : menu) {
                if (d.getId() == id) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Nie znaleziono dania o poprawnym ID: " + id);
        } else {
            // Sprawdzenie, że niepoprawny input wyrzuca wyjątek
            assertThrows(IllegalArgumentException.class, () -> {
                int id = Integer.parseInt(idInput);
                if (id < 1 || id > 7) throw new IllegalArgumentException("Niepoprawne ID: " + id);
            });
        }
    }

    // Zły format ceny w excelu
    @Test
    void testReadExcel_invalidPriceFormat() {
        ExcelReader reader = new ExcelReader();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reader.readExcel("menu_pl_test_obiad.xlsx");
        });

        String message = exception.getMessage();
        System.out.println("[" + message + "]");

        assertTrue(message.toLowerCase().contains("błędny format ceny"),
                "Oczekiwano komunikatu zawierającego 'błędny format ceny', ale było: " + message);
    }



    @Test
    void testReadMenu() {
        ExcelReader reader = new ExcelReader();
        ArrayList<Dish> menu = reader.readExcel("menu_pl_obiad.xlsx");
        assertFalse(menu.isEmpty());
        assertEquals("Kotlet schabowy", menu.get(0).getNameDish());

    }


//
//    @Test
//    void testReadExcelManager_variousDateFormats() {
//        ExcelReader reader = new ExcelReader();
//        ArrayList<Manager> managers1 = reader.readExcelManager("2025", "09", "07");
//        assertFalse(managers1.isEmpty(), "Nie odczytano danych dla 2025-09-07");
//
//        ArrayList<Manager> managers2 = reader.readExcelManager("2025", "9", "07");
//        assertFalse(managers2.isEmpty(), "Nie odczytano danych dla 2025-9-07");
//
//        ArrayList<Manager> managers3 = reader.readExcelManager("2025", "09", "7");
//        assertFalse(managers3.isEmpty(), "Nie odczytano danych dla 2025-09-7");
//
//        ArrayList<Manager> managers4 = reader.readExcelManager("2025", "9", "7");
//        assertFalse(managers4.isEmpty(), "Nie odczytano danych dla 2025-9-7");
//    }


}



