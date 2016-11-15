package com.cpubliccn.birt.oda.influxdb.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;

public class InfluxAdapter {
	private String host;
	private String dbname;
	private String dbuser;
	private String dbpass;	
	
	private InfluxDB influxdb;
	private QueryResult queryResult;
	private boolean hasError;
	
	public InfluxAdapter(String host, String dbname) {
		this(host, dbname, "admin", "admin");
	}
	
	public InfluxAdapter(String host, String dbname, String dbuser, String dbpass) {
		this.host = host;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpass = dbpass;
		this.init();
	}
	
	private void init() {
		influxdb = InfluxDBFactory.connect(host, dbuser, dbpass);
	}	
	
	public boolean getResponeStatus() {
		try {
			Pong pong = influxdb.ping();
			pong.getResponseTime();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
	public void executeQuery(String queryText) {
		Query query = new Query(queryText, this.dbname);
		queryResult = influxdb.query(query);
		hasError = queryResult.hasError();	
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public List<String> getSeriesColumns(int resultIndex, int seriesIndex) {
		return queryResult.getResults().get(resultIndex).getSeries().get(seriesIndex).getColumns();
	}
	
	public Map<String, String> getSeriesTag(int resultIndex, int seriesIndex) {
		return queryResult.getResults().get(resultIndex).getSeries().get(seriesIndex).getTags();
	}
	
	public List<List<Object>> getSeriesValues(int resultIndex, int seriesIndex) {
		return queryResult.getResults().get(resultIndex).getSeries().get(seriesIndex).getValues();
	}
	
	   public Object[][] copyValueToArray() {
			Object[][] rowSet = null;;
	        List<Series> seriesList = queryResult.getResults().get(0).getSeries();

	        if (seriesList == null || seriesList.size() == 0) {
	            return new Object[0][0];
	        }
	        
	        Map<String, String> tagsTemplateMap = seriesList.get(0).getTags();
	        String[] tagNames = new String[tagsTemplateMap.size()];

	        int tagsIndex = 0;
	        for (Iterator<String> it = tagsTemplateMap.keySet().iterator();
	             it.hasNext();) {
	            tagNames[tagsIndex] = it.next();
	            tagsIndex++;
	        }

	        List<List<Object>> valueList = new ArrayList<List<Object>>();
	        for (Series series : seriesList) {
	            Map<String, String> tagsMap = series.getTags();
	            for (List<Object> valueArray : series.getValues()) {
	                for (int i = 0; i < tagNames.length; i++) {
	                    valueArray.add(i, tagsMap.get(tagNames[i]));
	                }
	                valueList.add(valueArray);
	            }
	        }

	        int rowCount = valueList.size();
	        int colCount = valueList.get(0).size();
	        rowSet = new Object[rowCount][colCount];
	        for (int row = 0; row < rowCount; row++) {
	            List<Object> rowData = valueList.get(row);
	            for (int col = 0; col < colCount; col++) {
	            	rowSet[row][col] = rowData.get(col);
	            }
	        }
	        return rowSet;

	    }

	    public void close() {

	    }

	    public static void main(String[] args) {
	        String url = "http://10.8.159.107:8086";
	        String dbname = "seecloud";
//	        String sql = "select value  from NT_DISK_TOTAL where time < '2016-11-01 23:59:59' group by \"name\", \"subname\",\"units\" order by time desc limit 1";
	        String sql = "select last(value) from TONGWEB_POOL_maxActive  where time > '2016-11-12 00:00:00' and time < '2016-11-12 23:59:59' group by \"name\", \"subname\"";
	        InfluxAdapter adapter = new InfluxAdapter(url, dbname);
	        adapter.executeQuery(sql);
	        Object[][] results = adapter.copyValueToArray();
	        for (int row = 0; row < results.length; row++) {
	            for (int col = 0; col < results[0].length; col++) {
	                System.out.print(results[row][col] + "\t");
	            }
	            System.out.println("");
	        }
	    }
	}
