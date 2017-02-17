package fr.imie.huard;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Serveur {

    public static ArrayList<ThreadClient> listeThread = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket server = new ServerSocket(222);
        while (true){
            Socket client = server.accept();
            ThreadClient t = new ThreadClient(client);
            listeThread.add(t);
            t.start();
        }
    }
}
