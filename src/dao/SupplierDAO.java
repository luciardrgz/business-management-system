package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.EPersonStatus;
import model.Supplier;

public class SupplierDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Supplier supplier) throws DBException {
        String sql = "INSERT INTO suppliers (first_name, last_name, social_name, cuit, phone, address) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, supplier.getFirstName());
            ps.setString(2, supplier.getLastName());
            ps.setString(3, supplier.getSocialName());
            ps.setString(4, supplier.getCuit());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());
            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public void update(Supplier supplier) throws DBException {
        String sql = "UPDATE suppliers SET first_name = ?, last_name = ?, social_name = ?, cuit = ?, phone = ?, address = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, supplier.getFirstName());
            ps.setString(2, supplier.getLastName());
            ps.setString(3, supplier.getSocialName());
            ps.setString(4, supplier.getCuit());
            ps.setString(5, supplier.getPhone());
            ps.setString(6, supplier.getAddress());
            ps.setInt(7, supplier.getId());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE suppliers SET status = ? WHERE id = ?";

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
    
        public List<Supplier> retrieveSuppliersList(String value) throws DBException {
        List<Supplier> suppliersList = new ArrayList();

        String sql = "SELECT * FROM suppliers ORDER BY status ASC";
        String valueToSearch = "SELECT * FROM suppliers WHERE first_name LIKE '%" + value + "%' OR last_name LIKE '%" + value + "%' OR social_name LIKE '%" + value + "%'";

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
                Supplier currentSupplier = new Supplier();
                currentSupplier.setId(rs.getInt("id"));
                currentSupplier.setFirstName(rs.getString("first_name"));
                currentSupplier.setLastName(rs.getString("last_name"));
                currentSupplier.setSocialName(rs.getString("social_name"));
                currentSupplier.setCuit(rs.getString("cuit"));
                currentSupplier.setPhone(rs.getString("phone"));
                currentSupplier.setAddress(rs.getString("address"));
                
                EPersonStatus status = EPersonStatus.valueOf(rs.getString("status"));
                currentSupplier.setStatus(status);


                suppliersList.add(currentSupplier);
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return suppliersList;
    }

    public List<String> retrieveSupplierNames() throws DBException {
        List<Supplier> suppliers;
        List<String> supplierNames = new ArrayList<>();
        try {
            suppliers = retrieveSuppliersList("");
            for (Supplier supplier : suppliers) {
                supplierNames.add(supplier.getSocialName());
            }
        } catch (DBException ex) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return supplierNames;
    }

    public int retrieveSupplierIdByName(String supplierName) throws DBException {
        int foundSupplierId = -1;

        String sql = "SELECT id FROM suppliers WHERE social_name LIKE ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + supplierName + "%");

            rs = ps.executeQuery();

            if (rs.next()) {
                foundSupplierId = rs.getInt("id");
            }

        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
        return foundSupplierId;
    }

    public String retrieveSupplierNameById(int supplierId) throws DBException {
        String foundSupplierName = null;
        String sql = "SELECT social_name FROM suppliers WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, supplierId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundSupplierName = rs.getString("social_name");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundSupplierName;
    }

}
