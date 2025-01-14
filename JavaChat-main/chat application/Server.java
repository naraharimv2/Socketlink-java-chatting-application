import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.*;


class Server extends JFrame
{

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter pw;

    private JLabel heading =new JLabel("[Server END]");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font hfont =new Font("Roboro", Font.BOLD,20);
    private Font font =new Font("Roboro", Font.PLAIN,20);



    public Server() 
    {
        try {
            server=new ServerSocket(7777);
            System.out.println("server is ready to accept connection\n waiting ....");
            socket=server.accept();

            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw=new PrintWriter(socket.getOutputStream(), true);

            createGui();
            startReading();
            startWriting();
        }
         catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
    
    private void createGui() {
        this.setTitle("[Server END]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        heading.setFont(hfont);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        heading.setBackground(Color.BLUE);

        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);

        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }


    // private void startWriting() {
    //     // Event handling to send messages via GUI
    //     messageInput.addActionListener(e -> {
    //         String contentToSend = messageInput.getText();
    //         messageArea.append("Me : " + contentToSend + "\n");
    //         pw.println(contentToSend);
    //         pw.flush();
    //         messageInput.setText("");
    //         messageInput.requestFocus();
    //         if (contentToSend.equalsIgnoreCase("exit")) {
    //             try {
    //                 socket.close();
    //                 messageInput.setEnabled(false);
    //                 pw.println("exit");
    //                 pw.flush();
    //                 JOptionPane.showMessageDialog(this, "You terminated the chat!!");
    //                 System.exit(0);
    //             } catch (IOException e1) {
    //                 e1.printStackTrace();
    //             }
    //         }
    //     });
    // }

    private void startWriting() {
        Runnable wrt = () -> {
            messageInput.addActionListener(e -> {
                String contentToSend = messageInput.getText();
                messageArea.append("Me : " + contentToSend + "\n");
                pw.println(contentToSend);
                pw.flush();
                messageInput.setText("");
                messageInput.requestFocus();
                if (contentToSend.equalsIgnoreCase("exit")) {
                    try {
                        socket.close();
                        messageInput.setEnabled(false);
                        pw.println("exit");
                        pw.flush();
                        JOptionPane.showMessageDialog(this, "You terminated the chat!!");
                        System.exit(0);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        };
    
        new Thread(wrt).start();
    }
    

    private void startReading() {
        // Reader thread to read incoming messages
        Runnable r1 = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equalsIgnoreCase("exit")) {
                        messageArea.setEnabled(false);
                        JOptionPane.showMessageDialog(this, "Client terminated the chat!!");
                        System.exit(0);
                        messageInput.setEnabled(false);
                        break;
                    }
                    messageArea.append("Client : " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }


    public static void main(String[] args) {
        System.out.println("this is server ... going to start");
        new Server();
    }
}
