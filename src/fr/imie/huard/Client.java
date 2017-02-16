package fr.imie.huard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by huard.cdi04 on 10/02/2017.
 */
public class Client {

    private Socket client = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public Client() throws IOException {
        client = new Socket("127.0.0.1",222);
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    /*public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket client = new Socket("127.0.0.1",22);//10.2.6.27
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        //System.out.println("> "+(String)in.readObject());
        Scanner scan = new Scanner(System.in);
        while (true){
            System.out.println("ecrire qqchose : ");
            String str = scan.nextLine();
            out.writeObject(str);
            out.flush();
            String reponse = (String)in.readObject();
            System.out.println("vous avez ecris : "+reponse);
            if(reponse.equals("Logout")){
                System.out.println("Vous allez être déconnecté");
                break;
            }
        }
    }*/
}
