package TowDef.network;

import TowDef.GUI.GUI;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;

public class Client{
    private Socket socket = null;
    private OutputStream outputStream = null;
    private Formatter formatter = null;
    private int mapNumber;
    private int maxClientsNum;
    private int[] scores;
    private String[] names;

    public int getScore(String name){
        for (int i = 0; i <maxClientsNum; i++) {
            if(names.equals(name))
                return scores[i];
        }
        return -1;
    }
    public void setMoney(SimpleIntegerProperty money){
        money.addListener(event -> {
            sendMessage("///money///"+money.getValue());
        });
    }

    public void setMaxClientsNum(int maxClientsNum) {
        this.maxClientsNum = maxClientsNum;
        scores = new int[maxClientsNum];
        names = new String[maxClientsNum];
    }

    public void setScores(int score, int i) {
        this.scores[i] = score;
    }

    public void setNames(String name, int i) {
        this.names[i] = name;
    }

    Thread thread;


    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public Client(int portNumber, String IpAdress){
        try {
            socket = new Socket(IpAdress, portNumber);
            outputStream = socket.getOutputStream();
            formatter = new Formatter(outputStream);


            runClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runClient(){
        if (socket!= null && outputStream!=null) {
            thread = new Thread(new ClientHelperThread(socket,this));
            thread.start();
        }
    }

    public void sendMessage(String text){
        formatter.format(text+"\n");
        formatter.flush();
    }

    public void close(){
        try {
            if(formatter != null) {
                formatter.format("/quit\n");
                formatter.flush();
                formatter.close();
            }
            if(thread != null)
                thread.stop();
            if(socket!= null)
                socket.close();
            if(outputStream != null)
                outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class ClientHelperThread implements Runnable {
    private Socket socket = null;
    private Client client = null;
    public ClientHelperThread(Socket socket,Client client){
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run(){
//        String next;
//        while ((next = scanner.next()) != null){
//
//            formatter.format(next+"\n");
//            formatter.flush();
//            if(next.contains("exit"))
//                break;
//
//        }
//        formatter.close();
//        scanner.close();
//        client.closed = true;
        try{
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            while (true) {
                String inputMessage = reader.readLine();
                if(inputMessage!=null) {
                    System.out.println(inputMessage);
                    if (inputMessage.startsWith("startTheGame")) {
                        GUI.getInstance().getMainMenuGraphics().startMultiPlayerGame(client.getMapNumber());
                    } else if (inputMessage.startsWith("///mapNumber: ")) {
                        client.setMapNumber(Integer.parseInt(inputMessage.replace("///mapNumber: ", "")));
                    } else if (inputMessage.startsWith("///maxNumClients: ")) {
                        client.setMaxClientsNum(Integer.parseInt(inputMessage.replace("///maxNumClients: ", "")));
                    }else if( inputMessage.startsWith("$$$private")){

                    } else if(inputMessage.startsWith("***")){
                        inputMessage = inputMessage.replace("***","");
                        String senderName = inputMessage.substring(0,inputMessage.indexOf("///"));
                        inputMessage = inputMessage.replace(senderName+"///","");
                        String senderColor = inputMessage.substring(0,inputMessage.indexOf("///"));
                        inputMessage = inputMessage.replace(senderColor+"///","");
                        String msgText = inputMessage.substring(0,inputMessage.indexOf("///"));
                        final String time = inputMessage.replace(msgText+"///","");
                        Platform.runLater(()->{GUI.getInstance().getGameGraphics().getChatGraphics().addMsg(senderName,senderColor,msgText,time);});

                    }else if(inputMessage.startsWith("///score///")){
                        inputMessage = inputMessage.replace("***","");
                        String senderName = inputMessage.substring(0,inputMessage.indexOf("///"));
                        inputMessage = inputMessage.replace(senderName+"///","");
                        String senderNumber = inputMessage.substring(0,inputMessage.indexOf("///"));
                        inputMessage = inputMessage.replace(senderNumber+"///","");
                        String score = inputMessage.substring(0,inputMessage.indexOf("///"));
                        client.setScores(Integer.parseInt(score), Integer.parseInt(senderNumber));
                        client.setNames(senderName, Integer.parseInt(senderNumber));


                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
            e.printStackTrace();
        }
    }
}