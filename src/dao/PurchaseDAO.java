package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.Purchase;

public class PurchaseDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public void add(Purchase purchase) throws DBException {
        String sql = "INSERT INTO purchases (name, quantity, unitary_price, date, supplier, payment_method) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, purchase.getName());
            ps.setString(2, purchase.getQuantity());
            ps.setDouble(3, purchase.getUnitaryPrice());
            ps.setString(4, purchase.getDate());
            ps.setInt(5, purchase.getSupplier());
            ps.setInt(6, purchase.getPaymentMethod());

            ps.execute();
        } catch (SQLException e) {
            throw new DBException();
        }
    }

    public List<Purchase> getPurchasesList() throws DBException {
        List<Purchase> purchasesList = new ArrayList();

        String sql = "SELECT * FROM purchases ORDER BY id ASC";

        try {
            conn = connector.getConn();

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Purchase currentPurchase = new Purchase();
                currentPurchase.setId(rs.getInt("id"));
                currentPurchase.setName(rs.getString("name"));
                currentPurchase.setQuantity(rs.getString("quantity"));
                currentPurchase.setUnitaryPrice(rs.getDouble("unitary_price"));
                currentPurchase.setDate(rs.getString("date"));
                currentPurchase.setSupplier(rs.getInt("supplier"));
                currentPurchase.setPaymentMethod(rs.getInt("payment_method"));

                purchasesList.add(currentPurchase);
            }
        } catch (SQLException e) {
            throw new DBException();
        }

        return purchasesList;
    }

    public void update(Purchase purchase) throws DBException {
        String sql = "UPDATE purchases SET name = ?, quantity = ?, unitary_price = ?, date = ?, supplier = ?, payment_method = ? WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);

            ps.setString(1, purchase.getName());
            ps.setString(2, purchase.getQuantity());
            ps.setDouble(3, purchase.getUnitaryPrice());
            ps.setString(4, purchase.getDate());
            ps.setDouble(5, purchase.getSupplier());
            ps.setInt(6, purchase.getPaymentMethod());
            ps.setInt(7, purchase.getId());

            ps.execute();
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }
}
