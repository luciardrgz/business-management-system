package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.EPaymentMethod;
import model.Sale;
import repositories.CustomerRepository;
import repositories.ProductRepository;

public class SaleDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;

    public void add(Sale sale) throws DBException {
        String sql = "INSERT INTO sales (id, product, quantity, customer, payment_method, total) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, sale.getId());
            ps.setInt(2, sale.getProduct());
            ps.setInt(3, sale.getQuantity());
            ps.setInt(4, sale.getCustomer());
            ps.setString(5, sale.getPaymentMethod().toString());
            ps.setDouble(6, sale.getTotal());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        }
    }
    
    public void update(Sale sale) throws DBException {
        String sql = "UPDATE sales SET product = ?, quantity = ?, customer = ?, payment_method = ?, total = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, productRepository.getProductNameById(sale.getProduct()));
            ps.setInt(2, sale.getQuantity());
            String[] customerName = customerRepository.getCustomerNameById(sale.getCustomer());
            ps.setString(3, customerName[0] + " " + customerName[1]);
            ps.setString(4, sale.getPaymentMethod().toString());
            ps.setDouble(5, sale.getTotal());
            ps.setInt(6, sale.getId());

            ps.execute();
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public int retrieveLastId() throws DBException {
        int lastId = 0;
        String sql = "SELECT MAX(id) FROM sales";
        
        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                lastId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DBException();
        }
        
        return lastId;
    }

    public List<Sale> retrieveSalesList(String value) throws DBException {
        List<Sale> salesList = new ArrayList();

        String sql = "SELECT * FROM sales ORDER BY id ASC";
        String valueToSearch = "SELECT * FROM sales WHERE product LIKE ? or customer LIKE ?";

        try {
            conn = connector.getConn();

            if (value.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
            } else {
                ps = conn.prepareStatement(valueToSearch);
                ps.setString(1, "%" + value + "%");
                ps.setString(2, "%" + value + "%");
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                Sale currentSale = new Sale();
                currentSale.setId(rs.getInt("id"));
                currentSale.setProduct(rs.getInt("product"));
                currentSale.setQuantity(rs.getInt("quantity"));
                currentSale.setCustomer(rs.getInt("customer"));

                EPaymentMethod paymentMethod = EPaymentMethod.valueOf(rs.getString("payment_method"));
                currentSale.setPaymentMethod(paymentMethod);

                currentSale.setTotal(rs.getInt("total"));

                salesList.add(currentSale);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return salesList;
    }
    
    public int retrieveSaleCustomer(int saleId) throws DBException{
        int customerId = -1;
        
        String sql = "SELECT DISTINCT customer FROM sales WHERE id = ?";
        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, saleId);
            rs = ps.executeQuery();

            if (rs.next()) {
                customerId = rs.getInt("customer");
            }
        } catch (SQLException ex) {
            throw new DBException();
        }
        return customerId;
    }
}
