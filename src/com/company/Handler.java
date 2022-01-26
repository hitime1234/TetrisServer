package com.company;



import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable {


    private Socket client;
    private ServerQueue ListConn;
    private CSVHandling Csv;
    private ServerQueue queue;
    public boolean Inuse = false;
    public PrintWriter writerToClient;
    public BufferedReader in;
    public String password;
    public String username;


    public Handler(Socket connection, ServerQueue connections){
        this.client = connection;
        this.ListConn = connections;
    }



    public static String Recv(BufferedReader in){
        String userOut =null;
        try {
            while ((userOut = in.readLine()) != null)       //Waits for response
            {
                //System.out.println("Server says: " + userOut);  //Prints response
                break;
            }
        } catch (Exception e){
            System.out.println(e);
        }


        return userOut.replace(";::;","\n");
    }






    public static void send(PrintWriter writer,String data){
        writer.println(data.replace("\n",";::;"));
        writer.flush();
    }
    public void Cleanup(int i){
        ListConn.getConnections().remove(i);
    }


    public static String RecvAll(BufferedReader in){
        String userOut =null;
        try {
            while ((userOut = in.readLine()) != null)       //Waits for response
            {
                System.out.println("Server says: " + userOut);  //Prints response
            }
        } catch (Exception e){
            System.out.println(e);
        }
        return userOut;
    }

    public void match(){
        Handler Partner = null;
        boolean found =false;
        int type = 0;
        if (ListConn.getConnections().size() > 0) {
            while (found == false) {
                for (int i = 0; i < ListConn.getConnections().size(); i++) {
                    if (ListConn.getConnections().get(i).Inuse == false) {
                        try {
                            Inuse = true;
                            Partner = ListConn.getConnections().get(i);
                            Partner.Inuse = true;
                            send(Partner.writerToClient, "alive?");
                            String hold = "";
                            while (hold == "") {
                                hold = (Recv(Partner.in));
                                send(writerToClient,"alive?");
                            }
                            if (hold.equals("yes")) {
                                send(writerToClient,"done");
                                Cleanup(i);
                                break;
                            } else {
                                throw new Exception("Connection failed");
                            }
                        } catch (Exception e) {
                            System.err.println(e);
                            Inuse = false;
                            Partner.Inuse = true;
                            Cleanup(i);
                        }
                    }
                }
                if (Partner != null) {
                    found = true;
                }
                else{
                    send(writerToClient,"loop");
                }
            }
        }



        else{
            ListConn.AddConnections(this);
            send(writerToClient,"waiting");
            if (Recv(in).equals("yes")){
                found = true;
                type = 1;

            }
        }
        if (type == 0 && found){
            send(Partner.writerToClient, "done");
            send(Partner.writerToClient, "READY");
            send(writerToClient,"READY");
            Partner.Csv.CheckAuth(Partner.username,Partner.password);
            Csv.CheckAuth(username,password);
            p2pMode(in,writerToClient,Partner);
        }
        else if(type == 1 && found){
            //end here
        }
        else{
            //match();
        }
    }



    public void p2pMode(BufferedReader in,PrintWriter writer,Handler partner) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        GetRecv Get = new GetRecv(writer, in);
        GetRecv PartnerGet = new GetRecv(partner.writerToClient, partner.in);
        p2pHandling client1 = new p2pHandling(Get, writer, partner,Csv);
        p2pHandling client2 = new p2pHandling(PartnerGet, partner.writerToClient, this, partner.Csv);
        p2pHandling client3 = new p2pHandling(Get, writer, partner,Csv);
        p2pHandling client4 = new p2pHandling(PartnerGet, partner.writerToClient, this,partner.Csv);
        threadPool.submit(Get);
        threadPool.submit(PartnerGet);
        threadPool.submit(client1);
        threadPool.submit(client2);
        threadPool.submit(client3);
        threadPool.submit(client4);

        while (true) {
            if (client1.StopCode == false || client2.StopCode == false || client3.StopCode == false || client4.StopCode == false) {
                threadPool.shutdown();
                if (threadPool.isShutdown()) {
                    break;
                }
            }
        }



        /*
        while (true) {
            String hold = "";
           hold = PartnerGet.result;

            if (hold.isEmpty() || hold == null) {
                send(partner.writerToClient, "data?");
            }
            else if (hold.equals("end")){
                send(writerToClient, "end");
                send(writer, "end");
                System.out.println("Connection closed");
                threadPool.shutdown();
                break;
            }
            else if (hold.equals("yes")){
                send(partner.writerToClient,"yes");
            }
            else {
                send(writer, hold);
            }

            hold = Get.result;

            if (hold.isEmpty()) {
                send(writer, "data?");
            }
            else if (hold.equals("end")){
                send(partner.writerToClient, "end");
                send(writer, "end");
                System.out.println("Connection closed");
                threadPool.shutdown();
                break;
            }
            else if (hold.equals("yes")){
                send(writer,"yes");
            }
            else {
                send(partner.writerToClient, hold);
            }
        }

         */



    }



    public boolean auth(BufferedReader in, PrintWriter writer, int Typer){
        username = Recv(in);
        password = Recv(in);
        String type = Recv(in);
        //send(writer,username + ":" + password);
        Csv.read();
        if ( (username.trim().length() == 0) || (password.trim().length() == 0)){
            send(writer, "Error Couldn't be added");
            try {
                in.close();
                writer.close();
                client.close();
                System.out.println("auth closed due to wrong details");
            } catch (Exception e) {
                System.out.println("auth failed " + e);
            }
            return false;
        }
        else {
            if (type.equals("OLD")) {
                if (Csv.CheckAuth(username, password)) {
                    if (Typer != 1) {
                        send(writer, "hello " + username + "\nwelcome back");
                        match();
                    } else{
                        send(writer,Csv.array.get(Csv.id)[0] + "," + Csv.array.get(Csv.id)[1] + "," + Csv.array.get(Csv.id)[2] + "," + Csv.array.get(Csv.id)[3] + "," + Csv.array.get(Csv.id)[4] + "," + Csv.array.get(Csv.id)[5] + ",");
                        try {
                            in.close();
                            writer.close();
                            client.close();
                            System.out.println("closed because request finished");
                            return true;
                        } catch (Exception e){

                        }
                    }
                    return true;
                } else {
                    send(writer, "Die SCUM");
                    try {
                        in.close();
                        writer.close();
                        client.close();
                        System.out.println("auth closed due to wrong details");
                    } catch (Exception e) {
                        System.out.println("auth failed " + e);
                    }
                    return false;
                }
            } else {
                if (Csv.ExistingAC(username, password)) {
                    send(writer, "Already exists");
                } else {
                    Csv.setUser(username);
                    Csv.setPass(password);
                    Csv.setId(0);
                    Csv.setRank(0);
                    Csv.setScore(0);
                    Csv.setTime(0);
                    Csv.setNumberWins(0);
                    if (Csv.append() == true) {
                        send(writer, "Account Created");
                        //to get details in memory
                        Csv.CheckAuth(username, password);
                    } else {
                        send(writer, "Error Couldn't be added");
                        try {
                            in.close();
                            writer.close();
                            client.close();
                            System.out.println("auth closed due to wrong details");
                        } catch (Exception e) {
                            System.out.println("auth failed " + e);
                        }
                        return false;
                    }
                }
                return true;
            }
        }
    }


    @Override
    public void run() {
        this.queue = queue;
        try {
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.writerToClient = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Csv = new CSVHandling("DataBase.csv",1,username, password);
        int Type = 0;
        String hold = Recv(in);
        if (hold.equals("GetRank")){
            send(writerToClient,"Getting");
            Type = 1;
        }
        else {
            send(writerToClient, "auth Required");
        }
        auth(in,writerToClient,Type);

    }
}
