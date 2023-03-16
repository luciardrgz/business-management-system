package dao;

import controllers.ProductController;
import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Connector;

public class CategoryDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private ProductController productController;

    public CategoryDAO() {
    }

    public CategoryDAO(ProductController productController) {
        this.productController = productController;
    }

    public void register(Category category) throws DBException {
        String sql = "INSERT INTO categories (name) VALUES (?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        }
    }

    public List getCategoriesList(String value) throws DBException {
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
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return categoriesList;
    }

    public int retrieveCategoryIdByName(String categoryName) throws DBException {
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
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundCategoryId;
    }

    public String retrieveCategoryNameById(int categoryId) throws DBException {
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
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundCategoryName;
    }

    public Category retrieveCategoryByName(String categoryName) throws DBException {
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
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundCategory;
    }

    public List<String> getCategoryNames() throws DBException {
        List<Category> categories;
        List<String> categoryNames = new ArrayList<>();
        try {
            categories = getCategoriesList("");
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
        } catch (DBException ex) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return categoryNames;
    }

    public void update(Category category) throws DBException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.setInt(2, category.getId());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE categories SET status = ? WHERE id = ?";

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
