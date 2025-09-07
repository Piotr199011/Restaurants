
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelReaderTest {
    static Stream<String[]> provideDateFormats() {
        return Stream.of(
                new String[]{"2025", "08", "07"},
                new String[]{"2025", "8", "07"},
                new String[]{"2025", "08", "7"},
                new String[]{"2025", "8", "7"}
        );
    }

    @ParameterizedTest
    @MethodSource("provideDateFormats")
    void testReadExcelManager_variousDateFormats(String[] dateParts) {
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];

        ExcelReader reader = new ExcelReader();
        var managers = reader.readExcelManager(year, month, day);

        assertFalse(managers.isEmpty(),
                "Nie odczytano danych dla: " + year + "-" + month + "-" + day);
    }
    @Test
    void testReadExcelManager_dateNoExist() {
        ExcelReader reader = new ExcelReader();
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            reader.readExcelManager("2023","8","5");

        });
    }





    @Test
    void testReadLogins() {
        ExcelReader reader = new ExcelReader();
        Map<String, String> logins = reader.readLoginsFromSheet("logins.xlsx", 0);
        assertEquals("abcd", logins.get("piotr")); // sprawdza poprawnoœæ has³a


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



