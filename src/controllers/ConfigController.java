package controllers;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import views.AdminPanel;

public class ConfigController implements MouseListener{
    
    private AdminPanel adminView;

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
    
    private final Color hoverColor = new Color(255, 51, 51);
    private final Color noHoverColor = new Color(102, 255, 153);

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e){
        
        if(e.getSource() == adminView.lblCategories){
            setBg(adminView.lblCategories, hoverColor);
            } else if(e.getSource() == adminView.lblCustomers){
            setBg(adminView.lblCustomers, hoverColor);
            }
            else if(e.getSource() == adminView.lblConfiguration){
            setBg(adminView.lblConfiguration, hoverColor);
            }
            else if(e.getSource() == adminView.lblNewBuy){
            setBg(adminView.lblNewBuy, hoverColor);
            }
            else if(e.getSource() == adminView.lblNewSale){
            setBg(adminView.lblNewSale, hoverColor);
            }
            else if(e.getSource() == adminView.lblSuppliers){
            setBg(adminView.lblSuppliers, hoverColor);
            }
            else if(e.getSource() == adminView.lblUsers){
            setBg(adminView.lblUsers, hoverColor);
            }
            else {
            setBg(adminView.lblProducts, hoverColor);
            }
    }

    @Override
    public void mouseExited(MouseEvent e) {
       if(e.getSource() == adminView.lblCategories){
            setBg(adminView.lblCategories, noHoverColor);
            } else if(e.getSource() == adminView.lblCustomers){
            setBg(adminView.lblCustomers, noHoverColor);
            }
            else if(e.getSource() == adminView.lblConfiguration){
            setBg(adminView.lblConfiguration, noHoverColor);
            }
            else if(e.getSource() == adminView.lblNewBuy){
            setBg(adminView.lblNewBuy, noHoverColor);
            }
            else if(e.getSource() == adminView.lblNewSale){
            setBg(adminView.lblNewSale, noHoverColor);
            }
            else if(e.getSource() == adminView.lblSuppliers){
            setBg(adminView.lblSuppliers, noHoverColor);
            }
            else if(e.getSource() == adminView.lblUsers){
            setBg(adminView.lblUsers, noHoverColor);
            }
            else {
            setBg(adminView.lblProducts, noHoverColor);
            }
    }
    
    public void setBg(JLabel jLabel, Color color){
        jLabel.setOpaque(true);
        jLabel.setBackground(color);
    }
    
}
