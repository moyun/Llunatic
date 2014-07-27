package it.unibas.lunatic.persistence;

import it.unibas.lunatic.LunaticConfiguration;
import it.unibas.lunatic.OperatorFactory;
import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.exceptions.DAOException;
import it.unibas.lunatic.model.chase.chasemc.costmanager.ICostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.MinCostRepairCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.SamplingCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.SimilarityToMostFrequentCostManager;
import it.unibas.lunatic.model.chase.chasemc.costmanager.StandardCostManager;
import it.unibas.lunatic.model.chase.chasemc.partialorder.FrequencyPartialOrder;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.EmptyDB;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.parser.operators.ParseDependencies;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import it.unibas.lunatic.model.chase.chasemc.partialorder.ScriptPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.StandardPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.DateComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.FloatComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.IValueComparator;
import it.unibas.lunatic.model.chase.chasemc.partialorder.valuecomparator.StandardValueComparator;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNForkUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.AfterLLUNUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.InteractiveUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.StandardUserManager;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.xml.DAOXmlUtility;
import it.unibas.lunatic.persistence.xml.operators.TransformFilePaths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptException;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOMCScenario {

    private static Logger logger = LoggerFactory.getLogger(DAOMCScenario.class);

    private static final String DB_TYPE_MAINMEMORY = "XML";
    private static final String DB_TYPE_MAINMEMORY_GENERATE = "GENERATE";
    private static final String DB_TYPE_DBMS = "DBMS";
    ///////////////////// PARTIAL ORDER
    private static final String PARTIAL_ORDER_STANDARD = "Standard";
    private static final String PARTIAL_ORDER_FREQUENCY = "Frequency";
    private static final String PARTIAL_ORDER_FREQUENCY_FO = "Frequency FO";
    ///////////////////// COST MANAGER
    private static final String COST_MANAGER_STANDARD = "Standard";
    private static final String COST_MANAGER_SIMILARITY = "Similarity";
//    private static final String COST_MANAGER_SIMILARITY_MULTI_REPAIR = "SimilarityMultiRepair";
    private static final String COST_MANAGER_SAMPLING = "Sampling";
    private static final String COST_MANAGER_MINCOST = "MinCost";
    ///////////////////// USER MANAGER
    private static final String USER_MANAGER_STANDARD = "Standard";
    private static final String USER_MANAGER_INTERACTIVE = "Interactive";
    private static final String USER_MANAGER_AFTER_LLUN = "AfterLLUN";
    private static final String USER_MANAGER_AFTER_LLUN_FORK = "AfterLLUNFork";
    private static final String USER_MANAGER_AFTER_FORK = "AfterFork";
    private static final String VALUE_COMPARATOR_FLOAT = "floatComparator";
    private static final String VALUE_COMPARATOR_DATE = "dateComparator";
    private DAOXmlUtility daoUtility = new DAOXmlUtility();
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private DAOMainMemoryDatabase daoMainMemoryDatabase = new DAOMainMemoryDatabase();
    private String fileScenario;

    public Scenario loadScenario(String fileScenario) throws DAOException {
        this.fileScenario = fileScenario;
        try {
            Scenario scenario = new Scenario(fileScenario);
            Document document = daoUtility.buildDOM(fileScenario);
            Element rootElement = document.getRootElement();
            //SOURCE
            Element sourceElement = rootElement.getChild("source");
            IDatabase sourceDatabase = loadDatabase(sourceElement);
            scenario.setSource(sourceDatabase);
            //TARGET
            Element targetElement = rootElement.getChild("target");
            IDatabase targetDatabase = loadDatabase(targetElement);
            scenario.setTarget(targetDatabase);
            //AUTHORITATIVE SOURCES
            Element authoritativeSourcesElement = rootElement.getChild("authoritativeSources");
            List<String> authoritativeSources = loadAuthoritativeSources(authoritativeSourcesElement, scenario);
            scenario.setAuthoritativeSources(authoritativeSources);
            //DEPENDENCIES
            Element dependenciesElement = rootElement.getChild("dependencies");
            loadDependecies(dependenciesElement, scenario);
            //ADDITIONAL ATTRIBUTES
            Element additionalAttributesElement = rootElement.getChild("additionalAttributes");
            loadAdditionalAttributes(additionalAttributesElement, scenario);
            //ORDERING ATTRIBUTES
            Element orderingAttributesElement = rootElement.getChild("orderingAttributes");
            loadOrderingAttributes(orderingAttributesElement, scenario);
            //PARTIAL-ORDER
            Element partialOrderElement = rootElement.getChild("partialOrder");
            IPartialOrder partialOrder = loadPartialOrder(partialOrderElement);
            scenario.setPartialOrder(partialOrder);
            //SCRIPT PARTIAL-ORDER
            Element scriptPartialOrderElement = rootElement.getChild("scriptPartialOrder");
            ScriptPartialOrder scriptPartialORder = loadScriptPartialOrder(scriptPartialOrderElement);
            scenario.setScriptPartialOrder(scriptPartialORder);
            //COST-MANAGER
            Element costManagerElement = rootElement.getChild("costManager");
            ICostManager costManager = loadCostManager(costManagerElement);
            scenario.setCostManager(costManager);
            //USER-MANAGER
            Element userManagerElement = rootElement.getChild("userManager");
            IUserManager userManager = loadUserManager(userManagerElement, scenario);
            scenario.setUserManager(userManager);
            //CONFIGURATION
            Element configurationElement = rootElement.getChild("configuration");
            LunaticConfiguration configuration = loadConfiguration(configurationElement);
            scenario.setConfiguration(configuration);
            return scenario;
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load scenario from file " + fileScenario;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    private IDatabase loadDatabase(Element databaseElement) throws DAOException {
        if (databaseElement == null || databaseElement.getChildren().isEmpty()) {
            return new EmptyDB();
        }
        Element typeElement = databaseElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        String databaseType = typeElement.getValue();
        if (DB_TYPE_MAINMEMORY.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("xml");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <xml>");
            }
            String schemaRelativeFile = xmlElement.getChild("xml-schema").getValue();
            String schemaAbsoluteFile = filePathTransformator.expand(fileScenario, schemaRelativeFile);
            String instanceRelativeFile = xmlElement.getChild("xml-instance").getValue();
            String instanceAbsoluteFile = null; //Optional field
            if (instanceRelativeFile != null && !instanceRelativeFile.trim().isEmpty()) {
                instanceAbsoluteFile = filePathTransformator.expand(fileScenario, instanceRelativeFile);
            }
            return daoMainMemoryDatabase.loadXMLScenario(schemaAbsoluteFile, instanceAbsoluteFile);
        } else if (DB_TYPE_MAINMEMORY_GENERATE.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("generate");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <generate>");
            }
            String plainInstance = xmlElement.getValue();
            return daoMainMemoryDatabase.loadPlainScenario(plainInstance);
        } else if (DB_TYPE_DBMS.equalsIgnoreCase(databaseType)) {
            Element dbmsElement = databaseElement.getChild("access-configuration");
            if (dbmsElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <access-configuration>");
            }
            AccessConfiguration accessConfiguration = new AccessConfiguration();
            accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
            accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
            accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
            accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
            accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
            Element initDbElement = databaseElement.getChild("init-db");
            DBMSDB database = new DBMSDB(accessConfiguration);
            if (initDbElement != null) {
                database.setInitDBScript(initDbElement.getValue());
            }
            return database;
        } else {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Unknown database type " + databaseType);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAdditionalAttributes(Element additionalAttributesElement, Scenario scenario) {
        if (additionalAttributesElement == null || additionalAttributesElement.getChildren().isEmpty()) {
            return;
        }
        List<Element> dependencies = additionalAttributesElement.getChildren("dependency");
        for (Element dependencyElement : dependencies) {
            String dependencyId = dependencyElement.getChildText("id");
            String stringAttributeRef = dependencyElement.getChildText("attribute");
            AttributeRef attributeRef = parseAttributeRef(stringAttributeRef);
            Dependency dependency = scenario.getDependency(dependencyId);
            if (dependency == null) {
                throw new DAOException("Unable to set additional attribute for unkown dependency " + dependencyId);
            }
            dependency.addAdditionalAttribute(attributeRef);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadOrderingAttributes(Element orderingAttributesElement, Scenario scenario) {
        if (orderingAttributesElement == null || orderingAttributesElement.getChildren().isEmpty()) {
            return;
        }
        List<OrderingAttribute> orderingAttributes = new ArrayList<OrderingAttribute>();
        List<Element> orderingAttributeElements = orderingAttributesElement.getChildren("orderingAttribute");
        for (Element orderingAttributeElement : orderingAttributeElements) {
            AttributeRef attribute = parseAttributeRef(orderingAttributeElement.getChildText("attribute"));
            AttributeRef associatedAttribute = parseAttributeRef(orderingAttributeElement.getChildText("associatedAttribute"));
            IValueComparator valueComparator = extractValueComparator(orderingAttributeElement.getChild("valueComparator"));
            OrderingAttribute orderingAttribute = new OrderingAttribute(attribute, associatedAttribute, valueComparator);
            orderingAttributes.add(orderingAttribute);
        }
        scenario.setOrderingAttributes(orderingAttributes);
    }

    private IValueComparator extractValueComparator(Element valueComparatorElement) {
        if (valueComparatorElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <valueComparator>");
        }
        IValueComparator comparator = null;
        if (valueComparatorElement.getChildren().isEmpty()) {
            comparator = new StandardValueComparator();
        } else {
            Element valueComparatorImplElement = (Element) valueComparatorElement.getChildren().get(0);
            String valueComparatorImplName = valueComparatorImplElement.getName();
            if (VALUE_COMPARATOR_FLOAT.equalsIgnoreCase(valueComparatorImplName)) {
                comparator = new FloatComparator();
            } else if (VALUE_COMPARATOR_DATE.equalsIgnoreCase(valueComparatorImplName)) {
                comparator = new DateComparator(valueComparatorImplElement.getAttributeValue("pattern"));
            }
            if (comparator == null) {
                throw new DAOException("Unable to load scenario from file " + fileScenario + ". Unknown value comparator " + valueComparatorImplElement.getName());
            }
        }
        String sort = valueComparatorElement.getAttributeValue("sort");
        comparator.setSort(sort);
        return comparator;
    }

    private IPartialOrder loadPartialOrder(Element partialOrderElement) throws DAOException {
        if (partialOrderElement == null || partialOrderElement.getChildren().isEmpty()) {
            return new StandardPartialOrder();
        }
        Element typeElement = partialOrderElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        String partialOrderType = typeElement.getValue();
        if (PARTIAL_ORDER_STANDARD.equals(partialOrderType)) {
            return new StandardPartialOrder();
        }
        if (PARTIAL_ORDER_FREQUENCY.equals(partialOrderType)) {
            return new FrequencyPartialOrder();
        }
        if (PARTIAL_ORDER_FREQUENCY_FO.equals(partialOrderType)) {
            return new FrequencyPartialOrder();
//            return new FrequencyPartialOrderFO();
        }
        throw new DAOException("Unable to load scenario from file " + fileScenario + ". Unknown partial-order type " + partialOrderType);
    }

    private ScriptPartialOrder loadScriptPartialOrder(Element scriptPartialOrderElement) {
        if (scriptPartialOrderElement == null || scriptPartialOrderElement.getChildren().isEmpty()) {
            return null;
        }
        Element xmlElement = scriptPartialOrderElement.getChild("script");
        if (xmlElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <script>");
        }
        String scriptRelativeFile = xmlElement.getValue();
        String scriptAbsoluteFile = filePathTransformator.expand(fileScenario, scriptRelativeFile);
        try {
            return new ScriptPartialOrder(scriptAbsoluteFile);
        } catch (ScriptException ex) {
            throw new DAOException("Unable to load partial-order script " + scriptAbsoluteFile + ". " + ex.getLocalizedMessage());
        }
    }

    private ICostManager loadCostManager(Element costManagerElement) throws DAOException {
        if (costManagerElement == null || costManagerElement.getChildren().isEmpty()) {
            return new StandardCostManager();
        }
        Element typeElement = costManagerElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        ICostManager costManager = null;
        String costManagerType = typeElement.getValue();
        if (COST_MANAGER_STANDARD.equals(costManagerType)) {
            costManager = new StandardCostManager();
        }
        if (COST_MANAGER_SIMILARITY.equals(costManagerType)) {
            costManager = new SimilarityToMostFrequentCostManager();
            Element similarityStrategyElement = costManagerElement.getChild("similarityStrategy");
            if (similarityStrategyElement != null) {
                ((SimilarityToMostFrequentCostManager) costManager).setSimilarityStrategy(similarityStrategyElement.getValue());
            }
            Element similarityThresholdElement = costManagerElement.getChild("similarityThreshold");
            if (similarityThresholdElement != null) {
                ((SimilarityToMostFrequentCostManager) costManager).setSimilarityThreshold(Double.parseDouble(similarityThresholdElement.getValue()));
            }
        }
        if (COST_MANAGER_SAMPLING.equals(costManagerType)) {
            costManager = new SamplingCostManager();
            Element maxRepairsForStepElement = costManagerElement.getChild("maxRepairsForStep");
            if (maxRepairsForStepElement != null) {
                ((SamplingCostManager) costManager).setMaxRepairsForStep(Integer.parseInt(maxRepairsForStepElement.getValue()));
            }
        }
        if (COST_MANAGER_MINCOST.equals(costManagerType)) {
            costManager = new MinCostRepairCostManager();
        }
        if (costManager == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Unknown cost-manager type " + costManagerType);
        }
        Element doBackwardElement = costManagerElement.getChild("doBackward");
        if (doBackwardElement != null) {
            costManager.setDoBackward(Boolean.parseBoolean(doBackwardElement.getValue()));
        }
        Element doPermutationsElement = costManagerElement.getChild("doPermutations");
        if (doPermutationsElement != null) {
            costManager.setDoPermutations(Boolean.parseBoolean(doPermutationsElement.getValue()));
        }
//        Element chaseTreeSizeThresholdElement = costManagerElement.getChild("chaseTreeSizeThreshold");
//        if (chaseTreeSizeThresholdElement != null) {
//            throw new IllegalArgumentException("Replace chase tree size with leavesThreshold");
//        }
        Element chaseBranchingThresholdElement = costManagerElement.getChild("chaseBranchingThreshold");
        if (chaseBranchingThresholdElement != null) {
            costManager.setChaseBranchingThreshold(Integer.parseInt(chaseBranchingThresholdElement.getValue()));
        }
        Element dependencyLimitElement = costManagerElement.getChild("dependencyLimit");
        if (dependencyLimitElement != null) {
            costManager.setDependencyLimit(Integer.parseInt(dependencyLimitElement.getValue()));
        }
        Element potentialSolutionsThresholdElement = costManagerElement.getChild("potentialSolutionsThreshold");
        if (potentialSolutionsThresholdElement != null) {
            costManager.setPotentialSolutionsThreshold(Integer.parseInt(potentialSolutionsThresholdElement.getValue()));
        }
        return costManager;
    }

    private IUserManager loadUserManager(Element userManagerElement, Scenario scenario) {
        if (userManagerElement == null || userManagerElement.getChildren().isEmpty()) {
            return new StandardUserManager();
        }
        Element typeElement = userManagerElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileScenario + ". Missing tag <type>");
        }
        IUserManager userManager = null;
        String userManagerType = typeElement.getValue();
        if (USER_MANAGER_STANDARD.equals(userManagerType)) {
            userManager = new StandardUserManager();
        }
        if (USER_MANAGER_INTERACTIVE.equals(userManagerType)) {
            userManager = new InteractiveUserManager();
        }
        if (USER_MANAGER_AFTER_FORK.equals(userManagerType)) {
            userManager = new AfterForkUserManager();
        }
        if (USER_MANAGER_AFTER_LLUN.equals(userManagerType)) {
            userManager = new AfterLLUNUserManager(OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario));
        }
        if (USER_MANAGER_AFTER_LLUN_FORK.equals(userManagerType)) {
            userManager = new AfterLLUNForkUserManager(OperatorFactory.getInstance().getOccurrenceHandlerMC(scenario));
        }
        return userManager;
    }

    private LunaticConfiguration loadConfiguration(Element configurationElement) {
        LunaticConfiguration configuration = new LunaticConfiguration();
        if (configurationElement == null || configurationElement.getChildren().isEmpty()) {
            return configuration;
        }
        Element useLimit1Element = configurationElement.getChild("useLimit1");
        if (useLimit1Element != null) {
            configuration.setUseLimit1(Boolean.parseBoolean(useLimit1Element.getValue()));
        }
        Element useCellGroupsForTGDsElement = configurationElement.getChild("useCellGroupsForTGDs");
        if (useCellGroupsForTGDsElement != null) {
            configuration.setUseCellGroupsForTGDs(Boolean.parseBoolean(useCellGroupsForTGDsElement.getValue()));
        }
        Element removeDuplicatesElement = configurationElement.getChild("removeDuplicates");
        if (removeDuplicatesElement != null) {
            configuration.setRemoveDuplicates(Boolean.parseBoolean(removeDuplicatesElement.getValue()));
        }
        Element checkGroundSolutionsElement = configurationElement.getChild("checkGroundSolutions");
        if (checkGroundSolutionsElement != null) {
            configuration.setCheckGroundSolutions(Boolean.parseBoolean(checkGroundSolutionsElement.getValue()));
        }
        Element removeSuspiciousSolutionsElement = configurationElement.getChild("removeSuspiciousSolutions");
        if (removeSuspiciousSolutionsElement != null) {
            configuration.setRemoveSuspiciousSolutions(Boolean.parseBoolean(removeSuspiciousSolutionsElement.getValue()));
        }
        return configuration;
    }

    @SuppressWarnings("unchecked")
    private List<String> loadAuthoritativeSources(Element authoritativeSourcesElement, Scenario scenario) {
        if (authoritativeSourcesElement == null || authoritativeSourcesElement.getChildren().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<String> sources = new ArrayList<String>();
        List<Element> sourceElements = authoritativeSourcesElement.getChildren("source");
        for (Element sourceElement : sourceElements) {
            sources.add(sourceElement.getText());
        }
        return sources;
    }

    private void loadDependecies(Element dependenciesElement, Scenario scenario) throws DAOException {
        if (dependenciesElement == null) {
            return;
        }
        String dependenciesString = dependenciesElement.getValue().trim();
        ParseDependencies generator = new ParseDependencies();
        try {
            generator.generateDependencies(dependenciesString, scenario);
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    private AttributeRef parseAttributeRef(String stringAttributeRef) {
        String[] tokens = stringAttributeRef.split("\\.");
        if (tokens.length != 2) {
            throw new DAOException("Unable to parse attribute " + stringAttributeRef);
        }
        return new AttributeRef(tokens[0], tokens[1]);
    }
}