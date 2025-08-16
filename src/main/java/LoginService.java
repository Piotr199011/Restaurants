import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private ExcelReader excelReader;
    private static final String FILE_PATH = "logins.xlsx";

    private LoginService(ExcelReader excelReader) {
        this.excelReader = new ExcelReader();
    }

    private static LoginService instance;

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService(new ExcelReader());
        }
        return instance;
    }

    public User authenticate(String role, String username, String password) {
        Map<String, Integer> roleMap = new HashMap<>();
        roleMap.put("Waiter", 0);
        roleMap.put("Cook", 1);
        roleMap.put("Manager", 2);
        if (!roleMap.containsKey(role)) {
            System.out.println("Nieznana rola: "+role);
            return null;
        }
        Map<String, String> logins = this.excelReader.readLoginsFromSheet(
                FILE_PATH, roleMap.get(role));

        if (logins.containsKey(username) && logins.get(username).equals(password)) {
            switch (role) {
                case "Waiter":
                    return new Waiter(username);
                case "Cook":
                    return new Cook(username);
                case "Manager":
                    return new Manager(username);
            }
        } else return null;


        return null;
    }

}
