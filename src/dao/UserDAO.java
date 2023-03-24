package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.EPersonStatus;
import model.ERole;
import model.User;

public class UserDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public User login(String username, String pass) throws DBException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        User user = new User();

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, pass);

            rs = ps.executeQuery();

            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));

                ERole role = ERole.valueOf(rs.getString("role"));
                user.setRole(role);

                EPersonStatus status = EPersonStatus.valueOf(rs.getString("status"));
                user.setStatus(status);

            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return user;
    }

    public void register(User user) throws DBException {
        String sql = "INSERT INTO users (username, first_name, last_name, password, role) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().toString());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public List getUsersList(String value) throws DBException {
        List<User> usersList = new ArrayList();

        String sql = "SELECT * FROM users ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM users WHERE username LIKE '%" + value + "%' OR first_name LIKE '%" + value + "%'";

        try {
            conn = connector.getConn();

            if (value.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
            } else {
                ps = conn.prepareStatement(valueToSearch);
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                User currentUser = new User();
                currentUser.setId(rs.getInt("id"));
                currentUser.setUsername(rs.getString("username"));
                currentUser.setFirstName(rs.getString("first_name"));
                currentUser.setLastName(rs.getString("last_name"));

                ERole role = ERole.valueOf(rs.getString("role"));
                currentUser.setRole(role);

                EPersonStatus status = EPersonStatus.valueOf(rs.getString("status"));
                currentUser.setStatus(status);

                usersList.add(currentUser);
            }
        } catch (SQLException ex) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return usersList;
    }

    public void update(User user) throws DBException {
        String sql = "UPDATE users SET username = ?, first_name = ?, last_name = ?, role = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getRole().toString());
            ps.setInt(5, user.getId());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.execute();
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

}
