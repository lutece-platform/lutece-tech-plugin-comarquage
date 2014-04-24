/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter;
import fr.paris.lutece.plugins.comarquage.util.cache.IObjectTransform;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


/**
 * Http net Object Accessor <br/>
 *
 * <b>Properties read here: </b>
 * <ul>
 * <li><i>base </i> <b><code>.proxyHost</code> </b>&nbsp;: The host proxy to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.proxyPort</code> </b>&nbsp;: The port proxy to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.proxyUserName</code> </b>&nbsp;: The proxy username to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.proxyPassword</code> </b>&nbsp;: The proxy password to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.hostName</code> </b>&nbsp;: The host name to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.domainName</code> </b>&nbsp;: The domain name to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.realm</code> </b>&nbsp;: The realm to
 * use for http access.</li>
 * <li><i>base </i> <b><code>.baseURL</code> </b>&nbsp;: <b>Required </b>
 * The URL of the documents source.</li>
 * <li><i>base </i> <b><code>.defaultEncoding</code> </b>&nbsp;: Encoding to
 * use if no encoding will be give in the HTTP header.</li>
 * <li><i>base </i> <b><code>.defaultBufferSize</code> </b>&nbsp;: Default
 * buffer size to use if no length will be give in the HTTP header.</li>
 * <li><i>base </i> <b><code>.keyAdapter.class</code> </b>&nbsp;:
 * <b>Required </b> The class used to implements key adapter (transform key to a
 * valid relatif URL path).</li>
 * <li><i>base </i> <b><code>.transform.class</code> </b>&nbsp;: <b>Required
 * </b> The class used to transform the datas received from network.</li>
 * </ul>
 */
public class HttpAccess extends AbstractAccessor
{
    private static final int ERR_NUMBER_COMARQUAGE_IO = 1001;

    // proxy authentication settings
    private static final String PROPERTY_FRAGMENT_PROXY_HOST = ".proxyHost";
    private static final String PROPERTY_FRAGMENT_PROXY_PORT = ".proxyPort";
    private static final String PROPERTY_FRAGMENT_CONNECTION_TIMEOUT = ".connectionTimeout";
    private static final String PROPERTY_FRAGMENT_PROXY_USERNAME = ".proxyUserName";
    private static final String PROPERTY_FRAGMENT_PROXY_PASSWORD = ".proxyPassword";
    private static final String PROPERTY_FRAGMENT_HOST_NAME = ".hostName";
    private static final String PROPERTY_FRAGMENT_DOMAIN_NAME = ".domainName";
    private static final String PROPERTY_FRAGMENT_REALM = ".realm";

    // other settings
    private static final String PROPERTY_FRAGMENT_BASE_URL = ".baseURL";
    private static final String PROPERTY_FRAGMENT_DEFAULT_ENCODING = ".defaultEncoding";
    private static final String PROPERTY_FRAGMENT_DEFAULT_BUFFER_SIZE = ".defaultBufferSize";
    private static final String PROPERTY_BASE_KEY_ADAPTER = ".keyAdapter";
    private static final String PROPERTY_BASE_TRANSFORM = ".transform";
    private static final String PARAMETER_CONTENT_LENGTH = "Content-Length";

    // private fields
    private IKeyAdapter _keyAdapter;
    private IObjectTransform _objTransform;
    private String _strProxyHost;
    private String _strProxyPort;
    private String _strConnectionTimeout;
    private String _strProxyUserName;
    private String _strProxyPassword;
    private String _strHostName;
    private String _strDomainName;
    private String _strRealm;
    private String _strBaseURL;
    private String _strDefaultEncoding;
    private int _nDefaultBufferSize;

    /**
     * Public constructor
     *
     */
    public HttpAccess(  )
    {
        super( "HttpFilter", "HTTP network access reader" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
     * @param strBase the prefix of params to search
     */
    public void init( String strBase )
    {
        _strProxyHost = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PROXY_HOST );
        _strProxyPort = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PROXY_PORT );
        _strConnectionTimeout = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_CONNECTION_TIMEOUT );

        _strProxyUserName = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PROXY_USERNAME );
        _strProxyPassword = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PROXY_PASSWORD );
        _strHostName = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_HOST_NAME );
        _strDomainName = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_DOMAIN_NAME );
        _strRealm = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_REALM );

        _strBaseURL = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_BASE_URL );
        _strDefaultEncoding = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_DEFAULT_ENCODING );

        final String strDefaultBufferSize = AppPropertiesService.getProperty( strBase +
                PROPERTY_FRAGMENT_DEFAULT_BUFFER_SIZE );

        if ( strDefaultBufferSize != null )
        {
            try
            {
                _nDefaultBufferSize = Integer.parseInt( strDefaultBufferSize );
            }
            catch ( NumberFormatException e )
            {
                throw new RuntimeException( strBase + PROPERTY_FRAGMENT_DEFAULT_BUFFER_SIZE +
                    " must be a valid integer value (' " + strDefaultBufferSize + "')." );
            }
        }

        _keyAdapter = readInitKeyAdapter( strBase + PROPERTY_BASE_KEY_ADAPTER );
        _objTransform = readInitObjectTransform( strBase + PROPERTY_BASE_TRANSFORM );
    }

    /**
     * @see AbstractCache#doSearch(IContextChainManager, Serializable)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @return the element in cache
     */
    public Object doSearch( IContextChainManager filterManager, Serializable key )
    {
        final Object adaptedKey = _keyAdapter.adaptKey( key );

        AppLogService.debug( "HttpAcces: URL '" + _strBaseURL + adaptedKey + "'" );

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient(  );

        String strUrl = _strBaseURL + adaptedKey;
        HttpMethodBase method = new GetMethod( strUrl );

        // if connection timeout found, set the corresponding elements
        if ( ( _strConnectionTimeout != null ) )
        {
            client.setTimeout( Integer.parseInt( _strConnectionTimeout ) );
        }

        // if proxy host and port found, set the correponding elements
        if ( ( _strProxyHost != null ) &&( !_strProxyHost.equals("") ) && ( _strProxyPort!=null )&& ( !_strProxyPort.equals("") ) )
        {
            client.getHostConfiguration(  ).setProxy( _strProxyHost, Integer.parseInt( _strProxyPort ) );
        }

        Credentials cred = null;

        //  if hostname and domain name found, consider we are in NTLM authentication scheme
        // else if only username and password found, use simple UsernamePasswordCredentials
        if ( ( _strHostName != null ) && ( _strDomainName != null ) )
        {
            cred = new NTCredentials( _strProxyUserName, _strProxyPassword, _strHostName, _strDomainName );
        }
        else if ( ( _strProxyUserName != null ) && ( _strProxyPassword != null ) )
        {
            cred = new UsernamePasswordCredentials( _strProxyUserName, _strProxyPassword );
        }

        if ( cred != null )
        {
            client.getState(  ).setProxyCredentials( _strRealm, _strProxyHost, cred );
            client.getState(  ).setAuthenticationPreemptive( true );
            method.setDoAuthentication( true );
        }

        try
        {
            // Execute the method.
            int resultCode = client.executeMethod( method );

            // Handle returned status code
            if ( ( HttpStatus.SC_MULTI_STATUS < resultCode ) || ( HttpStatus.SC_OK > resultCode ) )
            {
                throw new AppException( "An error occured while getting the xml stream for comarquage (errorCode = " +
                    ERR_NUMBER_COMARQUAGE_IO + ", httpError = " + resultCode );
            }

            // Read the response body.
            InputStream in = method.getResponseBodyAsStream(  );

            // Deal with the response.
            String encoding = method.getResponseCharSet(  );

            // if encoding not retrieved, use default one
            if ( encoding == null )
            {
                encoding = _strDefaultEncoding;
            }

            // retrieve content-length header
            String strContentLength = null;
            Header contentLengthHeader = method.getResponseHeader( PARAMETER_CONTENT_LENGTH );

            if ( contentLengthHeader != null )
            {
                strContentLength = contentLengthHeader.getValue(  );
            }

            int contentLength = 0;

            if ( strContentLength != null )
            {
                try
                {
                    contentLength = Integer.parseInt( strContentLength );
                }
                catch ( NumberFormatException e1 )
                {
                    contentLength = 0;
                }
            }

            // if content-length not retrieve, or retrieved value corrupted, use default value
            if ( contentLength <= 0 )
            {
                contentLength = _nDefaultBufferSize;
            }

            final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream( contentLength );

            byte[] buf = new byte[_nDefaultBufferSize];
            int len = 0;

            while ( len >= 0 )
            {
                len = in.read( buf );

                if ( len > 0 )
                {
                    arrayOut.write( buf, 0, len );
                }
            }

            in.close(  );
            arrayOut.close(  );

            return _objTransform.transformToObject( arrayOut.toByteArray(  ) );
        }
        catch ( IOException e )
        {
            throw new AppException( "An error occured while getting the xml stream for comarquage", e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection(  );
        }
    }
}
