package it.unibas.lunatic.test.mc.mainmemory.basicscenarios;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersonsDeps03 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestPersonsDeps03.class);

    public void ttest03() throws Exception { //CFD
        String scenarioName = "persons-deps-03";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_03);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(true);
        DeltaChaseStep result = ChaserFactoryMC.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(4, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }

    public void ttest03b() throws Exception { //CFD
        String scenarioName = "persons-deps-03b";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_03b);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(true);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        DeltaChaseStep result = ChaserFactoryMC.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
        Assert.assertEquals(7, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Temp/expected-" + scenarioName, result);
        checkExpectedSolutions("expected-" + scenarioName, result);
    }

    public void test03c() throws Exception { //CFD
        String scenarioName = "persons-deps-03c";
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.persons_deps_03c);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(true);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        if (logger.isDebugEnabled()) logger.debug("Source"+ scenario.getSource().printInstances());
        DeltaChaseStep result = ChaserFactoryMC.getChaser(scenario).doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenarioName);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Number of solutions: " + resultSizer.getPotentialSolutions(result));
        if (logger.isDebugEnabled()) logger.debug("Number of duplicate solutions: " + resultSizer.getDuplicates(result));
//        Assert.assertEquals(7, resultSizer.getPotentialSolutions(result));
//        Assert.assertEquals(0, resultSizer.getDuplicates(result));
//        checkSolutions(result);
////        exportResults("/Temp/expected-" + scenarioName, result);
//        checkExpectedSolutions("expected-" + scenarioName, result);
    }
}
