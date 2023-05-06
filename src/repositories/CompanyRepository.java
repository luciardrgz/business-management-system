package repositories;

import dao.CompanyDAO;
import java.io.IOException;
import model.Company;

public class CompanyRepository {

    private final CompanyDAO companyDAO;

    public CompanyRepository(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    public CompanyRepository() {
        this.companyDAO = new CompanyDAO();
    }

    public void setupData(Company company) throws IOException {
        try {
            companyDAO.setCompanyData(company);
        } catch (IOException ex) {
            throw new IOException();
        }
    }

    public void updateData(Company newCompanyData)throws IOException {
        try {
            companyDAO.updateCompanyData(newCompanyData);
        } catch (IOException ex) {
            throw new IOException();
        }
    }
    
    public Company getData() throws IOException {
        Company co = null;
        try {
            co = companyDAO.getCompanyData();
        } catch (IOException ex) {
            throw new IOException();
        }
        return co;
    }
}
