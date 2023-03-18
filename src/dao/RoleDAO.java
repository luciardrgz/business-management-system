package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.Role;

public class RoleDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List getRolesList() throws DBException {
        List<Role> rolesList = new ArrayList();

        String sql = "SELECT * FROM roles ORDER BY id ASC";

        try {
            conn = connector.getConn();

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Role currentRole = new Role();
                currentRole.setId(rs.getInt("id"));
                currentRole.setName(rs.getString("name"));
                rolesList.add(currentRole);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return rolesList;
    }

    public List<String> getRolesNamesList() throws DBException {
        List<String> rolesList = new ArrayList();

        String sql = "SELECT name FROM roles";

        try {
            conn = connector.getConn();

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String roleName = rs.getString("name");
                rolesList.add(roleName);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return rolesList;
    }
    
    public String retrieveRoleNameById(int roleId) throws DBException{
        String foundRoleName = null;
        String sql = "SELECT name FROM roles WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundRoleName = rs.getString("name");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundRoleName;
    }
    
    public int retrieveRoleIdByName(String roleName) throws DBException{
        int foundRoleId = -1;
        String sql = "SELECT id FROM roles WHERE name LIKE ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, roleName);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundRoleId = rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundRoleId;
    }
}
