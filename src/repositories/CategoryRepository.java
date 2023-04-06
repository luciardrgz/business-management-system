package repositories;

import dao.CategoryDAO;
import exceptions.DBException;
import java.sql.SQLException;
import java.util.List;
import model.Category;
import model.ECategoryType;

public class CategoryRepository {

    private final CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public CategoryRepository() {
        this.categoryDAO = new CategoryDAO();
    }

    public void register(Category category) throws DBException {
        try {
            categoryDAO.register(category);
        } catch (SQLException ex) {
            throw new DBException();
        }
    }

    public void update(Category category) throws DBException {
        try {
            categoryDAO.update(category);
        } catch (SQLException ex) {
            throw new DBException();
        }
    }

    public List<Category> retrieveAllCategories(String search) throws DBException {
        return categoryDAO.getCategoriesList(search);
    }

    public String retrieveCategoryNameById(int categoryId) throws DBException {
        return categoryDAO.retrieveCategoryNameById(categoryId);
    }

    public int retrieveCategoryIdByName(String categoryName) throws DBException {
        return categoryDAO.retrieveCategoryIdByName(categoryName);
    }

    public List<String> retrieveCategoryNames(ECategoryType categoryType) throws DBException {
        return categoryDAO.retrieveCategoryNames(categoryType);
    }

    public List<String> retrieveCategoryTypes() throws DBException {
        return categoryDAO.retrieveCategoryTypes();
    }
}
