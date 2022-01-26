package com.company;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class GetRecv implements Runnable {
    private final BufferedReader out;
    private final PrintWriter in;
    public String result = "";

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


    public  GetRecv(PrintWriter in, BufferedReader out){
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        while (true){
            try {
                String hold = Recv(out);
                if (!hold.isEmpty()) {
                    result = hold;
                }
            }
            catch (Exception e){
                result = "end";
                break;
            }
        }
    }
}
