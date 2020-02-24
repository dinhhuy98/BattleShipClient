/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 *
 * @author DELL
 */
public class ChatSession implements Runnable{
    private Socket player;
    private JTextArea view;
    private JButton sendB;
    private JTextField messTF;
    private String user;

    public ChatSession(Socket player, JTextArea view, JButton sendB, JTextField messTF, String user) {
        this.player = player;
        this.view = view;
        this.sendB = sendB;
        this.messTF = messTF;
        this.user=user;
        view.setEditable(false);
    }

   

   
    @Override
    public void run() {
        try {
           DataInputStream fromServer = new DataInputStream(player.getInputStream());
           DataOutputStream toServer = new DataOutputStream(player.getOutputStream());
           
           view.append(fromServer.readUTF()+"\n");
            System.out.println("start chat");
            sendB.addActionListener(new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                   try {
                       System.out.println("send");
                       toServer.writeUTF("["+user+"]: "+messTF.getText());
                       
                       view.append("Toi: "+messTF.getText()+"\n");
                       messTF.setText("");
                   } catch (IOException ex) {
                       Logger.getLogger(ChatSession.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }
           });
            
            new Thread(()->{
                try {
                    while(true){
                    String mess = fromServer.readUTF();
                    view.append(mess+"\n");
                    
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
            
        } catch (IOException ex) {
            Logger.getLogger(ChatSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
