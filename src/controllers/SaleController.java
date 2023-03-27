package controllers;



import dao.SaleDAO;
import exceptions.DBException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.ComboBoxUtils;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.EPaymentMethod;
import model.EPurchaseStatus;
import model.Sale;
import repositories.CustomerRepository;
import repositories.ProductRepository;
import views.AdminPanel;
import views.Table;

public class SaleController implements ActionListener, MouseListener, KeyListener {

    private Sale sale;
    private SaleDAO saleDAO;
    public AdminPanel adminView;
    private ProductRepository productRepository = new ProductRepository();
    private CustomerRepository customerRepository = new CustomerRepository();
    private final Table color = new Table();
    private DefaultTableModel newSaleTable = new DefaultTableModel();

    public SaleController() {
    }

    public SaleController(Sale sale, SaleDAO saleDAO, AdminPanel adminView) {
        this.sale = sale;
        this.saleDAO = saleDAO;
        this.adminView = adminView;
        this.adminView.btnAddProductToNewSale.addActionListener(this);
        this.adminView.btnGenerateNewSale.addActionListener(this);
        this.adminView.purchasesTable.addMouseListener(this);

        loadProductsComboBox();
        loadCustomersComboBox();
        ComboBoxUtils.loadPaymentMethodsComboBox(adminView.cbxNewSalePaymentMethod);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnAddProductToNewSale) {
            addSaleToTable(sale);
        } else if (e.getSource() == adminView.btnGenerateNewSale) {
            generateSale();
        } 
    }

    private void setupSale() {
        setSaleProductId();
        sale.setQuantity(Integer.parseInt(adminView.inputNewSaleQty.getText()));
        setSaleCustomerId();

        EPaymentMethod paymentMethod = EPaymentMethod.nameForUserToConstant(adminView.cbxNewSaleCustomer.getSelectedItem().toString());
        sale.setPaymentMethod(paymentMethod);

        sale.setTotal(Integer.parseInt(adminView.inputNewSaleTotal.getText()));
    }

    private void setSaleProductId() {
        int productId;
        try {
            productId = productRepository.retrieveProductIdByName(adminView.cbxNewSaleProduct.getSelectedItem().toString());
            if (productId != -1) {
                sale.setProduct(productId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void setSaleCustomerId() {
        int customerId;

        try {
            customerId = customerRepository.retrieveCustomerIdByName(adminView.cbxNewSaleCustomer.getSelectedItem().toString());
            if (customerId != -1) {
                sale.setCustomer(customerId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    private void resetView() {
        clearSalesInput();
        //listPurchases();
        clearSalesTable();
    }

    private void generateSale() {
        if (adminView.cbxNewSaleProduct.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Elija un producto a vender.");
        } else {
            setupSale();
            try {
                saleDAO.add(sale);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Venta registrada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void addSaleToTable(Sale sale) {
        if (adminView.cbxNewSaleProduct.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Elija un producto a vender.");
        } else {
            setupSale();
            listThisSale(sale);
            resetView();
            JOptionPane.showMessageDialog(null, "¡Venta registrada con éxito!");
        }
    }

    private void listThisSale(Sale sale) {
        adminView.newSaleTable.setDefaultRenderer(adminView.newSaleTable.getColumnClass(0), color);

        try {
            newSaleTable = (DefaultTableModel) adminView.newSaleTable.getModel();
            newSaleTable.setRowCount(0);

            Object[] currentSale = new Object[4];

            currentSale[0] = sale.getId();
            currentSale[1] = productRepository.retrieveProductNameById(sale.getProduct());
            currentSale[2] = sale.getQuantity();
            currentSale[3] = productRepository.retrieveProductPrice(sale.getId());

            newSaleTable.addRow(currentSale);

            adminView.newSaleTable.setModel(newSaleTable);
            JTableHeader header = adminView.newSaleTable.getTableHeader();
            color.changeHeaderColors(header);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private Double takeNumbersIn(String purchaseQty) {
        String pattern = "\\d+";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(purchaseQty);

        String numbers = "";

        while (m.find()) {
            numbers = m.group();
        }

        return Double.valueOf(numbers);
    }

    private void clearSalesInput() {
        adminView.cbxNewSaleProduct.setSelectedIndex(-1);
        adminView.inputNewSaleQty.setText("");
        adminView.cbxNewSaleCustomer.setSelectedIndex(-1);
        adminView.cbxPurchasePaymentMethod.setSelectedIndex(-1);
    }

    private void clearSalesTable() {
        for (int i = 0; i < newSaleTable.getRowCount(); i++) {
            newSaleTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.purchasesTable) {

        }
    }

    private void setPaymentMethodIndex(String paymentMethod) {
        EPaymentMethod[] paymentMethods = EPaymentMethod.values();
        int purchasePaymentMethodIndex = -1;

        for (int i = 0; i < paymentMethods.length; i++) {
            if (paymentMethods[i].getNameForUser().equals(paymentMethod)) {
                purchasePaymentMethodIndex = i;
                break;
            }
        }
        if (purchasePaymentMethodIndex != -1) {
            adminView.cbxPurchasePaymentMethod.setSelectedIndex(purchasePaymentMethodIndex);
        }
    }

    private void setStatusIndex(String purchaseStatus) {
        EPurchaseStatus[] statuses = EPurchaseStatus.values();
        int purchaseStatusIndex = -1;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].getNameForUser().equals(purchaseStatus)) {
                purchaseStatusIndex = i;
                break;
            }
        }
        if (purchaseStatusIndex != -1) {
            adminView.cbxPurchaseStatus.setSelectedIndex(purchaseStatusIndex);
        }
    }

    private void loadProductsComboBox() {
        List<String> products;
        try {
            products = productRepository.retrieveProductNames();
            adminView.cbxNewSaleProduct.removeAllItems();

            for (String product : products) {
                adminView.cbxNewSaleProduct.addItem(product);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void loadCustomersComboBox() {
        List<String> customers;

        try {
            customers = customerRepository.retrieveCustomerNames();
            adminView.cbxNewSaleCustomer.removeAllItems();
            for (String customer : customers) {
                adminView.cbxNewSaleCustomer.addItem(customer);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputPurchaseSearch) {
            System.out.println("ayuda");
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
