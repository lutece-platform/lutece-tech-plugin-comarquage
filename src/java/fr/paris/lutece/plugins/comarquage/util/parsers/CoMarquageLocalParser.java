/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.comarquage.util.parsers;

import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Parser for local cards (comarquage)
 */
public class CoMarquageLocalParser extends DefaultHandler
{
    // -------------
    // - Constants -
    // -------------
    // Plugin name
    private static final String PROPERTY_PLUGIN_NAME = "comarquage.plugin.name";

    // Local cards path
    private static final String PROPERTY_INDEXING_LOCAL_PATH = "comarquage.indexing.localBasePath";
    private static final String PROPERTY_INDEXING_XML_BASE_VAR = "comarquage.path.xml";

    // XPath comparisons
    private static final String PROPERTY_XPATH_CARD = "comarquage.parser.xpath.local.card";
    private static final String PROPERTY_XPATH_DATE = "comarquage.parser.xpath.local.date";
    private static final String PROPERTY_XPATH_TITLE = "comarquage.parser.xpath.local.title";
    private static final String PROPERTY_ATTRIBUTE_URL = "comarquage.parser.xpath.local.attribute.url";

    // Index type
    private static final String PROPERTY_INDEXING_TYPE = "comarquage.indexing.localType";

    // Path contents
    private static final String PROPERTY_PATH_BASE = "lutece.portal.path";
    private static final String PROPERTY_PATH_ID = "comarquage.parser.path.id";
    private static final String PROPERTY_PATH_FIRST_NODE = "comarquage.parser.path.first.node";
    private static final String PATH_PAGE = "page";

    // URL delimiter
    private static final String PROPERTY_URL_DELIMITER = "comarquage.parser.url.local.delimiter";

    // Strings
    private static final String STRING_AMP = "&amp;";
    private static final String STRING_EMPTY = "";
    private static final String STRING_EQUAL = "=";
    private static final String STRING_POINT = ".";
    private static final String STRING_QUESTION = "?";
    private static final String STRING_SLASH = "/";
    private static final String STRING_SPACE = " ";

    // -------------
    // - Variables -
    // -------------
    // List of lucene documents
    private List<Document> _listDocuments;

    // XPath
    private String _strXPath;

    // Contents
    private String _strURL;
    private String _strDate;
    private String _strType;
    private String _strTitle;
    private String _strContents;

    /**
     * Initializes and launches the parsing of the local cards (public constructor)
     */
    public CoMarquageLocalParser(  )
    {
        // Gets the local cards path
        String strLocalBasePath = AppPropertiesService.getProperty( PROPERTY_INDEXING_LOCAL_PATH );
        String strLocalPath = AppPathService.getPath( PROPERTY_INDEXING_XML_BASE_VAR, strLocalBasePath );
        File fileBasePath = new File( strLocalPath );

        // Initializes the document list
        _listDocuments = new ArrayList<Document>(  );

        // Initializes the indexing type
        _strType = AppPropertiesService.getProperty( PROPERTY_INDEXING_TYPE );

        try
        {
            // Initializes the SAX parser
            SAXParserFactory factory = SAXParserFactory.newInstance(  );
            SAXParser parser = factory.newSAXParser(  );

            // Launches the parsing on each local card
            parseAllLocalCards( fileBasePath, parser );
        }
        catch ( ParserConfigurationException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( SAXException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }

    /**
     * Launches the parsing on each local card
     *
     * @param fileBasePath the base path
     * @param parser the SAX parser
     */
    private void parseAllLocalCards( File fileBasePath, SAXParser parser )
    {
        if ( fileBasePath.isFile(  ) )
        {
            // Launches the parsing of this local card (with the current handler)
            try
            {
                parser.parse( fileBasePath.getAbsolutePath(  ), this );
            }
            catch ( SAXException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }
        else
        {
            // Processes all the files of the current directory
            File[] files = fileBasePath.listFiles(  );

            for ( File fileCurrent : files )
            {
                if ( !fileCurrent.getAbsolutePath(  ).endsWith( "CVS" ) )
                {
                    // Launches the parsing on each local card (recursive)
                    parseAllLocalCards( fileCurrent, parser );
                }
            }
        }
    }

    /**
    * Event received when starting the parsing operation
    *
    * @throws SAXException any SAX exception
    */
    public void startDocument(  ) throws SAXException
    {
        // Initializes the XPATH
        _strXPath = STRING_EMPTY;

        // Initializes the contents
        _strURL = STRING_EMPTY;
        _strDate = STRING_EMPTY;
        _strTitle = STRING_EMPTY;
        _strContents = STRING_EMPTY;
    }

    /**
    * Event received at the end of the parsing operation
    *
    * @throws SAXException any SAX exception
    */
    public void endDocument(  ) throws SAXException
    {
        // Sets the ID 
        String strDelimiter = STRING_POINT + AppPropertiesService.getProperty( PROPERTY_URL_DELIMITER );
        String strFirstNode = AppPropertiesService.getProperty( PROPERTY_PATH_FIRST_NODE ) + STRING_SLASH;
        String strId = strFirstNode + _strURL.split( strDelimiter )[0];

        // Sets the full URL
        String strUrlBase = AppPropertiesService.getProperty( PROPERTY_PATH_BASE );
        String strUrlPage = STRING_QUESTION + PATH_PAGE + STRING_EQUAL;
        String strPluginName = AppPropertiesService.getProperty( PROPERTY_PLUGIN_NAME );
        String strUrlId = STRING_AMP + AppPropertiesService.getProperty( PROPERTY_PATH_ID ) + STRING_EQUAL;

        String strFullUrl = strUrlBase + strUrlPage + strPluginName + strUrlId + strId;

        // Converts the date from "dd MMMMM yyyy" to "yyyyMMdd"
        Locale locale = Locale.FRENCH;
        String strDate;

        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd MMMMM yyyy", locale );
            Date dateUpdate = dateFormat.parse( _strDate );

            dateFormat.applyPattern( "yyyyMMdd" );
            strDate = dateFormat.format( dateUpdate );
        }
        catch ( ParseException e )
        {
            strDate = "";
        }

        // Creates a new lucene document
        Document document = new Document(  );

        // Sets the document fields
        // * FIELD_URL		: stored and indexed (without the analyser)
        // * FIELD_DATE		: stored and indexed (without the analyser)
        // * FIELD_UID		: stored and not indexed (the UID already exists in the URL)
        // * FIELD_CONTENTS	: not stored (saves disk space) and indexed (with the analyser)
        // * FIELD_TITLE	: stored and not indexed (the title already exists in the contents)
        // * FIELD_TYPE		: stored and indexed (without the analyser) -> allows to filter the search by type
        document.add( new Field( SearchItem.FIELD_URL, strFullUrl, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        document.add( new Field( SearchItem.FIELD_DATE, strDate, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        document.add( new Field( SearchItem.FIELD_UID, strId, Field.Store.YES, Field.Index.NO ) );
        document.add( new Field( SearchItem.FIELD_CONTENTS, _strContents, Field.Store.NO, Field.Index.ANALYZED ) );
        document.add( new Field( SearchItem.FIELD_TITLE, _strTitle, Field.Store.YES, Field.Index.NO ) );
        document.add( new Field( SearchItem.FIELD_TYPE, _strType, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Adds the new document to the list
        _listDocuments.add( document );
    }

    /**
     * Event received at the start of an element
     *
     * @param uri the Namespace URI
     * @param localName the local name
     * @param qName the qualified XML name
     * @param atts the attributes attached to the element
     *
     * @throws SAXException any SAX exception
     */
    public void startElement( String uri, String localName, String qName, Attributes atts )
        throws SAXException
    {
        // Updates the XPath
        _strXPath += ( STRING_SLASH + qName );

        // Gets the URL (attribute)
        String strXPathCard = AppPropertiesService.getProperty( PROPERTY_XPATH_CARD );

        if ( ( _strXPath != null ) && _strXPath.equals( strXPathCard ) )
        {
            String strAttributeUrl = AppPropertiesService.getProperty( PROPERTY_ATTRIBUTE_URL );
            _strURL = atts.getValue( strAttributeUrl );
        }
    }

    /**
     * Event received at the end of an element
     *
     * @param uri the Namespace URI
     * @param localName the local name
     * @param qName the qualified XML name
     *
     * @throws SAXException any SAX exception
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        // Updates the XPath
        _strXPath = _strXPath.substring( 0, _strXPath.lastIndexOf( STRING_SLASH ) );
    }

    /**
     * Event received when the analyzer encounters text (between two tags)
     *
     * @param ch the characters from the XML document
     * @param start the start position in the array
     * @param length the number of characters to read from the array
     *
     * @throws SAXException any SAX exception
     */
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        // Gets the XPath comparisons properties
        String strXPathDate = AppPropertiesService.getProperty( PROPERTY_XPATH_DATE );
        String strXPathTitle = AppPropertiesService.getProperty( PROPERTY_XPATH_TITLE );

        // Gets the date
        if ( ( _strXPath != null ) && _strXPath.equals( strXPathDate ) )
        {
            _strDate += new String( ch, start, length );
        }

        // Gets the title
        else if ( ( _strXPath != null ) && _strXPath.equals( strXPathTitle ) )
        {
            _strTitle += new String( ch, start, length );
        }

        // Gets the contents
        if ( ( _strContents != null ) && !_strContents.equals( STRING_EMPTY ) )
        {
            _strContents += ( STRING_SPACE + new String( ch, start, length ) );
        }
        else
        {
            _strContents += new String( ch, start, length );
        }
    }

    /**
    * Gets the list of lucene documents
    *
    * @return The list of lucene documents
    */
    public List<Document> getLocalDocuments(  )
    {
        return _listDocuments;
    }
}
