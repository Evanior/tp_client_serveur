package fr.imie.huard;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by huard.cdi04 on 10/02/2017.
 */
public class Fenetre extends JFrame implements ActionListener, KeyListener {
    private JButton button =new JButton("Envoyer");
    private JTextArea text =new JTextArea();
    private JTextField field = new JTextField();

    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    public Fenetre() throws IOException {
        Client client = new Client();
        out = client.getOut();
        in = client.getIn();

        this.setSize(new DimensionUIResource(500,500));
        this.setTitle("Chat");
        //this.setLocation(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().add(text,BorderLayout.CENTER);
        this.getContentPane().add(field,BorderLayout.NORTH);
        this.getContentPane().add(button,BorderLayout.SOUTH);
        button.addActionListener(this);
        this.getRootPane().setDefaultButton(button);

        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
            actionPerformed(null);
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    public JTextArea getText() {
        return text;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            out.writeObject(field.getText());
            out.flush();
            field.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] arg) throws Exception{
        Fenetre f = new Fenetre();
        Thread t = new Thread(new Update(f));
        t.start();
    }
}

class Update implements Runnable{
    JTextArea text = null;
    ObjectInputStream in = null;

    public Update(Fenetre f) {
        this.text = f.getText();
        this.in = f.getIn();
    }

    @Override
    public void run() {
        while (true){
            String reponse = null;
            try {
                reponse = (String)in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(reponse.equals("vous : Logout")){
                System.exit(0);
            }
            text.append(reponse+"\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

