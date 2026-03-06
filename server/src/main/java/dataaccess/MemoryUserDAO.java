package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData userData = new UserData(username, hashedPassword, email);
        users.put(username, userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(users);
    }

    public HashMap<String, UserData> getUsers() {
        return users;
    }
}
