/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatmulticast.UI;

import com.mycompany.chatmulticast.backend.Message;
import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.mycompany.chatmulticast.backend.Recibe;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.DefaultListModel;

/**
 *
 * @author Mauricio
 */
public class ChatView extends javax.swing.JFrame {
    private String nickName, path;
    public JLabel[] emojisLabels;
    public ImageIcon[] emojisIcons;
    public MulticastSocket m;
    public DefaultListModel usersModel;
    
    /**
     * Creates new form ChatView
     */
    public ChatView() {
        initComponents();
    }
    
    /**
     * Creates new form ChatView
     */
    public ChatView(String nickName) {
        initComponents();
        this.nickName = nickName;
        this.usersModel = new DefaultListModel();
        this.usersList.setModel(usersModel);
        loadEmojis();
        connectToGroup();
    }
    
    private void loadEmojis(){
        int xReference = 15, yReference = 80;
        int width=30;
        int height=30;
        emojisIcons = new ImageIcon[40];
        emojisLabels = new JLabel[40];
        File f = new File("");
        String path = f.getAbsolutePath();
        this.path=path;
        System.out.println("Path: " + path);
        JLabel name = new JLabel(nickName);
        name.setForeground(Color.green);
        name.setBounds(xReference+10, yReference-20, width, height);
        emojisPanel.add(name);
        int i;
             
        for (i = 0; i < emojisLabels.length; i++) {
            emojisIcons[i] = new ImageIcon(path+"\\src\\img\\emoji" + (i+1) + ".png");
            Icon emoji = new ImageIcon(emojisIcons[i].getImage().getScaledInstance(width,height,Image.SCALE_DEFAULT));
            emojisLabels[i] = new JLabel(emoji);
            //emojisLabels[i] = new JLabel("Hello");
            emojisLabels[i].setForeground(Color.green);
            //emojisLabels[i].setBounds(xReference+10, yReference-20, width, height);
            //ImageIcon foto = new ImageIcon("C:\\Users\\52552\\Documents\\NetBeansProjects\\tttttttttt\\cara.gif");
            //emojisLabels[i].setIcon(emoji); 
            if(i%4 == 0){
                if(i == 0){
                    emojisLabels[i].setBounds(xReference+10, yReference-40, width, height);
                }
                else{
                    emojisLabels[i].setBounds(
                            xReference+10,
                            emojisLabels[i-4].getY()+emojisLabels[i-4].getHeight()+5, 
                            width, height);
                }
                
            }
            else{
                if(i < 4)
                {
                    emojisLabels[i].setBounds(
                            emojisLabels[i-1].getX()+emojisLabels[i-1].getWidth()+5,
                            yReference-40, 
                            width, height);
                }
                
                else{
                    emojisLabels[i].setBounds(
                            emojisLabels[i-1].getX()+emojisLabels[i-1].getWidth()+5,
                            emojisLabels[i-4].getY()+emojisLabels[i-4].getHeight()+5, 
                            width, height);
                }
                
            
            }
            emojisPanel.add(emojisLabels[i]); 
        }
    }
    
    private void connectToGroup(){
        try{
            int pto= 1234,z=0;            
            //NetworkInterface ni = NetworkInterface.getByName("eth2");
            NetworkInterface ni = NetworkInterface.getByIndex(14);
            //br.close();
            System.out.println("\nElegiste "+ni.getDisplayName());
            m= new MulticastSocket(pto);
            //m.setReuseAddress(true);
            //m.setTimeToLive(255);
            String dir= "230.1.1.1";
            InetAddress gpo = InetAddress.getByName(dir);
            //InetAddress gpo = InetAddress.getByName("ff3e:40:2001::1");
            SocketAddress dirm;
                try{
                    dirm = new InetSocketAddress(gpo,pto);
                }catch(Exception e){
                  e.printStackTrace();
                   return;
                }//catch
                m.joinGroup(gpo);
                //m.joinGroup(dirm, ni);
                Message message = new Message(1, this.nickName);
                ByteArrayOutputStream baos= new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                oos.flush();
                byte[] b = baos.toByteArray();
                DatagramPacket pkt = new DatagramPacket(b,b.length,gpo,pto);
                m.send(pkt);
                String textInChat = this.chatPane.getText();
                this.chatPane.setText("Bienvenido al grupo " + nickName + ". A partir de ahora puedes enviar mensajes.");
                this.chatPane.setForeground(Color.blue);
                
                int[] usersIndexes = this.usersList.getSelectedIndices();
                Recibe r = new Recibe(m,this.nickName, chatPane, this.usersModel, usersIndexes, userSelectedCBox);
                r.start();
        }catch(Exception e){}
    }
    
    private void sendMessage(){
        try{
            //BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
            String dir= "230.1.1.1";
            int pto=1234;
            InetAddress gpo = InetAddress.getByName(dir);
            String msg = messagePanel.getText();
            String userSelected = (String) userSelectedCBox.getSelectedItem();
            Message message;
            //Message for everyone
            if(userSelected.equals("Todos")){
                message = new Message(2, msg, this.nickName);
            }
            //Private message
            else{
                message = new Message(3, msg, this.nickName, userSelected);
            }
            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            byte[] b = baos.toByteArray();
            DatagramPacket pkt = new DatagramPacket(b,b.length,gpo,pto);
            m.send(pkt);
            String textInChat = this.chatPane.getText();
            this.chatPane.setText(textInChat+ "\n\n\n[" + message.getSender() + "] :       " + message.getMessage());
            this.chatPane.setForeground(Color.blue);
            messagePanel.setText("");
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        sendMessageBtn = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        messagePanel = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        userSelectedCBox = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        usersPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        usersList = new javax.swing.JList<>();
        emojisPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        background.setBackground(new java.awt.Color(255, 255, 255));
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(153, 204, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(590, 590));

        sendMessageBtn.setBackground(new java.awt.Color(0, 102, 255));
        sendMessageBtn.setForeground(new java.awt.Color(255, 255, 255));
        sendMessageBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sendMessageBtn.setPreferredSize(new java.awt.Dimension(96, 42));
        sendMessageBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sendMessageBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendMessageBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendMessageBtnMouseExited(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Enviar");

        javax.swing.GroupLayout sendMessageBtnLayout = new javax.swing.GroupLayout(sendMessageBtn);
        sendMessageBtn.setLayout(sendMessageBtnLayout);
        sendMessageBtnLayout.setHorizontalGroup(
            sendMessageBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sendMessageBtnLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel1)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        sendMessageBtnLayout.setVerticalGroup(
            sendMessageBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
        );

        messagePanel.setBorder(null);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Enviar a:");

        userSelectedCBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));
        userSelectedCBox.setBorder(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 102, 204), null));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel5.setForeground(new java.awt.Color(0, 51, 255));
        jLabel5.setText("Salir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userSelectedCBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(messagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendMessageBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(sendMessageBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(messagePanel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(userSelectedCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        background.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 370, 670, 120));

        usersPanel.setBackground(new java.awt.Color(0, 0, 255));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Usuarios Conectados");

        jScrollPane2.setBackground(new java.awt.Color(0, 102, 204));

        usersList.setBackground(new java.awt.Color(0, 0, 255));
        usersList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        usersList.setForeground(new java.awt.Color(255, 255, 255));
        usersList.setSelectionBackground(new java.awt.Color(51, 102, 255));
        jScrollPane2.setViewportView(usersList);

        javax.swing.GroupLayout usersPanelLayout = new javax.swing.GroupLayout(usersPanel);
        usersPanel.setLayout(usersPanelLayout);
        usersPanelLayout.setHorizontalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(usersPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel2)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        usersPanelLayout.setVerticalGroup(
            usersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        background.add(usersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 490));

        emojisPanel.setBackground(new java.awt.Color(245, 245, 245));

        jLabel3.setBackground(new java.awt.Color(102, 204, 255));
        jLabel3.setForeground(new java.awt.Color(51, 102, 255));
        jLabel3.setText("Panel de Emojis y Stickers");

        javax.swing.GroupLayout emojisPanelLayout = new javax.swing.GroupLayout(emojisPanel);
        emojisPanel.setLayout(emojisPanelLayout);
        emojisPanelLayout.setHorizontalGroup(
            emojisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emojisPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        emojisPanelLayout.setVerticalGroup(
            emojisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(emojisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(345, Short.MAX_VALUE))
        );

        background.add(emojisPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 180, 370));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportView(chatPane);

        background.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 490, 370));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendMessageBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendMessageBtnMouseExited
        sendMessageBtn.setBackground(new Color(0,102,255));
    }//GEN-LAST:event_sendMessageBtnMouseExited

    private void sendMessageBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendMessageBtnMouseEntered
        sendMessageBtn.setBackground(new Color(0,70,245));
    }//GEN-LAST:event_sendMessageBtnMouseEntered

    private void sendMessageBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sendMessageBtnMouseClicked
        if(messagePanel.getText().length() > 0){
            sendMessage();
        }
    }//GEN-LAST:event_sendMessageBtnMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private javax.swing.JEditorPane chatPane;
    private javax.swing.JPanel emojisPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField messagePanel;
    private javax.swing.JPanel sendMessageBtn;
    private javax.swing.JComboBox<String> userSelectedCBox;
    private javax.swing.JList<String> usersList;
    private javax.swing.JPanel usersPanel;
    // End of variables declaration//GEN-END:variables
}
