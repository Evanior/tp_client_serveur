package fr.imie.huard;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by huard.cdi04 on 10/02/2017.
 */
public class Fenetre extends JFrame implements ActionListener, KeyListener {
    private JButton button = new JButton("Envoyer");
    private JTextArea text = new JTextArea();
    private JTextField field = new JTextField();

    ObjectOutputStream out = null;
    ObjectInputStream in = null;

    public Fenetre() throws IOException {
        Client client = new Client();
        out = client.getOut();
        in = client.getIn();

        JPanel panel = new JPanel();
        BorderLayout gl = new BorderLayout();
        panel.setLayout(gl);
        panel.add(field,BorderLayout.CENTER);
        panel.add(button,BorderLayout.EAST);

        this.setSize(new DimensionUIResource(500,500));
        this.setTitle("Chat multi-thread");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout bl = new BorderLayout();
        this.setLayout(bl);
        this.getContentPane().add(text,BorderLayout.CENTER);
        this.getContentPane().add(panel, BorderLayout.SOUTH);
        button.addActionListener(this);
        this.getRootPane().setDefaultButton(button);

        text.setFocusable(false);
        text.setLineWrap(true);
        text.setAutoscrolls(true);
        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
            envoieText();
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
        if(actionEvent.getSource() == button){
            envoieText();
        }
    }

    /**
     * envoie du text au serveur
     */
    public void envoieText(){
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

/**
 * Runnable permettant de rafraichir la fenetre
 */
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
                reponse = (String)in.readObject();//reception de la reponse du serveur
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(reponse.equals("vous : Logout")){
                System.exit(0);//quitte si on écris Logout
            }
            text.append(reponse+"\n");//afficher la reponse
            if(text.getLineCount() > 28){//realise le defilement du text
                int firtLine = text.getText().indexOf('\n')+1;
                int taille = text.getText().length() - firtLine;
                try {
                    text.setText(text.getText(firtLine,taille));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

