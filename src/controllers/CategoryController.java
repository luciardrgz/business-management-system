package controllers;

import exceptions.DBException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Category;
import dao.CategoryDAO;
import model.ECategoryType;
import repositories.CategoryRepository;
import views.Table;
import views.AdminPanel;
import listeners.ICategoryUpdateListener;
import utils.TableUtils;

public class CategoryController implements ActionListener, MouseListener, KeyListener {

    private final Category category;
    private final CategoryDAO categoryDAO;
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final AdminPanel adminView;
    private final Table color = new Table();
    private DefaultTableModel categoriesTable = new DefaultTableModel();
    private final ICategoryUpdateListener categoryUpdateListener;

    public CategoryController(Category category, CategoryDAO categoryDAO, ProductController productController, AdminPanel adminView) {
        this.category = category;
        this.categoryDAO = categoryDAO;
        this.categoryUpdateListener = productController;
        this.adminView = adminView;
        this.adminView.btnRegisterCategory.addActionListener(this);
        this.adminView.btnUpdateCategory.addActionListener(this);
        this.adminView.jMenuItemDeleteCategory.addActionListener(this);
        this.adminView.jMenuItemReenterCategory.addActionListener(this);
        this.adminView.btnNewCategory.addActionListener(this);
        this.adminView.inputCategorySearch.addKeyListener(this);
        this.adminView.categoriesTable.addMouseListener(this);

        listCategories();
        loadTypesComboBox();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterCategory) {
            registerCategory();
        } else if (e.getSource() == adminView.btnUpdateCategory) {
            updateCategory();
        } else if (e.getSource() == adminView.jMenuItemDeleteCategory) {
        } else if (e.getSource() == adminView.jMenuItemReenterCategory) {
        } else {
            clearCategoriesInput();
        }
    }

    private void resetView() {
        TableUtils.clearTable(categoriesTable);
        listCategories();
        clearCategoriesInput();
    }

    private void setupCategory() {
        ECategoryType type = ECategoryType.nameForUserToConstant(adminView.cbxCategoryTypes.getSelectedItem().toString());
        category.setCategoryType(type);

        category.setName(adminView.inputCategoryName.getText());
    }

    private void registerCategory() {
        if (adminView.inputCategoryName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            setupCategory();
            try {
                categoryRepository.register(category);
                resetView();
                categoryUpdateListener.onCategoryUpdate();
                JOptionPane.showMessageDialog(null, "¡Categoría registrada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void updateCategory() {
        if (adminView.inputCategoryName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            category.setId(Integer.parseInt((adminView.inputCategoryId.getText())));
            setupCategory();

            try {
                categoryRepository.update(category);
                resetView();
                categoryUpdateListener.onCategoryUpdate();
                JOptionPane.showMessageDialog(null, "¡Categoría modificada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void listCategories() {
        adminView.categoriesTable.setDefaultRenderer(adminView.categoriesTable.getColumnClass(0), color);

        
        categoriesTable.setRowCount(0);

        try {
            List<Category> categoriesList = categoryRepository.getAllCategories(adminView.inputCategorySearch.getText());
            categoriesTable = (DefaultTableModel) adminView.categoriesTable.getModel();
            
            categoryListToObjectArray(categoriesList);
            
            adminView.categoriesTable.setModel(categoriesTable);
            JTableHeader header = adminView.categoriesTable.getTableHeader();
            TableUtils.changeHeaderColors(header);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void categoryListToObjectArray(List<Category> categoriesList) {
        Object[] currentCategory = new Object[3];

        for (int i = 0; i < categoriesList.size(); i++) {
            currentCategory[0] = categoriesList.get(i).getId();

            currentCategory[1] = categoriesList.get(i).getName();

            Enum currentTypeEnum = categoriesList.get(i).getCategoryType();
            ECategoryType currentType = ECategoryType.valueOf(currentTypeEnum.name());
            currentCategory[2] = currentType.getNameForUser();

            categoriesTable.addRow(currentCategory);
        }
    }

    private void loadTypesComboBox() {
        ECategoryType[] types;

        types = ECategoryType.class.getEnumConstants();
        adminView.cbxCategoryTypes.removeAllItems();
        for (ECategoryType type : types) {
            adminView.cbxCategoryTypes.addItem(type.getNameForUser());
        }
    }

    private void clearCategoriesInput() {
        adminView.inputCategoryId.setText("");
        adminView.cbxCategoryTypes.setSelectedIndex(-1);
        adminView.inputCategoryName.setText("");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.categoriesTable) {
            int row = adminView.categoriesTable.rowAtPoint(e.getPoint());

            adminView.inputCategoryId.setText(adminView.categoriesTable.getValueAt(row, 0).toString());
            adminView.inputCategoryName.setText(adminView.categoriesTable.getValueAt(row, 1).toString());
            setTypeIndex(adminView.categoriesTable.getValueAt(row, 2).toString());

        }
    }

    private void setTypeIndex(String categoryType) {
        ECategoryType[] types = ECategoryType.values();
        int categoryTypesIndex = -1;

        for (int i = 0; i < types.length; i++) {
            if (types[i].getNameForUser().equals(categoryType)) {
                categoryTypesIndex = i;
                break;
            }
        }
        if (categoryTypesIndex != -1) {
            adminView.cbxCategoryTypes.setSelectedIndex(categoryTypesIndex);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputCategorySearch) {
            resetView();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
