package controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Category;
import model.CategoryDAO;
import model.Combo;
import model.Product;
import model.ProductDAO;
import model.Table;
import views.AdminPanel;

public class ProductController implements ActionListener, MouseListener, KeyListener {

    private Product product;
    private ProductDAO productDAO;
    private AdminPanel adminView;
    private CategoryController categoryController = new CategoryController();

    DefaultTableModel productsTable = new DefaultTableModel();

    public ProductController(Product product, ProductDAO productDAO, AdminPanel adminView) {
        this.product = product;
        this.productDAO = productDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterProduct.addActionListener(this);
        this.adminView.btnUpdateProduct.addActionListener(this);
        this.adminView.btnNewProduct.addActionListener(this);
        this.adminView.jMenuItemDeleteProduct.addActionListener(this);
        this.adminView.jMenuItemReenterProduct.addActionListener(this);
        this.adminView.inputProductSearch.addKeyListener(this);
        this.adminView.productsTable.addMouseListener(this);
        loadCategories();
        listProducts();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterProduct) {
            registerProduct();
        } else if (e.getSource() == adminView.btnUpdateProduct) {
            updateProduct();
        } else if (e.getSource() == adminView.jMenuItemDeleteProduct) {
            deleteProduct();
        } else if (e.getSource() == adminView.jMenuItemReenterProduct) {
            recoverProduct();
        } else {
            clearProductsInput();
        }
    }

    public void setupProduct() {
        product.setName(adminView.inputProductName.getText());
        product.setDescription(adminView.inputProductDescription.getText());
        product.setStock(Integer.parseInt(adminView.inputProductStock.getText()));
        product.setProductionCost(Integer.parseInt(adminView.inputProductStock.getText()));
        product.setProductionCost(Double.parseDouble(adminView.inputProductionCost.getText()));
        product.setSellingPrice(Double.parseDouble(adminView.inputProductSellPrice.getText()));

        int productCategoryId = categoryController.findCategoryIdByName(adminView.cbxProductCategories.getSelectedItem().toString());

        if (productCategoryId != -1) {
            product.setCategoryId(productCategoryId);
        }
    }

    public void resetView() {
        clearProductsTable();
        listProducts();
        clearProductsInput();
    }

    public void registerProduct() {
        if (adminView.inputProductName.getText().equals("") || adminView.cbxProductCategories.getSelectedItem().toString().equals("")) {
            JOptionPane.showMessageDialog(null, "Nombre y categoría son campos obligatorios.");
        } else {
            setupProduct();

            if (productDAO.add(product)) {
                resetView();
                JOptionPane.showMessageDialog(null, "¡Producto registrado con éxito!");
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el producto.");
            }

        }
    }

    public void updateProduct() {
        if (adminView.inputProductName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            product.setName(adminView.inputProductName.getText());
            product.setDescription(adminView.inputProductDescription.getText());
            product.setStock(Integer.parseInt(adminView.inputProductStock.getText()));
            product.setProductionCost(Double.parseDouble(adminView.inputProductionCost.getText()));
            product.setSellingPrice(Double.parseDouble(adminView.inputProductSellPrice.getText()));

            int productCategoryId = categoryController.findCategoryIdByName(adminView.cbxProductCategories.getSelectedItem().toString());

            if (productCategoryId != -1) {
                product.setCategoryId(productCategoryId);
            }

            product.setId(Integer.parseInt((adminView.inputProductId.getText())));

            if (productDAO.update(product)) {
                resetView();
                JOptionPane.showMessageDialog(null, "¡Producto modificado con éxito!");
            } else {
                JOptionPane.showMessageDialog(null, "Error al modificar el producto.");
            }

        }
    }

    public void deleteProduct() {
        if (!adminView.inputProductId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputProductId.getText());
            if (productDAO.changeStatus("Discontinuado", id)) {
                resetView();
                JOptionPane.showMessageDialog(null, "Producto dado de baja exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al intentar dar de baja el producto.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para darlo de baja.");
        }
    }

    public void recoverProduct() {
        if (!adminView.inputProductId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputProductId.getText());
            if (productDAO.changeStatus("Disponible", id)) {
                resetView();
                JOptionPane.showMessageDialog(null, "Producto dado de alta exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al intentar dar de alta el producto.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para darlo de alta.");
        }
    }

    public void listProducts() {
        Table color = new Table();
        adminView.productsTable.setDefaultRenderer(adminView.productsTable.getColumnClass(0), color);

        List<Product> productsList = productDAO.getProductsList(adminView.inputProductSearch.getText());
        productsTable = (DefaultTableModel) adminView.productsTable.getModel();

        productsTable.setRowCount(0);

        Object[] currentProduct = new Object[8];
        for (int i = 0; i < productsList.size(); i++) {
            currentProduct[0] = productsList.get(i).getId();
            currentProduct[1] = productsList.get(i).getName();
            currentProduct[2] = productsList.get(i).getDescription();
            currentProduct[3] = productsList.get(i).getStock();
            currentProduct[4] = productsList.get(i).getProductionCost();
            currentProduct[5] = productsList.get(i).getSellingPrice();
            currentProduct[6] = categoryController.findCategoryNameById(productsList.get(i).getCategoryId());
            currentProduct[7] = productsList.get(i).getStatus();

            productsTable.addRow(currentProduct);
        }

        adminView.productsTable.setModel(productsTable);
        JTableHeader header = adminView.productsTable.getTableHeader();
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setForeground(Color.white);
    }

    private void clearProductsInput() {
        adminView.inputProductId.setText("");
        adminView.inputProductName.setText("");
        adminView.inputProductDescription.setText("");
        adminView.inputProductStock.setText("");
        adminView.inputProductionCost.setText("");
        adminView.inputProductSellPrice.setText("");
        adminView.cbxProductCategories.setSelectedIndex(-1);
    }

    public void clearProductsTable() {
        for (int i = 0; i < productsTable.getRowCount(); i++) {
            productsTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.productsTable) {
            int row = adminView.productsTable.rowAtPoint(e.getPoint());

            adminView.inputProductId.setText(adminView.productsTable.getValueAt(row, 0).toString());
            adminView.inputProductName.setText(adminView.productsTable.getValueAt(row, 1).toString());
            adminView.inputProductDescription.setText(adminView.productsTable.getValueAt(row, 2).toString());
            adminView.inputProductStock.setText(adminView.productsTable.getValueAt(row, 3).toString());
            adminView.inputProductionCost.setText(adminView.productsTable.getValueAt(row, 4).toString());
            adminView.inputProductSellPrice.setText(adminView.productsTable.getValueAt(row, 5).toString());
            
            int index = categoryController.findCategoryIdByName(adminView.productsTable.getValueAt(row, 6).toString());
            adminView.cbxProductCategories.setSelectedIndex(index - 1);
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
        if (e.getSource() == adminView.inputProductSearch) {
            clearProductsTable();
            listProducts();
        }
    }

    public void loadCategories() {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getCategoriesList("");

        for (int i = 0; i < categories.size(); i++) {
            int id = categories.get(i).getId();
            String name = categories.get(i).getName();
            adminView.cbxProductCategories.addItem(new Combo(id, name));
        }
    }

}
