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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utils.ComboBoxUtils;
import model.EPaymentMethod;
import model.EPurchaseStatus;
import model.EUnit;
import model.Purchase;
import repositories.PurchaseRepository;
import repositories.SupplierRepository;
import utils.TableUtils;
import views.AdminPanel;

public class PurchaseController implements ActionListener, MouseListener, KeyListener {

    private Purchase purchase;
    private PurchaseDAO purchaseDAO;
    public AdminPanel adminView;
    private PurchaseRepository purchaseRepository = new PurchaseRepository();
    private SupplierRepository supplierRepository = new SupplierRepository();
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
        loadUnitsComboBox();
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

        EUnit unit = EUnit.nameForUserToConstant(adminView.cbxUnit.getSelectedItem().toString());
        purchase.setEUnit(unit);

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

        if (adminView.cbxPurchaseStatus.getSelectedIndex() != -1) {
            EPurchaseStatus status = EPurchaseStatus.nameForUserToConstant(adminView.cbxPurchaseStatus.getSelectedItem().toString());
            purchase.setEStatus(status);
        } else {
            purchase.setEStatus(EPurchaseStatus.DONE);
        }

    }

    private void resetView() {
        TableUtils.clearTable(purchasesTable);
        listPurchases();
        clearPurchasesInput();
    }

    private void registerPurchase() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Ingrese al menos producto, cantidad, unidad, proveedor y método de pago.");
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
                || adminView.cbxUnit.getSelectedIndex() == -1
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
        adminView.purchasesTable.setDefaultRenderer(adminView.purchasesTable.getColumnClass(0),  new TableUtils());

        try {
            List<Purchase> purchasesList = purchaseRepository.getPurchasesList(adminView.inputPurchaseSearch.getText());
            purchasesTable = (DefaultTableModel) adminView.purchasesTable.getModel();
            purchasesTable.setRowCount(0);

            purchaseListToObjectArray(purchasesList);

            TableUtils.setUpTableStyle(adminView.purchasesTable, purchasesTable);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void purchaseListToObjectArray(List<Purchase> purchasesList) {
        Object[] currentPurchase = new Object[10];

        for (int i = 0; i < purchasesList.size(); i++) {
            currentPurchase[0] = purchasesList.get(i).getId();
            currentPurchase[1] = purchasesList.get(i).getName();
            currentPurchase[2] = purchasesList.get(i).getQuantity();

            Enum currentUnitEnum = purchasesList.get(i).getEUnit();
            EUnit currentUnit = EUnit.valueOf(currentUnitEnum.name());
            currentPurchase[3] = currentUnit.getNameForUser();

            currentPurchase[4] = purchasesList.get(i).getUnitaryPrice();
            currentPurchase[5] = purchasesList.get(i).getDate();
            try {
                currentPurchase[6] = supplierRepository.getSupplierNameById(purchasesList.get(i).getSupplier());
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

            Enum currentPaymentMethodEnum = purchasesList.get(i).getEPaymentMethod();
            EPaymentMethod currentPaymentMethod = EPaymentMethod.valueOf(currentPaymentMethodEnum.name());
            currentPurchase[7] = currentPaymentMethod.getNameForUser();

            Enum currentStatusEnum = purchasesList.get(i).getEStatus();
            EPurchaseStatus currentStatus = EPurchaseStatus.valueOf(currentStatusEnum.name());
            currentPurchase[8] = currentStatus.getNameForUser();
            currentPurchase[9] = Integer.parseInt(purchasesList.get(i).getQuantity()) * purchasesList.get(i).getUnitaryPrice();

            purchasesTable.addRow(currentPurchase);
        }
    }

    private void clearPurchasesInput() {
        adminView.inputPurchaseId.setText("");
        adminView.inputPurchaseName.setText("");
        adminView.inputPurchaseQty.setText("");
        adminView.cbxUnit.setSelectedIndex(-1);
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
            setUnitIndex(adminView.purchasesTable.getValueAt(row, 3).toString());
            adminView.inputPurchasePrice.setText(adminView.purchasesTable.getValueAt(row, 4).toString());
            adminView.inputPurchaseDate.setText(adminView.purchasesTable.getValueAt(row, 5).toString());
            setSupplierIndex(row, 6);
            setPaymentMethodIndex(adminView.purchasesTable.getValueAt(row, 7).toString());
            setStatusIndex(adminView.purchasesTable.getValueAt(row, 8).toString());
        }
    }

    private void setUnitIndex(String unit) {
        EUnit[] units = EUnit.values();
        int purchaseUnitIndex = -1;

        for (int i = 0; i < units.length; i++) {
            if (units[i].getNameForUser().equals(unit)) {
                purchaseUnitIndex = i;
                break;
            }
        }
        if (purchaseUnitIndex != -1) {
            adminView.cbxUnit.setSelectedIndex(purchaseUnitIndex);
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

    private void loadUnitsComboBox() {
        EUnit[] units = EUnit.class.getEnumConstants();
        adminView.cbxUnit.removeAllItems();

        for (EUnit unit : units) {
            adminView.cbxUnit.addItem(unit.getNameForUser());
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
