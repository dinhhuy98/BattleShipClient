/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import javax.swing.JTextArea;

/**
 *
 * @author DELL
 */
public class ScoreBoard extends JTextArea {
    public int score;
    public int myShipBroken;
    public int shipDestroyed;
    public String namePlayer;
    public ScoreBoard(){
        super();
    }
    public void init(String name, int myShipBroken,int shipDestroyed, int score ){
        this.namePlayer=name;
        this.myShipBroken=myShipBroken;
        this.shipDestroyed=shipDestroyed;
        this.score=score;
    }
  
    public void updateBoard(){
        this.setText("");
        this.append("Player: "+this.namePlayer+"\n");
        this.append("Số tàu địch đã phá hủy: "+this.shipDestroyed+"\n");
        this.append("Số tàu mình bị chìm: "+this.myShipBroken+"\n");
        this.append("Điểm số: "+this.score);
    
        
    
}   

   
    
}
