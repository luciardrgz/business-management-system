package repositories;

import dao.ProductDAO;
import exceptions.DBException;
import java.util.List;
import model.EProductStatus;
import model.Product;

public class ProductRepository {

    private ProductDAO productDAO;

    public ProductRepository(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public ProductRepository() {
        this.productDAO = new ProductDAO();
    }

    public void register(Product product) throws DBException {
        try {
            productDAO.add(product);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void update(Product product) throws DBException {
        try {
            productDAO.update(product);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public void changeStatus(EProductStatus status, int id) throws DBException {
        try {
            productDAO.changeStatus(status.name(), id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }

    public List<Product> getProductsList(String value) throws DBException {
        List<Product> products;
        try {
            products = productDAO.retrieveProductsList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return products;
    }

    public Product getProductById(int productId) throws DBException {
        Product product = new Product();
        try {
            product = productDAO.retrieveProductById(productId);
        } catch (DBException ex) {
            throw new DBException();
        }
        return product;
    }

    public String getProductNameById(int productId) throws DBException {
        return productDAO.retrieveProductNameById(productId);
    }

    public int getProductIdByName(String productName) throws DBException {
        return productDAO.retrieveProductIdByName(productName);
    }

    public double getProductPrice(int id) throws DBException {
        return productDAO.retrieveProductPrice(id);
    }

    public List<String> getProductNames() throws DBException {
        return productDAO.retrieveProductNames();
    }

    public int getProductStock(int id) throws DBException {
        return productDAO.retrieveStockOfProduct(id);
    }

    public void updateStock(int productId, int soldUnits) throws DBException {
        try {
            if (getProductStock(productId) >= 1) {
            productDAO.updateStock(productId, soldUnits);
            }
        } catch (DBException ex) {
            throw ex;
        }
    }
}
