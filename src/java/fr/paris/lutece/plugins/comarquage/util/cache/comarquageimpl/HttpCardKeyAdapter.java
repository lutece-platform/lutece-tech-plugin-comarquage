/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.comarquage.util.cache.genericimpl.AbstractKeyAdapter;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;

import java.util.Iterator;


/**
 * Adapt a <code>CardKey</code> for the Http Accessor.<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.code</code></b>&nbsp;: <b>Required</b> The request parameter <code>code</code> name used to make the URL.</li>
 * <li><i>base</i><b><code>.ref</code></b>&nbsp;: <b>Required</b> The request parameter <code>reference</code> name used to make the URL.</li>
 * </ul>
 *
 * @see fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl.CardKey
 * @see fr.paris.lutece.plugins.comarquage.util.cache.genericimpl.HttpAccess
 */
public class HttpCardKeyAdapter extends AbstractKeyAdapter
{
    private static final String PROPERTY_FRAGMENT_PARAM_CODE = ".code";
    private static final String PROPERTY_FRAGMENT_PARAM_REF = ".ref";

    /**
     * The code request parameter name used to construct the URL
     */
    private String _strCode;

    /**
     * The reference request parameter name used to construct the URL
     */
    private String _strRef;

    /**
     * Default constructor for <code>HttpCardKeyAdapter</http>
     */
    public HttpCardKeyAdapter(  )
    {
        super( "Adapt a CardKey for the Http Object Accessor." );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        _strCode = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PARAM_CODE );

        if ( _strCode == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_PARAM_CODE + " must be define." );
        }

        _strRef = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PARAM_REF );

        if ( _strRef == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_PARAM_REF + " must be define." );
        }
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter#adaptKey(Serializable)
     *
     * @param key the key to adapt
     * @return an adaptation of the key in one technologie / view...
     */
    public Object adaptKey( Serializable key )
    {
        final CardKey cardKey = (CardKey) key;

        final StringBuffer buf = new StringBuffer(  );

        if ( getPrefix(  ) != null )
        {
            buf.append( getPrefix(  ) );
        }

        buf.append( _strCode ).append( '=' ).append( cardKey.getCDCCode(  ) );

        Iterator itId = cardKey.getPathCard(  ).iterator(  );

        if ( itId.hasNext(  ) )
        {
            buf.append( '&' ).append( _strRef ).append( '=' );

            while ( itId.hasNext(  ) )
            {
                final Object id = itId.next(  );

                buf.append( id );

                if ( itId.hasNext(  ) )
                {
                    buf.append( '/' );
                }
            }
        }

        if ( getSuffix(  ) != null )
        {
            buf.append( getSuffix(  ) );
        }

        return buf.toString(  );
    }
}
