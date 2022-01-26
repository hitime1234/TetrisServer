package com.company;

import java.net.Socket;
import java.util.ArrayList;

public class ServerQueue {
    private static ArrayList<Handler> connections;
    public ServerQueue(){
        connections = new ArrayList<>();
    }

    public static ArrayList<Handler> getConnections() {
        return connections;
    }

    public static void AddConnections(Handler Connection) {
        ServerQueue.connections.add(Connection);
    }
}
