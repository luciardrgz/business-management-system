package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.Customer;

public class CustomerDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void register(Customer customer) throws DBException {
        String sql = "INSERT INTO customers (first_name, last_name, phone, address) VALUES (?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public List getCustomersList(String value) throws DBException {
        List<Customer> customersList = new ArrayList();

        String sql = "SELECT * FROM customers ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM customers WHERE first_name LIKE '%" + value + "%' OR last_name LIKE '%" + value + "%'";

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
                Customer currentCustomer = new Customer();
                currentCustomer.setId(rs.getInt("id"));
                currentCustomer.setFirstName(rs.getString("first_name"));
                currentCustomer.setLastName(rs.getString("last_name"));
                currentCustomer.setPhone(rs.getString("phone"));
                currentCustomer.setAddress(rs.getString("address"));
                currentCustomer.setStatus(rs.getString("status"));

                customersList.add(currentCustomer);
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return customersList;
    }

    public void update(Customer customer) throws DBException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, phone = ?, address = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getId());

            ps.execute();

        } catch (SQLException e) {
           throw new DBException();
        }  finally {
            connector.closeConn(conn);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE customers SET status = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, status);
            ps.setInt(2, id);

            ps.execute();
        } catch (SQLException e) {
            throw new DBException();
        }  finally {
            connector.closeConn(conn);
        }
    }
}
