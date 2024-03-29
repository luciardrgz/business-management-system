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
import model.Product;
import dao.ProductDAO;
import javax.swing.JButton;
import model.EProductStatus;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import repositories.CategoryRepository;
import views.AdminPanel;
import listeners.ICategoryUpdateListener;
import listeners.IStockListener;
import model.Sale;
import repositories.ProductRepository;
import utils.ButtonUtils;
import utils.TableUtils;

public class ProductController implements ActionListener, MouseListener, KeyListener, ICategoryUpdateListener, IStockListener {

    private Product product;
    private ProductDAO productDAO;
    public AdminPanel adminView;
    private ProductRepository productRepository = new ProductRepository();
    private CategoryRepository categoryRepository = new CategoryRepository();
    private DefaultTableModel productsTable = new DefaultTableModel();
    private JButton UPDATE_BTN;
    private JButton REGISTER_BTN;

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
        this.UPDATE_BTN = adminView.btnUpdateProduct;
        this.REGISTER_BTN = adminView.btnRegisterProduct;

        AutoCompleteDecorator.decorate(adminView.cbxProductCategories);

        loadCategoriesComboBox();
        ButtonUtils.setUpdateButtonVisible(false, UPDATE_BTN, REGISTER_BTN);
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
            updateStockInput("add");
        } else if (e.getSource() == adminView.btnRemoveStock) {
            updateStockInput("remove");
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
            ButtonUtils.setUpdateButtonVisible(true, UPDATE_BTN, REGISTER_BTN);
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
        adminView.productsTable.setDefaultRenderer(adminView.productsTable.getColumnClass(0), new TableUtils());

        try {
            List<Product> productsList = productRepository.getProductsList(adminView.inputProductSearch.getText());
            productsTable = (DefaultTableModel) adminView.productsTable.getModel();
            productsTable.setRowCount(0);

            productListToObjectArray(productsList);

            TableUtils.setUpTableStyle(adminView.productsTable, productsTable);
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
        ButtonUtils.setUpdateButtonVisible(false, UPDATE_BTN, REGISTER_BTN);
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
            ButtonUtils.setUpdateButtonVisible(true, UPDATE_BTN, REGISTER_BTN);
            adminView.extendedTableInformation.setText(adminView.productsTable.getValueAt(row, 1).toString() + ": " + adminView.productsTable.getValueAt(row, 2).toString());
        } else {
            adminView.extendedTableInformation.setText("");
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == adminView.productsTable) {
            //adminView.extendedTableInformation.setText("");
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
            categories = categoryRepository.getCategoryNames();
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

    private void updateStockInput(String operation) {
        String currentStock = adminView.inputProductStock.getText();

        if (!currentStock.isEmpty()) {
            if (operation.equals("add")) {
                adminView.inputProductStock.setText(String.valueOf(Integer.parseInt(currentStock) + 1));
            } else if (operation.equals("remove")) {
                adminView.inputProductStock.setText(String.valueOf(Integer.parseInt(currentStock) - 1));
            }
        }
    }

    public void updateProductStock(Sale tempSale) {
        try {
            int soldStock = (int) (tempSale.getTotal() / productRepository.getProductPrice(tempSale.getProduct()));
            productRepository.updateStock(productRepository.getProductById(tempSale.getProduct()).getId(), soldStock);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
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
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
