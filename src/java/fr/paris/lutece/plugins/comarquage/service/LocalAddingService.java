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

import fr.paris.lutece.plugins.comarquage.business.Card;
import fr.paris.lutece.plugins.comarquage.util.parsers.DomParser;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * This class provide the way to performs the local adding in stream source
 *
 */
public class LocalAddingService
{
    private static final String ELEMENT_ITEM = "item";
    private static final String ELEMENT_LIEN = "lien";
    private static final String ELEMENT_TITRE = "titre";
    private static final String ELEMENT_TYPE = "type";
    private static final String ELEMENT_FICHE_PRATIQUE = "fiche_pratique";
    private static final String ELEMENT_FICHE_TECH = "fiche_tech";
    private static final String ELEMENT_URL = "url";
    private static final String ELEMENT_CHEMIN = "chemin";
    private static final String ELEMENT_INFOS_TECHNIQUES = "infos_techniques";
    private static final String ELEMENT_INFOS_THEMATIQUES = "infos_thematique";
    private static final String ELEMENT_NOEUDS_FILS = "noeuds_fils";
    private static final String CONSTANT_FICHE_EXTENTION = ".xml";
    private static final String CONSTANT_DEFAULT_NODE = "N0";

    // parameters
    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_PATH = "path";
    private static final String PARAMETER_TITLE = "titre";
    private static final String PARAMETER_SLASH = "/";
    private static final String PARAMETER_PLUGIN_NAME = "pluginName";
    private static final String PARAMETER_INITIAL_KEY = "initialKey";

    /**
     * Private constructor
     */
    private LocalAddingService(  )
    {
    	// nothing
    }
    
    /**
     * Process the local adding
     *
     * @param inStream The input stream
     * @param model The parameters
     * @return The modified stream
     */
    public static byte[] processLocalAdding( InputStream inStream, Map<String, String> model )
    {
        DomParser domParser = new DomParser( inStream );
        Document document = domParser.getDocument(  );
        Collection<Node> parentNodes = new ArrayList<Node>(  );
        Node infoThematiques = getNode( document, ELEMENT_INFOS_THEMATIQUES );

        if ( infoThematiques == null )
        {
            infoThematiques = document.createElement( ELEMENT_INFOS_THEMATIQUES );
        }

        parentNodes.add( infoThematiques );

        Node noeudsFils = getNode( document, ELEMENT_NOEUDS_FILS );

        if ( noeudsFils == null )
        {
            noeudsFils = document.createElement( ELEMENT_NOEUDS_FILS );
        }

        parentNodes.add( noeudsFils );

        addLocalAddingCards( document, model.get( PARAMETER_PLUGIN_NAME ), parentNodes );

        // Process resolvePath :
        updateInfosTechniques( document, model.get( PARAMETER_INITIAL_KEY ), model.get( PARAMETER_PLUGIN_NAME ) );

        return domParser.toByteArray(  );
    }

    /**
     * Get the node with the specified name
     * @param document the DOM document
     * @param strNodeName The node name
     * @return The node
     */
    private static Node getNode( Document document, String strNodeName )
    {
        Node node = null;
        NodeList nodes = document.getElementsByTagName( strNodeName );

        if ( nodes.getLength(  ) == 1 )
        {
            node = nodes.item( 0 );
        }

        return node;
    }

    /**
     * Get the node with the specified name
     * @param document the DOM document
     * @param strNodeName The node name
     * @param strParentNodeName The parent node name
     * @return The node
     */
    private static Node getNode( Document document, String strNodeName, String strParentNodeName )
    {
        Node node = null;
        NodeList nodes = document.getElementsByTagName( strNodeName );

        for ( int i = 0; i < nodes.getLength(  ); i++ )
        {
            node = nodes.item( i );

            if ( node.getParentNode(  ).getNodeName(  ).equals( strParentNodeName ) )
            {
                return node;
            }
        }

        return node;
    }

    /**
     * Method that update the tag info_techniques
     *
     * @param document The DOM {@link Document}
     * @param strInitialKey The initial key
     * @param strPluginName The plugin name
     */
    private static void updateInfosTechniques( Document document, String strInitialKey, String strPluginName )
    {
        Node nodeInfoTechnique = getNode( document, ELEMENT_INFOS_TECHNIQUES );

        if ( nodeInfoTechnique == null )
        {
            nodeInfoTechnique = document.createElement( ELEMENT_INFOS_TECHNIQUES );
        }

        if ( !resolvePath( document, strInitialKey, nodeInfoTechnique, strPluginName ) )
        {
            createDefaultPath( document, nodeInfoTechnique );
        }
    }

    /**
     * Create a default "chemin" with node N0
     * @param document The DOM {@link Document}
     * @param node The node to be updated
     */
    private static void createDefaultPath( Document document, Node node )
    {
        NodeList nodesChemin = document.getElementsByTagName( ELEMENT_CHEMIN );

        while ( nodesChemin.getLength(  ) > 0 )
        {
            node.removeChild( nodesChemin.item( 0 ) );
        }

        Node nodeFichePratique = getNode( document, ELEMENT_FICHE_PRATIQUE );

        Node urlAttribute = nodeFichePratique.getAttributes(  ).getNamedItem( ELEMENT_URL );
        String strDefaultId = null;

        if ( urlAttribute != null )
        {
            String strDefaultIdTmp = urlAttribute.getTextContent(  );
            strDefaultId = strDefaultIdTmp.substring( 0, strDefaultIdTmp.indexOf( CONSTANT_FICHE_EXTENTION ) );
        }

        String strDefaultPath = null;

        if ( ( strDefaultId != null ) && strDefaultId.equals( CONSTANT_DEFAULT_NODE ) )
        {
            strDefaultPath = strDefaultId;
        }
        else
        {
            strDefaultPath = CONSTANT_DEFAULT_NODE + PARAMETER_SLASH + strDefaultId;
        }

        Node nodeTitreFichePratique = getNode( document, ELEMENT_TITRE, ELEMENT_FICHE_PRATIQUE );
        String strDefaultTitle = null;

        if ( nodeTitreFichePratique != null )
        {
            strDefaultTitle = nodeTitreFichePratique.getTextContent(  );
        }

        Element chemin = document.createElement( ELEMENT_CHEMIN );
        chemin.setAttribute( PARAMETER_ID, strDefaultPath );

        Element ficheTech = document.createElement( ELEMENT_FICHE_TECH );
        ficheTech.setAttribute( PARAMETER_ID, CONSTANT_DEFAULT_NODE );
        ficheTech.setAttribute( PARAMETER_PATH, CONSTANT_DEFAULT_NODE );
        chemin.appendChild( ficheTech );

        Element ficheTech2 = document.createElement( ELEMENT_FICHE_TECH );
        ficheTech2.setAttribute( PARAMETER_ID, strDefaultId );
        ficheTech2.setAttribute( PARAMETER_PATH, strDefaultPath );
        ficheTech2.setAttribute( PARAMETER_TITLE, strDefaultTitle );
        chemin.appendChild( ficheTech2 );

        node.appendChild( chemin );
    }

    /**
     * Return the id of the current theme (where local nodes might have to be added
     * @param document The DOM {@link Document}
     * @return The theme Id
     */
    private static String getThemeId( Document document )
    {
        Node node = null;
        NodeList nodes = document.getElementsByTagName( ELEMENT_FICHE_PRATIQUE );

        if ( nodes.getLength(  ) == 1 )
        {
            node = nodes.item( 0 );
        }

        NamedNodeMap namedNodeMap = node.getAttributes(  );
        Node nodeUrl = namedNodeMap.getNamedItem( ELEMENT_URL );
        String strThemeId = nodeUrl.getNodeValue(  );
        strThemeId = strThemeId.substring( 0, strThemeId.indexOf( CONSTANT_FICHE_EXTENTION ) );

        return strThemeId;
    }

    /**
     * Method that performs the addition of the local nodes
     * to the DOM {@link Document}.
     * @param document The DOM {@link Document}
     * @param strPluginName the plugin name
     * @param parentNodes the parent nodes to add local adding
     */
    private static void addLocalAddingCards( Document document, String strPluginName, Collection<Node> parentNodes )
    {
        String strThemeId = getThemeId( document );

        // retrieve the hashmap referencing the local cards
        Map<String, Map> mapId = CoMarquageLocalListing.getMapId( strPluginName );

        if ( mapId == null )
        {
            AppLogService.debug( "nodesAttached (Xalan extension): No MapID found for plugin '" + strPluginName + "'." );

            return;
        }

        Map<String, Collection<Card>> mapParentId = mapId.get( CoMarquageLocalListing.PARAMETER_MAP_PARENT_ID );

        if ( mapParentId == null )
        {
            AppLogService.debug( "nodesAttached (Xalan extension): No MapParentId found for plugin '" + strPluginName +
                "'. " + CoMarquageLocalListing.PARAMETER_MAP_PARENT_ID );

            return;
        }

        // find in the hashmap the collection corresponding to the given id
        Collection<Card> list = mapParentId.get( strThemeId );

        if ( list != null )
        {
            //        	 go through the collection of "Card" objects
            //          Add local cards into DOM document
            for ( Card card : list )
            {
                // Add cards to the specified parent nodes
                for ( Node node : parentNodes )
                {
                    addCard( document, node, card );
                }
            }
        }
    }

    /**
     * Method that performs the creation of the path
     *
     * @param document The DOM {@link Document}
     * @param initialKey the id parameter (node or card asked)
     * @param node path node containing all paths available
     * @param strPluginName the plugin name
     * @return the xml code corresponding to the path
     */
    private static boolean resolvePath( Document document, String initialKey, Node node, String strPluginName )
    {
        // retrieve the hashmap referencing the local cards
        Map<String, Map> mapId = CoMarquageLocalListing.getMapId( strPluginName );
        Map<String, String> mapXml = new HashMap<String, String>(  );
        ArrayList<String> arXml = new ArrayList<String>(  );
        String childNodeName = null;

        if ( mapId == null )
        {
            AppLogService.debug( "[resolvePath] nodesAttached (Xalan extension): No MapID found for plugin '" +
                strPluginName + "'." );

            return false;
        }

        Map<String, String> mapLocalCard = mapId.get( CoMarquageLocalListing.PARAMETER_MAP_LOCAL_CARD );

        if ( mapLocalCard == null )
        {
            AppLogService.debug( "[resolvePath] nodesAttached (Xalan extension): No MapLocalCard found for plugin '" +
                strPluginName + "'. " + CoMarquageLocalListing.PARAMETER_MAP_LOCAL_CARD );

            return false;
        }

        boolean bPathOk = false;

        //String strStartInfo = XML_TAG_OPEN_DEBUT + node.getLocalName(  ) + XML_TAG_OPEN_FIN;
        //String strEndInfo = XML_TAG_CLOSE_DEBUT + node.getLocalName(  ) + XML_TAG_CLOSE_FIN;
        final int pos = initialKey.indexOf( PARAMETER_SLASH, 1 ) + 1;

        // strKey : the key founded in url.
        final String strKey = initialKey.substring( pos );
        AppLogService.debug( "[resolvePath] the key in url : " + strKey );

        final String strSeparator = PARAMETER_SLASH;
        StringTokenizer tokenizer = new StringTokenizer( strKey, strSeparator );
        ArrayList<String> arKeyPath = new ArrayList<String>(  );
        ArrayList<String> arKey = new ArrayList<String>(  );
        ArrayList<ArrayList<String>> arKeyResult = new ArrayList<ArrayList<String>>(  );
        int nKey = 0;

        while ( tokenizer.hasMoreTokens(  ) )
        {
            String strKeyTemp = tokenizer.nextToken(  );
            String strKeyPathTemp = null;

            if ( nKey > 0 )
            {
                strKeyPathTemp = arKeyPath.get( nKey - 1 ) + PARAMETER_SLASH + strKeyTemp;
            }
            else
            {
                strKeyPathTemp = strKeyTemp;
            }

            arKey.add( strKeyTemp );
            arKeyPath.add( strKeyPathTemp );
            arKeyResult.add( (ArrayList<String>) arKey.clone(  ) );
            nKey++;
        }

        //warning : bug referenced about navigation into child nodes (don't modify navigation)
        Node childrenNodeTmp = node.getFirstChild(  );
        Collection<Node> nodeList = new ArrayList<Node>(  );

        while ( childrenNodeTmp != null )
        {
            AppLogService.debug( "[resolvePath] childrenNodeTmp, localName=" + childrenNodeTmp.getLocalName(  ) +
                ", nodeName=" + childrenNodeTmp.getNodeName(  ) + ", textContent=" +
                childrenNodeTmp.getTextContent(  ) );

            Element myNewElement = null;

            //Verify child node type
            if ( childrenNodeTmp.getNodeType(  ) == Node.ELEMENT_NODE )
            {
                NamedNodeMap nodeMap = childrenNodeTmp.getAttributes(  );
                String strId = null;

                if ( nodeMap.getNamedItem( PARAMETER_ID ) != null )
                {
                    strId = nodeMap.getNamedItem( PARAMETER_ID ).getNodeValue(  );
                    AppLogService.debug( "[resolvePath] id=" + strId );

                    if ( strId.equals( strKey ) )
                    {
                        AppLogService.debug( "[resolvePath] Create new Element with name : " +
                            childrenNodeTmp.getNodeName(  ) + " and parameter id=" + strId );
                        myNewElement = document.createElement( childrenNodeTmp.getNodeName(  ) );
                        myNewElement.setAttribute( PARAMETER_ID, strId );
                        bPathOk = true;
                    }
                }

                // path is equal to id asked
                if ( bPathOk )
                {
                    //build map of information from node
                    Node subChildrenNodeTmp = childrenNodeTmp.getFirstChild(  );
                    Integer nSubChildren = new Integer( 0 );

                    while ( subChildrenNodeTmp != null )
                    {
                        if ( subChildrenNodeTmp.getNodeType(  ) == Node.ELEMENT_NODE )
                        {
                            NamedNodeMap nodeSubMap = subChildrenNodeTmp.getAttributes(  );
                            String strSubTitle = null;
                            String strSubId = null;

                            if ( nodeSubMap.getNamedItem( PARAMETER_PATH ) != null )
                            {
                                strSubId = nodeSubMap.getNamedItem( PARAMETER_PATH ).getNodeValue(  );
                            }

                            if ( nodeSubMap.getNamedItem( PARAMETER_TITLE ) != null )
                            {
                                strSubTitle = nodeSubMap.getNamedItem( PARAMETER_TITLE ).getNodeValue(  );
                            }

                            AppLogService.debug( "[resolvePath] strSubId=" + strSubId + ", strSubTitle=" + strSubTitle );
                            mapXml.put( strSubId, strSubTitle );
                            arXml.add( strSubId );

                            childNodeName = subChildrenNodeTmp.getNodeName(  );

                            nSubChildren = new Integer( nSubChildren.intValue(  ) + 1 );
                        }

                        //go to next child node
                        subChildrenNodeTmp = subChildrenNodeTmp.getNextSibling(  );
                    }

                    //build path
                    int nbKey = 0;

                    while ( nbKey < ( arKey.size(  ) ) )
                    {
                        String strKeyTemp = arKey.get( nbKey );
                        String strKeyPathTemp = arKeyPath.get( nbKey );
                        ArrayList<String> arKeyTemp = arKeyResult.get( nbKey );
                        String strKeyPathCompTemp = createPath( arKeyTemp );
                        boolean bFound = false;

                        int nbXml = 0;

                        while ( nbXml < mapXml.size(  ) )
                        {
                            String xmlTemp = arXml.get( nbXml );
                            AppLogService.debug( "[resolvePath] xmlTemp=" + xmlTemp );

                            if ( xmlTemp.equals( strKeyPathCompTemp ) )
                            {
                                Element title = null;
                                String titleTemp = mapXml.get( xmlTemp );
                                AppLogService.debug( "[resolvePath] Create new Title with name : " + childNodeName +
                                    " and parameter path=" + strKeyPathTemp + ", id=" + strKeyTemp + ", title=" +
                                    titleTemp );
                                title = document.createElement( childNodeName );
                                title.setAttribute( PARAMETER_PATH, strKeyPathTemp );
                                title.setAttribute( PARAMETER_ID, strKeyTemp );
                                title.setAttribute( PARAMETER_TITLE, titleTemp );
                                myNewElement.appendChild( title );
                                bFound = true;

                                break;
                            }

                            nbXml++;
                        }

                        if ( !bFound )
                        {
                            // find in the hashmap the collection corresponding to the given id
                            String strTitle = mapLocalCard.get( strKeyTemp );

                            if ( strTitle != null )
                            {
                                suppKey( arKeyResult, strKeyTemp );

                                AppLogService.debug( "[resolvePath] Create new Title with name : " + childNodeName +
                                    " and parameter path=" + strKeyPathTemp + ", id=" + strKeyTemp + ", title=" +
                                    strTitle );

                                Element title = document.createElement( childNodeName );
                                title.setAttribute( PARAMETER_PATH, strKeyPathTemp );
                                title.setAttribute( PARAMETER_ID, strKeyTemp );
                                title.setAttribute( PARAMETER_TITLE, strTitle );
                                myNewElement.appendChild( title );
                            }
                            else
                            {
                                AppLogService.debug( "Pas de chemin valide dans la fiche pour l'url demand�e" );
                            }

                            bFound = true;
                        }

                        nbKey++;
                    }

                    if ( myNewElement != null )
                    {
                        AppLogService.debug( "Append Element in nodeList" );
                        nodeList.add( myNewElement );
                    }

                    break;
                }
            }

            bPathOk = false;
            childrenNodeTmp = childrenNodeTmp.getNextSibling(  );
        }

        if ( bPathOk )
        {
            AppLogService.debug( "[resolvePath] Append nodeList in node" );

            if ( nodeList.size(  ) > 0 )
            {
                NodeList nodesChemin = document.getElementsByTagName( ELEMENT_CHEMIN );

                while ( nodesChemin.getLength(  ) > 0 )
                {
                    AppLogService.debug( "[resolvePath] delete nodesChemin=" +
                        nodesChemin.item( 0 ).getAttributes(  ).item( 0 ).getTextContent(  ) );
                    node.removeChild( nodesChemin.item( 0 ) );
                }

                for ( Node newNode : nodeList )
                {
                    AppLogService.debug( "[resolvePath] Add nodeChemin=" +
                        newNode.getAttributes(  ).item( 0 ).getTextContent(  ) );
                    node.appendChild( newNode );
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Create path using arraylist
     *
     * @param arKeyTemp arrayList of keys
     * @return list of keys separated by slash
     */
    private static String createPath( List<String> arKeyTemp )
    {
        int i = 0;
        StringBuffer strPathBuffer = new StringBuffer(  );

        for ( String strTmp : arKeyTemp )
        {
            if ( i != 0 )
            {
                strPathBuffer.append( PARAMETER_SLASH );
            }

            strPathBuffer.append( strTmp );
            i++;
        }

        return strPathBuffer.toString(  );
    }

    /**
     * Delete key in an arrayList
     *
     * @param arKeyResult arrayList of keys
     * @param strKeyTemp key
     */
    private static void suppKey( List<ArrayList<String>> arKeyResult, String strKeyTemp )
    {
        for ( ArrayList arListTmp : arKeyResult )
        {
            arListTmp.remove( strKeyTemp );
        }
    }

    /**
     * Add a new card to a node
     *
     * @param document The DOM document
     * @param node The parent node
     * @param card The card to add
     */
    private static void addCard( Document document, Node node, Card card )
    {
        // Example :
        //<item>
        //  <lien type="theme">LN5</lien>
        //  <titre>Activités périscolaires</titre>
        //</item>
        //Create and append item element
        Element item = document.createElement( ELEMENT_ITEM );

        //Create and append lien element
        Element lien = document.createElement( ELEMENT_LIEN );
        lien.setAttribute( ELEMENT_TYPE, card.getType(  ) );
        lien.setTextContent( card.getId(  ) );
        item.appendChild( lien );

        //Create and append title element
        Element titre = document.createElement( ELEMENT_TITRE );
        titre.setTextContent( card.getTitle(  ) );
        item.appendChild( titre );

        node.appendChild( item );
    }
}
