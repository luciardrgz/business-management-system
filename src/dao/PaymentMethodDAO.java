package dao;

import exceptions.DBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Connector;
import model.PaymentMethod;

public class PaymentMethodDAO {

    private final Connector connector = new Connector();
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List getPaymentMethodsList(String value) throws DBException {
        List<PaymentMethod> paymentMethodsList = new ArrayList();

        String sql = "SELECT * FROM payment_methods ORDER BY id ASC";
        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PaymentMethod currentPaymentMethod = new PaymentMethod();
                currentPaymentMethod.setId(rs.getInt("id"));
                currentPaymentMethod.setName(rs.getString("name"));

                paymentMethodsList.add(currentPaymentMethod);
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return paymentMethodsList;
    }

    public List<String> getPaymentMethodNames() throws DBException {
        List<PaymentMethod> paymentMethods;
        List<String> paymentMethodNames = new ArrayList<>();
        try {
            paymentMethods = getPaymentMethodsList("");
            for (PaymentMethod paymentMethod : paymentMethods) {
                paymentMethodNames.add(paymentMethod.getName());
            }
        } catch (DBException ex) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return paymentMethodNames;
    }

    public int retrievePaymentMethodIdByName(String paymentMethodName) throws DBException {
        int foundPaymentMethodId = -1;

        String sql = "SELECT id FROM payment_methods WHERE name LIKE ?";

        try {
            conn = connector.getConn();

            if (!paymentMethodName.equalsIgnoreCase("")) {
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + paymentMethodName + "%");
                rs = ps.executeQuery();

                if (rs.next()) {
                    foundPaymentMethodId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }
        return foundPaymentMethodId;
    }

    public String retrievePaymentMethodNameById(int paymentMethodId) throws DBException {
        String foundPaymentMethodName = null;
        String sql = "SELECT name FROM payment_methods WHERE id = ?";

        try {
            conn = connector.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, paymentMethodId);
            rs = ps.executeQuery();

            if (rs.next()) {
                foundPaymentMethodName = rs.getString("name");
            }
        } catch (SQLException e) {
            throw new DBException();
        } finally {
            connector.closeConn(conn);
        }

        return foundPaymentMethodName;
    }
}
