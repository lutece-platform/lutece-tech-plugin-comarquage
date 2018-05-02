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
package fr.paris.lutece.plugins.comarquage.util.localnodes;

import com.sun.org.apache.xpath.internal.XPathAPI;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Tools for local file handling
 */
public final class LocalXmlFilesUtils
{
    private static final String PROPERTY_LOCALS_XML_PATH_FRAGMENT = ".path.xml.locals";
    private static final String PROPERTY_ERROR_LOCALS_XML_FRAGMENT = ".message.error.xml.local.title";
    private static final String XPATH_EXPRESSION_TITLE = "//fiche_pratique/titre/text()";

    /**
    * Hidden constructor
    *
    */
    private LocalXmlFilesUtils(  )
    {
    }

    /**
     * Parse a local xml file to get its title
     * @param strXmlFileName the name of the file to retrieve the title of
     * @param strPluginName the plugin name
     * @return the title of the given file
     */
    public static String getTitleFromXmlFile( String strXmlFileName, String strPluginName )
    {
        String strTitle = "";

        try
        {
            String strXmlDirectoryProperty = strPluginName + PROPERTY_LOCALS_XML_PATH_FRAGMENT;
            String strXmlPath = AppPathService.getPath( strXmlDirectoryProperty, strXmlFileName );
            File xmlFile = new File( strXmlPath );

            if ( xmlFile.exists(  ) && xmlFile.canRead(  ) )
            {
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(  );
                DocumentBuilder builder = dbfactory.newDocumentBuilder(  );

                // parse the xml content of the current file
                Document doc = builder.parse( xmlFile );

                Node nodeCurrentTitle = XPathAPI.selectSingleNode( doc, XPATH_EXPRESSION_TITLE );

                // get the current title
                if ( nodeCurrentTitle != null )
                {
                    strTitle = nodeCurrentTitle.getNodeValue(  );
                }
            }
        }
        catch ( Exception e )
        {
            AppLogService.debug( "Can't parse XML file (" + strXmlFileName + "): " + e.getMessage(  ), e );
            strTitle = AppPropertiesService.getProperty( strPluginName + PROPERTY_ERROR_LOCALS_XML_FRAGMENT );

            if ( strTitle == null )
            {
                return "";
            }
            else
            {
                return strTitle;
            }
        }

        return strTitle;
    }
}
