package fr.imie.huard.serveur;

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

    public String getNom() {
        return name;
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
            out.writeObject("bonjour "+name+", bienvenu sur le serveur de Timothée\n" +
                    "Ecrire 'Logout' pour quitter");
            out.flush();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        System.out.println(name+" c'est connecté, il y a : "+nbClient+" client");
        for (ThreadClient t: Serveur.listeThread) {//pour tous les autres envoyer le nom et le message
            try {
                t.getOut().writeObject("CliEnt : " + name);
                t.getOut().flush();
                out.writeObject("CliEnt : " + t.getNom());
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        while(!str.equals("Logout")){
            try {
                str = (String)in.readObject();
                System.out.println(name+" : "+str);
                out.writeObject("vous : "+str);//echo vers le client
                out.flush();
            }catch (IOException e){
                e.printStackTrace();
                break;
            }catch (ClassNotFoundException e){
                e.printStackTrace();
                break;
            }
            for (ThreadClient t: Serveur.listeThread) {//pour tous les autres envoyer le nom et le message
                if(!t.equals(this)) {
                    try {
                        t.getOut().writeObject(name + " : " + str);
                        t.getOut().flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    t.getOut().writeObject("CliEnt : " + name);
                    t.getOut().flush();
                    out.writeObject("CliEnt : " + t.getNom());
                    out.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        nbClient--;
        Serveur.listeThread.remove(this);
        for (ThreadClient t: Serveur.listeThread) {//pour tous les autres envoyer le nom et le message
            try {
                t.getOut().writeObject(name + " : Logout");
                t.getOut().flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(name+" c'est déconnecté, il reste : "+nbClient+" client");
    }
}
