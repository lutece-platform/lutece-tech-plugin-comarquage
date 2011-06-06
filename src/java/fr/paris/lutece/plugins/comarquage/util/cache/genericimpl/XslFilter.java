/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.comarquage.util.cache.genericimpl;

import fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager;
import fr.paris.lutece.plugins.comarquage.util.cache.IObjectTransform;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;


/**
 * XSL Filter<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.xslBaseVar</code></b>&nbsp;: <b>Required</b> The path to the default schema to use.</li>
 * <li><i>base></i><b><code>.xslPathByRef</code></b>&nbsp;: <b>Optionnal</b>If not specified or set to 0, xslPath refers to a filename. Otherwise, xslPath refers to another property.</li>
 * <li><i>base</i><b><code>.xslPath</code></b>&nbsp;: <b>Required</b> The path to the default schema to use.</li>
 * <li><i>base</i><b><code>.transformA.class</code></b>&nbsp;: <b>Required</b> The class used to transform the element filtered into bytes array.</li>
 * <li><i>base</i><b><code>.transformB.class</code></b>&nbsp;: <b>Required</b> The class used to transform the result of XSL transform into string.</li>
 * </ul>
 */
public class XslFilter extends AbstractFilter
{
    private static final String PROPERTY_FRAGMENT_XSL_BASE_PROPERTIE = ".xslBaseVar";
    private static final String PROPERTY_FRAGMENT_XSL_PATH = ".xslPath";
    private static final String PROPERTY_FRAGMENT_XSL_IS_PATH_BY_REF = ".xslPathByRef";
    private static final String PROPERTY_BASE_TRANSFORM_A = ".transformA";
    private static final String PROPERTY_BASE_TRANSFORM_B = ".transformB";
    private static final String PROPERTY_FRAGMENT_CDC_CODE = ".code";
    private static final String PROPERTY_FRAGMENT_INSERT_SERVICE_TYPE = ".insert_service_type";
    private static final String PROPERTY_PATHLABEL_FRAGMENT = ".pagePathLabel";
    private static final String PARAMETER_INITIAL_KEY = "initialKey";
    private static final String PARAMETER_PLUGIN_NAME = "pluginName";
    private static final String PARAMETER_ADMIN_PATH = "adminPath";
    private static final String PARAMETER_LINK_SERVICE_ID = "linkServiceId";
    private static final String PARAMETER_CDC_CODE = "selectedCdcCode";
    private static final String PARAMETER_ROOT_PATH_LABEL = "rootPathLabel";
    private static final String PATH_ADMIN_JSP = "/jsp/admin/plugins/comarquage";
    private IObjectTransform _objTransformA;
    private IObjectTransform _objTransformB;
    private String _strXslBasePathVar;
    private String _strXslFile;
    private String _strAdminPathValue;
    private String _strInsertServiceType;

    /**
     * Public constructor
     *
     */
    public XslFilter(  )
    {
        super( "XslValidator", "XSL Filter" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
         * @param strBase the base
     */
    public void init( String strBase )
    {
        super.init( strBase );

        _strXslBasePathVar = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_XSL_BASE_PROPERTIE );

        if ( _strXslBasePathVar == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_XSL_BASE_PROPERTIE + " must be define." );
        }

        String strXslFileByRef = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_XSL_IS_PATH_BY_REF );

        if ( ( strXslFileByRef == null ) || ( Integer.parseInt( strXslFileByRef ) == 0 ) )
        {
            _strXslFile = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_XSL_PATH );
        }
        else
        {
            String strXslFileProperty = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_XSL_PATH );
            _strXslFile = AppPropertiesService.getProperty( strXslFileProperty );
        }

        if ( _strXslFile == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_XSL_PATH + " must be define." );
        }

        //  String strWebbAppPath = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PATH );
        String strWebbAppPath = AppPathService.getWebAppPath(  );

        if ( strWebbAppPath == null )
        {
            throw new RuntimeException( strWebbAppPath + " must be define." );
        }

        String strJspPath = PATH_ADMIN_JSP;

        if ( ( strJspPath != null ) && ( strWebbAppPath != null ) )
        {
            _strAdminPathValue = strWebbAppPath + strJspPath;
        }

        _strInsertServiceType = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_INSERT_SERVICE_TYPE );

        _objTransformA = readInitObjectTransform( strBase + PROPERTY_BASE_TRANSFORM_A );
        _objTransformB = readInitObjectTransform( strBase + PROPERTY_BASE_TRANSFORM_B );
    }

    /**
     * @see AbstractFilter#doFilter(IContextChainManager, Object)
         * @param filterManager the IContextChainManager
         * @param element The element
         * @return doFilter
     */
    public Object doFilter( IContextChainManager filterManager, Object element )
    {
        try
        {
            String strContent = null;

            InputStream inStream = null;
            StreamSource sourceStyleSheet = null;

            try
            {
                String strXslPath = AppPathService.getPath( _strXslBasePathVar, _strXslFile );
                File fileXsl = new File( strXslPath );

                sourceStyleSheet = new StreamSource( fileXsl );

                final byte[] buf = _objTransformA.transformToBinary( element );
                inStream = new ByteArrayInputStream( buf );

                final StreamSource inSource = new StreamSource( inStream );

                try
                {
                    Map<String, String> params = new HashMap<String, String>(  );
                    String strPluginName = filterManager.getPluginName(  );

                    // set the root path of the xpage. Use the property defined for the plugin.
                    String strPathLabel = AppPropertiesService.getProperty( strPluginName +
                            PROPERTY_PATHLABEL_FRAGMENT );

                    params.put( PARAMETER_PLUGIN_NAME, strPluginName );

                    if ( strPathLabel != null )
                    {
                        params.put( PARAMETER_ROOT_PATH_LABEL, strPathLabel );
                    }

                    params.put( PARAMETER_INITIAL_KEY, filterManager.getIntialKey(  ).toString(  ) );

                    String strCdcCode = AppPropertiesService.getProperty( strPluginName + PROPERTY_FRAGMENT_CDC_CODE );

                    if ( strCdcCode != null )
                    {
                        params.put( PARAMETER_CDC_CODE, strCdcCode );
                    }

                    if ( _strAdminPathValue != null )
                    {
                        params.put( PARAMETER_ADMIN_PATH, _strAdminPathValue );
                    }

                    if ( _strInsertServiceType != null )
                    {
                        params.put( PARAMETER_LINK_SERVICE_ID, _strInsertServiceType );
                    }

                    XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                    strContent = xmlTransformerService.transformBySourceWithXslCache( inSource, sourceStyleSheet,
                            strXslPath, params, null );
                }
                catch ( Exception e )
                {
                    AppLogService.debug( "Can't parse XML: " + e.getMessage(  ), e );

                    throw new AppException( "An error occured while getting the xml stream for comarquage", e );
                }
            }
            finally
            {
                if ( inStream != null )
                {
                    inStream.close(  );
                }

                if ( ( sourceStyleSheet != null ) && ( sourceStyleSheet.getInputStream(  ) != null ) )
                {
                    sourceStyleSheet.getInputStream(  ).close(  );
                }
            }

            final Object result;

            if ( strContent != null )
            {
                result = _objTransformB.transformToObject( strContent.getBytes(  ) );
            }
            else
            {
                result = null;
            }

            return super.doFilter( filterManager, result );
        }
        catch ( IOException e )
        {
            throw new AppException( "An error occured while getting the xml stream for comarquage", e );
        }
    }
}
