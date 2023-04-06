package repositories;

import dao.PurchaseDAO;
import exceptions.DBException;
import java.util.List;
import model.Purchase;

public class PurchaseRepository {
    private final PurchaseDAO purchaseDAO;

    public PurchaseRepository(PurchaseDAO purchaseDAO) {
        this.purchaseDAO = purchaseDAO;
    }

    public PurchaseRepository() {
        this.purchaseDAO = new PurchaseDAO();
    }

    public void register(Purchase purchase) throws DBException {
        try {
            purchaseDAO.add(purchase);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void update(Purchase purchase) throws DBException {
        try {
            purchaseDAO.update(purchase);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        try {
            purchaseDAO.changeStatus(status, id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public List<Purchase> getPurchasesList(String value) throws DBException {
        List<Purchase> purchases;
        try {
            purchases = purchaseDAO.getPurchasesList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return purchases;
    }
}
