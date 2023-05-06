package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Connector;
import model.EProductStatus;
import model.Product;

public class ProductDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Product product) throws DBException {
        String sql = "INSERT INTO products (name, description, production_cost, stock, selling_price, category) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getProductionCost());
            ps.setInt(4, product.getStock());
            ps.setDouble(5, product.getSellingPrice());
            ps.setInt(6, product.getCategoryId());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        }
    }

    public void update(Product product) throws DBException {
        String sql = "UPDATE products SET name = ?, description = ?, stock = ?, production_cost = ?, selling_price = ?, category = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setInt(3, product.getStock());
            ps.setDouble(4, product.getProductionCost());
            ps.setDouble(5, product.getSellingPrice());
            ps.setInt(6, product.getCategoryId());
            ps.setInt(7, product.getId());

            ps.execute();

        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE products SET status = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.execute();
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    private void setupProduct(Product product) throws DBException {
        try {
            product.setId(rs.getInt("id"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setStock(rs.getInt("stock"));
            product.setProductionCost(rs.getDouble("production_cost"));
            product.setSellingPrice(rs.getDouble("selling_price"));
            product.setCategoryId(rs.getInt("category"));

            EProductStatus status = EProductStatus.valueOf(rs.getString("status"));
            product.setStatus(status);
        } catch (SQLException ex) {
            throw new DBException();
        }
    }

    public List<Product> retrieveProductsList(String value) throws DBException {
        List<Product> productsList = new ArrayList();
        CategoryDAO categoryDAO = new CategoryDAO();

        String sql = "SELECT * FROM products ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM products WHERE name LIKE ? OR category = ? OR description LIKE ?";

        try {
            conn = connector.getConn();

            if (value.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
            } else {

                ps = conn.prepareStatement(valueToSearch);
                ps.setString(1, "%" + value + "%");
                ps.setInt(2, categoryDAO.retrieveCategoryIdByName(value));
                ps.setString(3, "%" + value + "%");
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                Product currentProduct = new Product();
                setupProduct(currentProduct);
                productsList.add(currentProduct);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return productsList;
    }

    public Product retrieveProductById(int productId) throws DBException {
        Product foundProduct = new Product();
        String sql = "SELECT * FROM products WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();

            if (rs.next()) {
                setupProduct(foundProduct);
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundProduct;
    }

    public String retrieveProductNameById(int productId) throws DBException {
        String foundProductName = null;
        String sql = "SELECT name FROM products WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundProductName = rs.getString("name");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundProductName;
    }

    public int retrieveProductIdByName(String productName) throws DBException {
        int foundProductId = -1;
        String sql = "SELECT id FROM products WHERE name LIKE '%" + productName + "%'";

        try {
            conn = connector.getConn();

            if (!productName.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                if (rs.next()) {
                    foundProductId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundProductId;
    }

    public double retrieveProductPrice(int id) throws DBException {
        double price = -1;
        String sql = "SELECT selling_price FROM products WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("selling_price");
            }

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return price;
    }

    public List<String> retrieveProductNames() throws DBException {
        List<String> productNames = new ArrayList<>();
        String sql = "SELECT name FROM products";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                productNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            throw new DBException();
        }

        return productNames;
    }

    public int retrieveStockOfProduct(int productId) throws DBException {
        String sql = "SELECT stock FROM products WHERE id = ?";
        int stock = -1;

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            rs = ps.executeQuery();

            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
        return stock;
    }

    public void updateStock(int productId, int soldUnits) throws DBException {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";

        try {
            int newStock = retrieveStockOfProduct(productId) - soldUnits;

            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }
}