package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.EProductStatus;
import model.Product;

public class ProductDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Product product) throws DBException {
        String sql = "INSERT INTO products (name, description, production_cost, stock, selling_price, id_category) VALUES (?, ?, ?, ?, ?, ?)";

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

    public List<Product> getProductsList(String value) throws DBException {
        List<Product> productsList = new ArrayList();
        CategoryDAO categoryDAO = new CategoryDAO();

        String sql = "SELECT * FROM products ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM products WHERE name LIKE ? OR id_category = ?";

        try {
            conn = connector.getConn();

            if (value.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
            } else {

                ps = conn.prepareStatement(valueToSearch);
                ps.setString(1, "%" + value + "%");
                ps.setInt(2, categoryDAO.retrieveCategoryIdByName(value));
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                Product currentProduct = new Product();
                currentProduct.setId(rs.getInt("id"));
                currentProduct.setName(rs.getString("name"));
                currentProduct.setDescription(rs.getString("description"));
                currentProduct.setStock(rs.getInt("stock"));
                currentProduct.setProductionCost(rs.getDouble("production_cost"));
                currentProduct.setSellingPrice(rs.getDouble("selling_price"));
                currentProduct.setCategoryId(rs.getInt("id_category"));

                EProductStatus status = EProductStatus.valueOf(rs.getString("status"));
                currentProduct.setStatus(status);

                productsList.add(currentProduct);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return productsList;
    }

    public void update(Product product) throws DBException {
        String sql = "UPDATE products SET name = ?, description = ?, stock = ?, production_cost = ?, selling_price = ?, id_category = ? WHERE id = ?";

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
            throw new DBException(e);
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

            if (rs.next()) {
                productNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            throw new DBException(e);
        }

        return productNames;
    }
}
