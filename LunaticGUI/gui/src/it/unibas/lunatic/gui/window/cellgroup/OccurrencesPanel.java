package it.unibas.lunatic.gui.window.cellgroup;

import it.unibas.lunatic.gui.MultiViewExplorerTopComponent;
import it.unibas.lunatic.gui.R;
import it.unibas.lunatic.gui.model.LoadedScenario;
import it.unibas.lunatic.gui.node.cellgroup.OccurrenceRootNode;
import it.unibas.lunatic.gui.node.cellgroup.OccurrenceTupleNode;
import it.unibas.lunatic.gui.node.cellgroup.StepCellGroupNode;
import it.unibas.lunatic.gui.node.utils.ITableColumnGenerator;
import it.unibas.lunatic.gui.table.OutlineTableHelper;
import it.unibas.lunatic.gui.window.ScenarioChangeListener;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tony
 */
@TopComponent.Description(
        preferredID = R.Window.CELL_GROUP_OCCURRENCES,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@NbBundle.Messages("CTL_Occurrences=Occurrences")
public class OccurrencesPanel extends MultiViewExplorerTopComponent implements ScenarioChangeListener.Target {

    private OutlineTableHelper tableHelper = new OutlineTableHelper();
    private ITableColumnGenerator columnGenerator = OccurrenceTupleNode.getColumnGenerator();
    private final CellGroupDetails details;

    public OccurrencesPanel(CellGroupDetails details) {
        this.details = details;
        initComponents();
        setName(R.Window.CELL_GROUP_OCCURRENCES);
        setDisplayName(Bundle.CTL_Occurrences());
        outlineView2.getOutline().setRootVisible(false);
        tableHelper.hideNodesColumn(outlineView2);
        columnGenerator.createTableColumns(outlineView2);
        outlineView2.getOutline().setFullyNonEditable(true);
        associateExplorerLookup();
        add(details, BorderLayout.PAGE_START);
        listener.register(this);
    }
    private CellGroupSelectionListener listener = new CellGroupSelectionListener();

    @Override
    public void setRootContext(Node node) {
        StepCellGroupNode stepCellGroupNode = (StepCellGroupNode) node;
        details.setCellGroupValue(stepCellGroupNode.getValue());
        explorer.setRootContext(new OccurrenceRootNode(stepCellGroupNode));
    }

    @Override
    public void removeRootContext() {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        outlineView2 = new org.openide.explorer.view.OutlineView();

        setLayout(new java.awt.BorderLayout());
        add(outlineView2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.OutlineView outlineView2;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getToolBar() {
        return CellGroupToolBarFactory.getInstance().getToolBar();
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
    
    private ScenarioChangeListener scenarioChangeListener = new ScenarioChangeListener();

    @Override
    protected void componentOpened() {
        scenarioChangeListener.register(this);
    }

    @Override
    protected void componentActivated() {
        scenarioChangeListener.remove();
    }

    @Override
    public void onScenarioChange(LoadedScenario oldScenario, LoadedScenario newScenario) {
        onScenarioClose(oldScenario);
    }

    @Override
    public void onScenarioClose(LoadedScenario scenario) {
        callback.getTopComponent().close();
    }
}
