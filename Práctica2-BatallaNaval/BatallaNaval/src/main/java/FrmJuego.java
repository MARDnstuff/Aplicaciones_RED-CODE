
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mauricio
 */
public class FrmJuego extends javax.swing.JFrame {
    
    int numFilas=10;
    int numColumnas=10;
    int numMinas=10;
    
    JButton[][] botonesTablero1 = new JButton[numFilas][numColumnas];
    JButton[][] botonesTablero2 = new JButton[numFilas][numColumnas];
    JLabel[] coordenadasX1;
    JLabel[] coordenadasY1;
    JLabel[] coordenadasX2;
    JLabel[] coordenadasY2;
    
    Tablero tablero;
  
    /**
     * Creates new form FrmJuego
     */
    public FrmJuego() {
        initComponents();
        cargarControles();
    }
    
    private void cargarControles(){
        
        int posXReferencia=28;
        int posYReferencia=35;
        int anchoControl=30;
        int altoControl=30;
        int i;
        
        coordenadasX1 = new JLabel[numFilas];
        coordenadasY1 = new JLabel[numFilas];
        coordenadasX2 = new JLabel[numFilas];
        coordenadasY2 = new JLabel[numFilas];
        String[] coordY = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        
        for (i = 0; i < coordenadasX2.length; i++) {
            coordenadasX1[i] = new JLabel(""+(i+1));
            coordenadasY1[i] = new JLabel(coordY[i]);
            coordenadasX2[i] = new JLabel(""+(i+1));
            coordenadasY2[i] = new JLabel(coordY[i]);
            if(i == 0){
                coordenadasX1[i].setBounds(posXReferencia+10, posYReferencia-20, anchoControl, altoControl);
                coordenadasY1[i].setBounds(posXReferencia-10, posYReferencia-2, anchoControl, altoControl);
                coordenadasX2[i].setBounds(posXReferencia+10, posYReferencia-20, anchoControl, altoControl);
                coordenadasY2[i].setBounds(posXReferencia-10, posYReferencia-2, anchoControl, altoControl);
            }
            else{
                coordenadasX1[i].setBounds(
                            coordenadasX1[i-1].getX()+coordenadasX1[i-1].getWidth(),
                            posYReferencia-20, 
                            anchoControl, altoControl);
                coordenadasY1[i].setBounds(
                            posXReferencia-10, 
                            coordenadasY1[i-1].getY()+coordenadasY1[i-1].getHeight(), 
                            anchoControl, altoControl);
                coordenadasX2[i].setBounds(
                            coordenadasX1[i-1].getX()+coordenadasX1[i-1].getWidth(),
                            posYReferencia-20, 
                            anchoControl, altoControl);
                coordenadasY2[i].setBounds(
                            posXReferencia-10, 
                            coordenadasY1[i-1].getY()+coordenadasY1[i-1].getHeight(), 
                            anchoControl, altoControl);
            }
            PlayerPanel.add(coordenadasX1[i]); 
            PlayerPanel.add(coordenadasY1[i]);
            PCPanel.add(coordenadasX2[i]); 
            PCPanel.add(coordenadasY2[i]);
        }
        
        for (i = 0; i < botonesTablero1.length; i++) {
            for (int j = 0; j < botonesTablero1[i].length; j++) {
                botonesTablero1[i][j]=new JButton();
                botonesTablero1[i][j].setName(i+","+j);
                botonesTablero1[i][j].setBorder(null);
                botonesTablero2[i][j]=new JButton();
                botonesTablero2[i][j].setName(i+","+j);
                botonesTablero2[i][j].setBorder(null);
                if (i==0 && j==0){
                    botonesTablero1[i][j].setBounds(posXReferencia, 
                            posYReferencia, anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(posXReferencia, 
                            posYReferencia, anchoControl, altoControl);
                    
                }else if (i==0 && j!=0){
                    botonesTablero1[i][j].setBounds(
                            botonesTablero1[i][j-1].getX()+botonesTablero1[i][j-1].getWidth(), 
                            posYReferencia, anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(
                            botonesTablero1[i][j-1].getX()+botonesTablero1[i][j-1].getWidth(), 
                            posYReferencia, anchoControl, altoControl);
                }else{
                    botonesTablero1[i][j].setBounds(
                            botonesTablero1[i-1][j].getX(), 
                            botonesTablero1[i-1][j].getY()+botonesTablero1[i-1][j].getHeight(), 
                            anchoControl, altoControl);
                    botonesTablero2[i][j].setBounds(
                            botonesTablero2[i-1][j].getX(), 
                            botonesTablero2[i-1][j].getY()+botonesTablero2[i-1][j].getHeight(), 
                            anchoControl, altoControl);
                }
                botonesTablero1[i][j].addMouseListener(new MouseListener(){
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }
                    
                    
                    @Override
                    public void mouseEntered(MouseEvent e){
                        //btnClick(e);
                        //botonesTablero1[4][2].setBackground((Color.red));
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e){
                    
                    }                
                });
                botonesTablero1[i][j].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        btnClick(e);
                    }
                
                
                });
               
                PlayerPanel.add(botonesTablero1[i][j]);
                PCPanel.add(botonesTablero2[i][j]);
            }
        }
        this.setSize(botonesTablero1[numFilas-1][numColumnas-1].getX()+
                botonesTablero1[numFilas-1][numColumnas-1].getWidth()+30,
                botonesTablero1[numFilas-1][numColumnas-1].getY()+
                botonesTablero1[numFilas-1][numColumnas-1].getHeight()+70
                );
        this.setSize(botonesTablero2[numFilas-1][numColumnas-1].getX()+
                botonesTablero2[numFilas-1][numColumnas-1].getWidth()+30,
                botonesTablero2[numFilas-1][numColumnas-1].getY()+
                botonesTablero2[numFilas-1][numColumnas-1].getHeight()+70
                );
    }
    private void ordenarBarcos() {
        JOptionPane.showMessageDialog(ContainerPanel, "Ordena tu barco acorazado(4 casillas)");
        Casilla[] acorazado = new Casilla[4];
        Casilla[] submarino = new Casilla[5];
        
    }
    private void btnClick(ActionEvent e) {
        JButton btn=(JButton)e.getSource();
        String[] coordenada=btn.getName().split(",");
        btn.setBackground(Color.green);
        int posFila=Integer.parseInt(coordenada[0]);
        int posColumna=Integer.parseInt(coordenada[1]);
        //JOptionPane.showMessageDialog(rootPane, posFila+","+posColumna);
    }
    private void btnClick(MouseEvent e) {
        JButton btn=(JButton)e.getSource();
        String[] coordenada=btn.getName().split(",");
        btn.setBackground(Color.red);
        int posFila=Integer.parseInt(coordenada[0]);
        int posColumna=Integer.parseInt(coordenada[1]);
        //JOptionPane.showMessageDialog(rootPane, posFila+","+posColumna);
    }

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ContainerPanel = new javax.swing.JPanel();
        PlayerPanel = new javax.swing.JPanel();
        PCPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        PlayBTN = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        PlayerNameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ContainerPanel.setBackground(new java.awt.Color(255, 255, 255));

        PlayerPanel.setBackground(new java.awt.Color(245, 245, 245));

        javax.swing.GroupLayout PlayerPanelLayout = new javax.swing.GroupLayout(PlayerPanel);
        PlayerPanel.setLayout(PlayerPanelLayout);
        PlayerPanelLayout.setHorizontalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PlayerPanelLayout.setVerticalGroup(
            PlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        PCPanel.setBackground(new java.awt.Color(245, 245, 245));

        javax.swing.GroupLayout PCPanelLayout = new javax.swing.GroupLayout(PCPanel);
        PCPanel.setLayout(PCPanelLayout);
        PCPanelLayout.setHorizontalGroup(
            PCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PCPanelLayout.setVerticalGroup(
            PCPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 255));
        jLabel1.setText("Base Naval");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 255));
        jLabel2.setText("Mapa Contrincante");

        PlayBTN.setBackground(new java.awt.Color(0, 51, 255));
        PlayBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PlayBTN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PlayBTNMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                PlayBTNMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                PlayBTNMouseExited(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("JUGAR");

        javax.swing.GroupLayout PlayBTNLayout = new javax.swing.GroupLayout(PlayBTN);
        PlayBTN.setLayout(PlayBTNLayout);
        PlayBTNLayout.setHorizontalGroup(
            PlayBTNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayBTNLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel3)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        PlayBTNLayout.setVerticalGroup(
            PlayBTNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PlayBTNLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PlayerNameLabel.setBackground(new java.awt.Color(51, 102, 255));
        PlayerNameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        PlayerNameLabel.setForeground(new java.awt.Color(51, 51, 255));
        PlayerNameLabel.setText("Nombre del Jugador:  ");

        javax.swing.GroupLayout ContainerPanelLayout = new javax.swing.GroupLayout(ContainerPanel);
        ContainerPanel.setLayout(ContainerPanelLayout);
        ContainerPanelLayout.setHorizontalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(152, 152, 152))
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PlayerNameLabel)
                    .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(ContainerPanelLayout.createSequentialGroup()
                            .addComponent(PlayBTN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 606, Short.MAX_VALUE))
                        .addGroup(ContainerPanelLayout.createSequentialGroup()
                            .addComponent(PlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        ContainerPanelLayout.setVerticalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContainerPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(PlayerNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContainerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContainerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PCPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(PlayBTN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PlayBTNMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseEntered
        PlayBTN.setBackground(new Color(0,0,153));
    }//GEN-LAST:event_PlayBTNMouseEntered

    private void PlayBTNMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseExited
        PlayBTN.setBackground(new Color(0,51,255));
    }//GEN-LAST:event_PlayBTNMouseExited

    private void PlayBTNMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PlayBTNMouseClicked
        String name = JOptionPane.showInputDialog(ContainerPanel, "Ingresa un nombre de jugador para iniciar el juego:");
        //TODO: Send the name to the server to start the game
        PlayerNameLabel.setText("Nombre del Jugador: "+ name);
        JOptionPane.showMessageDialog(ContainerPanel, "El juego está a punto de iniciar. Ordena tus barcos seleccionando "
                + "la primera casilla y la última de cada barco.\n Recuerda"
                + " puedes ordenarlos vertical u horizontalmente.");
        ordenarBarcos();
    }//GEN-LAST:event_PlayBTNMouseClicked

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
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContainerPanel;
    private javax.swing.JPanel PCPanel;
    private javax.swing.JPanel PlayBTN;
    private javax.swing.JLabel PlayerNameLabel;
    private javax.swing.JPanel PlayerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
