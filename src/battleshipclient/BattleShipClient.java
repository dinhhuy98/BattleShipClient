/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshipclient;
import client.controller.GamePlay;
import client.controller.MainMenu;
import client.view.CreateRoomView;
import client.view.GameView;
import client.view.ListRoomView;
import client.view.LoginView;
import client.view.MainView;
import client.view.RegisterView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.BattleShipConst;
import model.Room;
import model.User;
/**
 *
 * @author DELL
 */
public class BattleShipClient implements BattleShipConst{
    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static LoginView loginView;
    private static MainView mainView;
    private static RegisterView registerView;
    private static Socket socket;
    private static boolean waiting = true;
    public static void main(String[] args) {
        try{
            socket = new Socket("localhost", 2019);
            fromServer  = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
           //login step
            loginView = new LoginView();
            loginView.setVisible(true);
            
            loginView.getLoginB().addActionListener(login());
            loginView.getRegisterB().addActionListener(register());
       
        }catch(IOException e){
            e.printStackTrace();
        }
                   
    }
    public static ActionListener login(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                        toServer.writeInt(LOGIN);
                        toServer.writeUTF(loginView.getUserTF().getText());
                        toServer.writeUTF(loginView.getPassTF().getText());
                        int action = fromServer.readInt();
                        if(action==LOGIN_SUCCESS){
                            loginView.setVisible(false);
                            new Thread(new MainMenu(socket,fromServer,toServer)).start();
                        }
                        else if(action==LOGIN_ERROR){
                            JOptionPane.showMessageDialog(null, "Thong tin dang nhap khong dung!",null,JOptionPane.ERROR_MESSAGE);
                        }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                   ex.printStackTrace();
                }
           
            }
        };
        return p;
    }
    public static ActionListener register(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    toServer.writeInt(REGISTER);
                    registerView = new RegisterView();
                    registerView.setVisible(true);
                    registerView.getBackB().addActionListener(back(registerView));
                    registerView.getRegisterB().addActionListener(submit());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }
        };
        return p;
    }
    
    public static ActionListener back(JFrame view){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    toServer.writeInt(BACK);
                    view.setVisible(false);
                } catch (IOException ex) {
                    Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return p;
    }
    
          public static ActionListener submit(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    toServer.writeInt(REGISTER);
                    toServer.writeUTF(registerView.getUsernameTF().getText());
                    toServer.writeUTF(registerView.getPasswordTF().getText());
                    int action = fromServer.readInt();
                    if(action==REGISTER_SUCCESS){
                        registerView.setVisible(false);
                        JOptionPane.showMessageDialog(null, "Dang ki thanh cong",null,JOptionPane.INFORMATION_MESSAGE);
                    }
                    else if(action==REGISTER_ERROR){
                        JOptionPane.showMessageDialog(null, "Thong tin dang ki khong dung!",null,JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return p;
    }
    
    private static void waitForPlayerAction() throws InterruptedException{
        waiting=true;
        while(waiting){
            Thread.sleep(100);
        }
        waiting=true;
    }
    
}
