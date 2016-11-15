/*
 *************************************************************************
 * Copyright (c) 2014 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package com.cpubliccn.birt.oda.influxdb.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.cpubliccn.birt.oda.influxdb.i18n.Messages;
import com.cpubliccn.birt.oda.influxdb.util.InfluxAdapter;
import com.ibm.icu.util.ULocale;


/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection
{
    private boolean m_isOpen = false;
    
    private String httpUrl = null;
    private String dbname = null;
    private String username = null;
    private String password = null;
    private InfluxAdapter influxAdapter = null;
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
        if (connProperties == null) {
        	throw new OdaException(Messages.getString("connection_CONNECTION_PROPERTIES_MISSING"));
        }
        this.httpUrl = connProperties.getProperty(CommonConstants.CONN_HTTP_URL);
        this.dbname = connProperties.getProperty(CommonConstants.CONN_DBNAME);
        influxAdapter = new InfluxAdapter(httpUrl, dbname);
	    m_isOpen = influxAdapter.getResponeStatus();        
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException
	{
		if (influxAdapter != null) {
			influxAdapter.close();
		}
	    m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException
	{
        // TODO Auto-generated method stub
		return m_isOpen;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException
	{
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
		return new Query(this.influxAdapter, this.httpUrl, this.dbname);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException
	{
		return 0;	// no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException
	{
	    // do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException
	{
        // do nothing; assumes no transaction support needed
	}

	@Override
	public void setLocale(ULocale arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

}
