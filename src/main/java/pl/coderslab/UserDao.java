package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.utils.DbUtil;
import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?;";
    private static final String READ_USER_BY_ID =
            "SELECT * FROM users WHERE id = ?;";
    private static final String DELETE_USER_BY_ID =
            "DELETE FROM users WHERE id = ?;";
    private static final String READ_ALL_USERS =
            "SELECT * FROM users;";

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    public User create(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public User read(int userId) {
        try (Connection conn = DbUtil.getConnection()) {
            User justReadUser = new User();
            PreparedStatement statement = conn.prepareStatement(READ_USER_BY_ID);
            statement.setInt(1, userId);
            ResultSet rS = statement.executeQuery();

            if (rS.next()) {
                justReadUser.setEmail(rS.getString("email"));
                justReadUser.setUserName(rS.getString("username"));
                justReadUser.setId(rS.getInt("id"));
                justReadUser.setPassword(rS.getString("password"));
            }
            return justReadUser;
        }  catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //
    // UPDATE_USER = "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?;"

    public void update(User user) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_USER);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUserName());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setInt(4, user.getId());
            statement.executeUpdate();
        }
        catch (SQLException e) {
        e.printStackTrace();
        }
    }

    public void delete(int userId) {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(DELETE_USER_BY_ID);
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1);
        tmpUsers[users.length] = u;
        return tmpUsers;
    }

    public User[] findAll() {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(READ_ALL_USERS);
            ResultSet rS = statement.executeQuery();
            User[] findAllTempArr = new User[0];
            while (rS.next()) {
                User u = new User();

                u.setEmail(rS.getString("email"));
                u.setUserName(rS.getString("username"));
                u.setId(rS.getInt("id"));
                u.setPassword(rS.getString("password"));

                findAllTempArr = addToArray(u, findAllTempArr);

            }
            return findAllTempArr;

        }  catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




}




