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
import model.EPersonStatus;

public class CustomerDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Customer customer) throws DBException {
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
    
    public List<Customer> retrieveCustomersList(String value) throws DBException {
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
                 EPersonStatus status = EPersonStatus.valueOf(rs.getString("status"));
                currentCustomer.setStatus(status);

                customersList.add(currentCustomer);
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return customersList;
    }
    
    public String[] retrieveCustomerNameById(int customerId) throws DBException {
        String[] foundCustomerName = new String[2];
        String sql = "SELECT first_name, last_name FROM customers WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundCustomerName[0] = rs.getString("first_name");
                foundCustomerName[1] = rs.getString("last_name");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
        return foundCustomerName;
    }
    
    
    public int retrieveCustomerIdByName(String firstName, String lastName) throws DBException {
        int foundCustomerId = -1;

        String sql = "SELECT id FROM customers WHERE first_name LIKE '%" + firstName + "%' AND last_name LIKE '%" + lastName + "%'";

        try {
            conn = connector.getConn();

            if (!firstName.equalsIgnoreCase("") && !lastName.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                if (rs.next()) {
                    foundCustomerId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
        return foundCustomerId;
    }
    
     public List<String> retrieveCustomerNames() throws DBException {
        List<String> customerNames = new ArrayList<>();
        String sql = "SELECT first_name, last_name FROM customers";
        
        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
             rs = ps.executeQuery();

            while (rs.next()) {
                customerNames.add(rs.getString("first_name") + " " + rs.getString("last_name"));
            }

        } catch (SQLException e) {
            throw new DBException(e);
        }
        return customerNames;
    }
}