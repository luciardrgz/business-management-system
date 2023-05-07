package repositories;

import dao.SupplierDAO;
import exceptions.DBException;
import java.util.List;
import model.EPersonStatus;
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
            supplierDAO.add(supplier);
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

    public void changeStatus(EPersonStatus status, int id) throws DBException {
        try {
            supplierDAO.changeStatus(status.name(), id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public List<Supplier> getSuppliersList(String value) throws DBException {
        List<Supplier> suppliers;
        try {
            suppliers = supplierDAO.retrieveSuppliersList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return suppliers;
    }
    
    public int getSupplierIdByName(String name) throws DBException {
        int id = -1;
        try {
            id = supplierDAO.retrieveSupplierIdByName(name);
        } catch (DBException ex) {
            throw new DBException();
        }
        return id;
    }
    
     public String getSupplierNameById(int id) throws DBException {
        String name = "";
        try {
            name = supplierDAO.retrieveSupplierNameById(id);
        } catch (DBException ex) {
            throw new DBException();
        }
        return name;
    }
     
     public List<String> getSupplierNames() throws DBException {
        List<String> names = null;
        try {
            names = supplierDAO.retrieveSupplierNames();
        } catch (DBException ex) {
            throw new DBException();
        }
        return names;
    }
}
