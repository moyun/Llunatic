package it.unibas.lunatic;

import it.unibas.lunatic.model.chase.chasemc.costmanager.CostManagerConfiguration;
import it.unibas.lunatic.model.dependency.Dependency;
import it.unibas.lunatic.model.chase.chasemc.partialorder.IPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.partialorder.OrderingAttribute;
import it.unibas.lunatic.model.chase.chasemc.partialorder.ScriptPartialOrder;
import it.unibas.lunatic.model.chase.chasemc.usermanager.IUserManager;
import it.unibas.lunatic.model.chase.chasemc.usermanager.StandardUserManager;
import it.unibas.lunatic.model.dependency.AttributesInSameCellGroups;
import it.unibas.lunatic.model.dependency.DED;
import it.unibas.lunatic.model.dependency.DependencyStratification;
import it.unibas.lunatic.utility.LunaticUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSVirtualDB;
import speedy.model.database.dbms.SQLQueryString;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.MainMemoryVirtualDB;
import speedy.model.database.operators.dbms.IValueEncoder;
import speedy.utility.SpeedyUtility;

public class Scenario {

    private String fileName;
    private String absolutePath;
    private IDatabase source;
    private IDatabase target;
    private String suffix;
    private List<Dependency> stTgds = new ArrayList<Dependency>();
    private List<Dependency> extTgds = new ArrayList<Dependency>();
    private List<Dependency> dcs = new ArrayList<Dependency>();
    private List<Dependency> egds = new ArrayList<Dependency>();
    private List<Dependency> extEgds = new ArrayList<Dependency>();
    private List<Dependency> egdsFromParser = new ArrayList<Dependency>();
    private List<DED> dedstTgds = new ArrayList<DED>();
    private List<DED> dedextTgds = new ArrayList<DED>();
    private List<DED> dedegds = new ArrayList<DED>();
    private List<Dependency> queries = new ArrayList<Dependency>();
    private List<SQLQueryString> sqlQueries = new ArrayList<SQLQueryString>();
    private List<String> authoritativeSources = new ArrayList<String>();
    private IPartialOrder partialOrder;
    private ScriptPartialOrder scriptPartialOrder;
    private List<OrderingAttribute> orderingAttributes = new ArrayList<OrderingAttribute>();
    private CostManagerConfiguration costManagerConfiguration = new CostManagerConfiguration();
    private IUserManager userManager = new StandardUserManager();
    private Set<AttributeRef> attributesWithLabeledNulls;
    private AttributesInSameCellGroups attributesInSameCellGroups;
    private DependencyStratification stratification;
    private IValueEncoder valueEncoder;
    //
    private LunaticConfiguration configuration;

    public Scenario(String fileName, String suffix) {
        this(fileName, suffix, null);
    }

    public Scenario(String fileName, String suffix, LunaticConfiguration configuration) {
        this.fileName = fileName;
        this.suffix = suffix;
        if (configuration == null) {
            this.configuration = new LunaticConfiguration();
        } else {
            this.configuration = configuration;
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isDEScenario() {
        return !isMCScenario() && !isDEDScenario();
    }

    public boolean isMCScenario() {
        return !this.extEgds.isEmpty() || (this.egds.isEmpty() && this.stTgds.isEmpty());
    }

    public boolean isDEDScenario() {
        return (!this.dedstTgds.isEmpty()) || (!this.dedegds.isEmpty()) || (!this.dedextTgds.isEmpty()) || (!this.dedegds.isEmpty());
    }

    public String getFileName() {
        return fileName;
    }

    public IDatabase getSource() {
        return source;
    }

    public void setSource(IDatabase source) {
        this.source = source;
    }

    public IDatabase getTarget() {
        return target;
    }

    public void setTarget(IDatabase target) {
        if (!(target instanceof MainMemoryDB) && !(target instanceof DBMSDB)) {
            throw new IllegalArgumentException();
        }
        this.target = target;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean hasSuffix() {
        return suffix != null && !suffix.trim().isEmpty();
    }

    public List<Dependency> getSTTgds() {
        return stTgds;
    }

    public void setSTTGDs(List<Dependency> stTgds) {
        this.stTgds = stTgds;
    }

    public List<Dependency> getExtTGDs() {
        return extTgds;
    }

    public void setExtTGDs(List<Dependency> eTgds) {
        this.extTgds = eTgds;
    }

    public List<Dependency> getDCs() {
        return dcs;
    }

    public void setDCs(List<Dependency> dcs) {
        this.dcs = dcs;
    }

    public List<Dependency> getEGDs() {
        return egds;
    }

    public void setEGDs(List<Dependency> egds) {
        if (!egds.isEmpty() && !this.extEgds.isEmpty()) {
            throw new IllegalArgumentException("Either egds or extended egds may be specified for a scenario");
        }
        this.egds = egds;
    }

    public List<Dependency> getExtEGDs() {
        return extEgds;
    }

    public void setExtEGDs(List<Dependency> eEgds) {
        if (!eEgds.isEmpty() && !this.egds.isEmpty()) {
            throw new IllegalArgumentException("Either egds or extended egds may be specified for a scenario");
        }
        this.extEgds = eEgds;
    }

    public List<Dependency> getEgdsFromParser() {
        return egdsFromParser;
    }

    public void setEgdsFromParser(List<Dependency> egdsFromParser) {
        this.egdsFromParser = egdsFromParser;
    }

    public List<DED> getDEDstTGDs() {
        return dedstTgds;
    }

    public void setDEDstTGDs(List<DED> dedstTgds) {
        this.dedstTgds = dedstTgds;
    }

    public List<DED> getDEDextTGDs() {
        return dedextTgds;
    }

    public void setDEDextTGDs(List<DED> dedextTgds) {
        this.dedextTgds = dedextTgds;
    }

    public List<DED> getDEDEGDs() {
        return dedegds;
    }

    public void setDEDEGDs(List<DED> dedegds) {
        this.dedegds = dedegds;
    }

    public List<Dependency> getQueries() {
        return queries;
    }

    public void setQueries(List<Dependency> queries) {
        this.queries = queries;
    }

    public IValueEncoder getValueEncoder() {
        return valueEncoder;
    }

    public void setValueEncoder(IValueEncoder valueEncoder) {
        this.valueEncoder = valueEncoder;
    }

    public Dependency getDependency(String dependencyId) {
        for (Dependency dependency : stTgds) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        for (Dependency dependency : extTgds) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        for (Dependency dependency : dcs) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        for (Dependency dependency : egds) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        for (Dependency dependency : extEgds) {
            if (dependency.getId().equals(dependencyId)) {
                return dependency;
            }
        }
        return null;
    }

    public IPartialOrder getPartialOrder() {
        return partialOrder;
    }

    public void setPartialOrder(IPartialOrder partialOrder) {
        this.partialOrder = partialOrder;
    }

    public ScriptPartialOrder getScriptPartialOrder() {
        return scriptPartialOrder;
    }

    public void setScriptPartialOrder(ScriptPartialOrder scriptPartialOrder) {
        this.scriptPartialOrder = scriptPartialOrder;
    }

    public List<OrderingAttribute> getOrderingAttributes() {
        return orderingAttributes;
    }

    public void setOrderingAttributes(List<OrderingAttribute> orderingAttributes) {
        this.orderingAttributes = orderingAttributes;
    }

    public IUserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    public CostManagerConfiguration getCostManagerConfiguration() {
        return costManagerConfiguration;
    }

    public void setCostManagerConfiguration(CostManagerConfiguration costManagerConfiguration) {
        this.costManagerConfiguration = costManagerConfiguration;
    }

    public boolean isMainMemory() {
        return (this.source == null || this.source instanceof MainMemoryDB || this.source instanceof EmptyDB)
                && (this.target instanceof MainMemoryDB || this.target instanceof MainMemoryVirtualDB || this.target instanceof EmptyDB);
    }

    public boolean isDBMS() {
        return (this.source == null || this.source instanceof EmptyDB || this.source instanceof DBMSDB)
                && (this.target instanceof DBMSDB || this.target instanceof DBMSVirtualDB);
    }

    public DependencyStratification getStratification() {
        return stratification;
    }

    public void setStratification(DependencyStratification stratification) {
        this.stratification = stratification;
    }

    public void setConfiguration(LunaticConfiguration configuration) {
        this.configuration = configuration;
    }

    public LunaticConfiguration getConfiguration() {
        return configuration;
    }

    public List<String> getAuthoritativeSources() {
        return authoritativeSources;
    }

    public void setAuthoritativeSources(List<String> authoritativeSources) {
        this.authoritativeSources = authoritativeSources;
    }

    public void addSQLQueryString(SQLQueryString sqlQueryString) {
        this.sqlQueries.add(sqlQueryString);
    }

    public List<SQLQueryString> getSQLQueries() {
        return sqlQueries;
    }

    public Set<AttributeRef> getAttributesWithLabeledNulls() {
        return attributesWithLabeledNulls;
    }

    public void setAttributesWithLabeledNulls(Set<AttributeRef> attributesWithLabeledNulls) {
        this.attributesWithLabeledNulls = attributesWithLabeledNulls;
    }

    public AttributesInSameCellGroups getAttributesInSameCellGroups() {
        return attributesInSameCellGroups;
    }

    public void setAttributesInSameCellGroups(AttributesInSameCellGroups attributesInSameCellGroups) {
        this.attributesInSameCellGroups = attributesInSameCellGroups;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Scenario other = (Scenario) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("=============================== SCENARIO ================================\n");
        if (!isDEScenario()) {
            result.append(this.costManagerConfiguration).append("\n");
            result.append("User manager: \n\t").append(this.userManager).append("\n");
            if (partialOrder != null) result.append("Partial order:\n\t").append(this.partialOrder).append("\n");
            if (!orderingAttributes.isEmpty()) result.append("Ordering attributes:\n").append(LunaticUtility.printCollection(orderingAttributes)).append("\n");
        }
        if (hasSuffix()) result.append("Suffix: ").append(suffix).append("\n");
        if (isMainMemory()) {
            result.append("Source:\n").append(this.getSource()).append("\n");
            result.append("Target:\n").append(this.getTarget()).append("\n");
        } else {
            result.append("Source:\n").append(this.getSource().printSchema()).append("\n");
            result.append("Target:\n").append(this.getTarget().printSchema()).append("\n");
        }
        if (!this.authoritativeSources.isEmpty()) {
            result.append("Authoritative sources: ").append(this.authoritativeSources).append("\n");
        }
        result.append(toStringDependencies());
        if (!this.sqlQueries.isEmpty()) {
            result.append("Final Queries: ").append(SpeedyUtility.printCollection(this.sqlQueries)).append("\n");
        }
        return result.toString();
    }

    public String toStringDependencies() {
        StringBuilder result = new StringBuilder();
        if (!this.stTgds.isEmpty()) {
            result.append("================ ST Tgds ===================\n");
            for (Dependency tgd : this.stTgds) {
                result.append(tgd.toLogicalString()).append("\n");
            }
        }
        if (!this.extTgds.isEmpty()) {
            result.append("================ Extended Tgds ===================\n");
            for (Dependency tgd : this.extTgds) {
                result.append(tgd.toLogicalString()).append("\n");
            }
        }
        if (!this.egds.isEmpty()) {
            result.append("================ Egds ===================\n");
            for (Dependency egd : this.egds) {
                result.append(egd.toLogicalString()).append("\n");
            }
        }
        if (!this.extEgds.isEmpty()) {
            result.append("================ Extended Egds ===================\n");
            for (Dependency egd : this.extEgds) {
                result.append(egd.toLogicalString()).append("\n");
            }
        }
        if (!this.dcs.isEmpty()) {
            result.append("================ Denial Constraints ===================\n");
            for (Dependency dc : this.dcs) {
                result.append(dc.toLogicalString()).append("\n");
            }
        }
        if (!this.dedstTgds.isEmpty()) {
            result.append("================ DED ST Tgds ===================\n");
            for (DED ded : this.dedstTgds) {
                result.append(ded).append("\n");
            }
        }
        if (!this.dedextTgds.isEmpty()) {
            result.append("================ DED Extended Tgds ===================\n");
            for (DED ded : this.dedextTgds) {
                result.append(ded).append("\n");
            }
        }
        if (!this.dedegds.isEmpty()) {
            result.append("================ DED EGDs ===================\n");
            for (DED ded : this.dedegds) {
                result.append(ded).append("\n");
            }
        }
        return result.toString();
    }
}
