package it.unibas.lunatic.model.chase.chaseded;

import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasede.DEChaserFactory;
import it.unibas.lunatic.model.chase.chasede.IDEChaser;
import it.unibas.lunatic.model.chase.commons.operators.IChaseSTTGDs;

public class DEDChaserFactory {

    public static IDEDChaser getChaser(Scenario scenario) {
        IChaseSTTGDs stChaser = OperatorFactory.getInstance().getSTChaser(scenario);
        IDEChaser deChaser = DEChaserFactory.getChaser(scenario);
        IDEDDatabaseManager databaseManager = OperatorFactory.getInstance().getDEDDatabaseManager(scenario);
        return new ChaseDEDScenarioGreedy(stChaser, deChaser, databaseManager);
    }
}
