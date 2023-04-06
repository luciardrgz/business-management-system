package repositories;

import dao.CustomerDAO;
import exceptions.DBException;
import java.util.List;
import model.Customer;
import utils.ComboBoxUtils;

public class CustomerRepository {
    private final CustomerDAO customerDAO;

    public CustomerRepository(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public CustomerRepository() {
        this.customerDAO = new CustomerDAO();
    }
    
     public void register(Customer customer) throws DBException {
        try {
            customerDAO.register(customer);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public void update(Customer customer) throws DBException {
        try {
            customerDAO.update(customer);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public void changeStatus(String status, int id) throws DBException {
        try {
            customerDAO.changeStatus(status, id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public List<Customer> getCustomersList(String value) throws DBException {
        List<Customer> customers;
        try {
            customers = customerDAO.getCustomersList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return customers;
    }

    public String retrieveCustomerNameById(int customerId) throws DBException {
        return customerDAO.retrieveCustomerNameById(customerId);
    }

    public int retrieveCustomerIdByName(String customerName) throws DBException {
        String[] splittedName = ComboBoxUtils.splitCboxCustomerName(customerName);
        return customerDAO.retrieveCustomerIdByName(splittedName[0], splittedName[1]);
    }

    public List<String> retrieveCustomerNames() throws DBException {
        return customerDAO.retrieveCustomerNames();
    }
}
