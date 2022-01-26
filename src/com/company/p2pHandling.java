package com.company;

import java.io.PrintWriter;

import static com.company.Handler.send;

public class p2pHandling implements Runnable{

    private final CSVHandling CSV;
    private GetRecv Get;
    private PrintWriter writer;
    private Handler partner;
    public boolean StopCode =false;

    @Override
    public void run() {
        try {
            while (true) {
                String hold = Get.result;
                if (hold.isEmpty()) {
                    send(writer, "data?");
                } else if (hold.equals("end")) {
                    send(partner.writerToClient, "end");
                    send(writer, "end");
                    System.out.println("Connection closed");
                    break;
                } else if (hold.equals("yes")) {
                    send(writer, "yes");
                } else if (hold.contains("Result:")){
                    System.out.println("Triggered");
                    String Cleaned = hold.replace("Result:","");
                    String[] data = Cleaned.split(",");
                    //score
                    CSV.setScore(Integer.parseInt(data[0]));
                    //time
                    CSV.setTime(Integer.parseInt(data[1]));
                    //NW
                    CSV.setNumberWins(CSV.getNumberWins()+1);
                    CSV.update();
                    send(partner.writerToClient, "end");
                    send(writer, "end");
                    System.out.println("Connection closed");
                    break;
                }
                else {
                    send(partner.writerToClient, hold);
                }

            }
            StopCode = false;
        } catch (Exception e){
            StopCode = false;
        }
    }

    public p2pHandling(GetRecv get, PrintWriter writer, Handler partner,CSVHandling csv){
        Get = get;
        this.writer = writer;
        this.CSV = csv;
        this.partner = partner;
    }
}
