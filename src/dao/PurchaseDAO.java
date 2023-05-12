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
import model.EPurchaseStatus;
import model.EUnit;
import model.Purchase;

public class PurchaseDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Purchase purchase) throws DBException {
        String sql = "INSERT INTO purchases (name, quantity, unit, unitary_price, date, supplier, payment_method, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, purchase.getName());
            ps.setString(2, purchase.getQuantity());
            ps.setString(3, purchase.getEUnit().toString());
            ps.setDouble(4, purchase.getUnitaryPrice());
            ps.setString(5, purchase.getDate());
            ps.setInt(6, purchase.getSupplier());
            ps.setString(7, purchase.getEPaymentMethod().toString());
            ps.setString(8, purchase.getEStatus().toString());

            ps.execute();

        } catch (SQLException e) {
            throw new DBException();
        }
    }

    public void update(Purchase purchase) throws DBException {
        String sql = "UPDATE purchases SET name = ?, quantity = ?, unit = ?, unitary_price = ?, date = ?, supplier = ?, payment_method = ?, status = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, purchase.getName());
            ps.setString(2, purchase.getQuantity());
            ps.setString(3, purchase.getEUnit().toString());
            ps.setDouble(4, purchase.getUnitaryPrice());
            ps.setString(5, purchase.getDate());
            ps.setDouble(6, purchase.getSupplier());
            ps.setString(7, purchase.getEPaymentMethod().toString());
            ps.setString(8, purchase.getEStatus().toString());
            ps.setInt(9, purchase.getId());

            ps.execute();
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        String sql = "UPDATE purchases SET status = ? WHERE id = ?";

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

    public List<Purchase> retrievePurchasesList(String value) throws DBException {
        List<Purchase> purchasesList = new ArrayList();
        SupplierDAO supplierDAO = new SupplierDAO();

        String sql = "SELECT * FROM purchases ORDER BY id ASC";
        String valueToSearch = "SELECT * FROM purchases WHERE name LIKE ? or supplier LIKE ?";

        try {
            conn = connector.getConn();

            if (value.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
            } else {
                ps = conn.prepareStatement(valueToSearch);
                ps.setString(1, "%" + value + "%");
                ps.setInt(2, supplierDAO.retrieveSupplierIdByName(value));
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                Purchase currentPurchase = new Purchase();
                currentPurchase.setId(rs.getInt("id"));
                currentPurchase.setName(rs.getString("name"));
                currentPurchase.setQuantity(rs.getString("quantity"));

                EUnit unit = EUnit.valueOf(rs.getString("unit"));
                currentPurchase.setEUnit(unit);

                currentPurchase.setUnitaryPrice(rs.getDouble("unitary_price"));
                currentPurchase.setDate(rs.getString("date"));
                currentPurchase.setSupplier(rs.getInt("supplier"));

                EPaymentMethod paymentMethod = EPaymentMethod.valueOf(rs.getString("payment_method"));
                currentPurchase.setEPaymentMethod(paymentMethod);

                EPurchaseStatus status = EPurchaseStatus.valueOf(rs.getString("status"));
                currentPurchase.setEStatus(status);

                purchasesList.add(currentPurchase);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return purchasesList;
    }
}
