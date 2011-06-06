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
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Xml Validator Filter<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.schema</code></b>&nbsp;: The path to the
 * default schema to use (from the webapp).</li>
 * <li><i>base</i><b><code>.transform.class</code></b>&nbsp;: <b>Required</b>
 * The class used to transform the element filtered into bytes array.</li>
 * </ul>
 */
public class XmlValidatorFilter extends AbstractFilter
{
    private static final String PROPERTY_BASE_TRANSFORM = ".transform";
    private static final String SEPARATOR = " / ";
    private IObjectTransform _objTransform;

    /**
     * Public constructor
     *
     */
    public XmlValidatorFilter(  )
    {
        super( "XmlValidator", "XML Validator" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     * @param strBase
     *            the strBase
     */
    public void init( String strBase )
    {
        super.init( strBase );
        _objTransform = readInitObjectTransform( strBase + PROPERTY_BASE_TRANSFORM );
    }

    /**
     * @see AbstractFilter#doFilter(IContextChainManager, Object)
     * @param filterManager
     *            the IContextChainManager
     * @param element
     *            The object element
     * @return element
     */
    public Object doFilter( IContextChainManager filterManager, Object element )
    {
        try
        {
            InputStream inStream = null;

            try
            {
                final byte[] buf = _objTransform.transformToBinary( element );
                inStream = new ByteArrayInputStream( buf );

                final InputSource inSource = new InputSource( inStream );
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(  );
                dbf.setNamespaceAware( true );

                DocumentBuilder db = dbf.newDocumentBuilder(  );
                db.setErrorHandler( new Validator(  ) );
                db.parse( inSource );
            }
            catch ( SAXNotSupportedException e )
            {
                AppLogService.error( getImplementationDescription(  ) + SEPARATOR + e.getMessage(  ), e );

                return null;
            }
            catch ( SAXNotRecognizedException e )
            {
                AppLogService.error( getImplementationDescription(  ) + SEPARATOR + e.getMessage(  ), e );

                return null;
            }
            catch ( FactoryConfigurationError e )
            {
                AppLogService.error( getImplementationDescription(  ) + SEPARATOR + e.getMessage(  ), e );

                return null;
            }
            catch ( ParserConfigurationException e )
            {
                AppLogService.error( getImplementationDescription(  ) + SEPARATOR + e.getMessage(  ), e );

                return null;
            }
            catch ( SAXException e )
            {
                AppLogService.error( getImplementationDescription(  ) + SEPARATOR + e.getMessage(  ), e );

                return null;
            }

            return super.doFilter( filterManager, element );
        }
        catch ( IOException e )
        {
            throw new AppException( "An error occured while getting the xml stream for comarquage", e );
        }
    }

    /**
     *
     * my validator
     *
     */
    private class Validator extends DefaultHandler
    {
        /**
         * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
         * @param e the SAXParseException
         * @throws SAXException SAXException
         */
        public void error( SAXParseException e ) throws SAXException
        {
            AppLogService.error( "ERROR line " + e.getLineNumber(  ) + SEPARATOR + e.getMessage(  ), e );
            throw e;
        }

        /**
         * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
         * @param e the SAXParseException
         * @throws SAXException SAXException
         */
        public void fatalError( SAXParseException e ) throws SAXException
        {
            AppLogService.error( "FATAL ERROR line " + e.getLineNumber(  ) + SEPARATOR + e.getMessage(  ), e );
            throw e;
        }

        /**
         * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
         * @param e the SAXParseException
         * @throws SAXException SAXException
         */
        public void warning( SAXParseException e ) throws SAXException
        {
            AppLogService.error( "WARNING line " + e.getLineNumber(  ) + SEPARATOR + e.getMessage(  ), e );
            throw e;
        }
    }
}
