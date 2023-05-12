package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Supplier;
import dao.SupplierDAO;
import exceptions.DBException;
import model.EPersonStatus;
import repositories.SupplierRepository;
import utils.TableUtils;
import views.AdminPanel;

public class SupplierController implements ActionListener, MouseListener, KeyListener {

    private final Supplier supplier;
    private final SupplierDAO supplierDAO;
    private final SupplierRepository supplierRepository = new SupplierRepository();
    private final AdminPanel adminView;
    private DefaultTableModel suppliersTable = new DefaultTableModel();

    public SupplierController(Supplier supplier, SupplierDAO supplierDAO, AdminPanel adminView) {
        this.supplier = supplier;
        this.supplierDAO = supplierDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterSupplier.addActionListener(this);
        this.adminView.btnUpdateSupplier.addActionListener(this);
        this.adminView.jMenuItemDeleteSupplier.addActionListener(this);
        this.adminView.jMenuItemReenterSupplier.addActionListener(this);
        this.adminView.btnNewSupplier.addActionListener(this);
        this.adminView.inputSupplierSearch.addKeyListener(this);
        this.adminView.suppliersTable.addMouseListener(this);
        listSuppliers();
    }

    private void setupSupplier() {
        supplier.setFirstName(adminView.inputSupplierFirstName.getText());
        supplier.setLastName(adminView.inputSupplierLastName.getText());
        supplier.setSocialName(adminView.inputSupplierSocial.getText());
        supplier.setCuit(adminView.inputSupplierCUIT.getText());
        supplier.setPhone(adminView.inputSupplierPhone.getText());
        supplier.setAddress(adminView.inputSupplierAddress.getText());
    }

    private void resetView() {
        TableUtils.clearTable(suppliersTable);
        listSuppliers();
        clearSuppliersInput();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterSupplier) {
            registerSupplier();
        } else if (e.getSource() == adminView.btnUpdateSupplier) {
            updateSupplier();
        } else if (e.getSource() == adminView.jMenuItemDeleteSupplier) {
            deleteSupplier();
        } else if (e.getSource() == adminView.jMenuItemReenterSupplier) {
            recoverSupplier();
        } else {
            clearSuppliersInput();
        }
    }

    private void registerSupplier() {
        if (adminView.inputSupplierSocial.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupSupplier();

            try {
                supplierRepository.register(supplier);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Proveedor registrado con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void updateSupplier() {
        if (adminView.inputSupplierSocial.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupSupplier();
            supplier.setId(Integer.parseInt(adminView.inputSupplierId.getText()));

            try {
                supplierRepository.update(supplier);
                resetView();
                JOptionPane.showMessageDialog(null, "¡Proveedor modificado con éxito!");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void deleteSupplier() {
        if (!adminView.inputSupplierId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputSupplierId.getText());

            try {
                supplierRepository.changeStatus(EPersonStatus.INACTIVE, id);
                resetView();
                JOptionPane.showMessageDialog(null, "Proveedor dado de baja exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un proveedor para darlo de baja.");
        }
    }

    private void recoverSupplier() {
        if (!adminView.inputSupplierId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputSupplierId.getText());

            try {
                supplierRepository.changeStatus(EPersonStatus.ACTIVE, id);
                resetView();
                JOptionPane.showMessageDialog(null, "Proveedor dado de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un proveedor para darlo de alta.");
        }
    }

    private void listSuppliers() {
        adminView.suppliersTable.setDefaultRenderer(adminView.suppliersTable.getColumnClass(0),  new TableUtils());

        try {
            List<Supplier> suppliersList = supplierRepository.getSuppliersList(adminView.inputSupplierSearch.getText());
            suppliersTable = (DefaultTableModel) adminView.suppliersTable.getModel();
            suppliersTable.setRowCount(0);
            TableUtils.centerTableContent(adminView.suppliersTable);
            suppliersListToObjectArray(suppliersList);

            TableUtils.setUpTableStyle(adminView.suppliersTable, suppliersTable);
        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void suppliersListToObjectArray(List<Supplier> suppliersList) {
        Object[] currentSupplier = new Object[8];
        for (int i = 0; i < suppliersList.size(); i++) {
            currentSupplier[0] = suppliersList.get(i).getId();
            currentSupplier[1] = suppliersList.get(i).getFirstName();
            currentSupplier[2] = suppliersList.get(i).getLastName();
            currentSupplier[3] = suppliersList.get(i).getSocialName();
            currentSupplier[4] = suppliersList.get(i).getCuit();
            currentSupplier[5] = suppliersList.get(i).getPhone();
            currentSupplier[6] = suppliersList.get(i).getAddress();
            currentSupplier[7] = suppliersList.get(i).getStatus();

            suppliersTable.addRow(currentSupplier);
        }
    }

    private void clearSuppliersInput() {
        adminView.inputSupplierId.setText("");
        adminView.inputSupplierFirstName.setText("");
        adminView.inputSupplierLastName.setText("");
        adminView.inputSupplierSocial.setText("");
        adminView.inputSupplierCUIT.setText("");
        adminView.inputSupplierPhone.setText("");
        adminView.inputSupplierAddress.setText("");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.suppliersTable) {
            int row = adminView.suppliersTable.rowAtPoint(e.getPoint());

            adminView.inputSupplierId.setText(adminView.suppliersTable.getValueAt(row, 0).toString());
            adminView.inputSupplierFirstName.setText(adminView.suppliersTable.getValueAt(row, 1).toString());
            adminView.inputSupplierLastName.setText(adminView.suppliersTable.getValueAt(row, 2).toString());
            adminView.inputSupplierSocial.setText(adminView.suppliersTable.getValueAt(row, 3).toString());
            adminView.inputSupplierCUIT.setText(adminView.suppliersTable.getValueAt(row, 4).toString());
            adminView.inputSupplierPhone.setText(adminView.suppliersTable.getValueAt(row, 5).toString());
            adminView.inputSupplierAddress.setText(adminView.suppliersTable.getValueAt(row, 6).toString());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputSupplierSearch) {
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
