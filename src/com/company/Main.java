package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static String IP;
    private static int PORT;
    private static ServerQueue connections;

    public static String getIp() throws Exception {
        //uses aws ip return
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            //gets return
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    //closes socket
                    in.close();
                } catch (IOException e) {
                    //sends error if there is a problem with socket
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        try{
            IP = getIp();
            PORT = Integer.parseInt(args[0]);
            System.out.println("your public ip is " + IP + ":" + PORT);
        }
        catch (Exception e){
            System.out.println("check internet connection");
        }
        //starts Socket
        try{

            ServerSocket connection = new ServerSocket(PORT);
            connections = new ServerQueue();
            ExecutorService threadPool = Executors.newCachedThreadPool();
            while (true){
                try
                    {

                        Socket client = connection.accept();
                        System.out.println("New user connected " + client.getInetAddress().getHostAddress());
                        Handler NewClient = new Handler(client, connections);
                        threadPool.submit(NewClient);

                        //if you want to die instantly don't uncheck this
                        //break;
                } catch (Exception e){
                    System.out.println("check your connection properties and ports + \n" + e);
                }
            }
        } catch (Exception e){
            System.out.println("check your connection properties and ports + \n" + e);
        }

    }
}
