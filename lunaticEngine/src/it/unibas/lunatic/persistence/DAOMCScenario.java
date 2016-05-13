package it.unibas.lunatic.persistence;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import java.io.File;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.persistence.xml.DAOXmlUtility;

public class DAOMCScenario {

    private static Logger logger = LoggerFactory.getLogger(DAOMCScenario.class);
    private final DAOXmlUtility daoUtility = new DAOXmlUtility();
    private final DAOMCScenarioStandard daoStandard = new DAOMCScenarioStandard();
    private final DAOMCScenarioCF daoCF = new DAOMCScenarioCF();

    public Scenario loadScenario(String fileScenario) throws DAOException {
        return loadScenario(fileScenario, null);
    }

    public Scenario loadScenario(String fileScenario, String suffix) throws DAOException {
        File file = new File(fileScenario);
        if (!file.exists()) {
            throw new DAOException("File " + fileScenario + " not found");
        }
        Document document = daoUtility.buildDOM(fileScenario);
        Element rootElement = document.getRootElement();
        if (isStandardScenario(rootElement)) {
            return daoStandard.loadScenario(fileScenario, suffix);
        } else {
            return daoCF.loadScenario(fileScenario, suffix);
        }
    }

    private boolean isStandardScenario(Element rootElement) {
        Element dependenciesElement = rootElement.getChild("dependencies");
        return dependenciesElement.getChildren().isEmpty();
    }

}
