package TowDef.network;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Vector;

public class Server extends Application {
    private static ServerGUI serverGUI;
    private static ServerThread serverThread;
    public static ServerThread getServerThread() {
        return serverThread;
    }

    public static void main(String[] args) {
        serverThread = new ServerThread();
        launch(args);
    }

    public void start(Stage primaryStage) {
        serverGUI = new ServerGUI(primaryStage,this);
        serverGUI.showServerOptions();
        primaryStage.show();
    }
}
class ServerThread implements Runnable{
    private static ServerSocket serverSocket = null;
    private Socket socket = null;
    private int maxClientsCount = 10;
    private int mapNumber;
    private clientThread[] threads;

    private int portNumber = 1375;
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setMaxClientsCount(int maxClientsCount) {
        this.maxClientsCount = maxClientsCount;
        threads = new clientThread[maxClientsCount];

    }




    public void stop(){
        try {
            for(clientThread thread:
                    threads){
                if(thread != null)
                    thread.stop();
            }
            if(serverSocket != null)
                serverSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(socket, threads,this)).start();
                        PrintStream os = new PrintStream(socket.getOutputStream());
                        os.println("///mapNumber :" + mapNumber);
                        os.println("///maxNumClients :" + maxClientsCount);
                     //   os.close();

                        break;
                    }
                }
                if (i == maxClientsCount - 1) {
                    for (int j = 0; j < maxClientsCount; j++) {
                        if(threads[j]!=null ) {
                            while (threads[j].writer == null) {

                                System.out.println("sex");
                                continue;
                            }
                            threads[j].writer.println("startTheGame");
                        }
                    }
                } else if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(socket.getOutputStream());
                    os.println("Server too busy. Try later.\n");
                    os.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class clientThread extends Thread {
    DataInputStream inputStream = null;
    OutputStream outputStream =null;
    PrintWriter writer;
    Formatter formatter = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;
    private static int clientCount = 0;
    private int clientNumber;
    private ServerThread serverThread;
    private String name;
    private String color;
    private int score;
    private Vector<String> messages;


    public clientThread(Socket clientSocket, clientThread[] threads, ServerThread serverThread) {
        this.serverThread = serverThread;
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        clientNumber = clientCount++;

        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = clientSocket.getOutputStream();
            writer = new PrintWriter(outputStream, true);
            writer.println("check");
//            writer.println("Enter your name.");
//            String name = inputStream.readLine().trim();
//            writer.println("Hello " + name
//                    + " to our chat room.\nTo leave enter /quit in a new line");
//            for (int i = 0; i < maxClientsCount; i++) {
//                if (threads[i] != null && threads[i] != this) {
//                    threads[i].writer.println("new user : " + name
//                            + " entered the chat room !!!");
//                }
//            }
            while (true) {
                String line = inputStream.readLine();
                if(line != null) {
                    System.out.println(line);
                    if (line.startsWith("/quit")) {
                        break;
                    } if(line.startsWith("///name///")){
                        name  = line.replace("///name///","");
                    } else if(line.startsWith("///color///")){
                        color = line.replace("///color///","");
                    }  else if(line.startsWith("///score///")){
                        score = Integer.parseInt(line.replace("///score///",""));
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null) {
                                threads[i].writer.println("///score///"+name+"///"+clientNumber+"///"+line);
                            }
                        }
                    } else if(line.startsWith("///public///")){
                     //   messages.add(line);
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null) {
                                threads[i].writer.println("***"+name+"///"+color+"///"+line.replace("///public///","")+"///"+ Calendar.getInstance());
                            }
                        }
                    } else if(line.startsWith("$$$")){
                        line = line.replace("$$$","");
                        String chatWith = line.substring(0,line.indexOf("///"));
                        System.out.println(chatWith);
                      //  messages.add( "line");
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i].name.equals(chatWith)) {
                                threads[i].writer.println("$$$"+"private"+"///"+name+"///"+color+"///"+line.replace(chatWith+"///",""));
                                break;
                            }
                        }
                    }

//                    else if( line.startsWith("mapNumber: ") || line.startsWith("maxClientsNumber: ") ){
//                        for (int i = 0; i < maxClientsCount; i++) {
//                            if (threads[i] != null) {
//                                threads[i].writer.println(line);
//                            }
//                        }
//                    }

                }
            }
//            for (int i = 0; i < maxClientsCount; i++) {
//                if (threads[i] != null && threads[i] != this) {
//                    threads[i].writer.println("The user :" + i
//                            + " is leaving the chat room !!!");
//                }
//            }
//            writer.println("Bye " + name );

            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}