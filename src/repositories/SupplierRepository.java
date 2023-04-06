package repositories;

import dao.SupplierDAO;
import exceptions.DBException;
import java.util.List;
import model.Supplier;

public class SupplierRepository {

    private final SupplierDAO supplierDAO;

    public SupplierRepository(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public SupplierRepository() {
        this.supplierDAO = new SupplierDAO();
    }

    public void register(Supplier supplier) throws DBException {
        try {
            supplierDAO.register(supplier);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void update(Supplier supplier) throws DBException {
        try {
            supplierDAO.update(supplier);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void changeStatus(String status, int id) throws DBException {
        try {
            supplierDAO.changeStatus(status, id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public List<Supplier> getSuppliersList(String value) throws DBException {
        List<Supplier> suppliers;
        try {
            suppliers = supplierDAO.getSuppliersList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return suppliers;
    }
}
