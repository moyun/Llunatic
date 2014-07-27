package it.unibas.lunatic.model.chase.chasemc.operators.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.algebra.IAlgebraOperator;
import it.unibas.lunatic.model.algebra.operators.ITupleIterator;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.TGDViolation;
import it.unibas.lunatic.model.chase.chasemc.operators.CellGroupUtility;
import it.unibas.lunatic.model.chase.chasemc.operators.IMaintainCellGroupsForTGD;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.chasemc.operators.IValueOccurrenceHandlerMC;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.IValue;
import it.unibas.lunatic.model.database.Tuple;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.dependency.FormulaVariable;
import it.unibas.lunatic.utility.DependencyUtility;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLMaintainCellGroupsForTGD implements IMaintainCellGroupsForTGD {

    private static Logger logger = LoggerFactory.getLogger(SQLMaintainCellGroupsForTGD.class);

    private IRunQuery queryRunner;
    private IValueOccurrenceHandlerMC occurrenceHandler;

    public SQLMaintainCellGroupsForTGD(IRunQuery queryRunner, IValueOccurrenceHandlerMC occurrenceHandler) {
        this.queryRunner = queryRunner;
        this.occurrenceHandler = occurrenceHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<TGDViolation> extractViolationValues(Dependency extTGD, IAlgebraOperator tgdQuery, IDatabase databaseForStep, Scenario scenario) {
        return Collections.EMPTY_SET;
    }

    @Override
    public void maintainCellGroupsForTGD(Dependency extTGD, IAlgebraOperator tgdSatisfactionQuery, Set<TGDViolation> tgdViolations, IDatabase deltaDB, String stepId, IDatabase databaseForStep, Scenario scenario) {
        if (logger.isDebugEnabled()) logger.debug("Maintaining cell groups for tgd " + extTGD.getId());
        if (logger.isDebugEnabled()) logger.debug("Database for step " + stepId + "\n" + databaseForStep.printInstances());
        ITupleIterator it = queryRunner.run(tgdSatisfactionQuery, scenario.getSource(), databaseForStep);
        if (logger.isDebugEnabled()) logger.debug("Satisfaction query: " + tgdSatisfactionQuery);
        if (logger.isDebugEnabled() && scenario.isMainMemory()) logger.debug("Result: " + LunaticUtility.printIterator(it));
        List<FormulaVariable> universalVariablesInConclusion = DependencyUtility.getUniversalVariablesInConclusion(extTGD);
        Map<FormulaVariable, List<CellGroup>> cellGroupsForVariable = new HashMap<FormulaVariable, List<CellGroup>>();
        List<IValue> lastUniversalValues = null;
        while (it.hasNext()) {
            Tuple tuple = it.next();
            List<IValue> universalValuesInConclusion = DependencyUtility.extractUniversalValuesInConclusion(tuple, extTGD);
            if (logger.isDebugEnabled()) logger.debug("Detected violation on values " + universalValuesInConclusion);
            if (lastUniversalValues == null || LunaticUtility.areDifferentConsideringOrder(lastUniversalValues, universalValuesInConclusion)) {
                //New equivalence class
                if (!cellGroupsForVariable.isEmpty()) {
                    updateCellGroupsInDeltaDB(cellGroupsForVariable, deltaDB, stepId);
                    cellGroupsForVariable.clear();
                }
                lastUniversalValues = universalValuesInConclusion;
            }
            for (FormulaVariable formulaVariable : universalVariablesInConclusion) {
                CellGroupUtility.addCellGroupsForTGDVariableOccurrences(tuple, formulaVariable, cellGroupsForVariable, deltaDB, stepId, occurrenceHandler);
            }
        }
        updateCellGroupsInDeltaDB(cellGroupsForVariable, deltaDB, stepId);
        it.close();
    }

    private void updateCellGroupsInDeltaDB(Map<FormulaVariable, List<CellGroup>> cellGroupsForVariable, IDatabase deltaDB, String stepId) {
        for (FormulaVariable formulaVariable : cellGroupsForVariable.keySet()) {
            List<CellGroup> cellGroups = cellGroupsForVariable.get(formulaVariable);
            CellGroup mergedCellGroup = CellGroupUtility.mergeCellGroupsForTGDs(cellGroups);
            if (logger.isDebugEnabled()) logger.debug("Creating new cell group in step " + stepId + ":\n" + mergedCellGroup);
//            deleteCellGroups(cellGroups, deltaDB, stepId);
            saveNewCellGroup(mergedCellGroup, deltaDB, stepId);
        }
    }

    private void deleteCellGroups(List<CellGroup> cellGroups, IDatabase deltaDB, String stepId) {
        for (CellGroup cellGroup : cellGroups) {
            occurrenceHandler.deleteCellGroup(cellGroup, deltaDB, stepId, true);
        }
    }

    private void saveNewCellGroup(CellGroup mergedCellGroup, IDatabase deltaDB, String stepId) {
        occurrenceHandler.saveNewCellGroup(mergedCellGroup, deltaDB, stepId, true);
    }
}