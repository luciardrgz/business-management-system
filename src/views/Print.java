package views;

import controllers.SaleController;
import exceptions.DBException;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import listeners.IPrintCloseListener;
import model.Sale;
import repositories.CompanyRepository;
import repositories.CustomerRepository;
import repositories.ProductRepository;
import repositories.SaleRepository;
import utils.TableUtils;

public class Print extends JFrame {

    private SaleRepository saleRepository = new SaleRepository();
    private ProductRepository productRepository = new ProductRepository();
    private CustomerRepository customerRepository = new CustomerRepository();
    private CompanyRepository companyRepository = new CompanyRepository();
    private DefaultTableModel table = new DefaultTableModel();
    private IPrintCloseListener printCloseListener;

    public Print(int id, IPrintCloseListener printCloseListener) throws DBException, IOException {
        initComponents();
        this.printCloseListener = printCloseListener;

        try {
            setCompanyData();
            listThisSale(id);
        } catch (DBException | IOException ex) {
            throw ex;
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                printWindowClosed();
            }
        });
    }

    private void printWindowClosed() {
        printCloseListener.onPrintWindowClosed();
    }

    private void listThisSale(int id) throws DBException {
        List<Sale> sales = saleRepository.getSales();
        double total = -1;

        table = (DefaultTableModel) finalSaleTable.getModel();
        table.setRowCount(0);

        try {
            lblSaleNo.setText(String.valueOf(id));

            for (int i = 0; i < sales.size(); i++) {
                if (sales.get(i).getId() == id) {
                    Object[] tempSale = saleToObject(sales.get(i));
                    table.addRow(tempSale);
                    total += sales.get(i).getTotal();
                }
            }

            lblCustomerName.setText(getCustomerNameOfSale(id));
            lblTotalContainer.setText(String.valueOf(total + 1));

            TableUtils.setUpTableStyle(finalSaleTable, table);
        } catch (DBException ex) {
            throw ex;
        }
    }

    private String getCustomerNameOfSale(int id) throws DBException {
        int saleCustomerId = -1;
        String saleCustomerName = "";

        try {
            saleCustomerId = saleRepository.getCustomerFromSale(id);
            if (saleCustomerId != -1) {
                String[] composedName = customerRepository.getCustomerNameById(saleCustomerId);
                saleCustomerName = composedName[0] + " " + composedName[1];
            }

        } catch (DBException ex) {
            throw ex;
        }

        return saleCustomerName;
    }

    private Object[] saleToObject(Sale sale) throws DBException {
        Object[] saleObj = new Object[4];
        try {
            saleObj[0] = productRepository.getProductNameById(sale.getProduct());
            saleObj[1] = productRepository.getProductPrice(sale.getProduct());
            saleObj[2] = sale.getQuantity();
            saleObj[3] = sale.getQuantity() * productRepository.getProductPrice(sale.getProduct());
        } catch (DBException ex) {
            throw ex;
        }
        return saleObj;
    }

    private void salesListToObjectArray(List<Sale> salesList) throws DBException {
        try {
            for (int i = 0; i < salesList.size(); i++) {
                Object[] tempSale = saleToObject(salesList.get(i));
                table.addRow(tempSale);
            }
        } catch (DBException ex) {
            throw ex;

        }
    }

    private void setCompanyData() throws IOException {
        try {
            lblCompanyMainData.setText(companyRepository.getData().getSocialName() + " - CUIT " + companyRepository.getData().getCUIT());
            lblCompanyOwner.setText(companyRepository.getData().getFirstName() + " " + companyRepository.getData().getLastName());
        } catch (IOException ex) {
            throw ex;
        }
    }

    public static void showPrintScreen(Sale finalSale, SaleController controller, AdminPanel adminView) throws DBException {
        try {
            Print printView = new Print(finalSale.getId(), controller);

            printView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    adminView.setEnabled(true);
                    adminView.toFront();
                }
            });

            printView.setLocationRelativeTo(null); 

            printView.setVisible(true);
            adminView.setEnabled(false);
        } catch (DBException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void listAllFinishedSales() throws DBException {
        List<Sale> sales = saleRepository.getSales();
        table = (DefaultTableModel) finalSaleTable.getModel();
        table.setRowCount(0);

        try {
            salesListToObjectArray(sales);

            TableUtils.setUpTableStyle(finalSaleTable, table);
        } catch (DBException ex) {
            throw ex;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        printPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        finalSaleTable = new javax.swing.JTable();
        lblCustomer = new javax.swing.JLabel();
        lblCustomerName = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblTotalContainer = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        lblSale = new javax.swing.JLabel();
        lblSaleNo = new javax.swing.JLabel();
        lblCompanyOwner = new javax.swing.JLabel();
        lblCompanyMainData = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        printPanel.setBackground(new java.awt.Color(255, 255, 255));

        Font headerFont = new Font("Montserrat", Font.BOLD, 14);
        finalSaleTable.setRowHeight(30);
        finalSaleTable.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        JTableHeader finalSaleHeader = finalSaleTable.getTableHeader();

        finalSaleHeader.setFont(headerFont);
        finalSaleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Producto", "Precio unitario", "Cantidad", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(finalSaleTable);
        if (finalSaleTable.getColumnModel().getColumnCount() > 0) {
            finalSaleTable.getColumnModel().getColumn(2).setMinWidth(80);
            finalSaleTable.getColumnModel().getColumn(2).setPreferredWidth(80);
            finalSaleTable.getColumnModel().getColumn(2).setMaxWidth(80);
            finalSaleTable.getColumnModel().getColumn(3).setMinWidth(80);
            finalSaleTable.getColumnModel().getColumn(3).setPreferredWidth(80);
            finalSaleTable.getColumnModel().getColumn(3).setMaxWidth(80);
        }

        lblCustomer.setFont(new java.awt.Font("Montserrat SemiBold", 0, 12)); // NOI18N
        lblCustomer.setForeground(new java.awt.Color(0, 0, 0));
        lblCustomer.setText("Cliente:");

        lblCustomerName.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        lblTotal.setFont(new java.awt.Font("Montserrat SemiBold", 0, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(0, 0, 0));
        lblTotal.setText("Total:");

        lblTotalContainer.setFont(new java.awt.Font("Montserrat", 0, 14)); // NOI18N
        lblTotalContainer.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalContainer.setText("----");

        btnSave.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pdf.png"))); // NOI18N
        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        lblSale.setFont(new java.awt.Font("Montserrat SemiBold", 0, 12)); // NOI18N
        lblSale.setForeground(new java.awt.Color(0, 0, 0));
        lblSale.setText("Venta #");

        lblSaleNo.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N

        lblCompanyOwner.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        lblCompanyOwner.setForeground(new java.awt.Color(0, 0, 0));

        lblCompanyMainData.setFont(new java.awt.Font("Montserrat SemiBold", 0, 14)); // NOI18N
        lblCompanyMainData.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout printPanelLayout = new javax.swing.GroupLayout(printPanel);
        printPanel.setLayout(printPanelLayout);
        printPanelLayout.setHorizontalGroup(
            printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(printPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(printPanelLayout.createSequentialGroup()
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(printPanelLayout.createSequentialGroup()
                        .addComponent(lblCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(printPanelLayout.createSequentialGroup()
                        .addComponent(lblSale, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSaleNo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCompanyOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(printPanelLayout.createSequentialGroup()
                    .addGap(161, 161, 161)
                    .addComponent(lblCompanyMainData, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(180, Short.MAX_VALUE)))
        );
        printPanelLayout.setVerticalGroup(
            printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, printPanelLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(lblCompanyOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSaleNo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSale, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
            .addGroup(printPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(printPanelLayout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(lblCompanyMainData, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(684, Short.MAX_VALUE)))
        );

        getContentPane().add(printPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 570, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        Toolkit toolkit = printPanel.getToolkit();
        PrintJob printJob = toolkit.getPrintJob(this, null, null);
        Graphics graphics = printJob.getGraphics();

        printPanel.print(graphics);
        graphics.dispose();
        printJob.end();
    }//GEN-LAST:event_btnSaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnSave;
    public javax.swing.JTable finalSaleTable;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lblCompanyMainData;
    public javax.swing.JLabel lblCompanyOwner;
    public javax.swing.JLabel lblCustomer;
    public javax.swing.JLabel lblCustomerName;
    public javax.swing.JLabel lblSale;
    public javax.swing.JLabel lblSaleNo;
    public javax.swing.JLabel lblTotal;
    public javax.swing.JLabel lblTotalContainer;
    private javax.swing.JPanel printPanel;
    // End of variables declaration//GEN-END:variables
}
