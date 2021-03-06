package it.unibas.lunatic.test.mc.mainmemory.tgd;

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

public class TestSyntheticTGD07 extends CheckExpectedSolutionsTest {

    private static Logger logger = LoggerFactory.getLogger(TestSyntheticTGD07.class);

    public void testScenario() throws Exception {
        Scenario scenario = UtilityTest.loadScenarioFromResources(References.synthetic_T07);
        setConfigurationForTest(scenario);
//        ChaserFactory.setDoBackward(false);
//        scenario.getConfiguration().setRemoveDuplicates(false);
        ChaseMCScenario chaser = ChaserFactoryMC.getChaser(scenario);
        DeltaChaseStep result = chaser.doChase(scenario);
        if (logger.isDebugEnabled()) logger.debug(scenario.toString());
        if (logger.isDebugEnabled()) logger.debug(result.toStringWithSort());
        if (logger.isDebugEnabled()) logger.debug("Solutions: " + resultSizer.getSolutions(result));
        checkSolutions(result);
        Assert.assertEquals(40, resultSizer.getPotentialSolutions(result));
        Assert.assertEquals(9, resultSizer.getDuplicates(result));
//        exportResults("/Users/enzoveltri/Temp/lunatic_tmp/expectedSyntheticTGD07", result);
//        checkExpectedSolutions("expectedSyntheticTGD07", result);
    }
}
