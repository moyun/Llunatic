package it.unibas.lunatic.model.chase.chaseded.dbms;

import it.unibas.lunatic.Scenario;
import it.unibas.lunatic.model.chase.chaseded.IDEDDatabaseManager;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.relational.LunaticDBMSUtility;
import speedy.SpeedyConstants;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;

public class SQLDEDDatabaseManager implements IDEDDatabaseManager {

    private static final String CLONE_SUFFIX = "_clone";

    public IDatabase cloneTarget(Scenario scenario) {
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration targetConfiguration = target.getAccessConfiguration();
        String originalTargetSchemaName = LunaticDBMSUtility.getSchemaWithSuffix(targetConfiguration, scenario);
        String cloneTargetSchemaName = originalTargetSchemaName + CLONE_SUFFIX;
        cloneSchema(originalTargetSchemaName, cloneTargetSchemaName, targetConfiguration);
        AccessConfiguration workConfiguration = getWorkAccessConfiguration(targetConfiguration);
        String originalWorkSchema = LunaticDBMSUtility.getSchemaWithSuffix(workConfiguration, scenario);
        String cloneWorkSchema = originalWorkSchema + CLONE_SUFFIX;
        cloneSchema(originalWorkSchema, cloneWorkSchema, workConfiguration);
        AccessConfiguration cloneAccessConfiguration = targetConfiguration.clone();
        cloneAccessConfiguration.setSchemaName(cloneTargetSchemaName);
//        DBMSDB clone = new DBMSDB(target, cloneAccessConfiguration); //shallow copy
        DBMSDB clone = new DBMSDB(cloneAccessConfiguration);
        return clone;
    }

    public void restoreTarget(IDatabase clonedDatabase, Scenario scenario) {
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration targetConfiguration = target.getAccessConfiguration();
        AccessConfiguration clonedTargetConfiguration = ((DBMSDB) clonedDatabase).getAccessConfiguration();
        String originalTargetSchemaName = LunaticDBMSUtility.getSchemaWithSuffix(targetConfiguration, scenario);
        String cloneTargetSchemaName = originalTargetSchemaName + CLONE_SUFFIX;
        removeSchema(originalTargetSchemaName, clonedTargetConfiguration);
        cloneSchema(cloneTargetSchemaName, originalTargetSchemaName, clonedTargetConfiguration);
        AccessConfiguration workConfiguration = getWorkAccessConfiguration(targetConfiguration);
        String originalWorkSchema = LunaticDBMSUtility.getSchemaWithSuffix(workConfiguration, scenario);
        String cloneWorkSchema = originalWorkSchema + CLONE_SUFFIX;
        removeSchema(originalWorkSchema, clonedTargetConfiguration);
        cloneSchema(cloneWorkSchema, originalWorkSchema, clonedTargetConfiguration);
    }

    public void removeClone(IDatabase clonedDatabase, Scenario scenario) {
        DBMSDB target = (DBMSDB) scenario.getTarget();
        AccessConfiguration targetConfiguration = target.getAccessConfiguration();
        String originalTargetSchemaName = LunaticDBMSUtility.getSchemaWithSuffix(targetConfiguration, scenario);
        String cloneTargetSchemaName = originalTargetSchemaName + CLONE_SUFFIX;
        removeSchema(cloneTargetSchemaName, targetConfiguration);
        AccessConfiguration workConfiguration = getWorkAccessConfiguration(targetConfiguration);
        String originalWorkSchema = LunaticDBMSUtility.getSchemaWithSuffix(workConfiguration, scenario);
        String cloneWorkSchema = originalWorkSchema + CLONE_SUFFIX;
        removeSchema(cloneWorkSchema, targetConfiguration);
    }

    private void cloneSchema(String src, String dest, AccessConfiguration ac) {
        StringBuilder script = new StringBuilder();
        script.append(getCloneFunction()).append("\n");
        script.append("SELECT clone_schema('").append(src).append("','").append(dest).append("');");
        QueryManager.executeScript(script.toString(), ac, true, true, true, false);
    }

    private void removeSchema(String schema, AccessConfiguration ac) {
        String function = "drop schema " + schema + " cascade;";
        QueryManager.executeScript(function, ac, true, true, false, false);
    }

    private String getCloneFunction() {
        StringBuilder function = new StringBuilder();
        function.append("CREATE OR REPLACE FUNCTION clone_schema(source_schema text, dest_schema text) RETURNS void AS").append("\n");
        function.append("$BODY$").append("\n");
        function.append("DECLARE ").append("\n");
        function.append("  objeto text;").append("\n");
        function.append("  buffer text;").append("\n");
        function.append("BEGIN").append("\n");
        function.append("    EXECUTE 'CREATE SCHEMA ' || dest_schema ;").append("\n");
        function.append("    FOR objeto IN").append("\n");
        function.append("        SELECT table_name::text FROM information_schema.TABLES WHERE table_schema = source_schema").append("\n");
        function.append("    LOOP").append("\n");
        function.append("        buffer := dest_schema || '.' || objeto;").append("\n");
        function.append("        EXECUTE 'CREATE TABLE ' || buffer || ' (LIKE ' || source_schema || '.' || objeto || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS) WITH OIDS';").append("\n");
        function.append("        EXECUTE 'INSERT INTO ' || buffer || '(SELECT * FROM ' || source_schema || '.' || objeto || ')';").append("\n");
        function.append("    END LOOP;").append("\n");
        function.append("END;").append("\n");
        function.append("$BODY$").append("\n");
        function.append("LANGUAGE plpgsql VOLATILE;").append("\n");
        return function.toString();
    }

    private AccessConfiguration getWorkAccessConfiguration(AccessConfiguration accessConfiguration) {
        AccessConfiguration workSchema = accessConfiguration.clone();
        workSchema.setSchemaName(SpeedyConstants.WORK_SCHEMA);
        return workSchema;
    }
}
