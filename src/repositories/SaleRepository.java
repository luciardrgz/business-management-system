package repositories;

import dao.SaleDAO;
import exceptions.DBException;
import model.Sale;

public class SaleRepository {
    
    private final SaleDAO saleDAO;

    public SaleRepository(SaleDAO saleDAO) {
        this.saleDAO = saleDAO;
    }

    public SaleRepository() {
        this.saleDAO = new SaleDAO();
    }
    
    public void generate(Sale sale) throws DBException {
        try {
            saleDAO.add(sale);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public int getLastId() throws DBException {
        int id = -1;
        try {
            id = saleDAO.retrieveLastId();
        } catch (DBException ex) {
            throw new DBException();
        }
        return id;
    }
}
