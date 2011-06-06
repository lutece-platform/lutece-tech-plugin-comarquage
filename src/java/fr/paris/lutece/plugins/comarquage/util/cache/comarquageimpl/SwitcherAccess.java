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
package fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl;

import fr.paris.lutece.plugins.comarquage.util.CoMarquageUtils;
import fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager;
import fr.paris.lutece.plugins.comarquage.util.cache.genericimpl.AbstractAccessor;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;


/**
 * Accessor that allows switching between between public / local card<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.next.public</code></b>&nbsp;: <b>Required</b> </li>
 * <li><i>base</i><b><code>.next.local</code></b>&nbsp;: <b>Required</b> </li>
 * <li><i>base</i><b><code>.localMask</code></b>&nbsp;: <b>Required</b> </li>
 * </ul>
 */
public class SwitcherAccess extends AbstractAccessor
{
    private static final String PROPERTY_FRAGMENT_NEXT_PLUBLIC = ".next.public";
    private static final String PROPERTY_FRAGMENT_NEXT_LOCAL = ".next.local";
    private static final String PROPERTY_FRAGMENT_LOCAL_MASK = ".localMask";
    private static final String PROPERTY_FICHE_NOEUD_LOCAL_REGEX_LAST_ID = AppPropertiesService.getProperty( 
            "comarquage.id.ficheNoeudLocal.regex" );
    private String _strPublicInstanceName;
    private String _strLocalInstanceName;
    private String _strLocalMask;

    /**
     */
    public SwitcherAccess(  )
    {
        super( "Orientation filter", "switcher to public/local" );
    }

    /**
     * Initialises
     * @param strBase the base path
     */
    public void init( String strBase )
    {
        super.init( strBase );

        _strPublicInstanceName = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_NEXT_PLUBLIC );

        if ( _strPublicInstanceName == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_NEXT_PLUBLIC + " must be define." );
        }

        _strLocalInstanceName = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_NEXT_LOCAL );

        if ( _strLocalInstanceName == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_NEXT_LOCAL + " must be define." );
        }

        _strLocalMask = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_LOCAL_MASK );

        if ( _strLocalMask == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_LOCAL_MASK + " must be define." );
        }
    }

    /**
     * @see AbstractAccessor#doSearch(fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager, java.io.Serializable)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @return the element
     */
    public Object doSearch( IContextChainManager filterManager, Serializable key )
    {
        CardKey cardKey = (CardKey) key;
        List<String> listId = cardKey.getPathCard(  );
        String strIdLast = listId.get( listId.size(  ) - 1 );

        final String strInstanceName;
        final CardKey newKey;
        Object element = null;

        if ( strIdLast.startsWith( _strLocalMask ) )
        {
            strInstanceName = _strLocalInstanceName;
            newKey = new CardKey( "", strIdLast, '/' );
            element = filterManager.callFilter( strInstanceName, newKey );
        }
        else
        {
            strInstanceName = _strPublicInstanceName;
            newKey = cardKey;

            if ( strIdLast.matches( PROPERTY_FICHE_NOEUD_LOCAL_REGEX_LAST_ID ) )
            {
                List<String> listPathCopy = new ArrayList<String>( newKey.getPathCard(  ) );
                listPathCopy.set( newKey.getPathCard(  ).size(  ) - 1,
                    CoMarquageUtils.getLocalId( strIdLast, newKey.getCDCCode(  ) ) );

                CardKey localKey = CoMarquageUtils.newCardKeyInstance( newKey.getCDCCode(  ), listPathCopy, '/' );
                element = filterManager.callFilter( strInstanceName, localKey );
            }

            if ( element == null )
            {
                element = filterManager.callFilter( strInstanceName, newKey );
            }
        }

        return element;
    }
}
