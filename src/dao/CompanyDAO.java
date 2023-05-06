package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Company;

public class CompanyDAO {

    public void setCompanyData(Company co) throws IOException {
        try {
            FileWriter writer = new FileWriter("companyConfig.txt");

            writer.write("socialName=" + co.getSocialName() + "\n");
            writer.write("CUIT=" + co.getCUIT() + "\n");
            writer.write("firstName=" + co.getFirstName() + "\n");
            writer.write("lastName=" + co.getLastName() + "\n");
            writer.write("phone=" + co.getPhone() + "\n");
            writer.write("address=" + co.getAddress() + "\n");
            writer.write("extras=" + co.getExtras() + "\n");

            writer.close();

        } catch (IOException ex) {
            throw ex;
        }
    }

    public void updateCompanyData(Company newCompanyData) throws IOException {
        Company updated = new Company();

        updated.setSocialName(newCompanyData.getSocialName());
        updated.setCUIT(newCompanyData.getCUIT());
        updated.setFirstName(newCompanyData.getFirstName());
        updated.setLastName(newCompanyData.getLastName());
        updated.setPhone(newCompanyData.getPhone());
        updated.setAddress(newCompanyData.getAddress());
        updated.setExtras(newCompanyData.getExtras());

        setCompanyData(updated);
    }

    public Company getCompanyData() throws IOException {
        Company co = new Company();

        try {
            FileReader reader = new FileReader("companyConfig.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                String propertyName = parts[0];
                String propertyValue = parts[1];

                switch (propertyName) {
                    case "socialName" ->
                        co.setSocialName(propertyValue);
                    case "CUIT" ->
                        co.setCUIT(propertyValue);
                    case "firstName" ->
                        co.setFirstName(propertyValue);
                    case "lastName" ->
                        co.setLastName(propertyValue);
                    case "phone" ->
                        co.setPhone(propertyValue);
                    case "address" ->
                        co.setAddress(propertyValue);
                    case "extras" ->
                        co.setExtras(propertyValue);
                }
            }

            bufferedReader.close();
            reader.close();
        } catch (IOException ex) {
            throw ex;
        }
        return co;
    }
}
