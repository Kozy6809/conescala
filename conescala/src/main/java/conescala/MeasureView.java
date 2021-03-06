/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MeasureView.java
 * Created on 2011/04/20, 10:37:08
 */

package conescala;

import javax.swing.JTextArea;

/**
 *
 * @author
 */
class MeasureView extends javax.swing.JFrame {
    boolean canDispose = false;
    
    /** Creates new form MeasureView */
    MeasureView() {
        initComponents();
    }

    JTextArea getProcessTA() {return processTA;}
    void setStatus(String s) {statusLabel.setText(s);}
    void setResult1(String s) {result1Label.setText(s);}
    void setResult2(String s) {result2Label.setText(s);}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        processTA = new javax.swing.JTextArea();
        result1Label = new javax.swing.JLabel();
        result2Label = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        statusLabel.setFont(new java.awt.Font("MS UI Gothic", 0, 36)); // NOI18N
        statusLabel.setText("測定中です");
        statusLabel.setName("statusLabel"); // NOI18N

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(142, 92));

        processTA.setColumns(20);
        processTA.setRows(5);
        processTA.setName("processTA"); // NOI18N
        jScrollPane1.setViewportView(processTA);

        result1Label.setFont(new java.awt.Font("MS UI Gothic", 0, 24)); // NOI18N
        result1Label.setText("結果"); // NOI18N
        result1Label.setName("result1Label"); // NOI18N

        result2Label.setFont(new java.awt.Font("MS UI Gothic", 0, 24)); // NOI18N
        result2Label.setText("結果");
        result2Label.setName("result2Label"); // NOI18N

        jButton1.setText("戻る");
        jButton1.setName("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(result1Label))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(result2Label)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(12, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(result1Label)
                .addGap(18, 18, 18)
                .addComponent(result2Label)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (canDispose) dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea processTA;
    private javax.swing.JLabel result1Label;
    private javax.swing.JLabel result2Label;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}
