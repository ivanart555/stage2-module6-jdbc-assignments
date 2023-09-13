package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Getter
@Setter
@AllArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final Logger LOGGER = Logger.getLogger(SimpleJDBCRepository.class.getName());
    private static final String CREATE_USER = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_ID = "SELECT * FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_NAME = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String FIND_ALL_USERS = "SELECT * FROM myusers";

    public SimpleJDBCRepository() {
        try {
            connection = CustomDataSource.getInstance(PropertiesLoader.loadPropertiesFromFile("app.properties")).getConnection();
        } catch (SQLException e) {
            LOGGER.warning("Failed to get connection!");
        }
    }

    public Long createUser(String firstName, String lastName, int age) {
        try {
            ps = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, age);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.warning("Failed to create user!");
        } finally {
            closeResources();
        }
        return null;
    }

    public User findUserById(Long userId) {
        try {
            ps = connection.prepareStatement(FIND_USER_BY_ID);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getInt("age")
                );
            }
        } catch (SQLException e) {
            LOGGER.warning("Failed to find user with ID: " + userId + "!");
        } finally {
            closeResources();
        }
        return null;
    }

    public User findUserByName(String userName) {
        try {
            ps = connection.prepareStatement(FIND_USER_BY_NAME);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getInt("age")
                );
            }
        } catch (SQLException e) {
            LOGGER.warning("Failed to find user with name: " + userName + "!");
        } finally {
            closeResources();
        }
        return null;
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(FIND_ALL_USERS);

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getInt("age")
                ));
            }
        } catch (SQLException e) {
            LOGGER.warning("Failed to find all users!");
        } finally {
            closeResources();
        }
        return users;
    }

    public User updateUser(User user) {
        try {
            ps = connection.prepareStatement(UPDATE_USER);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());

            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            LOGGER.warning("Failed to update user with ID: " + user.getId() + "!");
            return user;
        } finally {
            closeResources();
        }
    }

    public void deleteUser(Long userId) {
        try {
            ps = connection.prepareStatement(DELETE_USER);
            ps.setLong(1, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.warning("Failed to delete user with ID: " + userId + "!");
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            LOGGER.warning("Failed to close resource!");
        }
    }
}
