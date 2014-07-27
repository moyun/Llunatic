package it.unibas.lunatic.model.chase.chasede.operators.dbms;

import it.unibas.lunatic.LunaticConstants;
import it.unibas.lunatic.exceptions.DBMSException;
import it.unibas.lunatic.model.chase.chasede.operators.IValueOccurrenceHandlerDE;
import it.unibas.lunatic.model.database.AttributeRef;
import it.unibas.lunatic.model.database.CellRef;
import it.unibas.lunatic.model.database.IDatabase;
import it.unibas.lunatic.model.database.NullValue;
import it.unibas.lunatic.model.database.TupleOID;
import it.unibas.lunatic.model.database.dbms.DBMSDB;
import it.unibas.lunatic.persistence.relational.AccessConfiguration;
import it.unibas.lunatic.persistence.relational.QueryManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDEOccurrenceHandlerWithCache implements IValueOccurrenceHandlerDE {

    private static Logger logger = LoggerFactory.getLogger(SQLDEOccurrenceHandlerWithCache.class);

    private Map<String, List<CellRef>> cache;
    private boolean useCache = true;

    public List<CellRef> getOccurrencesForNull(IDatabase database, NullValue value) {
        AccessConfiguration accessConfiguration = ((DBMSDB) database).getAccessConfiguration();
        String skolem = value.toString();
        if (useCache) {
            loadCache(accessConfiguration);
            List<CellRef> cachedValue = cache.get(skolem);
            if (cachedValue != null) {
                return cachedValue;
            }
            if (logger.isDebugEnabled()) logger.debug("Failed to load cached occurrences for skolem " + skolem);
        }
        ResultSet rs = null;
        String query = "SELECT * FROM " + getSchema(accessConfiguration) + "." + LunaticConstants.SKOLEM_OCC_TABLE + " WHERE skolem = '" + skolem + "'";
        try {
            List<CellRef> result = new ArrayList<CellRef>();
            rs = QueryManager.executeQuery(query, accessConfiguration);
            while (rs.next()) {
                String tableName = rs.getString("table_name");
                String attribute = rs.getString("attribute");
                Object tupleOID = rs.getString("tuple_oid");
                CellRef cellRef = new CellRef(new TupleOID(tupleOID), new AttributeRef(tableName, attribute));
                result.add(cellRef);
            }
            return result;
        } catch (SQLException ex) {
            throw new DBMSException("Unable to read skolem occurrences. " + ex.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(rs);
        }
    }

    public void addOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef) {
        if(!useCache){
            //Occurrences over DB are handled by triggers
            return;
        }
        addCacheOccurrence(value.toString(), cellRef);
    }

    public void removeOccurrenceForNull(IDatabase database, NullValue value, CellRef cellRef) {
        if(!useCache){
            return;
        }
        removeCacheOccurrence(value.toString(), cellRef);
    }

    public void removeOccurrencesForNull(IDatabase database, NullValue value) {
       if(!useCache){
            return;
        }
       cache.remove(value.toString());
    }

    private void loadCache(AccessConfiguration accessConfiguration) {
        if (cache != null) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Creating cache for skolem occurrences...");
        cache = new HashMap<String, List<CellRef>>();
        ResultSet rs = null;
        String query = "SELECT * FROM " + getSchema(accessConfiguration) + "." + LunaticConstants.SKOLEM_OCC_TABLE;
        try {
            rs = QueryManager.executeQuery(query, accessConfiguration);
            while (rs.next()) {
                String skolem = rs.getString("skolem");
                String tableName = rs.getString("table_name");
                String attribute = rs.getString("attribute");
                Object tupleOID = rs.getString("tuple_oid");
                CellRef cellRef = new CellRef(new TupleOID(tupleOID), new AttributeRef(tableName, attribute));
                addCacheOccurrence(skolem, cellRef);
            }
        } catch (SQLException ex) {
            throw new DBMSException("Unable to read skolem occurrences. " + ex.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(rs);
        }
        if (logger.isDebugEnabled()) logger.debug("Cache loaded...");

    }

    private void addCacheOccurrence(String skolem, CellRef cellRef) {
        List<CellRef> result = cache.get(skolem);
        if (result == null) {
            result = new ArrayList<CellRef>();
            cache.put(skolem, result);
        }
        result.add(cellRef);
    }

    private void removeCacheOccurrence(String skolem, CellRef cellRef) {
        List<CellRef> result = cache.get(skolem);
        if (result == null) {
            return;
        }
        result.remove(cellRef);
    }

    private String getSchema(AccessConfiguration accessConfiguration) {
        return LunaticConstants.WORK_SCHEMA;
//        return accessConfiguration.getSchemaName();
    }
}