package controllers;

import java.awt.Color;
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
import model.CategoryDAO;
import model.Combo;
import model.Table;
import views.AdminPanel;

public class CategoryController implements ActionListener, MouseListener, KeyListener {

    private Category category;
    private CategoryDAO categoryDAO;
    private AdminPanel adminView;

    DefaultTableModel categoriesTable = new DefaultTableModel();

    public CategoryController() {
    }

    public CategoryController(Category category, CategoryDAO categoryDAO, AdminPanel adminView) {
        this.category = category;
        this.categoryDAO = categoryDAO;
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
            System.out.println("NO HAY NADAAAAAAAAAAAA");
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            category.setName(adminView.inputCategoryName.getText());

            if (categoryDAO.register(category)) {
                updateView();
                JOptionPane.showMessageDialog(null, "¡Categoría registrada con éxito!");
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar la categoría.");
            }
        }
    }

    public void updateCategory() {
        if (adminView.inputCategoryName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No ingresaste ninguna categoría.");
        } else {
            category.setName(adminView.inputCategoryName.getText());
            category.setId(Integer.parseInt((adminView.inputCategoryId.getText())));

            if (categoryDAO.update(category)) {
                clearCategoriesTable();
                listCategories();
                clearCategoriesInput();
                JOptionPane.showMessageDialog(null, "¡Categoría modificada con éxito!");
            } else {
                JOptionPane.showMessageDialog(null, "Error al modificar la categoría.");
            }

        }
    }

    public void deleteCategory() {
        if (!adminView.inputCategoryId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCategoryId.getText());
            if (categoryDAO.changeStatus("Inactivo", id)) {
                clearCategoriesTable();
                listCategories();
                JOptionPane.showMessageDialog(null, "Categoría dada de baja exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al intentar dar de baja la categoría.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una categoría para darla de baja.");
        }
    }

    public void recoverCategory() {
        if (!adminView.inputCategoryId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCategoryId.getText());
            if (categoryDAO.changeStatus("Activo", id)) {
                updateView();
                JOptionPane.showMessageDialog(null, "Categoría dada de alta exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al intentar dar de alta la categoría.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una categoría para darlo de alta.");
        }
    }

    public void listCategories() {
        Table color = new Table();
        adminView.categoriesTable.setDefaultRenderer(adminView.categoriesTable.getColumnClass(0), color);

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
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setForeground(Color.white);
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

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputCategorySearch) {
            clearCategoriesTable();
            listCategories();
        }
    }

    public int findCategoryIdByName(String categoryName) {
        CategoryDAO finder = new CategoryDAO();
        int id = finder.retrieveCategoryIdByName(categoryName);

        return id;
    }
    
    public String findCategoryNameById(int categoryId) {
        CategoryDAO finder = new CategoryDAO();
        String name = finder.retrieveCategoryNameById(categoryId);

        return name;
    }
}
