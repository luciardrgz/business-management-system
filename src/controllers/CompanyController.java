package controllers;

import dao.CompanyDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import model.Company;
import repositories.CompanyRepository;
import views.AdminPanel;

public class CompanyController implements ActionListener{

    private Company company;
    private CompanyDAO companyDAO;
    private CompanyRepository companyRepository = new CompanyRepository();
    private final AdminPanel adminView;

    public CompanyController(Company company, CompanyDAO companyDAO, AdminPanel adminView) {
        this.adminView = adminView;
        this.company = company;
        this.companyDAO = companyDAO;
        this.adminView.btnRegisterBusiness.addActionListener(this);
        this.adminView.btnUpdateBusiness.addActionListener(this);
        setupCompanyFields();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == adminView.btnRegisterBusiness) {
            registerCompany();
        } else if (e.getSource() == adminView.btnUpdateBusiness) {
            updateCompany();
        }
    }

    private void setupCompany() {
        company.setSocialName(adminView.inputBusinessName.getText());
        company.setFirstName(adminView.inputBusinessOwnerName.getText());
        company.setLastName(adminView.inputBusinessOwnerLastName.getText());
        company.setCUIT(adminView.inputBusinessCUIT.getText());
        company.setAddress(adminView.inputBusinessAddress.getText());
        company.setPhone(adminView.inputBusinessPhone.getText());
        company.setExtras(adminView.inputBusinessExtras.getText());
    }

    private void setupCompanyFields() {
        try {
            company = companyRepository.getData();
            adminView.inputBusinessName.setText(company.getSocialName());
            adminView.inputBusinessOwnerName.setText(company.getFirstName());
            adminView.inputBusinessOwnerLastName.setText(company.getLastName());
            adminView.inputBusinessCUIT.setText(company.getCUIT());
            adminView.inputBusinessAddress.setText(company.getAddress());
            adminView.inputBusinessPhone.setText(company.getPhone());
            adminView.inputBusinessExtras.setText(company.getExtras());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "A continuación, ingresarás al sistema. Por favor, haz click en 'Empresa' e ingresa los datos para comenzar a utilizarlo.");
        }
    }

    private void registerCompany() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupCompany();

            try {
                companyRepository.setupData(company);
                JOptionPane.showMessageDialog(null, "Empresa registrada exitosamente.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private void updateCompany() {
        if (checkNullFields() == false) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
        } else {
            setupCompany();

            try {
                companyRepository.updateData(company);
                JOptionPane.showMessageDialog(null, "Empresa modificada exitosamente.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        }
    }

    private boolean checkNullFields() {
        boolean check = true;

        if (adminView.inputBusinessName.getText().equals("")
                || adminView.inputBusinessOwnerName.getText().equals("")
                || adminView.inputBusinessOwnerLastName.getText().equals("")
                || adminView.inputBusinessCUIT.getText().equals("")
                || adminView.inputBusinessAddress.getText().equals("")
                || adminView.inputBusinessPhone.getText().equals("")
                || adminView.inputBusinessExtras.getText().equals("")) {
            check = false;
        }

        return check;
    }

}
