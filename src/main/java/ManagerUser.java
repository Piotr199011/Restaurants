public class ManagerUser extends User {

    public ManagerUser(String username) {
        super(username);
    }

    @Override
    public String getRole() {
        return "Manager";
    }
}
