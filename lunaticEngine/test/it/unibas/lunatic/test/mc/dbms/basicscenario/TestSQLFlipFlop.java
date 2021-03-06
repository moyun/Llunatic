package it.unibas.lunatic.test.mc.dbms.basicscenario;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chasemc.operators.ChaseMCScenario;
import it.unibas.lunatic.model.chase.chasemc.DeltaChaseStep;
import it.unibas.lunatic.model.chase.commons.operators.ChaserFactoryMC;
import it.unibas.lunatic.test.References;
import it.unibas.lunatic.test.UtilityTest;
import it.unibas.lunatic.test.checker.CheckExpectedSolutionsTest;
import junit.framework.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSQLFlipFlop extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSQLFlipFlop.class);

    public void testScenarioFlipFlop() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.flipflop_dmbs);
        setConfigurationForTest(scenario);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenario);
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
//        if (logger.isDebugEnabled()) logger.debug(result.toShortString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(2, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedFlipFlop", result);
        checkExpectedSolutions("expectedFlipFlop", result);
    }

    public void testScenarioContraddicting() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.contraddicting_dmbs);
        setConfigurationForTest(scenario);
        scenario.getConfiguration().setRemoveDuplicates(false);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenario);
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(32, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
    }

    public void testScenarioContraddictingForwardOnly() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.contraddicting_dmbs);
        setConfigurationForTest(scenario);
        scenario.getCostManagerConfiguration().setDoPermutations(false);
        scenario.getCostManagerConfiguration().setDoBackward(false);
        if (logger.isDebugEnabled()) logger.debug("Scenario " + scenario);
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        Assert.assertEquals(1, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(0, resultSizer.getDuplicates(result));
        checkSolutions(result);
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedContraddictingForwardOnly", result);
        checkExpectedSolutions("expectedContraddictingForwardOnly", result);
    }
}
