/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.gui.window.db;

import it.unibas.lunatic.gui.ExplorerTopComponent;
import it.unibas.lunatic.gui.node.TableNode;
import it.unibas.lunatic.gui.node.TableTupleNode;
import it.unibas.lunatic.model.database.Cell;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Antonio Galotta
 */
public abstract class TableWindow extends ExplorerTopComponent {


    public abstract TableNode getTableNode();

    @Override
    public void open() {
        super.open();
        super.requestActive();
    }

    public abstract TableTupleNode getSelectedNode();

    public abstract Cell getSelectedCell() throws IllegalAccessException, InvocationTargetException;

    public abstract void updateTable();
}