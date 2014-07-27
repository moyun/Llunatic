package it.unibas.lunatic.test.mc.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.IBuildDeltaDB;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDBForChaseStep;
import it.unibas.lunatic.model.chase.chasemc.operators.dbms.BuildSQLDeltaDB;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.model.extendedegdanalysis.operators.AnalyzeDependencies;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TDeltaDatabase extends CheckTest {

    private static Logger logger = LoggerFactory.getLogger(TDeltaDatabase.class);

    private IBuildDeltaDB deltaBuilder = new BuildSQLDeltaDB();
    private BuildSQLDBForChaseStep stepBuilder = new BuildSQLDBForChaseStep();

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenario(References.hospital_0_2p_dbms);
        new AnalyzeDependencies().prepareDependenciesAndGenerateStratification(scenario);
        IDatabase deltaDB = deltaBuilder.generate((DBMSDB)scenario.getTarget(), scenario,LunaticConstants.CHASE_STEP_ROOT);
        AccessConfiguration accessConfiguration = ((DBMSDB) scenario.getTarget()).getAccessConfiguration().clone();
        accessConfiguration.setSchemaName(LunaticConstants.WORK_SCHEMA);
        IDatabase stepDB = stepBuilder.extractDatabase("r.f", deltaDB, scenario.getTarget());
        if (logger.isDebugEnabled()) logger.debug(stepDB.toString());
    }
}