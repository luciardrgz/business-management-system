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
import model.ECategoryType;

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
        String sql = "INSERT INTO categories (name, type) VALUES (?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.setString(2, category.getCategoryType().toString());
            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        }
    }

    public void update(Category category) throws DBException {
        String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, category.getName());
            ps.setString(2, category.getCategoryType().toString());
            ps.setInt(3, category.getId());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
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

                ECategoryType categoryType = ECategoryType.valueOf(rs.getString("type"));
                currentCategory.setCategoryType(categoryType);

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

    public List<String> retrieveCategoryNames(ECategoryType categoryType) throws DBException {
        String sql = "SELECT name FROM categories WHERE type LIKE ?";
        List<String> categoryNames = new ArrayList<>();

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + categoryType.toString() + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                categoryNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            connector.closeConn(conn);
        }

        return categoryNames;
    }

    public List<String> retrieveCategoryTypes() throws DBException {
        String sql = "SELECT type FROM categories";
        List<String> categoryTypes = new ArrayList<>();

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                categoryTypes.add(rs.getString("type"));
            }

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            connector.closeConn(conn);
        }

        return categoryTypes;
    }
}
