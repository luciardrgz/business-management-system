package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ProductDAO {

    Connector connector = new Connector();
    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    public boolean add(Product product) {
        boolean check = true;
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
            JOptionPane.showMessageDialog(null, e.toString());
            System.out.println("SQL EXCEPTION EN ADD DE PRODUCT DAO");
            check = false;
        } finally {
            return check;
        }
    }

    public List getProductsList(String value) {
        List<Product> productsList = new ArrayList();
        CategoryDAO categoryDAO = new CategoryDAO();

        String sql = "SELECT * FROM products ORDER BY status DESC";
        String valueToSearch = "SELECT * FROM products WHERE name LIKE ? OR id_category = ?"; //+ categoryDAO.retrieveCategoryIdByName(value);

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
                currentProduct.setStatus(rs.getString("status"));

                productsList.add(currentProduct);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return productsList;
    }

    public boolean update(Product product) {
        boolean check = true;
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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            check = false;
        } finally {
            return check;
        }
    }

    public boolean changeStatus(String status, int id) {
        boolean check = true;
        String sql = "UPDATE products SET status = ? WHERE id = ?";

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
