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
package fr.paris.lutece.plugins.comarquage.util;

import fr.paris.lutece.plugins.comarquage.util.cache.IChainManager;
import fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl.CardKey;
import fr.paris.lutece.plugins.comarquage.util.cache.genericimpl.ChainManagerImpl;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


/**
 * CoMarquage utilitary access class
 */
public final class CoMarquageUtils
{
    private static final String XML_CONFIG_PARAM_TARGET_LINK_SERVICE_ID = "targetLinkServiceId";
    private static final String PROPERTY_RESPONSIBILITY_CHAIN_FRAGMENT = ".respchain";
    private static final String PARAMETER_ID = "id";
    private static final String MASK_PUBLIC_CARD = "/F";
    private static final String ID_SEPARATOR = "-";
    private static final Map<String, IChainManager> _mapPluginByPluginName = new HashMap<String, IChainManager>(  );
    private static final Map<String, String> _mapPluginNameByLinkServiceId = new HashMap<String, String>(  );
    private static final String PROPERTY_REGEX_VALIDATION = "comarquage.id.regexValidation";
    private static final String PROPERTY_FICHE_NOEUD_LOCAL_REGEX = AppPropertiesService.getProperty( 
            "comarquage.id.ficheNoeudLocal.regex" );
    private static final String PROPERTY_FRAGMENT_FICHE_NOEUD_LOCAL_TYPE_MAP = "comarquage.id.ficheNoeudLocal.type.map.";
    private static final int PROPERTY_FICHE_NOEUD_LOCAL_POSITION_TYPE = AppPropertiesService.getPropertyInt( "comarquage.id.ficheNoeudLocal.pos.type",
            1 );
    private static final int PROPERTY_FICHE_NOEUD_LOCAL_POSITION_COMPTEUR = AppPropertiesService.getPropertyInt( "comarquage.id.ficheNoeudLocal.pos.compteur",
            2 );
    private static final int PROPERTY_FICHE_NOEUD_LOCAL_POSITION_CODE_MAIRIE = AppPropertiesService.getPropertyInt( "comarquage.id.ficheNoeudLocal.pos.codeMairie",
            0 );

    /**
     * Hidden constructor
     *
     */
    private CoMarquageUtils(  )
    {
        // nothing here
    }

    /**
     * Return id with one Card max and without double
     * @param request the request
     * @return the prepared id or null if Id is not correct
     */
    public static synchronized String getId( HttpServletRequest request ) //TODO : getId( ) synchronized ?
    {
        String strId = request.getParameter( PARAMETER_ID );
        String strRegexValidationId = AppPropertiesService.getProperty( PROPERTY_REGEX_VALIDATION );

        if ( null != strId )
        {
            final StringTokenizer st = new StringTokenizer( strId, "/" );

            while ( st.hasMoreTokens(  ) )
            {
                final String e = (String) st.nextToken(  );

                if ( strId.indexOf( e ) != strId.lastIndexOf( e ) )
                {
                    final String s1 = strId.substring( 0, strId.indexOf( e ) );
                    String s2 = strId.substring( strId.indexOf( e ) );
                    s2 = s2.substring( s2.indexOf( "/" ) + 1 );
                    strId = s1 + s2;
                }

                if ( !e.matches( strRegexValidationId ) )
                {
                    return null;
                }
            }

            final int i = strId.lastIndexOf( MASK_PUBLIC_CARD );

            if ( -1 != i )
            {
                strId = strId.substring( 0, strId.indexOf( MASK_PUBLIC_CARD ) ) + strId.substring( i );
            }
        }

        return strId;
    }

    /**
     * Call a chain manager by the plugin name and entry point.<br>
     * Find element referenced by key <code>key</code>
     *
     * @param strPluginName The name of the plugin (for retrieve configuration)
     * @param strFirstModuleInstanceName The name of the entry node
     * @param key Key reference for the object to search
     * @return The string fragment associate with this chain
     */
    public static String callChainManagerByPluginName( String strPluginName, String strFirstModuleInstanceName,
        Serializable key )
    {
        IChainManager chainManager = getChainManagerByPluginName( strPluginName );

        String strContents;

        try
        {
            strContents = (String) chainManager.callFilter( strFirstModuleInstanceName, key );
        }
        catch ( AppException e )
        {
            return null;
        }

        return strContents;
    }

    /**
     * Get chain manager by the plugin name.<br>
     *
     * @param strPluginName The name of the plugin (for retrieve configuration)
     * @return The chain manager associate with this plugin
     */
    public static synchronized IChainManager getChainManagerByPluginName( String strPluginName )
    {
        IChainManager chainManager = (IChainManager) _mapPluginByPluginName.get( strPluginName );

        if ( chainManager == null )
        {
            chainManager = new ChainManagerImpl( strPluginName );
            chainManager.init( strPluginName + PROPERTY_RESPONSIBILITY_CHAIN_FRAGMENT );

            // Register it in caches
            Plugin plugin = PluginService.getPlugin( strPluginName );
            String strLinkServiceId = plugin.getParamValue( XML_CONFIG_PARAM_TARGET_LINK_SERVICE_ID );
            _mapPluginByPluginName.put( strPluginName, chainManager );
            _mapPluginNameByLinkServiceId.put( strLinkServiceId, strPluginName );
        }

        return chainManager;
    }

    /**
     * Returns the local id of the given id.
     * Ex : <code>strLastId = 12345FL67</code> and <code>strCDCCode = 98765</code>
     *  returns <code>98765XL67-12345</code>
     * @param strLastId the id to modify
     * @param strCDCCode the cdc code
     * @return the new id, empty string otherwise
     */
    public static String getLocalId( String strLastId, String strCDCCode )
    {
        if ( StringUtils.isBlank( strLastId ) || StringUtils.isBlank( strCDCCode ) )
        {
            return "";
        }

        Pattern pattern = Pattern.compile( PROPERTY_FICHE_NOEUD_LOCAL_REGEX );

        Matcher matcher = pattern.matcher( strLastId );

        if ( matcher.matches(  ) )
        {
            String strCompteur = matcher.group( PROPERTY_FICHE_NOEUD_LOCAL_POSITION_COMPTEUR );
            String strCodeMairie = matcher.group( PROPERTY_FICHE_NOEUD_LOCAL_POSITION_CODE_MAIRIE );
            String strType = matcher.group( PROPERTY_FICHE_NOEUD_LOCAL_POSITION_TYPE );

            String strNewType = AppPropertiesService.getProperty( PROPERTY_FRAGMENT_FICHE_NOEUD_LOCAL_TYPE_MAP +
                    strType );

            return strCDCCode + strNewType + strCompteur + ID_SEPARATOR + strCodeMairie;
        }

        return "";
    }

    /**
     * Builds a new card key instance with the parameters given
     * @param strCDCCode the CDCCode
     * @param listPath the path list
     * @param cSeparator the path separator
     * @return the built cardkey
     */
    public static CardKey newCardKeyInstance( String strCDCCode, List<String> listPath, char cSeparator )
    {
        StringBuilder sbPath = new StringBuilder(  );

        for ( int nIndex = 0; nIndex < listPath.size(  ); nIndex++ )
        {
            sbPath.append( listPath.get( nIndex ) );
            sbPath.append( cSeparator );
        }

        return new CardKey( strCDCCode, sbPath.toString(  ), cSeparator );
    }
}
