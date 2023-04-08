package repositories;

import dao.UserDAO;
import exceptions.DBException;
import java.util.List;
import model.EPersonStatus;
import model.User;

public class UserRepository {

    private final UserDAO userDAO;

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserRepository() {
        this.userDAO = new UserDAO();
    }

    public void register(User user) throws DBException {
        try {
            userDAO.add(user);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public void update(User user) throws DBException {
        try {
            userDAO.update(user);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public void changeStatus(EPersonStatus status, int id) throws DBException {
        try {
            userDAO.changeStatus(status.name(), id);
        } catch (DBException ex) {
            throw new DBException();
        }
    }
    
    public List<User> getUsersList(String value) throws DBException {
        List<User> users;
        try {
            users = userDAO.retrieveUsersList(value);
        } catch (DBException ex) {
            throw new DBException();
        }
        return users;
    }
}
