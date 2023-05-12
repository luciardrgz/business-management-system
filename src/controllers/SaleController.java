package controllers;

import dao.SaleDAO;
import exceptions.DBException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import utils.ComboBoxUtils;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import listeners.IPrintCloseListener;
import listeners.IStockListener;
import model.EPaymentMethod;
import model.Sale;
import repositories.CustomerRepository;
import repositories.ProductRepository;
import repositories.SaleRepository;
import utils.TableUtils;
import views.AdminPanel;
import views.Print;

public class SaleController implements ActionListener, MouseListener, KeyListener, IPrintCloseListener {

    private Sale finalSale;
    private SaleDAO saleDAO;
    public AdminPanel adminView;
    private ProductRepository productRepository = new ProductRepository();
    private CustomerRepository customerRepository = new CustomerRepository();
    private SaleRepository saleRepository = new SaleRepository();
    private DefaultTableModel newSaleTable = new DefaultTableModel();
    private List<Sale> tempSales = new ArrayList<>();
    private IStockListener stockUpdateListener;

    public SaleController() {
    }

    public SaleController(Sale sale, SaleDAO saleDAO, ProductController productController, AdminPanel adminView) {
        this.finalSale = sale;
        this.saleDAO = saleDAO;
        this.stockUpdateListener = productController;
        this.adminView = adminView;
        this.adminView.btnAddProductToNewSale.addActionListener(this);
        this.adminView.btnDeleteProductFromNewSale.addActionListener(this);
        this.adminView.btnSaveNewSaleInfo.addActionListener(this);
        this.adminView.btnEditNewSaleInfo.addActionListener(this);
        this.adminView.btnGenerateNewSale.addActionListener(this);
        this.adminView.newSaleTable.addMouseListener(this);

        newSaleTable = (DefaultTableModel) adminView.newSaleTable.getModel();
        TableUtils.setUpTableStyle(adminView.newSaleTable, newSaleTable);

        dataFieldsEnabled(true);
        productFieldsEnabled(false);

        loadProductsComboBox();
        loadCustomersComboBox();
        ComboBoxUtils.loadPaymentMethodsComboBox(adminView.cbxNewSalePaymentMethod);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnSaveNewSaleInfo) {
            setupFinalSale();
        } else if (e.getSource() == adminView.btnEditNewSaleInfo) {
            dataFieldsEnabled(true);
            productFieldsEnabled(false);
        } else if (e.getSource() == adminView.btnAddProductToNewSale) {
            addTempSaleToTable();
        } else if (e.getSource() == adminView.btnDeleteProductFromNewSale) {
            deleteTempSaleFromTable();
        } else if (e.getSource() == adminView.btnGenerateNewSale) {
            generateSale();
        }
    }

    private Sale setupTempSale() {
        Sale tempSale = new Sale();

        try {
            if (checkAvailableStock() && checkDuplicateProduct(getSaleProductId()) && getSaleProductId() != -1) {

                tempSale.setId(finalSale.getId());

                int productId = getSaleProductId();
                if (productId != -1) {
                    tempSale.setProduct(productId);
                }

                tempSale.setQuantity(Integer.parseInt(adminView.inputNewSaleQty.getText()));

                tempSale.setTotal(tempSale.getQuantity() * productRepository.getProductPrice(tempSale.getProduct()));

                tempSale.setCustomer(finalSale.getCustomer());

                tempSale.setPaymentMethod(finalSale.getPaymentMethod());

                tempSales.add(tempSale);
            } else {
                JOptionPane.showMessageDialog(null, "Producto sin stock o repetido.");
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return tempSale;
    }

    public boolean checkDuplicateProduct(int id) {
        boolean check = true;
        for (Sale temp : tempSales) {
            if (temp.getProduct() == id) {
                check = false;
            }
        }
        return check;
    }

    public boolean checkAvailableStock() {
        boolean check = true;
        try {
            if (productRepository.getProductStock(getSaleProductId()) < Integer.parseInt(adminView.inputNewSaleQty.getText())) {
                check = false;
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return check;
    }

    private void addTempSaleToTable() {
        adminView.newSaleTable.setDefaultRenderer(adminView.newSaleTable.getColumnClass(0),  new TableUtils());

        if (adminView.cbxNewSaleProduct.getSelectedItem() == null || adminView.inputNewSaleQty.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Producto y cantidad a vender son obligatorios.");
        } else {
            Sale tempSale = setupTempSale();
            tempSaleToObject(tempSale);
            resetTempView();
            calculateFinalTotal();
        }
    }

    private void deleteTempSaleFromTable() {
        if (adminView.cbxNewSaleProduct.getSelectedItem() == null || adminView.inputNewSaleQty.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Producto y cantidad a vender son obligatorios.");
        } else {
            int selectedRow = adminView.newSaleTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un producto a eliminar de la venta.");
            } else {
                calculateNewTotal(selectedRow);
                tempSales.remove(selectedRow);
                listTempSale();
            }
        }
    }

    private void listTempSale() {
        adminView.newSaleTable.setDefaultRenderer(adminView.newSaleTable.getColumnClass(0),  new TableUtils());

        newSaleTable = (DefaultTableModel) adminView.newSaleTable.getModel();
        newSaleTable.setRowCount(0);
        TableUtils.centerTableContent(adminView.newSaleTable);
        
        for (Sale tempSale : tempSales) {
            if (finalSale.getId() == tempSale.getId()) {
                Object[] currentTempSale = tempSaleToObject(tempSale);
                newSaleTable.addRow(currentTempSale);
            }
        }

    }

    private Object[] tempSaleToObject(Sale sale) {
        Object[] tempSaleCol = new Object[3];
        try {
            tempSaleCol[0] = productRepository.getProductNameById(sale.getProduct());
            tempSaleCol[1] = sale.getQuantity();
            tempSaleCol[2] = sale.getQuantity() * productRepository.getProductPrice(sale.getProduct());
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return tempSaleCol;
    }

    private void setupFinalSale() {
        setFinalSaleId();

        setFinalSaleCustomerId();

        EPaymentMethod paymentMethod = EPaymentMethod.nameForUserToConstant(adminView.cbxNewSalePaymentMethod.getSelectedItem().toString());
        finalSale.setPaymentMethod(paymentMethod);

        dataFieldsEnabled(false);
        productFieldsEnabled(true);
    }
    
    private void setFinalSaleId() {
        try {
            if (saleRepository.getLastId() != -1) {
                finalSale.setId(saleRepository.getLastId() + 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    private void setFinalSaleCustomerId() {
        int customerId;

        try {
            customerId = customerRepository.getCustomerIdByName(adminView.cbxNewSaleCustomer.getSelectedItem().toString());
            if (customerId != -1) {
                finalSale.setCustomer(customerId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void generateSale() {
        try {
            if (!adminView.cbxNewSaleCustomer.isEnabled() && !adminView.cbxNewSalePaymentMethod.isEnabled()) {
                ProductController productController = new ProductController();
                calculateFinalTotal();

                for (Sale tempSale : tempSales) {
                    saleRepository.generate(tempSale);
                    productController.updateProductStock(tempSale);
                    stockUpdateListener.onSale();
                }
                resetView();
                Print.showPrintScreen(finalSale, this, adminView);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private int getSaleProductId() {
        int productId = -1;
        try {
            productId = productRepository.getProductIdByName(adminView.cbxNewSaleProduct.getSelectedItem().toString());

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        return productId;
    }

    private void dataFieldsEnabled(boolean value) {
        setDataFieldsEditable(value);
        adminView.cbxNewSaleCustomer.setEnabled(value);
        adminView.cbxNewSalePaymentMethod.setEnabled(value);
    }

    private void productFieldsEnabled(boolean value) {
        setDataFieldsEditable(value);
        adminView.cbxNewSaleProduct.setEnabled(value);
        adminView.inputNewSaleQty.setEnabled(value);
    }

    private void setDataFieldsEditable(boolean value) {
        if (value == true) {
            adminView.btnEditNewSaleInfo.setVisible(true);
            adminView.btnSaveNewSaleInfo.setVisible(false);
        } else {
            adminView.btnEditNewSaleInfo.setVisible(false);
            adminView.btnSaveNewSaleInfo.setVisible(true);
        }
    }

    private void clearSalesInput() {
        adminView.cbxNewSaleProduct.setSelectedIndex(-1);
        adminView.inputNewSaleQty.setText("");
    }

    private void resetTempView() {
        clearSalesInput();
        listTempSale();
    }

    private void resetView() {
        TableUtils.clearTable(newSaleTable);
        clearSalesInput();
        dataFieldsEnabled(true);
    }

    @Override
    public void onPrintWindowClosed() {
        tempSales.clear();
        dataFieldsEnabled(true);
        productFieldsEnabled(false);
    }

    private void calculateFinalTotal() {
        int subtotalColumn = 2;
        double total = 0;

        for (int i = 0; i < newSaleTable.getRowCount(); i++) {
            total += (Double.parseDouble(newSaleTable.getValueAt(i, subtotalColumn).toString()));
        }
        adminView.inputNewSaleTotal.setText(String.valueOf(total));
    }

    private void calculateNewTotal(int row) {
        int subtotalCol = 2;
        double currentTotal = Double.parseDouble(adminView.inputNewSaleTotal.getText());
        double rowSubtotal = Double.parseDouble(newSaleTable.getValueAt(row, subtotalCol).toString());

        adminView.inputNewSaleTotal.setText(String.valueOf(currentTotal - rowSubtotal));
    }

    private void loadProductsComboBox() {
        List<String> products;
        try {
            products = productRepository.getProductNames();
            adminView.cbxNewSaleProduct.removeAllItems();

            for (String product : products) {
                adminView.cbxNewSaleProduct.addItem(product);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void loadCustomersComboBox() {
        List<String> customers;

        try {
            customers = customerRepository.getCustomerNames();
            adminView.cbxNewSaleCustomer.removeAllItems();
            for (String customer : customers) {
                adminView.cbxNewSaleCustomer.addItem(customer);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    private void setProductIndex(int row) {
        int index;
        try {
            index = productRepository.getProductIdByName(adminView.newSaleTable.getValueAt(row, 0).toString());
            if ((index - 1) < adminView.cbxNewSaleProduct.getItemCount()) {
                adminView.cbxNewSaleProduct.setSelectedIndex(index - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.newSaleTable) {
            int row = adminView.newSaleTable.rowAtPoint(e.getPoint());

            setProductIndex(row);
            adminView.inputNewSaleQty.setText(adminView.newSaleTable.getValueAt(row, 1).toString());
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
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}
