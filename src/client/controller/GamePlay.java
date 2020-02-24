/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.view.SButton;
import client.view.GameView;
import java.awt.Color;
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
import javax.swing.JOptionPane;
import model.BattleShipConst;
import model.Room;
import model.User;

/**
 *
 * @author DELL
 */
public class GamePlay implements Runnable, BattleShipConst {
    private GameView gameView;
    private Socket socket;
    private int player;
    private Room room;
    private User me;
    private User competitor;
    private ArrayList<Integer> locationShip;
    private int k1;
    private int k2;
    private int select;
    private boolean continueToPlay=true;
    private boolean myTurn=false;
    private static DataInputStream fromServer;
    private static DataOutputStream toServer;
    private static ObjectInputStream fromServerOject;
    private static ObjectOutputStream toServerObject;
    private static int winner;
    @Override
    public void run() {
        try {
            gameView.setVisible(true);
            gameView.getBackB().addActionListener(ccc());
            fromServer  = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServerOject = new ObjectInputStream(socket.getInputStream());
            System.out.println("------");
            toServerObject = new ObjectOutputStream(socket.getOutputStream());
            
            room=(Room) fromServerOject.readObject();
            
            gameView.getRoomNameL().setText("Room"+room.getId()+":"+room.getNameRoom());
            System.out.println(room);
            toServerObject.writeObject(me);
            
            if(player==PLAYER1){
                gameView.getNotifyTF().setText("waiting player2...");
                int action = fromServer.readInt();
                System.out.println(action);
                if(action==1){
                    gameView.getNotifyTF().setText(fromServer.readUTF());
                    room=(Room) fromServerOject.readObject();
                }
            }
            else if(player==PLAYER2){
                 gameView.getNotifyTF().setText(fromServer.readUTF());
            }
            competitor=(User)fromServerOject.readObject();
            boolean st=false;
            while(!st){
                gameView.resetBoard(gameView.getBoardButtonP1(), 3, Color.cyan);
                gameView.resetBoard(gameView.getBoardButtonP2(), 4, Color.red);
                
                Thread.sleep(1000);
                System.out.println("oooo");
                System.out.println("first");
                gameView.getNotifyTF().setText(fromServer.readUTF());
                if(player==PLAYER1){
                    gameView.getInfoP1TA().init(me.getUsername(),0,0,me.getScore());
                    gameView.getInfoP1TA().updateBoard();
                    gameView.getInfoP2TA().init(competitor.getUsername(),0,0,competitor.getScore());
                    gameView.getInfoP2TA().updateBoard();
                    gameView.getReadyP1B().setEnabled(true);
                    gameView.getReadyP1B().setText("Sẵn sàng");
                    gameView.getReadyP1B().addActionListener(ready());
                }
                else if(player==PLAYER2){
                    gameView.getInfoP2TA().init(me.getUsername(),0,0,me.getScore());
                    gameView.getInfoP2TA().updateBoard();
                    gameView.getInfoP1TA().init(me.getUsername(),0,0,competitor.getScore());
                    gameView.getInfoP1TA().updateBoard();
                    gameView.getReadyP2B().setEnabled(true);
                    gameView.getReadyP2B().setText("Sẵn sàng");
                    gameView.getReadyP2B().addActionListener(ready());
                }
                System.out.println("third");
                int action = fromServer.readInt();
                System.out.println("kkkkkkkkkkkkk");
                if(action==READY){
                    continueToPlay=true;
                    play();
                    System.out.println("finish play");
                    updateScoreToServer();
                    System.out.println("finish update");
                   gameView.removeActionListener();
               
                    System.out.println("finish remove");
                }
            }
           
        } catch (IOException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public GamePlay(GameView gameView, Socket socket, int player, User me) {
        this.gameView = gameView;
        this.socket = socket;
        this.player=player;
        this.me=me;
        
    }
    public void play() throws IOException, InterruptedException{
        gameView.getNotifyTF().setText("Trò chơi bắt đầu!");
                Thread.sleep(2000);
                if(player==PLAYER1)
                    gameView.disableBoard(gameView.getBoardButtonP2());
                else if(player==PLAYER2)
                    gameView.disableBoard(gameView.getBoardButtonP1());
             
                locationShip = new ArrayList<Integer>();
                for(int i=0;i<20;i++){
                    gameView.getBoardButtonP1()[i].addActionListener(putDownShip(gameView.getBoardButtonP1()[i],1));
                    gameView.getBoardButtonP2()[i].addActionListener(putDownShip(gameView.getBoardButtonP2()[i],2));
                }
                int i=15;
                while(i>=0){
                    int x = 5-locationShip.size();
                    gameView.getNotifyTF().setText("Bạn có 15s để chọn 5 vị trí đặt tàu<thiếu "+x+">:"+i+"s...");
                    if(x==0 || i==0){
                        if(player==PLAYER1)
                            gameView.disableBoard(gameView.getBoardButtonP1());
                        else if(player==PLAYER2)
                            gameView.disableBoard(gameView.getBoardButtonP2());
                    }
                    i--;
                    Thread.sleep(1000);
                    
                }
                toServerObject.writeObject(locationShip);
                if(player==PLAYER1)
                    myTurn=true;
                for(int k=0;k<20;k++){
                if(player==PLAYER1)
                    gameView.getBoardButtonP2()[k].addActionListener(selectTarget(gameView.getBoardButtonP2()[k],5));
                else if(player==PLAYER2)
                    gameView.getBoardButtonP1()[k].addActionListener(selectTarget(gameView.getBoardButtonP1()[k],5));
            }
                System.out.println("nnn");
                while(continueToPlay){
                    if(player==PLAYER1){
                        guessShip();
                        checkCorrect();
                        receiveInfoFromServer();
                        gameView.getNotifyTF().setText("Lượt đối thủ");
                    }
                    else if(player==PLAYER2){
                        gameView.getNotifyTF().setText("Lượt đối thủ");
                        receiveMove();
                        receiveInfoFromServer();
                        if(continueToPlay){
                            System.out.println("lllll");
                            guessShip();
                            checkCorrect();
                            receiveInfoFromServer();
                        }
                    }
                    
                    if(player==PLAYER1 && continueToPlay){
                        receiveMove();
                        receiveInfoFromServer();
                        
                    }
                    System.out.println("-----------------------------");
                }
    }
    
    public void updateScoreToServer() throws IOException{
        if(winner==1){
                    gameView.getInfoP1TA().score+=5;
                    int p=5-gameView.getInfoP1TA().myShipBroken;
                    gameView.getInfoP2TA().score-=p;
                    
        }
                else if(winner==2){
                     gameView.getInfoP2TA().score+=5;
                    int p=5-gameView.getInfoP2TA().myShipBroken;
                    gameView.getInfoP1TA().score-=p;
                }
                if(player==PLAYER1){
                    toServer.writeInt(gameView.getInfoP1TA().score);
                    me.setScore(gameView.getInfoP1TA().score);
                    competitor.setScore(gameView.getInfoP2TA().score);
                }
                else if(player==PLAYER2){
                    toServer.writeInt(gameView.getInfoP2TA().score);
                    me.setScore(gameView.getInfoP2TA().score);
                    competitor.setScore(gameView.getInfoP1TA().score);
                }
    }
    
    public void checkCorrect(){
        System.out.println("start check correct");
        try {
            int check1 = fromServer.readInt();
            int check2 = fromServer.readInt();
            System.out.println("nhan check tu server");
            if(check1==CORRECT){
                System.out.println("1correct");
                if(player==PLAYER1){
                    gameView.getBoardButtonP2()[k1].setImage(6);
                    gameView.getInfoP1TA().shipDestroyed++;
                    gameView.getInfoP2TA().myShipBroken++;
                }
                else if(player==PLAYER2){
                    gameView.getBoardButtonP1()[k1].setImage(6);
                    gameView.getInfoP2TA().shipDestroyed++;
                    gameView.getInfoP1TA().myShipBroken++;
                }
            }
            else if(check1==INCORRECT){
                System.out.println("1incorrect");
                if(player==PLAYER1)
                    gameView.getBoardButtonP2()[k1].setVisible(false);
                else if(player==PLAYER2)
                    gameView.getBoardButtonP1()[k1].setVisible(false);
            }
            
            if(check2==CORRECT){
                System.out.println("2correct");
                if(player==PLAYER1){
                    gameView.getBoardButtonP2()[k2].setImage(6);
                    gameView.getInfoP1TA().shipDestroyed++;
                    gameView.getInfoP2TA().myShipBroken++;
                }
                else if(player==PLAYER2){
                    gameView.getBoardButtonP1()[k2].setImage(6);
                    gameView.getInfoP2TA().shipDestroyed++;
                    gameView.getInfoP1TA().myShipBroken++;
                }
            }
            else if(check2==INCORRECT){
                System.out.println("2incorrect");
                if(player==PLAYER1)
                    gameView.getBoardButtonP2()[k2].setVisible(false);
                else if(player==PLAYER2)
                    gameView.getBoardButtonP1()[k2].setVisible(false);
            }
            gameView.getInfoP1TA().updateBoard();
            gameView.getInfoP2TA().updateBoard();
        } catch (IOException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("end checkcorrect");
    }
    
    public void guessShip(){
        System.out.println("start guesship");
        try {
            select=2;
            if(player==PLAYER1)
                gameView.enableBoard(gameView.getBoardButtonP2());
            else if(player==PLAYER2)
                gameView.enableBoard(gameView.getBoardButtonP1());
            
            int i=8;
            while(i>=0){
                
                gameView.getNotifyTF().setText("Lượt bạn: Bạn có 8s để chọn 2 vị trí muốn tấn công trên bàn đối thủ: "+i+"s...");
                if(select==0 || i==0){
                    
                    if(player==PLAYER1)
                        gameView.disableBoard(gameView.getBoardButtonP2());
                    else if(player==PLAYER2)
                        gameView.disableBoard(gameView.getBoardButtonP1());
                }
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
                }
                i--;
                
            }
            toServer.writeInt(k1);                    
            toServer.writeInt(k2);
            System.out.println("gui "+k1+" "+k2 +"toi server");
        } catch (IOException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("end quessship");
    }
    
    public void receiveMove(){
        System.out.println("start receivemove");
        try {
            int x1 = fromServer.readInt();
            int x2 = fromServer.readInt();
            System.out.println("nhan tu server nuoc di cua doi thu"+x1+" "+x2);
            if(locationShip.contains(x1)){
                if(player==PLAYER1){
                    gameView.getBoardButtonP1()[x1].setImage(7);
                    gameView.getInfoP1TA().myShipBroken++;
                    gameView.getInfoP2TA().shipDestroyed++;
                }
                else if(player==PLAYER2){
                    gameView.getBoardButtonP2()[x1].setImage(7);
                    gameView.getInfoP2TA().myShipBroken++;
                    gameView.getInfoP1TA().shipDestroyed++;
                }
                    
            }
            if(locationShip.contains(x2)){
                if(player==PLAYER1){
                    gameView.getBoardButtonP1()[x2].setImage(7);
                    gameView.getInfoP1TA().myShipBroken++;
                    gameView.getInfoP2TA().shipDestroyed++;
                }
                else if(player==PLAYER2){
                    gameView.getBoardButtonP2()[x2].setImage(7);
                    gameView.getInfoP2TA().myShipBroken++;
                    gameView.getInfoP1TA().shipDestroyed++;
                }
                    
            }
            
            gameView.getInfoP1TA().updateBoard();
            gameView.getInfoP2TA().updateBoard();
        } catch (IOException ex) {
            Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("endmove");
    }
    
    private void receiveInfoFromServer() throws IOException{
        System.out.println("start infoserver");
        int status= fromServer.readInt();
        System.out.println("nhan thong tin ket thuc hay tiep tuc");
        if(status==PLAYER1_WON){
            winner=1;
           continueToPlay=false;
           if(player==PLAYER1){
               JOptionPane.showMessageDialog(null, "Chiến thắng!!!",null,JOptionPane.INFORMATION_MESSAGE);
           }
           else if(player==PLAYER2){
               JOptionPane.showMessageDialog(null, "Thất bại!!!",null,JOptionPane.INFORMATION_MESSAGE);
           }
        }
        else if(status==PLAYER2_WON){
            winner=2;
           continueToPlay=false;
           if(player==PLAYER2){
                JOptionPane.showMessageDialog(null, "Chiến thắng!!!",null,JOptionPane.INFORMATION_MESSAGE);
           }
           else if(player==PLAYER1){
               JOptionPane.showMessageDialog(null, "Thất bại!!!",null,JOptionPane.INFORMATION_MESSAGE);
           }
        }
        System.out.println("end infoserver");
    }
    
    public ActionListener ccc(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("im backkkvvvvvvvv");
               
                
            }
        };
        return p;
    }
    
    
    public ActionListener ready(){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("second");
                    toServer.writeInt(READY);
                    gameView.getNotifyTF().setText("Chờ người kia sẵn sàng...");
                    if(player==PLAYER1){
                        gameView.getReadyP1B().setText("Đã sẵn sàng");
                        gameView.getReadyP1B().setEnabled(false);
                    }
                    if(player==PLAYER2){
                        gameView.getReadyP2B().setText("Đã sẵn sàng");
                        gameView.getReadyP2B().setEnabled(false);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GamePlay.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                
            }
        };
        return p;
    }
    public ActionListener putDownShip(SButton b,int type){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(locationShip.size()<5){
                System.out.println(b.getIndex());
                b.setImage(type);
                locationShip.add(b.getIndex());
                b.setEnabled(false);
                }
            }
        };
        return p;
    }
    
    public ActionListener selectTarget(SButton b,int type){
        ActionListener p = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("select"+select);
                if(select>0){
                    b.setImage(type);
                    b.setEnabled(false);
                    if(select==2)
                        k1 = b.getIndex();
                    else if(select==1)
                        k2 = b.getIndex();
                    select--;
                    
                    
                }
            }
        };
        return p;
    }
    
   
}
