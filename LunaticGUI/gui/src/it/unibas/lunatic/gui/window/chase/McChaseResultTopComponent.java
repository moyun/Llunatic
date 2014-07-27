/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.chase;

import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.McChaseResult;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//it.unibas.lunatic.gui//McChaseResult//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = R.Window.MC_CHASE_RESULT,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
//@ActionID(category = "Window", id = "it.unibas.lunatic.gui.action.OpenMcChaseResultView")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
//@TopComponent.OpenActionRegistration(
//        displayName = "#CTL_McChaseResultAction",
//        preferredID = R.Window.MC_CHASE_RESULT)
@Messages({
    "CTL_McChaseResultAction=Cleaning result",
    "CTL_McChaseResultTopComponent=Cleaning result",
    "HINT_McChaseResultTopComponent=Cleaning result window"
})
public final class McChaseResultTopComponent extends ExplorerTopComponent implements McResultView {

    private McResultView selectedView;
    private McStackNodesViewPanel stackViewPanel = new McStackNodesViewPanel();
    private McTreeViewPanel treeViewPanel = new McTreeViewPanel();

    public McChaseResultTopComponent() {
        initComponents();
        setName(R.Window.MC_CHASE_RESULT);
        setDisplayName(Bundle.CTL_McChaseResultTopComponent());
        setToolTipText(Bundle.HINT_McChaseResultTopComponent());
        associateExplorerLookup();
        selectedView = treeViewPanel;
        add(selectedView.toComponent(), BorderLayout.CENTER);
        initButtons();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewSelectionGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btnTreeView = new javax.swing.JToggleButton();
        btnStackView = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        viewSelectionGroup.add(btnTreeView);
        btnTreeView.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(btnTreeView, org.openide.util.NbBundle.getMessage(McChaseResultTopComponent.class, "McChaseResultTopComponent.btnTreeView.text")); // NOI18N
        btnTreeView.setToolTipText(org.openide.util.NbBundle.getMessage(McChaseResultTopComponent.class, "McChaseResultTopComponent.btnTreeView.toolTipText")); // NOI18N
        btnTreeView.setPreferredSize(new java.awt.Dimension(24, 24));

        viewSelectionGroup.add(btnStackView);
        org.openide.awt.Mnemonics.setLocalizedText(btnStackView, org.openide.util.NbBundle.getMessage(McChaseResultTopComponent.class, "McChaseResultTopComponent.btnStackView.text")); // NOI18N
        btnStackView.setToolTipText(org.openide.util.NbBundle.getMessage(McChaseResultTopComponent.class, "McChaseResultTopComponent.btnStackView.toolTipText")); // NOI18N
        btnStackView.setPreferredSize(new java.awt.Dimension(24, 24));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(McChaseResultTopComponent.class, "McChaseResultTopComponent.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTreeView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnStackView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTreeView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStackView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnStackView;
    private javax.swing.JToggleButton btnTreeView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.ButtonGroup viewSelectionGroup;
    // End of variables declaration//GEN-END:variables
    private McResultUpdateListener listener = new McResultUpdateListener();

    @Override
    public void componentOpened() {
        listener.register(this);
        selectedView.componentOpened();
    }

    @Override
    public void componentClosed() {
        listener.remove();
        selectedView.componentClosed();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.3");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private void initButtons() {
        btnTreeView.setIcon(ImageUtilities.loadImageIcon("it/unibas/lunatic/icons/tree.png", false));
        btnTreeView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setResultView(treeViewPanel);
            }
        });
        btnStackView.setIcon(ImageUtilities.loadImageIcon("it/unibas/lunatic/icons/stack.png", false));
        btnStackView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setResultView(stackViewPanel);
            }
        });
    }

    private void setResultView(McResultView view) {
        selectedView.componentClosed();
        remove(selectedView.toComponent());
        selectedView = view;
        add(selectedView.toComponent(), BorderLayout.CENTER);
        selectedView.componentOpened();
        selectedView.toComponent().updateUI();
    }

    @Override
    public void setRootContext(Node node) {
        explorer.setRootContext(node);
    }

    @Override
    public void removeRootContext() {
        explorer.setRootContext(Node.EMPTY);
    }

    @Override
    public void onChaseResultUpdate(McChaseResult result) {
    }

    @Override
    public void onChaseResultClose() {
        close();
    }

    @Override
    public JComponent toComponent() {
        return this;
    }
}