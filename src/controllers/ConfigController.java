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
        this.adminView.lblNewPurchase.addMouseListener(this);
        this.adminView.lblSales.addMouseListener(this);
        this.adminView.lblProducts.addMouseListener(this);
        this.adminView.lblSuppliers.addMouseListener(this);
        this.adminView.lblCategories.addMouseListener(this);
        this.adminView.lblHelp.addMouseListener(this);
        this.adminView.lblUsers.addMouseListener(this);
        this.adminView.lblCustomers.addMouseListener(this);
        this.adminView.lblCompany.addMouseListener(this);
    }

    private final Color defaultColor = new Color(71, 174, 105);
    private final Color hoverColor = new Color(102, 255, 153);

    @Override
    public void mouseEntered(MouseEvent e) {
        setAppearance(e.getSource(), defaultColor, hoverColor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setAppearance(e.getSource(), defaultColor, Color.WHITE);
    }

    private void setAppearance(Object source, Color backgroundColor, Color textColor) {
        if (source instanceof JLabel label) {
            label.setOpaque(true);
            label.setBackground(backgroundColor);
            label.setForeground(textColor);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == adminView.lblProducts) {
            adminView.jTabs.setSelectedIndex(0);
        } else if (e.getSource() == adminView.lblCategories) {
            adminView.jTabs.setSelectedIndex(1);
        } else if (e.getSource() == adminView.lblNewSale) {
            adminView.jTabs.setSelectedIndex(2);
        } else if (e.getSource() == adminView.lblSales) {
            adminView.jTabs.setSelectedIndex(3);
        } else if (e.getSource() == adminView.lblCustomers) {
            adminView.jTabs.setSelectedIndex(4);
        } else if (e.getSource() == adminView.lblNewPurchase) {
            adminView.jTabs.setSelectedIndex(5);
        } else if (e.getSource() == adminView.lblSuppliers) {
            adminView.jTabs.setSelectedIndex(6);
        } else if (e.getSource() == adminView.lblUsers) {
            adminView.jTabs.setSelectedIndex(7);
        } else if (e.getSource() == adminView.lblCompany) {
            adminView.jTabs.setSelectedIndex(8);
        } else if (e.getSource() == adminView.lblHelp) {
            adminView.jTabs.setSelectedIndex(9);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
