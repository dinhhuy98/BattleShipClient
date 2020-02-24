/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author DELL
 */
public class SButton extends JButton{
    private int index;
    private int type;
    public SButton(int index){
        super();
        this.index=index;
    }

    public int getIndex() {
        return index;
    }
    public void setImage(int type){
        if(type==2){
            ImageIcon ship2 = new ImageIcon(this.getClass().getResource("../../icon/1m.png"));
            this.setIcon(ship2);
            this.setDisabledIcon(ship2);
            this.type=2;
        }
        else if(type==1){
            ImageIcon ship1 = new ImageIcon(this.getClass().getResource("../../icon/2m.png"));
            this.setIcon(ship1);
            this.setDisabledIcon(ship1);
            this.type=1;
        }
        else if(type==3){
            ImageIcon bg1 = new ImageIcon(this.getClass().getResource("../../icon/cyan.png"));
            this.setIcon(bg1);
            this.setDisabledIcon(bg1);
            this.type=3;
        }
        else if(type==4){
            ImageIcon bg2 = new ImageIcon(this.getClass().getResource("../../icon/red.png"));
            this.setIcon(bg2);
            this.setDisabledIcon(bg2);
            this.type=4;
        }
        else if(type==5){
            ImageIcon cross = new ImageIcon(this.getClass().getResource("../../icon/crosshairm.png"));
            this.setIcon(cross);
            this.setDisabledIcon(cross);
            this.type=5;
        }
        else if(type==6){
            ImageIcon fire = new ImageIcon(this.getClass().getResource("../../icon/firem.png"));
            this.setIcon(fire);
            this.setDisabledIcon(fire);
            this.type=6;
        }
        else if(type==7){
            ImageIcon ship1x = new ImageIcon(this.getClass().getResource("../../icon/1xm.png"));
            this.setIcon(ship1x);
            this.setDisabledIcon(ship1x);
            this.type=7;
        }
        else if(type==8){
            ImageIcon ship2x = new ImageIcon(this.getClass().getResource("../../icon/2xm.png"));
            this.setIcon(ship2x);
            this.setDisabledIcon(ship2x);
            this.type=8;
        }
    }

    public int getType() {
        return type;
    }
    
 
}
