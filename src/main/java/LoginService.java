import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private ExcelReader excelReader;
    private static final String FILE_PATH = "logins.xlsx";

    private LoginService(ExcelReader excelReader) {
        this.excelReader = excelReader;
    }

    private static LoginService instance;

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService(new ExcelReader());
        }
        return instance;
    }

    public User authenticate(String role, String username, String password) {

        Map<String, Integer> roleMap = Map.of(
                "Waiter", 0,
                "Cook", 1,
                "Manager", 2
        );

        if (!roleMap.containsKey(role)) {
            System.out.println("Nieznana rola: " + role);
            return null;
        }

        Map<String, String> logins = excelReader.readLoginsFromSheet(FILE_PATH, roleMap.get(role));

        if (!logins.containsKey(username)) {
            System.out.println("Nie znaleziono użytkownika: " + username);
            return null;
        }

        if (!logins.get(username).equals(password)) {
            System.out.println("Błędne hasło dla użytkownika: " + username);
            return null;
        }

        switch (role) {
            case "Waiter": return new Waiter(username);
            case "Cook": return new Cook(username);
            case "Manager": return new ManagerUser(username);
            default: return null; // tu i tak nie trafimy
        }
    }


}
