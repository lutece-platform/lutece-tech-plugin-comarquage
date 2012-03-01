/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.comarquage.service;

import com.sun.org.apache.xpath.internal.XPathAPI;

import fr.paris.lutece.plugins.comarquage.business.Card;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * This class is responsible for making a list of the local cards available.
 */
public final class CoMarquageLocalListing
{
    public static final String PARAMETER_MAP_LOCAL_CARD = "MAP_LOCAL_FICHE";
    public static final String PARAMETER_MAP_PARENT_ID = "MAP_PARENT_ID";
    private static final String PROPERTY_LOCALS_XML_PATH_FRAGMENT = ".path.xml.locals";
    private static final String XPATH_EXPRESSION_PARENT_ID = "//fiche_localisee/@parentId";
    private static final String XPATH_EXPRESSION_URL = "//fiche_pratique/@url";
    private static final String XPATH_EXPRESSION_TITLE = "//fiche_pratique/titre/text()";

    /**
     * _mapPlugin is a hashmap of <br/>
     *  - key :  the pluginName <br/>
     *  - value : the hashmap referencing the local cards' ids. This hashmap is filled by processUpdatingLocalList</br>
     *  This allows the multi-instanciation of the plugin.
     * */
    private static Map<String, Map> _mapPlugin = new HashMap<String, Map>(  );

    /**
     * Creates a new CoMarquageLocalListing object
     */
    private CoMarquageLocalListing(  )
    {
    }

    /**
     * Process the listing, fills the hashmap of the (id, collection of local themes/cards)
     * @param plugin the plugin
     * @return The result of this process (strLogs)
     */
    public static synchronized String processUpdatingLocalList( Plugin plugin )
    {
        //String buffer for building the response page
        StringBuffer strLogs = new StringBuffer(  );

        // Define Locals directory
        String strLocalDirectoryName = AppPathService.getPath( plugin.getName(  ) + PROPERTY_LOCALS_XML_PATH_FRAGMENT,
                "" );

        // initialise a temporary hashmap that will be filled
        Map<String, Map> coupleOfMaps = new HashMap<String, Map>(  );
        Map<String, String> tempMapLocalCard = new HashMap<String, String>(  );
        Map<String, Collection<Card>> tempMapParentId = new HashMap<String, Collection<Card>>(  );

        List<File> listFiles = null;

        try
        {
            listFiles = FileSystemUtil.getFiles( strLocalDirectoryName, "" );
            listFiles.addAll( FileSystemUtil.getSubDirectories( strLocalDirectoryName, "" ) );
        }
        catch ( DirectoryNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        scanDirectoryFile( tempMapParentId, tempMapLocalCard, listFiles );

        coupleOfMaps.put( PARAMETER_MAP_LOCAL_CARD, tempMapLocalCard );
        coupleOfMaps.put( PARAMETER_MAP_PARENT_ID, tempMapParentId );

        // get the plugin name and fill the hashmap
        String strKeyPluginName = plugin.getName(  );
        _mapPlugin.put( strKeyPluginName, coupleOfMaps );

        return strLogs.toString(  );
    }

    /**
     * Scans the directory
     * @param linksMap the links map
     * @param tempMapLocalCard the temp card
     * @param listFiles the list of files
     */
    private static void scanDirectoryFile( Map<String, Collection<Card>> linksMap,
        Map<String, String> tempMapLocalCard, List<File> listFiles )
    {
        // loop through all the files of the list
        if ( listFiles.size(  ) == 0 )
        {
            return;
        }

        DocumentBuilder builder;

        try
        {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(  );
            builder = dbfactory.newDocumentBuilder(  );
        }
        catch ( ParserConfigurationException e )
        {
            // This case can't exist, many xml file have been parsed before this function call
            AppLogService.error( "Can't parse any XML file: " + e.getMessage(  ), e );
            throw new RuntimeException( "Bad server configuration: " + e.getMessage(  ) );
        }

        for ( File file : listFiles )
        {
            if ( file.isDirectory(  ) && !file.getAbsolutePath(  ).endsWith( "CVS" ) )
            {
                scanDirectoryFile( linksMap, tempMapLocalCard, Arrays.asList( file.listFiles(  ) ) );
            }

            if ( !file.isFile(  ) )
            {
                continue;
            }

            // file is a "file type"
            try
            {
                // parse the xml content of the current file
                Document doc = builder.parse( file );

                Node nodeParentId = XPathAPI.selectSingleNode( doc, XPATH_EXPRESSION_PARENT_ID );
                Node nodeCurrentId = XPathAPI.selectSingleNode( doc, XPATH_EXPRESSION_URL );
                Node nodeCurrentTitle = XPathAPI.selectSingleNode( doc, XPATH_EXPRESSION_TITLE );

                String strParentId = null;
                String strCurrentId = null;
                String strCurrentTitle = null;
                ArrayList<String> listParents = new ArrayList<String>(  );

                // get the parentId
                if ( nodeParentId != null )
                {
                    strParentId = nodeParentId.getNodeValue(  );
                }

                getListParentId( strParentId, listParents );

                // get the currentId
                if ( nodeCurrentId != null )
                {
                    strCurrentId = nodeCurrentId.getNodeValue(  );
                    strCurrentId = strCurrentId.substring( 0, strCurrentId.length(  ) - 4 );
                }

                // get the current title
                if ( nodeCurrentTitle != null )
                {
                    strCurrentTitle = nodeCurrentTitle.getNodeValue(  );
                }

                // put in the HashMap
                if ( ( strParentId != null ) && ( strCurrentId != null ) )
                {
                    for ( String strParentIdTemp : listParents )
                    {
                        // get the collection corresponding to the parent id
                        Collection<Card> collectionChildren = linksMap.get( strParentIdTemp );

                        // if the collection doesn't exist for this id, create it and add it to the map
                        if ( collectionChildren == null )
                        {
                            collectionChildren = new ArrayList<Card>(  );
                            linksMap.put( strParentIdTemp, collectionChildren );
                        }

                        // fill the collection with the card object inialized from the xml local card
                        Card card = new Card( strCurrentId, strCurrentTitle );
                        collectionChildren.add( card );
                    }

                    tempMapLocalCard.put( strCurrentId, strCurrentTitle );
                }
            }
            catch ( Exception e )
            {
                AppLogService.debug( "Can't parse XML file (" + file + "): " + e.getMessage(  ), e );
            }
        }
    }

    /**
     * Get the Hashmap associated with the current plugin_instance that list the local themes/cards.
     * @param strPluginName the name of the plugin
     * @return the hashmap hashmap from _mapPlugin associated with the plugin instance.
     */
    public static Map<String, Map> getMapId( String strPluginName )
    {
        return _mapPlugin.get( strPluginName );
    }

    /**
     * Create list of parent id
     * @param parentsId the ids in sequence, separated by a ","
     * @param list the list to set with the ids in the given sequence of ids
     */
    private static void getListParentId( String parentsId, List<String> list )
    {
        final StringTokenizer tokenizer = new StringTokenizer( parentsId, "," );

        while ( tokenizer.hasMoreTokens(  ) )
        {
            final String token = tokenizer.nextToken(  );
            list.add( token );
        }
    }
}
