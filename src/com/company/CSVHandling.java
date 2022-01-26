package com.company;


import java.io.*;
import java.util.*;


public class CSVHandling {
    private final String filename;
    private String User;
    private String pass;
    private int Score = 0;
    private int Time = 0;
    private int numberWins =0;
    private int rank = 0;
    public int id  =0;
    public ArrayList<String[]> array;

    public void ObtainRanking(){
        for (int i=0;i<array.size();i++){
            String[] Temp = array.get(i);
            Temp[5] = String.valueOf(i);
        }
    }

    public void SortArray(){
        boolean swap = true;
        while (swap)
            swap = false;
            for (int j=0;j<array.size();j++){
                try {
                    if (Integer.parseInt(array.get(j)[2]) < Integer.parseInt(array.get(j+1)[2])) {
                        String[] Temp = array.get(j);
                        array.set(j, array.get(j + 1));
                        array.set(j + 1, Temp);
                        swap = true;
                    }
                } catch (Exception e){
                    System.out.println("hello");
                }
            }
        ObtainRanking();

    }

    private void write() {
        try(FileWriter fw = new FileWriter(filename, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(User + "," + pass + "," + Score + "," + Time + "," + numberWins + "," + rank +",");
            //more code
        } catch (IOException e) {

        }
    }

    public boolean append(){
        try(FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(User + "," + pass + "," + Score + "," + Time + "," + numberWins + "," + rank +",");
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public boolean update(){

        String[] temp = new String[6];
        temp[0] = User;
        temp[1] = pass;
        temp[2] = String.valueOf(Score);
        temp[3] = String.valueOf(Time);
        temp[4] = String.valueOf(numberWins);
        temp[5] = String.valueOf(rank);
        array.set(id,temp);
        SortArray();
        try(FileWriter fw = new FileWriter(filename, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (int i=0;i<array.size();i++) {
                out.println(array.get(i)[0] + "," + array.get(i)[1] + "," + array.get(i)[2] + "," + array.get(i)[3] + "," + array.get(i)[4] + "," + array.get(i)[5] + ",");
            }
            out.close();
            bw.close();
            fw.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public void setUser(String user) {
        User = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setScore(int score) {
        Score = score;
    }

    public void setTime(int time) {
        Time = time;
    }

    public void setNumberWins(int numberWins) {
        this.numberWins = numberWins;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return User;
    }



    public int getScore() {
        return Score;
    }

    public int getTime() {
        return Time;
    }

    public int getNumberWins() {
        return numberWins;
    }

    public void read(){
        array = new ArrayList();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(",");
                if (data.length > 1) {
                    array.add(data);
                    System.out.println(Arrays.toString(data));
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    //CSV constructor
    public CSVHandling(String filename, int type, String user, String pass){
        this.filename = filename;
        this.User = user;
        this.pass = pass;
        switch (type){
            case 1:
                read();
                break;
            default:
                write();
        }
    }
    public boolean ExistingAC(String username,String password){
        boolean auth = false;
        for (int i=0;i<array.size();i++){
            if (array.get(i)[0].equals(username)){
                auth = true;
            }
        }
        return auth;
    }

    public boolean CheckAuth(String username,String password){
        boolean auth = false;
        for (int i=0;i<array.size();i++){
            if (array.get(i)[0].equals(username) && array.get(i)[1].equals(password)){
                auth = true;
                String[] data = array.get(i);
                User = data[0];
                pass = data[1];
                Score = Integer.parseInt(data[2]);
                Time = Integer.parseInt(data[3]);
                numberWins = Integer.parseInt(data[4]);
                rank = Integer.parseInt(data[5]);
                id = i;
                i=array.size()+2;
            }
        }
        return auth;

    }


}

