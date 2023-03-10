package controllers;

import java.awt.Color;
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
import model.Customer;
import model.CustomerDAO;
import model.Table;
import views.AdminPanel;

public class CustomerController implements ActionListener, MouseListener, KeyListener {

    private Customer customer;
    private CustomerDAO customerDAO;
    private AdminPanel adminView;

    DefaultTableModel customersTable = new DefaultTableModel();

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
        }else if(e.getSource() == adminView.jMenuItemReenterCustomer){            
            recoverCustomer();
        }
        else{
            clearCustomersInput();
        }
    }

    public boolean checkNullFields() {
        boolean check = true;

        if (adminView.inputCustomerFirstName.getText().equals("")
                || adminView.inputCustomerPhone.getText().equals("")
                || adminView.inputCustomerAddress.getText().equals("")) {
            check = false;
        }

        return check;
    }

    public void registerCustomer() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            customer.setFirstName(adminView.inputCustomerFirstName.getText());
            customer.setLastName(adminView.inputCustomerLastName.getText());
            customer.setPhone(adminView.inputCustomerPhone.getText());
            customer.setAddress(adminView.inputCustomerAddress.getText());

            if (customerDAO.register(customer)) {
                clearCustomersTable();
                listCustomers();
                clearCustomersInput();
                JOptionPane.showMessageDialog(null, "Cliente registrado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el cliente.");
            }
        }
    }

    public void updateCustomer() {
        if (!adminView.inputCustomerId.getText().equals("")) {
            if (checkNullFields() == false) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            } else {
                customer.setFirstName(adminView.inputCustomerFirstName.getText());
                customer.setLastName(adminView.inputCustomerLastName.getText());
                customer.setPhone(adminView.inputCustomerPhone.getText());
                customer.setAddress(adminView.inputCustomerAddress.getText());
                customer.setId(Integer.parseInt(adminView.inputCustomerId.getText()));

                if (customerDAO.update(customer)) {
                    clearCustomersTable();
                    listCustomers();
                    clearCustomersInput();
                    JOptionPane.showMessageDialog(null, "Cliente modificado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al modificar el cliente.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un cliente.");
        }
    }

    public void listCustomers() {
        Table color = new Table();
        adminView.customersTable.setDefaultRenderer(adminView.customersTable.getColumnClass(0), color);

        List<Customer> customersList = customerDAO.getCustomersList(adminView.inputCustomerSearch.getText());
        customersTable = (DefaultTableModel) adminView.customersTable.getModel();

        customersTable.setRowCount(0);

        Object[] currentCustomer = new Object[6];
        for (int i = 0; i < customersList.size(); i++) {
            currentCustomer[0] = customersList.get(i).getId();
            currentCustomer[1] = customersList.get(i).getFirstName();
            currentCustomer[2] = customersList.get(i).getLastName();
            currentCustomer[3] = customersList.get(i).getPhone();
            currentCustomer[4] = customersList.get(i).getAddress();
            currentCustomer[5] = customersList.get(i).getStatus();

            customersTable.addRow(currentCustomer);
        }

        adminView.customersTable.setModel(customersTable);
        JTableHeader header = adminView.customersTable.getTableHeader();
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setForeground(Color.white);
    }

    private void clearCustomersInput() {
        adminView.inputCustomerId.setText("");
        adminView.inputCustomerFirstName.setText("");
        adminView.inputCustomerLastName.setText("");
        adminView.inputCustomerPhone.setText("");
        adminView.inputCustomerAddress.setText("");
    }

    public void clearCustomersTable() {
        for (int i = 0; i < customersTable.getRowCount(); i++) {
            customersTable.removeRow(i);
            i = i - 1;
        }
    }

    public void deleteCustomer() {
        if (!adminView.inputCustomerId.getText().equals("")) {
            int id = Integer.parseInt(adminView.inputCustomerId.getText());

            if (customerDAO.changeStatus("Inactivo", id)) {
                clearCustomersTable();
                listCustomers();
                clearCustomersInput();
                JOptionPane.showMessageDialog(null, "Cliente dado de baja exitosamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al intentar dar de baja al cliente.");
            }

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione un cliente para darlo de baja.");
        }
    }
    
    public void recoverCustomer(){
        if(!adminView.inputCustomerId.getText().equals("")){
                int id = Integer.parseInt(adminView.inputCustomerId.getText());
                if (customerDAO.changeStatus("Activo", id)){
                    clearCustomersTable();
                    listCustomers();
                    clearCustomersInput();
                    JOptionPane.showMessageDialog(null, "Cliente dado de alta exitosamente.");
                }
                else{
                     JOptionPane.showMessageDialog(null, "Error al intentar dar de alta al cliente.");
                }
                
                
            }else{
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

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getSource() == adminView.inputCustomerSearch){
            clearCustomersTable();
            listCustomers();
        }
    }

}
