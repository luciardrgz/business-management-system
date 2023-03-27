package repositories;

import dao.ProductDAO;
import exceptions.DBException;
import java.util.List;

public class ProductRepository {

    private ProductDAO productDAO;
    
    public ProductRepository(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public ProductRepository() {
        this.productDAO = new ProductDAO();
    }

    public String retrieveProductNameById(int productId) throws DBException {
        return productDAO.retrieveProductNameById(productId);
    }

    public int retrieveProductIdByName(String productName) throws DBException {
        return productDAO.retrieveProductIdByName(productName);
    }

    public double retrieveProductPrice(int id) throws DBException {
        return productDAO.retrieveProductPrice(id);
    }

    public List<String> retrieveProductNames() throws DBException {
        return productDAO.retrieveProductNames();
    }
}

