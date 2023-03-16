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
import lombok.NoArgsConstructor;
import model.Category;
import dao.CategoryDAO;
import views.Table;
import views.AdminPanel;

@NoArgsConstructor
public class CategoryController implements ActionListener, MouseListener, KeyListener {

    private Category category;
    private CategoryDAO categoryDAO;
    private AdminPanel adminView;
    private Table color = new Table();
    private ProductController productController;
    private DefaultTableModel categoriesTable = new DefaultTableModel();

    public CategoryController(Category category, CategoryDAO categoryDAO, ProductController productController, AdminPanel adminView) {
        this.category = category;
        this.categoryDAO = categoryDAO;
        this.productController = productController;
        this.adminView = adminView;
        this.adminView.btnRegisterCategory.addActionListener(this);
        this.adminView.btnUpdateCategory.addActionListener(this);
        this.adminView.jMenuItemDeleteCategory.addActionListener(this);
        this.adminView.jMenuItemReenterCategory.addActionListener(this);
        this.adminView.btnNewCategory.addActionListener(this);
        this.adminView.inputCategorySearch.addKeyListener(this);
        this.adminView.categoriesTable.addMouseListener(this);
        listCategories();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterCategory) {
            registerCategory();
        } else if (e.getSource() == adminView.btnUpdateCategory) {
            updateCategory();
        } else if (e.getSource() == adminView.jMenuItemDeleteCategory) {
            deleteCategory();
        } else if (e.getSource() == adminView.jMenuItemReenterCategory) {
            recoverCategory();
        } else {
            clearCategoriesInput();
        }
    }

    public void updateView() {
        clearCategoriesTable();
        listCategories();
        clearCategoriesInput();
    }

    public void registerCategory() {
        if (adminView.inputCategoryName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            category.setName(adminView.inputCategoryName.getText());
            try {
                categoryDAO.register(category);
                updateView();
                productController.loadCategoriesComboBox();
                JOptionPane.showMessageDialog(null, "¡Categoría registrada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public void updateCategory() {
        if (adminView.inputCategoryName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            category.setName(adminView.inputCategoryName.getText());
            category.setId(Integer.parseInt((adminView.inputCategoryId.getText())));
            try {
                categoryDAO.update(category);
                updateView();
                productController.loadCategoriesComboBox();
                productController.clearProductsTable();
                productController.listProducts();
                JOptionPane.showMessageDialog(null, "¡Categoría modificada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public void deleteCategory() {
        if (!adminView.inputCategoryId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCategoryId.getText());
            try {
                categoryDAO.changeStatus("Inactivo", id);
                updateView();
                JOptionPane.showMessageDialog(null, "Categoría dada de baja exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una categoría para darla de baja.");
        }
    }

    public void recoverCategory() {
        if (!adminView.inputCategoryId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCategoryId.getText());
            try {
                categoryDAO.changeStatus("Activo", id);
                updateView();
                JOptionPane.showMessageDialog(null, "Categoría dada de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una categoría para darlo de alta.");
        }
    }

    public void listCategories() {
        adminView.categoriesTable.setDefaultRenderer(adminView.categoriesTable.getColumnClass(0), color);

        try {
            List<Category> categoriesList = categoryDAO.getCategoriesList(adminView.inputCategorySearch.getText());
            categoriesTable = (DefaultTableModel) adminView.categoriesTable.getModel();

            categoriesTable.setRowCount(0);

            Object[] currentCategory = new Object[2];
            for (int i = 0; i < categoriesList.size(); i++) {
                currentCategory[0] = categoriesList.get(i).getId();
                currentCategory[1] = categoriesList.get(i).getName();

                categoriesTable.addRow(currentCategory);
            }

            adminView.categoriesTable.setModel(categoriesTable);
            JTableHeader header = adminView.categoriesTable.getTableHeader();
 color.changeHeaderColors(header);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    private void clearCategoriesInput() {
        adminView.inputCategoryId.setText("");
        adminView.inputCategoryName.setText("");
    }

    public void clearCategoriesTable() {
        for (int i = 0; i < categoriesTable.getRowCount(); i++) {
            categoriesTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.categoriesTable) {
            int row = adminView.categoriesTable.rowAtPoint(e.getPoint());

            adminView.inputCategoryId.setText(adminView.categoriesTable.getValueAt(row, 0).toString());
            adminView.inputCategoryName.setText(adminView.categoriesTable.getValueAt(row, 1).toString());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputCategorySearch) {
            clearCategoriesTable();
            listCategories();
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
