package repositories;

import dao.CategoryDAO;
import exceptions.DBException;
import java.util.List;
import model.ECategoryType;

public class CategoryRepository {
    private final CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public CategoryRepository() {
        this.categoryDAO = new CategoryDAO();
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
