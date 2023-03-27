package repositories;

import dao.CustomerDAO;
import exceptions.DBException;
import java.util.List;

public class CustomerRepository {
    private final CustomerDAO customerDAO;

    public CustomerRepository(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public CustomerRepository() {
        this.customerDAO = new CustomerDAO();
    }

    public String retrieveCustomerNameById(int customerId) throws DBException {
        return customerDAO.retrieveCustomerNameById(customerId);
    }

    public int retrieveCustomerIdByName(String customerName) throws DBException {
        return customerDAO.retrieveCustomerIdByName(customerName);
    }

    public List<String> retrieveCustomerNames() throws DBException {
        return customerDAO.retrieveCustomerNames();
    }
}
