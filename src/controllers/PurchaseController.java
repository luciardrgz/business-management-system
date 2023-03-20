package controllers;

import dao.PurchaseDAO;
import dao.SupplierDAO;
import dao.PaymentMethodDAO;
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
import model.Purchase;
import views.AdminPanel;
import views.Table;

public class PurchaseController implements ActionListener, MouseListener, KeyListener {

    private Purchase purchase;
    private PurchaseDAO purchaseDAO;
    public AdminPanel adminView;
    private final Table color = new Table();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private PaymentMethodDAO paymentMethodDAO = new PaymentMethodDAO();
    private DefaultTableModel purchasesTable = new DefaultTableModel();

    public PurchaseController() {
    }

    public PurchaseController(Purchase purchase, PurchaseDAO purchaseDAO, AdminPanel adminView) {
        this.purchase = purchase;
        this.purchaseDAO = purchaseDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterPurchase.addActionListener(this);
        this.adminView.purchasesTable.addMouseListener(this);

        loadSuppliersComboBox();
        loadPaymentMethodsComboBox();
        listPurchases();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterPurchase) {
            registerPurchase();
        } else {
            clearPurchasesInput();
        }
    }

    public void setupPurchase() {
        purchase.setName(adminView.inputPurchaseName.getText());
        purchase.setQuantity(adminView.inputPurchaseQty.getText());
        purchase.setUnitaryPrice(Double.parseDouble(adminView.inputPurchasePrice.getText()));
        purchase.setDate(adminView.inputPurchaseDate.getText());

        int purchaseSupplierId;
        int paymentMethodId;

        try {
            purchaseSupplierId = supplierDAO.retrieveSupplierIdByName(adminView.cbxPurchaseSupplier.getSelectedItem().toString());
            if (purchaseSupplierId != -1) {
                purchase.setSupplier(purchaseSupplierId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        try {
            paymentMethodId = paymentMethodDAO.retrievePaymentMethodIdByName(adminView.cbxPurchasePaymentMethod.getSelectedItem().toString());
            if (paymentMethodId != -1) {
                purchase.setPaymentMethod(paymentMethodId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    public void resetView() {
        clearPurchasesTable();
        listPurchases();
        clearPurchasesInput();
    }

    public void registerPurchase() {
        if (adminView.inputPurchaseName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El nombre del producto comprado es obligatoria.");
        } else {
            setupPurchase();
            try {
                purchaseDAO.add(purchase);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Compra registrada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    public void updatePurchase() {
        if (adminView.inputPurchaseName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El nombre del producto comprado es obligatorio.");
        } else {
            setupPurchase();
            purchase.setId(Integer.parseInt((adminView.inputPurchaseId.getText())));

            try {
                purchaseDAO.update(purchase);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Compra modificada con éxito!");

            } catch (DBException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void listPurchases() {
        adminView.purchasesTable.setDefaultRenderer(adminView.purchasesTable.getColumnClass(0), color);

        try {
            List<Purchase> purchasesList = purchaseDAO.getPurchasesList();
            purchasesTable = (DefaultTableModel) adminView.purchasesTable.getModel();

            purchasesTable.setRowCount(0);

            Object[] currentPurchase = new Object[7];
            for (int i = 0; i < purchasesList.size(); i++) {
                currentPurchase[0] = purchasesList.get(i).getId();
                currentPurchase[1] = purchasesList.get(i).getName();
                currentPurchase[2] = purchasesList.get(i).getQuantity();
                currentPurchase[3] = purchasesList.get(i).getUnitaryPrice();
                currentPurchase[4] = purchasesList.get(i).getDate();
                currentPurchase[5] = supplierDAO.retrieveSupplierNameById(purchasesList.get(i).getSupplier());
                currentPurchase[6] = paymentMethodDAO.retrievePaymentMethodNameById(purchasesList.get(i).getPaymentMethod());

                purchasesTable.addRow(currentPurchase);
            }

            adminView.purchasesTable.setModel(purchasesTable);
            JTableHeader header = adminView.purchasesTable.getTableHeader();
            color.changeHeaderColors(header);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void clearPurchasesInput() {
        adminView.inputPurchaseId.setText("");
        adminView.inputPurchaseName.setText("");
        adminView.inputPurchaseQty.setText("");
        adminView.inputPurchasePrice.setText("");
        adminView.inputPurchaseDate.setText("");
        adminView.cbxPurchaseSupplier.setSelectedIndex(-1);
        adminView.cbxPurchasePaymentMethod.setSelectedIndex(-1);
    }

    public void clearPurchasesTable() {
        for (int i = 0; i < purchasesTable.getRowCount(); i++) {
            purchasesTable.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.purchasesTable) {
            int row = adminView.purchasesTable.rowAtPoint(e.getPoint());

            adminView.inputPurchaseId.setText(adminView.purchasesTable.getValueAt(row, 0).toString());
            adminView.inputPurchaseName.setText(adminView.purchasesTable.getValueAt(row, 1).toString());
            adminView.inputPurchaseQty.setText(adminView.purchasesTable.getValueAt(row, 2).toString());
            adminView.inputPurchasePrice.setText(adminView.purchasesTable.getValueAt(row, 3).toString());
            adminView.inputPurchaseDate.setText(adminView.purchasesTable.getValueAt(row, 4).toString());
            setSupplierIndex(row, 5);
            setPaymentMethodIndex(row, 6);
        }
    }

    public void setSupplierIndex(int row, int col) {
        int supplierIndex;
        try {
            supplierIndex = supplierDAO.retrieveSupplierIdByName(adminView.purchasesTable.getValueAt(row, col).toString());

            if ((supplierIndex - 1) < adminView.cbxPurchaseSupplier.getItemCount()) {
                adminView.cbxPurchaseSupplier.setSelectedIndex(supplierIndex - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void setPaymentMethodIndex(int row, int col) {
        int paymentMethodIndex;
        try {
            paymentMethodIndex = paymentMethodDAO.retrievePaymentMethodIdByName(adminView.purchasesTable.getValueAt(row, col).toString());
            if ((paymentMethodIndex - 1) < adminView.cbxPurchasePaymentMethod.getItemCount()) {
                adminView.cbxPurchasePaymentMethod.setSelectedIndex(paymentMethodIndex - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void loadSuppliersComboBox() {
        List<String> suppliers;
        try {
            suppliers = supplierDAO.getSupplierNames();
            adminView.cbxPurchaseSupplier.removeAllItems();
            for (String supplier : suppliers) {
                adminView.cbxPurchaseSupplier.addItem(supplier);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    public void loadPaymentMethodsComboBox() {
        List<String> paymentMethods;
        try {
            paymentMethods = paymentMethodDAO.getPaymentMethodNames();
            adminView.cbxPurchasePaymentMethod.removeAllItems();
            for (String paymentMethod : paymentMethods) {
                adminView.cbxPurchasePaymentMethod.addItem(paymentMethod);
            }
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
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
