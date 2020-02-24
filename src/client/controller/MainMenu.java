/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;


import client.view.CreateRoomView;
import client.view.GameView;
import client.view.GuideView;
import client.view.HighScoreView;
import client.view.ListRoomView;
import client.view.MainView;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.BattleShipConst;
import model.Room;
import model.User;

/**
 *
 * @author DELL
 */
public class MainMenu implements Runnable,BattleShipConst {
    private CreateRoomView createRoomView;
    private MainView mainView;
    private GameView gameView;
    private ListRoomView listRoomView;
    private HighScoreView highScoreView;
    private GuideView guideView;
    private Socket socket;
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    private static ObjectInputStream fromServerObject;
    private static ObjectOutputStream toServerObject;
    private User user;
    private static ArrayList<Room> listRoom;
    public MainMenu(Socket socket, DataInputStream fromServer, DataOutputStream toServer) throws IOException, ClassNotFoundException {
        this.socket = socket;
        this.fromServer = fromServer;
        this.toServer = toServer;
        toServerObject = new ObjectOutputStream(socket.getOutputStream());
        fromServerObject = new ObjectInputStream(socket.getInputStream());
        this.user=(User)fromServerObject.readObject();
    }

    
    @Override
    public void run() {
        mainView = new MainView();
        mainView.setVisible(true);
        mainView.getUserL().setText("User: "+this.user.getUsername());
        mainView.getCreateRoomB().addActionListener(clickMenu(CREATE_ROOM));
        mainView.getListRoomB().addActionListener(clickMenu(LIST_ROOM));
        mainView.getHighSocerB().addActionListener(clickMenu(HIGHSCORE));
        mainView.getGuideB().addActionListener(clickMenu(GUIDE));
    }

      public ActionListener clickMenu(int choose){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("click"+choose);
                    toServer.writeInt(choose);
                    if(choose==CREATE_ROOM){
                        createRoomView = new CreateRoomView();
                        createRoomView.setVisible(true);
                        createRoomView.getBackB().addActionListener(back(createRoomView));
                        createRoomView.getCreateRoomB().addActionListener(createRoom());
                    }
                    else if(choose==LIST_ROOM){
                        listRoomView = new ListRoomView();
                        listRoomView.setVisible(true);
                        listRoom = (ArrayList)fromServerObject.readObject();
                        listRoomView.loadListRoom(listRoom);
                        listRoomView.getBackB().addActionListener(back(listRoomView));
                        listRoomView.getIntoRoomB().addActionListener(intoRoom());
                    }
                    else if(choose==HIGHSCORE){
                        ArrayList<User> listUser = (ArrayList<User>)fromServerObject.readObject();
                        highScoreView = new HighScoreView();
                        highScoreView.setVisible(true);
                        highScoreView.getBackB().addActionListener(back(highScoreView));
                        int i=1;
                        for(User u:listUser){
                            highScoreView.getHighScoreTA().append(i+". "+u.getUsername()+"-"+u.getScore()+" Điểm\n");
                            i++;
                        }
                    }
                    else if(choose==GUIDE){
                        guideView = new GuideView();
                        guideView.setVisible(true);
                        guideView.getBackB().addActionListener(back(guideView));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return p;
    }
    
      public ActionListener back(JFrame view){
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
     
     public ActionListener createRoom(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String roomName = createRoomView.getNameRoomTF().getText();
                    //System.out.println(roomName);
                    if(!roomName.equals("")){
                        
                        toServer.writeInt(CREATE_ROOM);
                        toServer.writeUTF(roomName);
                        int id_room = fromServer.readInt();
                        System.out.println("nhan id_room"+id_room);
                        gameView = new GameView();
                        createRoomView.setVisible(false);
                      
                        new Thread(new GamePlay(gameView,socket,PLAYER1,user)).start();
                        createChatSession(gameView.getChatTA(),gameView.getSendB(),gameView.getMessageTF(),id_room);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Chua nhap ten phong",null,JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        return p;
    }
      public ActionListener intoRoom(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("kkkk");
                try {
                    toServer.writeInt(INTO_ROOM);
                    int index=listRoomView.getListRoomL().getSelectedIndex();
                    System.out.println(index);
                    Room b=(Room)listRoomView.getListRoomL().getModel().getElementAt(index);
                    if(b.isFull()){
                        JOptionPane.showMessageDialog(null, "Phong da day!",null,JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                    System.out.println(b);
                    toServerObject.writeObject(b);
                    gameView = new GameView();
                    listRoomView.setVisible(false);
                    gameView.getBackB().addActionListener(back(gameView));
                     joinChatSession(gameView.getChatTA(),gameView.getSendB(),gameView.getMessageTF(),b.getId());
                    new Thread(new GamePlay(gameView,socket,PLAYER2,user)).start();
                    }
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                
            }
        };
        return p;
    }
      
    public void createChatSession(JTextArea view,JButton sendB, JTextField messTF,int id) throws IOException{
        Socket socket2 = new Socket("localhost", 2019);
        DataInputStream fromServer2  = new DataInputStream(socket2.getInputStream());
        DataOutputStream toServer2 = new DataOutputStream(socket2.getOutputStream());
        toServer2.writeInt(CHAT);
        toServer2.writeInt(NEW_CHAT);
        toServer2.writeInt(id);
        new Thread(new ChatSession(socket2,view,sendB,messTF,user.getUsername())).start();
        
        
    }
    public void joinChatSession(JTextArea view,JButton sendB ,JTextField messTF,int id) throws IOException{
        Socket socket2 = new Socket("localhost", 2019);
        DataInputStream fromServer2  = new DataInputStream(socket2.getInputStream());
        DataOutputStream toServer2 = new DataOutputStream(socket2.getOutputStream());
        toServer2.writeInt(CHAT);
        toServer2.writeInt(JOIN_CHAT);
        toServer2.writeInt(id);
        new Thread(new ChatSession(socket2,view,sendB,messTF,user.getUsername())).start();
    }

   
}
