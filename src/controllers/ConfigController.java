package controllers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import views.AdminPanel;

public class ConfigController implements MouseListener {

    private final AdminPanel adminView;

    public ConfigController(AdminPanel adminView) {
        this.adminView = adminView;
        this.adminView.lblNewSale.addMouseListener(this);
        this.adminView.lblNewBuy.addMouseListener(this);
        this.adminView.lblProducts.addMouseListener(this);
        this.adminView.lblSuppliers.addMouseListener(this);
        this.adminView.lblCategories.addMouseListener(this);
        this.adminView.lblConfiguration.addMouseListener(this);
        this.adminView.lblUsers.addMouseListener(this);
        this.adminView.lblCustomers.addMouseListener(this);
    }

    private final Color defaultColor = new Color(102, 255, 153);
    private final Color hoverColor = new Color(71, 174, 105);

    @Override
    public void mouseEntered(MouseEvent e) {

        if (e.getSource() == adminView.lblCategories) {
            setBg(adminView.lblCategories, hoverColor);
        } else if (e.getSource() == adminView.lblCustomers) {
            setBg(adminView.lblCustomers, hoverColor);
        } else if (e.getSource() == adminView.lblConfiguration) {
            setBg(adminView.lblConfiguration, hoverColor);
        } else if (e.getSource() == adminView.lblNewBuy) {
            setBg(adminView.lblNewBuy, hoverColor);
        } else if (e.getSource() == adminView.lblNewSale) {
            setBg(adminView.lblNewSale, hoverColor);
        } else if (e.getSource() == adminView.lblSuppliers) {
            setBg(adminView.lblSuppliers, hoverColor);
        } else if (e.getSource() == adminView.lblUsers) {
            setBg(adminView.lblUsers, hoverColor);
        } else {
            setBg(adminView.lblProducts, hoverColor);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == adminView.lblCategories) {
            setBg(adminView.lblCategories, defaultColor);
        } else if (e.getSource() == adminView.lblCustomers) {
            setBg(adminView.lblCustomers, defaultColor);
        } else if (e.getSource() == adminView.lblConfiguration) {
            setBg(adminView.lblConfiguration, defaultColor);
        } else if (e.getSource() == adminView.lblNewBuy) {
            setBg(adminView.lblNewBuy, defaultColor);
        } else if (e.getSource() == adminView.lblNewSale) {
            setBg(adminView.lblNewSale, defaultColor);
        } else if (e.getSource() == adminView.lblSuppliers) {
            setBg(adminView.lblSuppliers, defaultColor);
        } else if (e.getSource() == adminView.lblUsers) {
            setBg(adminView.lblUsers, defaultColor);
        } else {
            setBg(adminView.lblProducts, defaultColor);
        }
    }

    public void setBg(JLabel jLabel, Color color) {
        jLabel.setOpaque(true);
        jLabel.setBackground(color);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == adminView.lblProducts){
            adminView.jTabs.setSelectedIndex(0);
        }
        else if (e.getSource() == adminView.lblCategories){
            adminView.jTabs.setSelectedIndex(1);
        }
         else if (e.getSource() == adminView.lblCustomers){
            adminView.jTabs.setSelectedIndex(2);
        }
        else if (e.getSource() == adminView.lblSuppliers){
            adminView.jTabs.setSelectedIndex(3);
        }
        else if (e.getSource() == adminView.lblUsers){
            adminView.jTabs.setSelectedIndex(4);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
