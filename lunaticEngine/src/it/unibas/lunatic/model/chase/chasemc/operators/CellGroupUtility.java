package it.unibas.lunatic.model.chase.chasemc.operators;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.ChaseException;
import it.unibas.lunatic.model.chase.chasemc.CellGroup;
import it.unibas.lunatic.model.chase.chasemc.CellGroupCell;
import it.unibas.lunatic.model.chase.chasemc.ChangeDescription;
import it.unibas.lunatic.model.chase.chasemc.Repair;
import it.unibas.lunatic.model.chase.commons.ChaseStats;
import it.unibas.lunatic.model.dependency.FormulaVariableOccurrence;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IValue;

public class CellGroupUtility {

    private static final Logger logger = LoggerFactory.getLogger(CellGroupUtility.class.getName());
    private static final CheckConsistencyOfCellGroups cellGroupChecker = new CheckConsistencyOfCellGroups();

    public static List<AttributeRef> extractAttributeRefs(List<FormulaVariableOccurrence> occurrences) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : occurrences) {
            result.add(formulaVariableOccurrence.getAttributeRef());
        }
        return result;
    }

    public static CellGroup createNewCellGroupFromCell(Cell cell) {
        CellGroup cellGroup = new CellGroup(cell.getValue(), true);
        IValue value = cell.getValue();
        if (cell.getAttributeRef().isSource()) {
            CellGroupCell cellGroupCell = new CellGroupCell(cell.getTupleOID(), cell.getAttributeRef(), value, value, LunaticConstants.TYPE_JUSTIFICATION, true);
            cellGroup.addJustificationCell(cellGroupCell);
        } else if (cell.getAttributeRef().isTarget()) {
            CellGroupCell cellGroupCell = new CellGroupCell(cell.getTupleOID(), cell.getAttributeRef(), value, value, LunaticConstants.TYPE_OCCURRENCE, true);
            cellGroup.addOccurrenceCell(cellGroupCell);
        }
        return cellGroup;
    }

    public static boolean haveAllEqualValues(Set<CellGroupCell> cells) {
        IValue firstValue = cells.iterator().next().getValue();
        for (CellGroupCell cell : cells) {
            if (cell.getValue().equals(firstValue)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static void mergeCells(CellGroup source, CellGroup dest) {
        if (source == dest) {
            throw new IllegalArgumentException("Unable to merge cell group with itself");
        }
        dest.getOccurrences().addAll(source.getOccurrences());
        dest.getJustifications().addAll(source.getJustifications());
        dest.getUserCells().addAll(source.getUserCells());
        if (source.hasInvalidCell() && !dest.hasInvalidCell()) {
            dest.setInvalidCell(source.getInvalidCell());
        }
        dest.addAllAdditionalCells(source.getAdditionalCells());
    }

//    public static List<CellGroup> extractCellGroups(List<EGDEquivalenceClassTupleCellsOLD> tupleGroups) {
//        List<CellGroup> cellGroups = new ArrayList<CellGroup>();
//        for (EGDEquivalenceClassTupleCellsOLD tupleGroup : tupleGroups) {
//            cellGroups.add(tupleGroup.getCellGroupForForwardRepair().clone());
//        }
//        return cellGroups;
//    }
    public static Set<IValue> findDifferentValuesInCellGroupsWithOccurrences(List<CellGroup> cellGroups) {
        Set<IValue> result = new HashSet<IValue>();
        for (CellGroup cellGroup : cellGroups) {
            if (cellGroup.getOccurrences().isEmpty()) {
                continue;
            }
            result.add(cellGroup.getValue());
        }
        return result;
    }

    public static boolean checkContainment(List<CellGroup> cellGroups) {
        Set<CellRef> allCellRefs = new HashSet<CellRef>();
        for (CellGroup cellGroup : cellGroups) {
            allCellRefs.addAll(extractAllCellRefs(cellGroup));
        }
        for (CellGroup cellGroup : cellGroups) {
            Set<CellRef> allCellRefsForCellGroup = extractAllCellRefs(cellGroup);
            if (allCellRefsForCellGroup.equals(allCellRefs)) {
                return true;
            }
        }
        return false;
    }

    public static Set<CellRef> extractAllCellRefs(CellGroup cellGroup) {
        Set<CellRef> result = new HashSet<CellRef>();
        for (CellGroupCell cell : cellGroup.getAllCells()) {
            result.add(new CellRef(cell));
        }
        return result;
    }

    public static Set<CellRef> extractAllCellRefs(Set<Cell> cells) {
        Set<CellRef> result = new HashSet<CellRef>();
        for (Cell cell : cells) {
            result.add(new CellRef(cell));
        }
        return result;
    }

    public static void checkCellGroupConsistency(Repair repair) throws ChaseException {
        long start = new Date().getTime();
        List<CellGroup> cellGroupsToCheck = new ArrayList<CellGroup>();
        for (ChangeDescription changeDescription : repair.getChangeDescriptions()) {
            cellGroupsToCheck.add(changeDescription.getCellGroup());
        }
        try {
            cellGroupChecker.checkConsistencyOfCellGroups(cellGroupsToCheck);
        } catch (ChaseException ex) {
            logger.error("Incorrect repair:\n" + repair);
            throw ex;
        }
        long end = new Date().getTime();
        ChaseStats.getInstance().addStat(ChaseStats.CELL_GROUP_CONSISTENCY_CHECK_TIME, end-start);
    }
}
