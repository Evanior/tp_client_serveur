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
import java.util.ArrayList;

/**
 * Created by huard.cdi04 on 10/02/2017.
 */
public class Fenetre extends JFrame implements ActionListener, KeyListener {
    private JButton button;
    private JTextArea text;
    private JTextArea user;
    private JTextField field;
    private JScrollPane scrollText;

    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public Fenetre() throws IOException {
        button = new JButton("Envoyer");
        text = new JTextArea();
        user = new JTextArea("Utilisateur : ");
        field = new JTextField();

        Client client = new Client();
        out = client.getOut();
        in = client.getIn();

        JPanel panelSud = new JPanel();
        BorderLayout layoutSud = new BorderLayout();
        panelSud.setLayout(layoutSud);
        panelSud.add(field,BorderLayout.CENTER);
        panelSud.add(button,BorderLayout.EAST);

        scrollText = new JScrollPane(text);
        JPanel panelPrincipal = new JPanel();
        JPanel panelScondaire = new JPanel();
        BorderLayout layoutPrincipal = new BorderLayout();
        panelPrincipal.setLayout(layoutPrincipal);
        panelScondaire.add(user,BorderLayout.CENTER);
        panelPrincipal.add(scrollText,BorderLayout.CENTER);

        this.setSize(new DimensionUIResource(500,500));
        this.setTitle("Chat multi-thread");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        this.getContentPane().add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelScondaire,panelPrincipal),BorderLayout.CENTER);
        this.getContentPane().add(panelSud, BorderLayout.SOUTH);
        button.addActionListener(this);
        this.getRootPane().setDefaultButton(button);

        text.setFocusable(false);
        user.setFocusable(false);
        text.setLineWrap(true);
        scrollText.setAutoscrolls(true);
        this.setResizable(false);
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

    public JTextArea getUserText() {
        return user;
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
    private JTextArea text = null;
    private JTextArea user = null;
    private ArrayList<String> userListe;
    private ObjectInputStream in = null;

    public Update(Fenetre f) {
        this.userListe = new ArrayList<>();
        this.text = f.getText();
        this.user = f.getUserText();
        this.in = f.getIn();
    }

    @Override
    public void run() {
        while (true){
            String reponse = null;
            try {
                reponse = (String)in.readObject();//reception de la reponse du serveur
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(reponse.equals("vous : Logout")){
                System.exit(0);//quitte si on écris Logout
            }
            if(reponse.startsWith("CliEnt : ")){
                String[] strSplit = reponse.split(" : ");
                if(!userListe.contains(strSplit[1]) && !strSplit[1].equals("default")){
                    userListe.add(strSplit[1]);
                }
                user.setText("Utilisateur : "+userListe.size());
                userListe.forEach(nom -> user.append("\n"+nom));
            }else if(reponse.endsWith("Logout")){
                String[] strSplit = reponse.split(" : ");
                userListe.remove(strSplit[0]);
                user.setText("Utilisateur : "+userListe.size());
                userListe.forEach(nom -> user.append("\n"+nom));
            }else {
                text.append(reponse+"\n");//afficher la reponse
            }

            //TODO à revoir via le scrollpane
            /*if(text.getLineCount() > 27){//realise le defilement du text
                int firtLine = text.getText().indexOf('\n')+1;
                int taille = text.getText().length() - firtLine;
                try {
                    text.setText(text.getText(firtLine,taille));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }*/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

