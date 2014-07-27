package it.unibas.lunatic;

import it.unibas.lunatic.model.algebra.operators.IDelete;
import it.unibas.lunatic.model.algebra.operators.IInsertTuple;
import it.unibas.lunatic.model.algebra.operators.mainmemory.Delete;
import it.unibas.lunatic.model.algebra.operators.mainmemory.InsertTuple;
import it.unibas.lunatic.model.algebra.sql.SQLDelete;
import it.unibas.lunatic.model.algebra.sql.SQLInsertTuple;
import it.unibas.lunatic.model.chase.commons.IChaseSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.IUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.ChaseSQLSTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.dbms.SQLUpdateCell;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.ChaseMainMemorySTTGDs;
import it.unibas.lunatic.model.chase.chasede.operators.mainmemory.MainMemoryUpdateCell;
import it.unibas.lunatic.model.chase.chasemc.operators.AddUserNode;
import it.unibas.lunatic.model.chase.chasemc.operators.ChangeCell;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtEGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseDeltaExtTGDsWithoutCellGroups;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseTreeToString;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckSolution;
import it.unibas.lunatic.model.chase.chasemc.operators.CheckUnsatisfiedDependencies;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDatabaseForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.IChaseDeltaExtTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.IInsertTuplesForTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.IMaintainCellGroupsForTGD;
import it.unibas.lunatic.model.chase.chasemc.operators.IOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.IRunQuery;
import it.unibas.lunatic.model.chase.chasemc.operators.IValueOccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryInsertTuplesForTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.InsertTuplesForTgdsWithoutCellGroups;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryMaintainCellGroupsForTGD;
import it.unibas.lunatic.model.chase.chasemc.operators.StandardOccurrenceHandlerMC;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerWithCacheGreedy;
import it.unibas.lunatic.model.chase.chasemc.operators.OccurrenceHandlerWithCacheLazy;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLRunQuery;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.GreedyJCSCacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.GreedySingleStepJCSCacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.ICacheManager;
import it.unibas.lunatic.model.chase.chasemc.operators.cache.SimpleCacheManagerForLazyOccurrenceHandler;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLInsertTuplesForTGDs;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.SQLMaintainCellGroupsForTGD;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.BuildMainMemoryDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryOIDGenerator;
import it.unibas.lunatic.model.chase.chasemc.operators.mainmemory.MainMemoryRunQuery;
import it.unibas.lunatic.persistence.relational.ExportChaseStepResultsCSV;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorFactory {

    private static Logger logger = LoggerFactory.getLogger(OperatorFactory.class);
    private static OperatorFactory singleton = new OperatorFactory();
    //
    private IRunQuery mainMemoryQueryRunner = new MainMemoryRunQuery();
    private IRunQuery sqlQueryRunner = new SQLRunQuery();
    //
    private IBuildDatabaseForChaseStep mainMemoryDatabaseBuilder = new BuildMainMemoryDBForChaseStep();
    private IBuildDatabaseForChaseStep sqlDatabaseBuilder = new BuildSQLDBForChaseStep();
    //
    private IInsertTuple mainMemoryInsertTuple = new InsertTuple();
    private IInsertTuple sqlInsertTuple = new SQLInsertTuple();
    //
    private IDelete mainMemoryDeleteOperator = new Delete();
    private IDelete sqlDeleteOperator = new SQLDelete();
    //
    private IUpdateCell mainMemoryCellUpdater = new MainMemoryUpdateCell();
    private IUpdateCell sqlCellUpdater = new SQLUpdateCell();
    //
    private IBuildDeltaDB mainMemoryDeltaBuilder = new BuildMainMemoryDeltaDB();
    private IBuildDeltaDB sqlDeltaBuilder = new BuildSQLDeltaDB();
    //
    private IChaseSTTGDs mainMemorySTTGDsChaser = new ChaseMainMemorySTTGDs();
    private IChaseSTTGDs sqlSTTGDsChaser = new ChaseSQLSTTGDs();
    //
    private IOIDGenerator mainMemoryOIDGenerator = new MainMemoryOIDGenerator();
    private IOIDGenerator sqlOIDGenerator = SQLOIDGenerator.getInstance();
    //
    private Map<Scenario, IValueOccurrenceHandlerMC> occurrenceHandlerMap = new HashMap<Scenario, IValueOccurrenceHandlerMC>();

    private OperatorFactory() {
    }

    public static OperatorFactory getInstance() {
        return singleton;
    }

    public void reset() {
        if (logger.isDebugEnabled()) logger.debug("Resetting occurrence handler map...");
        this.occurrenceHandlerMap.clear();
    }

    public IRunQuery getQueryRunner(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryQueryRunner;
        }
        return sqlQueryRunner;
    }

    public IInsertTuple getInsertTuple(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryInsertTuple;
        }
        return sqlInsertTuple;
    }

    public IBuildDatabaseForChaseStep getDatabaseBuilder(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDatabaseBuilder;
        }
        return sqlDatabaseBuilder;
    }

    public IDelete getDeleteOperator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDeleteOperator;
        }
        return sqlDeleteOperator;
    }

    public IUpdateCell getCellUpdater(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryCellUpdater;
        }
        return sqlCellUpdater;
    }

    public IInsertTuplesForTGDs getInsertTuplesForTgds(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryInsertTuplesForTGDs(getInsertTuple(scenario), getQueryRunner(scenario), getOccurrenceHandlerMC(scenario), getOIDGenerator(scenario));
        }
        return new SQLInsertTuplesForTGDs(getOIDGenerator(scenario));
    }

    public InsertTuplesForTgdsWithoutCellGroups getInsertTuplesForTgdsWithoutCellGroups(Scenario scenario) {
        return new InsertTuplesForTgdsWithoutCellGroups(getInsertTuple(scenario), getQueryRunner(scenario), getDatabaseBuilder(scenario), getOIDGenerator(scenario));
    }

    public IBuildDeltaDB getDeltaDBBuilder(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryDeltaBuilder;
        }
        return sqlDeltaBuilder;
    }

    public IChaseSTTGDs getSTChaser(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemorySTTGDsChaser;
        }
        return sqlSTTGDsChaser;
    }

    public IOIDGenerator getOIDGenerator(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return mainMemoryOIDGenerator;
        }
        return sqlOIDGenerator;
    }

    public IValueOccurrenceHandlerMC getOccurrenceHandlerMC(Scenario scenario) {
        IValueOccurrenceHandlerMC occurrenceHandler = occurrenceHandlerMap.get(scenario);
        if (occurrenceHandler != null) {
            return occurrenceHandler;
        }
        String cacheType = scenario.getConfiguration().getCacheType();
        if (cacheType.equals(LunaticConstants.NO_CACHE)) {
            occurrenceHandler = new StandardOccurrenceHandlerMC(getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
        }
        if (cacheType.equals(LunaticConstants.LAZY_CACHE)) {
            ICacheManager cacheManager = new SimpleCacheManagerForLazyOccurrenceHandler();
            occurrenceHandler = new OccurrenceHandlerWithCacheLazy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
        }
//        if (cacheType.equals(LunaticConstants.GREEDY_SIMPLE_CACHE)) {
//            ICacheManager cacheManager = new GreedySimpleCacheManager(getQueryRunner(scenario));
//            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
//        }
//        if (cacheType.equals(LunaticConstants.GREEDY_EHCACHE)) {
//            ICacheManager cacheManager = new GreedyEhCacheManager(getQueryRunner(scenario));
//            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
//        }
        if (cacheType.equals(LunaticConstants.GREEDY_JCS)) {
            ICacheManager cacheManager = new GreedyJCSCacheManager(getQueryRunner(scenario));
            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
        }
//        if (cacheType.equals(LunaticConstants.GREEDY_SINGLESTEP_SIMPLE_CACHE)) {
//            ICacheManager cacheManager = new GreedySingleStepSimpleCacheManager(getQueryRunner(scenario));
//            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
//        }
//        if (cacheType.equals(LunaticConstants.GREEDY_SINGLESTEP_EHCACHE_CACHE)) {
//            ICacheManager cacheManager = new GreedySingleStepEhCacheManager(getQueryRunner(scenario));
//            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
//        }
        if (cacheType.equals(LunaticConstants.GREEDY_SINGLESTEP_JCS_CACHE)) {
            ICacheManager cacheManager = new GreedySingleStepJCSCacheManager(getQueryRunner(scenario));
            occurrenceHandler = new OccurrenceHandlerWithCacheGreedy(cacheManager, getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
        }
        if (occurrenceHandler == null) {
            throw new IllegalArgumentException("Cache type " + cacheType + " unknown");
        }
        occurrenceHandlerMap.put(scenario, occurrenceHandler);
        return occurrenceHandler;
    }

//    public IValueOccurrenceHandlerMC getOccurrenceHandlerMCNoCache(Scenario scenario) {
//        return new StandardOccurrenceHandlerMC(getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getCellUpdater(scenario));
//    }
    public IChaseDeltaExtTGDs getExtTgdChaser(Scenario scenario) {
        if (scenario.getConfiguration().isUseCellGroupsForTGDs()) {
            return new ChaseDeltaExtTGDs(getInsertTuplesForTgds(scenario), getQueryRunner(scenario),
                    getDatabaseBuilder(scenario), getOccurrenceHandlerMC(scenario), getCellUpdater(scenario),
                    getCellGroupMaintainer(scenario));
        }
        return new ChaseDeltaExtTGDsWithoutCellGroups(getInsertTuplesForTgds(scenario), getDatabaseBuilder(scenario));
    }

    public IMaintainCellGroupsForTGD getCellGroupMaintainer(Scenario scenario) {
        if (scenario.isMainMemory()) {
            return new MainMemoryMaintainCellGroupsForTGD(getQueryRunner(scenario), getOccurrenceHandlerMC(scenario));
        } else {
            return new SQLMaintainCellGroupsForTGD(getQueryRunner(scenario), getOccurrenceHandlerMC(scenario));
        }
    }

    public ExportChaseStepResultsCSV getResultExporter(Scenario scenario) {
        return new ExportChaseStepResultsCSV(getDatabaseBuilder(scenario), getQueryRunner(scenario));
    }

    public CheckUnsatisfiedDependencies getUnsatisfiedDependenciesChecker(Scenario scenario) {
        return new CheckUnsatisfiedDependencies(getDatabaseBuilder(scenario), getOccurrenceHandlerMC(scenario), getQueryRunner(scenario));
    }

    public CheckSolution getSolutionChecker(Scenario scenario) {
        return new CheckSolution(getUnsatisfiedDependenciesChecker(scenario), getOccurrenceHandlerMC(scenario), getQueryRunner(scenario), getDatabaseBuilder(scenario));
    }

    public ChaseDeltaExtEGDs getEGDChaser(Scenario scenario) {
        return new ChaseDeltaExtEGDs(getDeltaDBBuilder(scenario), getDatabaseBuilder(scenario), getQueryRunner(scenario), getInsertTuple(scenario), getDeleteOperator(scenario), getOccurrenceHandlerMC(scenario), getUnsatisfiedDependenciesChecker(scenario));
    }

    public ChangeCell getCellChanger(Scenario scenario) {
        return new ChangeCell(getInsertTuple(scenario), getDeleteOperator(scenario), getOccurrenceHandlerMC(scenario));
    }

    public AddUserNode getUserNodeCreator(Scenario scenario) {
        return new AddUserNode(getCellChanger(scenario));
    }

    public ChaseTreeToString getChaseTreeToString(Scenario scenario) {
        return new ChaseTreeToString(getDatabaseBuilder(scenario), getOccurrenceHandlerMC(scenario));
    }
}