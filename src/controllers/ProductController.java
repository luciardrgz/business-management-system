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
import dao.CategoryDAO;
import model.Product;
import dao.ProductDAO;
import model.EProductStatus;
import views.Table;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import views.AdminPanel;

public class ProductController implements ActionListener, MouseListener, KeyListener {

    private Product product;
    private ProductDAO productDAO;
    public AdminPanel adminView;
    private final Table color = new Table();
    private CategoryDAO categoryDAO = new CategoryDAO(this);
    private DefaultTableModel productsTable = new DefaultTableModel();

    public ProductController() {
    }

    public ProductController(Product product, ProductDAO productDAO, AdminPanel adminView) {
        this.product = product;
        this.productDAO = productDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterProduct.addActionListener(this);
        this.adminView.btnUpdateProduct.addActionListener(this);
        this.adminView.btnNewProduct.addActionListener(this);
        this.adminView.btnAddStock.addActionListener(this);
        this.adminView.btnRemoveStock.addActionListener(this);
        this.adminView.jMenuItemDeleteProduct.addActionListener(this);
        this.adminView.jMenuItemReenterProduct.addActionListener(this);
        this.adminView.inputProductSearch.addKeyListener(this);
        this.adminView.productsTable.addMouseListener(this);

        AutoCompleteDecorator.decorate(adminView.cbxProductCategories);

        loadCategoriesComboBox();
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
        } else if (e.getSource() == adminView.btnAddStock) {
            addStockInInput();
        } else if (e.getSource() == adminView.btnRemoveStock) {
            removeStockInInput();
        } else {
            clearProductsInput();
        }
    }

    private void setupProduct() {
        product.setName(adminView.inputProductName.getText());
        product.setDescription(adminView.inputProductDescription.getText());
        product.setStock(Integer.parseInt(adminView.inputProductStock.getText()));
        product.setProductionCost(Integer.parseInt(adminView.inputProductStock.getText()));
        product.setProductionCost(Double.parseDouble(adminView.inputProductionCost.getText()));
        product.setSellingPrice(Double.parseDouble(adminView.inputProductSellPrice.getText()));
        setProductCategoryId();
    }

    private void setProductCategoryId() {
        int productCategoryId;
        try {
            productCategoryId = categoryDAO.retrieveCategoryIdByName(adminView.cbxProductCategories.getSelectedItem().toString());
            if (productCategoryId != -1) {
                product.setCategoryId(productCategoryId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void resetView() {
        clearProductsTable();
        listProducts();
        clearProductsInput();
    }

    private void registerProduct() {
        if (adminView.inputProductName.getText().equals("") || adminView.cbxProductCategories.getSelectedItem().toString().equals("")) {
            JOptionPane.showMessageDialog(null, "Nombre y categoría son campos obligatorios.");
        } else {
            setupProduct();
            try {
                productDAO.add(product);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Producto registrado con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void updateProduct() {
        if (adminView.inputProductName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupProduct();
            product.setId(Integer.parseInt((adminView.inputProductId.getText())));

            try {
                productDAO.update(product);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Producto modificado con éxito!");

            } catch (DBException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void deleteProduct() {
        if (!adminView.inputProductId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputProductId.getText());
            try {
                productDAO.changeStatus("Discontinuado", id);
                resetView();
                JOptionPane.showMessageDialog(null, "Producto dado de baja exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para darlo de baja.");
        }
    }

    private void recoverProduct() {
        if (!adminView.inputProductId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputProductId.getText());
            try {
                productDAO.changeStatus("Disponible", id);
                resetView();
                JOptionPane.showMessageDialog(null, "Producto dado de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para darlo de alta.");
        }
    }

    public void listProducts() {
        adminView.productsTable.setDefaultRenderer(adminView.productsTable.getColumnClass(0), color);

        try {
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
                currentProduct[6] = categoryDAO.retrieveCategoryNameById(productsList.get(i).getCategoryId());
                
                Enum currentStatusEnum = productsList.get(i).getStatus();
                EProductStatus currentStatus = EProductStatus.valueOf(currentStatusEnum.name());
                currentProduct[7] = currentStatus.getNameForUser();


                productsTable.addRow(currentProduct);
            }

            adminView.productsTable.setModel(productsTable);
            JTableHeader header = adminView.productsTable.getTableHeader();
            color.changeHeaderColors(header);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
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

            int index;
            try {
                index = categoryDAO.retrieveCategoryIdByName(adminView.productsTable.getValueAt(row, 6).toString());
                if ((index - 1) < adminView.cbxProductCategories.getItemCount()) {
                    adminView.cbxProductCategories.setSelectedIndex(index - 1);
                }
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputProductSearch) {
            clearProductsTable();
            listProducts();
        }
    }

    public void loadCategoriesComboBox() {
        List<String> categories;
        try {
            categories = categoryDAO.getCategoryNames();
            adminView.cbxProductCategories.removeAllItems();
            for (String category : categories) {
                adminView.cbxProductCategories.addItem(category);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void addStockInInput() {
        String currentStock = adminView.inputProductStock.getText();
        
        if (!currentStock.isEmpty()) {
            adminView.inputProductStock.setText(String.valueOf(Integer.parseInt(currentStock) + 1));
        }
    }

    private void removeStockInInput() {
    String currentStock = adminView.inputProductStock.getText();
    
    if(!currentStock.isEmpty()) {
        int newStock = Integer.parseInt(currentStock) - 1;
        adminView.inputProductStock.setText(String.valueOf(newStock));
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
