package controllers;

import dao.PurchaseDAO;
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utils.ComboBoxUtils;
import model.EPaymentMethod;
import model.EPurchaseStatus;
import model.Purchase;
import repositories.PurchaseRepository;
import repositories.SupplierRepository;
import utils.TableUtils;
import views.AdminPanel;
import views.Table;

public class PurchaseController implements ActionListener, MouseListener, KeyListener {

    private Purchase purchase;
    private PurchaseDAO purchaseDAO;
    public AdminPanel adminView;
    private PurchaseRepository purchaseRepository = new PurchaseRepository();
    private SupplierRepository supplierRepository = new SupplierRepository();
    private final Table color = new Table();
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
        ComboBoxUtils.loadPaymentMethodsComboBox(adminView.cbxPurchasePaymentMethod);
        ComboBoxUtils.loadStatusesComboBox(adminView.cbxPurchaseStatus);
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

        if (!adminView.inputPurchasePrice.getText().equals("")) {
            purchase.setUnitaryPrice(Double.parseDouble(adminView.inputPurchasePrice.getText()));
        } else {
            purchase.setUnitaryPrice(0.0);
        }

        if (!adminView.inputPurchaseDate.getText().equals("")) {
            purchase.setDate(adminView.inputPurchaseDate.getText());
        } else {
            purchase.setDate("-");
        }

        int purchaseSupplierId;
        try {
            purchaseSupplierId = supplierRepository.getSupplierIdByName(adminView.cbxPurchaseSupplier.getSelectedItem().toString());
            if (purchaseSupplierId != -1) {
                purchase.setSupplier(purchaseSupplierId);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        EPaymentMethod paymentMethod = EPaymentMethod.nameForUserToConstant(adminView.cbxPurchasePaymentMethod.getSelectedItem().toString());
        purchase.setEPaymentMethod(paymentMethod);

        EPurchaseStatus status = EPurchaseStatus.nameForUserToConstant(adminView.cbxPurchaseStatus.getSelectedItem().toString());
        purchase.setEStatus(status);
    }

    private void resetView() {
        TableUtils.clearTable(purchasesTable);
        listPurchases();
        clearPurchasesInput();
    }

    private void registerPurchase() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Ingrese al menos producto, cantidad, proveedor y método de pago.");
        } else {
            setupPurchase();
            try {
                purchaseRepository.register(purchase);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Compra registrada con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private boolean checkNullFields() {
        boolean check = true;
        if (adminView.inputPurchaseName.getText().equals("")
                || adminView.inputPurchaseQty.getText().equals("")
                || adminView.cbxPurchaseSupplier.getSelectedIndex() == -1
                || adminView.cbxPurchasePaymentMethod.getSelectedIndex() == -1) {
            check = false;
        }
        return check;
    }

    private void updatePurchase() {
        if (adminView.inputPurchaseName.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El nombre del producto comprado es obligatorio.");
        } else {
            setupPurchase();
            purchase.setId(Integer.parseInt((adminView.inputPurchaseId.getText())));

            try {
                purchaseRepository.update(purchase);
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
            List<Purchase> purchasesList = purchaseRepository.getPurchasesList(adminView.inputPurchaseSearch.getText());
            purchasesTable = (DefaultTableModel) adminView.purchasesTable.getModel();
            purchasesTable.setRowCount(0);

            purchaseListToObjectArray(purchasesList);
            

            TableUtils.changeHeaderColors(adminView.purchasesTable, purchasesTable);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void purchaseListToObjectArray(List<Purchase> purchasesList) {
        Object[] currentPurchase = new Object[9];
        
        for (int i = 0; i < purchasesList.size(); i++) {
            currentPurchase[0] = purchasesList.get(i).getId();
            currentPurchase[1] = purchasesList.get(i).getName();
            currentPurchase[2] = purchasesList.get(i).getQuantity();
            currentPurchase[3] = purchasesList.get(i).getUnitaryPrice();
            currentPurchase[4] = purchasesList.get(i).getDate();
            try {
                currentPurchase[5] = supplierRepository.getSupplierNameById(purchasesList.get(i).getSupplier());
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

            Enum currentPaymentMethodEnum = purchasesList.get(i).getEPaymentMethod();
            EPaymentMethod currentPaymentMethod = EPaymentMethod.valueOf(currentPaymentMethodEnum.name());
            currentPurchase[6] = currentPaymentMethod.getNameForUser();

            Enum currentStatusEnum = purchasesList.get(i).getEStatus();
            EPurchaseStatus currentStatus = EPurchaseStatus.valueOf(currentStatusEnum.name());
            currentPurchase[7] = currentStatus.getNameForUser();
            currentPurchase[8] = takeNumbersIn(purchasesList.get(i).getQuantity()) * purchasesList.get(i).getUnitaryPrice();
            
            purchasesTable.addRow(currentPurchase);
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
            setPaymentMethodIndex(adminView.purchasesTable.getValueAt(row, 6).toString());
            setStatusIndex(adminView.purchasesTable.getValueAt(row, 7).toString());
        }
    }

    private void setSupplierIndex(int row, int col) {
        int supplierIndex;
        try {
            supplierIndex = supplierRepository.getSupplierIdByName(adminView.purchasesTable.getValueAt(row, col).toString());

            if ((supplierIndex - 1) < adminView.cbxPurchaseSupplier.getItemCount()) {
                adminView.cbxPurchaseSupplier.setSelectedIndex(supplierIndex - 1);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
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

    private void loadSuppliersComboBox() {
        List<String> suppliers;
        try {
            suppliers = supplierRepository.getSupplierNames();
            adminView.cbxPurchaseSupplier.removeAllItems();
            for (String supplier : suppliers) {
                adminView.cbxPurchaseSupplier.addItem(supplier);
            }
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void deletePurchase() {
        if (!adminView.inputPurchaseId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputPurchaseId.getText());
            try {
                purchaseRepository.changeStatus(EPurchaseStatus.CANCELLED.name(), id);
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
                purchaseRepository.changeStatus(EPurchaseStatus.DONE.name(), id);
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
