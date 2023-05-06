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
import model.Product;
import dao.ProductDAO;
import model.ECategoryType;
import model.EProductStatus;
import views.Table;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import repositories.CategoryRepository;
import views.AdminPanel;
import listeners.ICategoryUpdateListener;
import listeners.IStockListener;
import repositories.ProductRepository;
import utils.TableUtils;

public class ProductController implements ActionListener, MouseListener, KeyListener, ICategoryUpdateListener, IStockListener {

    private Product product;
    private ProductDAO productDAO;
    public AdminPanel adminView;
    private ProductRepository productRepository = new ProductRepository();
    private CategoryRepository categoryRepository = new CategoryRepository();
    private final Table color = new Table();
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
        product.setProductionCost(Double.parseDouble(adminView.inputProductionCost.getText()));
        product.setSellingPrice(Double.parseDouble(adminView.inputProductSellPrice.getText()));
        setProductCategoryId();
    }

    private void setProductCategoryId() {
        int productCategoryId;
        try {
            productCategoryId = categoryRepository.getCategoryIdByName(adminView.cbxProductCategories.getSelectedItem().toString());
            if (productCategoryId != -1) {
                product.setCategoryId(productCategoryId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void setProductCategoryIndex(int row) {
        int index;
        try {
            index = categoryRepository.getCategoryIdByName(adminView.productsTable.getValueAt(row, 6).toString());
            if ((index - 1) < adminView.cbxProductCategories.getItemCount()) {
                adminView.cbxProductCategories.setSelectedIndex(index - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void registerProduct() {
        if (adminView.inputProductName.getText().equals("") || adminView.cbxProductCategories.getSelectedItem().toString().equals("")) {
            JOptionPane.showMessageDialog(null, "Nombre y categoría son campos obligatorios.");
        } else {
            setupProduct();
            try {
                productRepository.register(product);
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
                productRepository.update(product);
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
                productRepository.changeStatus(EProductStatus.DISCONTINUED, id);
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
                productRepository.changeStatus(EProductStatus.AVAILABLE, id);
                resetView();
                JOptionPane.showMessageDialog(null, "Producto dado de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un producto para darlo de alta.");
        }
    }

    private void listProducts() {
        adminView.productsTable.setDefaultRenderer(adminView.productsTable.getColumnClass(0), color);

        try {
            List<Product> productsList = productRepository.getProductsList(adminView.inputProductSearch.getText());
            productsTable = (DefaultTableModel) adminView.productsTable.getModel();

            productsTable.setRowCount(0);
            productListToObjectArray(productsList);

            adminView.productsTable.setModel(productsTable);
            JTableHeader header = adminView.productsTable.getTableHeader();
            TableUtils.changeHeaderColors(header);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void productListToObjectArray(List<Product> productsList) {
        Object[] currentProduct = new Object[8];

        for (int i = 0; i < productsList.size(); i++) {
            currentProduct[0] = productsList.get(i).getId();
            currentProduct[1] = productsList.get(i).getName();
            currentProduct[2] = productsList.get(i).getDescription();
            currentProduct[3] = productsList.get(i).getStock();
            currentProduct[4] = productsList.get(i).getProductionCost();
            currentProduct[5] = productsList.get(i).getSellingPrice();
            try {
                currentProduct[6] = categoryRepository.getCategoryNameById(productsList.get(i).getCategoryId());
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

            Enum currentStatusEnum = productsList.get(i).getStatus();
            EProductStatus currentStatus = EProductStatus.valueOf(currentStatusEnum.name());
            currentProduct[7] = currentStatus.getNameForUser();

            productsTable.addRow(currentProduct);
        }
    }

    private void resetView() {
        listProducts();
        clearProductsInput();
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
            setProductCategoryIndex(row);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputProductSearch) {
            resetView();
        }
    }

    private void loadCategoriesComboBox() {
        List<String> categories;

        try {
            categories = categoryRepository.getCategoryNames(ECategoryType.PRODUCT);
            adminView.cbxProductCategories.removeAllItems();

            for (String category : categories) {
                adminView.cbxProductCategories.addItem(category);
            }

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void onCategoryUpdate() {
        loadCategoriesComboBox();
        resetView();
    }
    
    @Override
    public void onSale() {
        loadCategoriesComboBox();
        resetView();
    }

    private void addStockInInput() {
        String currentStock = adminView.inputProductStock.getText();

        if (!currentStock.isEmpty()) {
            adminView.inputProductStock.setText(String.valueOf(Integer.parseInt(currentStock) + 1));
        }
    }

    private void removeStockInInput() {
        String currentStock = adminView.inputProductStock.getText();

        if (!currentStock.isEmpty()) {
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