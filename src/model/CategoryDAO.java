package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class CategoryDAO {

    Connector connector = new Connector();
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    
    public boolean register(Category category) {
        boolean check = true;
        String sql = "INSERT INTO categories name VALUES ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.execute();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        } finally {
            return check;
        }
    }

    public List getCategoriesList(String value) {
        List<Category> categoriesList = new ArrayList();

        String sql = "SELECT * FROM categories ORDER BY id ASC";
        String valueToSearch = "SELECT * FROM categories WHERE name LIKE '%" + value + "%'";

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
                Category currentCategory = new Category();
                currentCategory.setId(rs.getInt("id"));
                currentCategory.setName(rs.getString("name"));

                categoriesList.add(currentCategory);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return categoriesList;
    }

    public int retrieveCategoryIdByName(String categoryName) {
        int foundCategoryId = -1;

        String sql = "SELECT id FROM categories WHERE name LIKE '%" + categoryName + "%'";

        try {
            conn = connector.getConn();

            if (!categoryName.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                if (rs.next()) {
                    foundCategoryId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return foundCategoryId;
    }

    public String retrieveCategoryNameById(int categoryId) {
        String foundCategoryName = null;
        String sql = "SELECT name FROM categories WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundCategoryName = rs.getString("name");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return foundCategoryName;
    }
    
    public Category retrieveCategoryByName(String categoryName) {
        Category foundCategory = new Category();

        String sql = "SELECT * FROM categories WHERE name LIKE ?";

        try {
            conn = connector.getConn();

            if (!categoryName.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, categoryName);
                rs = ps.executeQuery();

                if (rs.next()) {
                    foundCategory.setId(rs.getInt("id"));
                    foundCategory.setName(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return foundCategory;
    }

    public boolean update(Category category) {
        boolean check = true;
        String sql = "UPDATE categories SET name = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());

            ps.execute();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        } finally {
            return check;
        }
    }

    public boolean changeStatus(String status, int id) {
        boolean check = true;
        String sql = "UPDATE categories SET status = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        } finally {
            return check;
        }

    }

}
