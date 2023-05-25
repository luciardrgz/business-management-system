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
import model.Customer;
import dao.CustomerDAO;
import exceptions.DBException;
import javax.swing.JButton;
import model.EPersonStatus;
import repositories.CustomerRepository;
import utils.ButtonUtils;
import views.AdminPanel;
import utils.TableUtils;

public class CustomerController implements ActionListener, MouseListener, KeyListener {

    private final Customer customer;
    private final CustomerDAO customerDAO;
    private final AdminPanel adminView;
    private final CustomerRepository customerRepository = new CustomerRepository();
    private DefaultTableModel customersTable = new DefaultTableModel();
    private JButton UPDATE_BTN;
    private JButton REGISTER_BTN;

    public CustomerController(Customer customer, CustomerDAO customerDAO, AdminPanel adminView) {
        this.customer = customer;
        this.customerDAO = customerDAO;
        this.adminView = adminView;
        this.adminView.btnRegisterCustomer.addActionListener(this);
        this.adminView.btnUpdateCustomer.addActionListener(this);
        this.adminView.btnNewCustomer.addActionListener(this);
        this.adminView.customersTable.addMouseListener(this);
        this.adminView.jMenuItemDeleteCustomer.addActionListener(this);
        this.adminView.jMenuItemReenterCustomer.addActionListener(this);
        this.adminView.inputCustomerSearch.addKeyListener(this);
        this.adminView.lblCustomers.addMouseListener(this);
        this.UPDATE_BTN = adminView.btnUpdateCustomer;
        this.REGISTER_BTN = adminView.btnRegisterCustomer;

        ButtonUtils.setUpdateButtonVisible(false, UPDATE_BTN, REGISTER_BTN);
        listCustomers();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterCustomer) {
            registerCustomer();
        } else if (e.getSource() == adminView.btnUpdateCustomer) {
            updateCustomer();
        } else if (e.getSource() == adminView.jMenuItemDeleteCustomer) {
            deleteCustomer();
        } else if (e.getSource() == adminView.jMenuItemReenterCustomer) {
            recoverCustomer();
        } else {
            clearCustomersInput();
        }
    }

    private void setupCustomer() {
        customer.setFirstName(adminView.inputCustomerFirstName.getText());
        customer.setLastName(adminView.inputCustomerLastName.getText());
        customer.setPhone(adminView.inputCustomerPhone.getText());
        customer.setAddress(adminView.inputCustomerAddress.getText());
    }

    private void resetView() {
        TableUtils.clearTable(customersTable);
        listCustomers();
        clearCustomersInput();
    }

    private boolean checkNullFields() {
        boolean check = true;

        if (adminView.inputCustomerFirstName.getText().equals("")
                || adminView.inputCustomerPhone.getText().equals("")
                || adminView.inputCustomerAddress.getText().equals("")) {
            check = false;
        }

        return check;
    }

    private void registerCustomer() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupCustomer();

            try {
                customerRepository.register(customer);
                resetView();
                JOptionPane.showMessageDialog(null, "Cliente registrado exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void updateCustomer() {
        if (!adminView.inputCustomerId.getText().equals("")) {
            if (checkNullFields() == false) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            } else {
                ButtonUtils.setUpdateButtonVisible(true, UPDATE_BTN, REGISTER_BTN);
                setupCustomer();
                customer.setId(Integer.parseInt(adminView.inputCustomerId.getText()));

                try {
                    customerRepository.update(customer);
                    resetView();
                    JOptionPane.showMessageDialog(null, "Cliente modificado exitosamente.");
                } catch (DBException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un cliente.");
        }
    }

    private void listCustomers() {
        adminView.customersTable.setDefaultRenderer(adminView.customersTable.getColumnClass(0), new TableUtils());

        try {
            List<Customer> customersList = customerRepository.getCustomersList(adminView.inputCustomerSearch.getText());
            customersTable = (DefaultTableModel) adminView.customersTable.getModel();
            customersTable.setRowCount(0);
            customersListToObjectArray(customersList);

            TableUtils.setUpTableStyle(adminView.customersTable, customersTable);

        } catch (DBException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void customersListToObjectArray(List<Customer> customersList) {
        Object[] currentCustomer = new Object[6];
        for (int i = 0; i < customersList.size(); i++) {
            currentCustomer[0] = customersList.get(i).getId();
            currentCustomer[1] = customersList.get(i).getFirstName();
            currentCustomer[2] = customersList.get(i).getLastName();
            currentCustomer[3] = customersList.get(i).getPhone();
            currentCustomer[4] = customersList.get(i).getAddress();

            Enum currentStatusEnum = customersList.get(i).getStatus();
            EPersonStatus currentStatus = EPersonStatus.valueOf(currentStatusEnum.name());
            currentCustomer[5] = currentStatus.getNameForUser();

            customersTable.addRow(currentCustomer);
        }
    }

    private void clearCustomersInput() {
        adminView.inputCustomerId.setText("");
        adminView.inputCustomerFirstName.setText("");
        adminView.inputCustomerLastName.setText("");
        adminView.inputCustomerPhone.setText("");
        adminView.inputCustomerAddress.setText("");
        ButtonUtils.setUpdateButtonVisible(false, UPDATE_BTN, REGISTER_BTN);
    }

    private void deleteCustomer() {
        if (!adminView.inputCustomerId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCustomerId.getText());

            try {
                customerRepository.changeStatus(EPersonStatus.INACTIVE, id);
                resetView();
                JOptionPane.showMessageDialog(null, "Cliente dado de baja exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para darlo de baja.");
        }
    }

    private void recoverCustomer() {
        if (!adminView.inputCustomerId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCustomerId.getText());

            try {
                customerRepository.changeStatus(EPersonStatus.ACTIVE, id);
                resetView();
                JOptionPane.showMessageDialog(null, "Cliente dado de alta exitosamente.");
            } catch (DBException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para darlo de alta.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.customersTable) {
            int row = adminView.customersTable.rowAtPoint(e.getPoint());

            adminView.inputCustomerId.setText(adminView.customersTable.getValueAt(row, 0).toString());
            adminView.inputCustomerFirstName.setText(adminView.customersTable.getValueAt(row, 1).toString());
            adminView.inputCustomerLastName.setText(adminView.customersTable.getValueAt(row, 2).toString());
            adminView.inputCustomerPhone.setText(adminView.customersTable.getValueAt(row, 3).toString());
            adminView.inputCustomerAddress.setText(adminView.customersTable.getValueAt(row, 4).toString());
            ButtonUtils.setUpdateButtonVisible(true, UPDATE_BTN, REGISTER_BTN);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == adminView.inputCustomerSearch) {
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
