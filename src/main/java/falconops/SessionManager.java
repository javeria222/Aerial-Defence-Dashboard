package falconops;

public class SessionManager {

    public static boolean authenticate(String username, String password) {
        return "admin".equals(username) && "password".equals(password);
    }

}