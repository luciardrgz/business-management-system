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
import model.EStatus;
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
        this.adminView.btnUpdatePurchase.addActionListener(this);
        this.adminView.btnClearPurchase.addActionListener(this);
        this.adminView.jMenuItemDeletePurchase.addActionListener(this);
        this.adminView.jMenuItemReenterPurchase.addActionListener(this);
        this.adminView.inputPurchaseSearch.addKeyListener(this);
        this.adminView.purchasesTable.addMouseListener(this);

        loadSuppliersComboBox();
        loadPaymentMethodsComboBox();
        loadStatusesComboBox();
        listPurchases();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterPurchase) {
            registerPurchase();
        } else if (e.getSource() == adminView.btnUpdatePurchase) {
            updatePurchase();
        } else if (e.getSource() == adminView.jMenuItemDeletePurchase) {
            deletePurchase();
        } else if (e.getSource() == adminView.jMenuItemReenterPurchase) {
            recoverPurchase();
        } else {
            clearPurchasesInput();
        }
    }

    private void setupPurchase() {
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

        purchase.setEStatus(EStatus.valueOf(adminView.cbxPurchaseStatus.getSelectedItem().toString()));

    }

    private void resetView() {
        clearPurchasesTable();
        listPurchases();
        clearPurchasesInput();
    }

    private void registerPurchase() {
        if (adminView.inputPurchaseName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El nombre del producto comprado es obligatorio.");
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

    private void updatePurchase() {
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

    private void listPurchases() {
        adminView.purchasesTable.setDefaultRenderer(adminView.purchasesTable.getColumnClass(0), color);

        try {
            List<Purchase> purchasesList = purchaseDAO.getPurchasesList(adminView.inputPurchaseSearch.getText());
            purchasesTable = (DefaultTableModel) adminView.purchasesTable.getModel();

            purchasesTable.setRowCount(0);

            Object[] currentPurchase = new Object[8];
            for (int i = 0; i < purchasesList.size(); i++) {
                currentPurchase[0] = purchasesList.get(i).getId();
                currentPurchase[1] = purchasesList.get(i).getName();
                currentPurchase[2] = purchasesList.get(i).getQuantity();
                currentPurchase[3] = purchasesList.get(i).getUnitaryPrice();
                currentPurchase[4] = purchasesList.get(i).getDate();
                currentPurchase[5] = supplierDAO.retrieveSupplierNameById(purchasesList.get(i).getSupplier());
                currentPurchase[6] = paymentMethodDAO.retrievePaymentMethodNameById(purchasesList.get(i).getPaymentMethod());
                currentPurchase[7] = purchasesList.get(i).getEStatus() != null ? purchasesList.get(i).getEStatus().toString() : "";

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
        adminView.cbxPurchaseStatus.setSelectedIndex(-1);
    }

    private void clearPurchasesTable() {
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
            setStatusIndex(adminView.purchasesTable.getValueAt(row, 7).toString());
        }
    }

    private void setSupplierIndex(int row, int col) {
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

    private void setPaymentMethodIndex(int row, int col) {
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

    private void setStatusIndex(String purchaseStatus) {
        EStatus[] statuses = EStatus.values();
        int purchaseStatusIndex = -1;
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].toString().equals(purchaseStatus)) {
                purchaseStatusIndex = i;
                break;
            }
        }
        if (purchaseStatusIndex != -1) {
            adminView.cbxPurchaseStatus.setSelectedIndex(purchaseStatusIndex);
        }
    }

    private void loadSuppliersComboBox() {
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

    private void loadPaymentMethodsComboBox() {
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

    private void loadStatusesComboBox() {
        EStatus[] statuses;

        statuses = EStatus.class.getEnumConstants();
        adminView.cbxPurchaseStatus.removeAllItems();
        for (EStatus status : statuses) {
            adminView.cbxPurchaseStatus.addItem(status.name());
        }
    }

    private void deletePurchase() {
        if (!adminView.inputPurchaseId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputPurchaseId.getText());
            try {
                purchaseDAO.changeStatus(EStatus.CANCELADA.name(), id);
                resetView();
                JOptionPane.showMessageDialog(null, "Compra cancelada exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una compra para darla de baja.");
        }
    }

    private void recoverPurchase() {
        if (!adminView.inputPurchaseId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputPurchaseId.getText());
            try {
                purchaseDAO.changeStatus(EStatus.REALIZADA.name(), id);
                resetView();
                JOptionPane.showMessageDialog(null, "Compra reactivada exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una compra para darla de alta.");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputPurchaseSearch) {
            resetView();
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
