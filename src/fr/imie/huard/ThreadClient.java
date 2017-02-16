package fr.imie.huard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by huard.cdi04 on 10/02/2017.
 */
public class ThreadClient extends Thread {

    private Socket client;
    public static int nbClient;

    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private String name = "default";

    public ThreadClient(Socket client) {
        this.client = client;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    @Override
    public void run(){
        nbClient++;
        String str = "";
        try {//Entre un pseudo
            out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            out.writeObject("Veuillez entrer un nom :");
            in = new ObjectInputStream(client.getInputStream());
            str = (String)in.readObject();
            this.name = str;
            out.flush();
            out.writeObject("bonjour "+name+", bienvenu sur le serveur de Timothée");
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        System.out.println(name+" c'est connecté, il y a : "+nbClient+" client");

        while(!str.equals("Logout")){
            try {
                str = (String)in.readObject();
                System.out.println(name+" : "+str);
                out.writeObject("vous : "+str);
                out.flush();
            }catch (IOException e){
                e.printStackTrace();
                break;
            }catch (ClassNotFoundException e){
                e.printStackTrace();
                break;
            }
            for (ThreadClient t: Serveur.listeThread) {
                if(!t.equals(this)) {
                    try {
                        t.getOut().writeObject(name+" : " + str);
                        t.getOut().flush();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }

        nbClient--;
        Serveur.listeThread.remove(this);
        System.out.println(name+" c'est déconnecté, il reste : "+nbClient+" client");
    }
}
