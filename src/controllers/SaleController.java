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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import utils.ComboBoxUtils;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import listeners.IPrintCloseListener;
import listeners.IStockListener;
import model.EPaymentMethod;
import model.Sale;
import model.Product;
import repositories.CustomerRepository;
import repositories.ProductRepository;
import repositories.SaleRepository;
import utils.ButtonUtils;
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
    private DefaultTableModel salesTable = new DefaultTableModel();
    private List<Sale> tempSales = new ArrayList<>();
    private IStockListener stockUpdateListener;
    private JButton UPDATE_BTN;
    private JButton REGISTER_BTN;

    public SaleController() {
    }

    public SaleController(Sale sale, SaleDAO saleDAO, ProductController productController, AdminPanel adminView) {
        this.finalSale = sale;
        this.saleDAO = saleDAO;
        this.stockUpdateListener = productController;
        this.adminView = adminView;
        this.adminView.btnRegisterProductInNewSale.addActionListener(this);
        this.adminView.btnDeleteProductFromNewSale.addActionListener(this);
        this.adminView.btnSaveNewSaleInfo.addActionListener(this);
        this.adminView.btnUpdateNewSaleInfo.addActionListener(this);
        this.adminView.btnGenerateNewSale.addActionListener(this);
        this.adminView.btnPrintSale.addMouseListener(this);
        this.adminView.newSaleTable.addMouseListener(this);
        this.adminView.salesTable.addMouseListener(this);
        this.UPDATE_BTN = adminView.btnUpdateNewSaleInfo;
        this.REGISTER_BTN = adminView.btnSaveNewSaleInfo;

        newSaleTable = (DefaultTableModel) adminView.newSaleTable.getModel();
        TableUtils.setUpTableStyle(adminView.newSaleTable, newSaleTable);

        dataFieldsEnabled(true);
        productFieldsEnabled(false);
        handlePrintSaleButton(false, -1);

        loadProductsComboBox();
        loadCustomersComboBox();
        ComboBoxUtils.loadPaymentMethodsComboBox(adminView.cbxNewSalePaymentMethod);

        listSales();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnSaveNewSaleInfo) {
            setupFinalSale();
        } else if (e.getSource() == adminView.btnUpdateNewSaleInfo) {
            dataFieldsEnabled(true);
            productFieldsEnabled(false);
        } else if (e.getSource() == adminView.btnRegisterProductInNewSale) {
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
        adminView.newSaleTable.setDefaultRenderer(adminView.newSaleTable.getColumnClass(0), new TableUtils());

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
        adminView.newSaleTable.setDefaultRenderer(adminView.newSaleTable.getColumnClass(0), new TableUtils());

        newSaleTable = (DefaultTableModel) adminView.newSaleTable.getModel();
        newSaleTable.setRowCount(0);

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

    private void listSales() {
        if (adminView.salesTable.getColumnCount() > 0) {
            adminView.salesTable.setDefaultRenderer(adminView.salesTable.getColumnClass(0), new TableUtils());
        }

        try {
            List<Sale> salesList = saleRepository.getSales();
            salesTable = (DefaultTableModel) adminView.salesTable.getModel();
            salesTable.setRowCount(0);

            salesListToObjectArray(salesList);

            TableUtils.setUpTableStyle(adminView.salesTable, salesTable);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void salesListToObjectArray(List<Sale> salesList) {
        Object[] currentSale = new Object[5];
        Set<Integer> processedSaleIds = new HashSet<>();

        for (Sale sale : salesList) {
            int saleId = sale.getId();

            if (processedSaleIds.contains(saleId)) {
                continue;
            }

            try {
                String[] customerName = customerRepository.getCustomerNameById(sale.getCustomer());
                currentSale[1] = customerName[0] + " " + customerName[1];
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

            List<String> saleProducts = getFinalSaleInfo(salesList, saleId);

            updateSalesTable(salesList, currentSale, saleId, saleProducts);

            processedSaleIds.add(saleId);
        }
    }

    // Obtains and concatenates products names for this Sale
    private List<String> getFinalSaleInfo(List<Sale> salesList, int saleId) {
        List<String> saleProducts = new ArrayList<>();
        for (Sale saleEntry : salesList) {
            if (saleEntry.getId() == saleId) {
                try {
                    Product product = productRepository.getProductById(saleEntry.getProduct());
                    String productName = product.getName();
                    double productPrice = product.getSellingPrice();
                    int productQuantity = saleEntry.getQuantity();
                    String productInfo = productName + " x " + productQuantity + " ($" + productPrice + " c/u)";
                    saleProducts.add(productInfo);
                } catch (DBException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }
        return saleProducts;
    }

    // Updates table only if there are products for this Sale 
    private void updateSalesTable(List<Sale> salesList, Object[] currentSale, int saleId, List<String> saleProducts) {
        currentSale[0] = saleId;
        currentSale[2] = String.join(" + \n", saleProducts);
        currentSale[3] = getFinalSaleTotal(salesList, saleId);
        currentSale[4] = "23-5-2023";
        salesTable.addRow(currentSale);
    }

    private double getFinalSaleTotal(List<Sale> salesList, int id) {
        double total = 0;
        for (Sale sale : salesList) {
            if (sale.getId() == id) {
                total += sale.getTotal();
            }
        }
        return total;
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
        ButtonUtils.setUpdateButtonVisible(value, UPDATE_BTN, REGISTER_BTN);
        adminView.cbxNewSaleCustomer.setEnabled(value);
        adminView.cbxNewSalePaymentMethod.setEnabled(value);
    }

    private void productFieldsEnabled(boolean value) {
        ButtonUtils.setUpdateButtonVisible(value, UPDATE_BTN, REGISTER_BTN);
        adminView.cbxNewSaleProduct.setEnabled(value);
        adminView.inputNewSaleQty.setEnabled(value);
        adminView.btnRegisterProductInNewSale.setEnabled(value);
        adminView.btnDeleteProductFromNewSale.setEnabled(value);
    }

    private void clearSalesInput() {
        adminView.cbxNewSaleProduct.setSelectedIndex(-1);
        adminView.inputNewSaleQty.setText("");
        ButtonUtils.setUpdateButtonVisible(false, UPDATE_BTN, REGISTER_BTN);
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
            handleNewSaleTableClick(e);
        } else if (e.getSource() == adminView.salesTable) {
            handleSalesTableClick(e);
        } else if (e.getSource() == adminView.btnPrintSale) {
            handlePrintSaleButtonClick(e);
        } else {
            adminView.btnPrintSale.setEnabled(false);
        }
    }

    private void handleNewSaleTableClick(MouseEvent e) {
        int row = adminView.newSaleTable.rowAtPoint(e.getPoint());
        setProductIndex(row);
        adminView.inputNewSaleQty.setText(adminView.newSaleTable.getValueAt(row, 1).toString());
        ButtonUtils.setUpdateButtonVisible(true, UPDATE_BTN, REGISTER_BTN);
    }

    private void handleSalesTableClick(MouseEvent e) {
        int row = adminView.salesTable.rowAtPoint(e.getPoint());
        adminView.extendedTableInformation.setText(adminView.salesTable.getValueAt(row, 1).toString()
                + ": " + adminView.salesTable.getValueAt(row, 2).toString());
        handlePrintSaleButton(true, row);
    }

    private void handlePrintSaleButtonClick(MouseEvent e) {
        int row = adminView.salesTable.rowAtPoint(e.getPoint());
        try {
            int saleId = Integer.parseInt(adminView.salesTable.getValueAt(row, 0).toString());
            Print.showPrintScreen(saleRepository.getSaleById(saleId), this, adminView);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void handlePrintSaleButton(boolean state, int row) {
        adminView.btnPrintSale.setEnabled(state);
        adminView.btnPrintSale.setText(state ? "Imprimir venta #" + salesTable.getValueAt(row, 0) : "Click en una venta para imprimirla");
    }

    @Override
    public void mouseExited(MouseEvent e
    ) {
        if (e.getSource() == adminView.newSaleTable) {
            adminView.extendedTableInformation.setText("");
        } else if (e.getSource() == adminView.salesTable) {
            adminView.extendedTableInformation.setText("");
        }
    }

    @Override
    public void mousePressed(MouseEvent e
    ) {
    }

    @Override
    public void mouseReleased(MouseEvent e
    ) {
    }

    @Override
    public void mouseEntered(MouseEvent e
    ) {
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
    }

    @Override
    public void keyPressed(KeyEvent e
    ) {

    }
}
