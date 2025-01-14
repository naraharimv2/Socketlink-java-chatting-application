import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame {

    BufferedReader br;
    PrintWriter pw;
    Socket socket;

    private JLabel heading=new JLabel("CLIENT SPACE");
    Icon icon=new ImageIcon("logo1.png",getName());
    // private JLabel logo=new JLabel("logo1.png");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput =new JTextField();
    private Font headFont=new Font("Roboto",Font.CENTER_BASELINE,25);
    private Font font=new Font("Roboto",Font.PLAIN,20);

    
    public Client()
    {
        try {
            // System.out.println("sending request to server");
            socket=new Socket("192.168.137.1", 7777 );
            System.out.println("connection done");

            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw=new PrintWriter(socket.getOutputStream(), true);

            createGui();
            startReading();
            startWriting();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    private void createGui() {
        this.setTitle("Client Messenger[END]");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //coding for component
        heading.setFont(headFont);
        messageArea.setFont(font);
        messageInput.setFont(font);
        

        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        

        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);
        // this.add(Icon ,icon,BorderLayout.NORTH);

        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }


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
                        JOptionPane.showMessageDialog(this, "Server terminated the chat!!");
                        System.exit(0);
                        messageInput.setEnabled(false);
                        break;
                    }
                    messageArea.append("Server : " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }
    
    public static void main(String[] args) {
        System.out.println("this is client ...");
        new Client();
    }
}
