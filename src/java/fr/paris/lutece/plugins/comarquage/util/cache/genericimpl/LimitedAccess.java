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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;


/**
 * Limit access to sub-nodes<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.timeout</code></b>&nbsp;: The timeout (in milliseconds) for client that wait a ressource already in search.</li>
 * <li><i>base</i><b><code>.accessCount</code></b>&nbsp;: <b>required</b> Total access count.</li>
 * </ul>
 */
public class LimitedAccess extends AbstractAccessor
{
    private static final String PROPERTY_FRAGMENT_TIMEOUT = ".timeout";
    private static final String PROPERTY_FRAGMENT_ACCESS_COUNT = ".accessCount";
    private final Object _mutex;
    private int _nTotalAccessCount;
    private int _nCurrentAccessCount;
    private int _nCurrentWaiters;

    /**
     * The timeout (in milliseconds) for client that wait a ressource already in search.
     */
    private long _lTimeout;

    /**
     * Public constructor
     *
     */
    public LimitedAccess(  )
    {
        super( "LimitedAccess", "Limit access to sub-nodes" );
        _mutex = new Object(  );
        _nCurrentWaiters = 0;
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        final String strAccessCount = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_ACCESS_COUNT );

        if ( strAccessCount != null )
        {
            try
            {
                _nTotalAccessCount = Integer.parseInt( strAccessCount );
            }
            catch ( NumberFormatException e )
            {
                throw new RuntimeException( strBase + PROPERTY_FRAGMENT_ACCESS_COUNT +
                    " must be a valid integer value (' " + strAccessCount + "')." );
            }
        }

        final String strTimeout = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_TIMEOUT );

        if ( strTimeout != null )
        {
            try
            {
                _lTimeout = Long.parseLong( strTimeout );

                if ( _lTimeout < 0 )
                {
                    _lTimeout = Long.MAX_VALUE;
                }
            }
            catch ( NumberFormatException e )
            {
                throw new RuntimeException( strBase + PROPERTY_FRAGMENT_TIMEOUT + " must be a valid integer value (' " +
                    strTimeout + "')." );
            }
        }

        _nCurrentAccessCount = _nTotalAccessCount;
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
        boolean bAccessGranted = false;

        long lTimeStart = System.currentTimeMillis(  );
        long lTimeToSleep = ( _lTimeout < 0 ) ? Long.MAX_VALUE : _lTimeout;

        while ( !bAccessGranted && ( lTimeToSleep >= 0 ) )
        {
            synchronized ( _mutex )
            {
                if ( _nCurrentAccessCount > 0 )
                {
                    --_nCurrentAccessCount;
                    bAccessGranted = true;
                    AppLogService.info( "Access granted to filter '" + filterManager.getCurrentFilterName(  ) +
                        "' access (stay " + _nCurrentAccessCount + " free access), current waiters: " +
                        _nCurrentWaiters );
                }
                else
                {
                    try
                    {
                        ++_nCurrentWaiters;
                        AppLogService.info( "Too many ressources ask for limited '" +
                            filterManager.getCurrentFilterName(  ) + "' access (" + _nTotalAccessCount +
                            "), current waiters: " + _nCurrentWaiters );
                        _mutex.wait( lTimeToSleep );
                    }
                    catch ( InterruptedException e )
                    {
                        // Nothing to do!
                        AppLogService.debug( "Interrupted : " + filterManager.getCurrentFilterName(  ) );
                    }

                    --_nCurrentWaiters;
                    lTimeToSleep = _lTimeout - ( System.currentTimeMillis(  ) - lTimeStart );
                }
            }
        }

        if ( !bAccessGranted )
        {
            return null;
        }

        try
        {
            return filterManager.callNextFilter( key, null );
        }
        finally
        {
            synchronized ( _mutex )
            {
                ++_nCurrentAccessCount;
                _mutex.notifyAll(  );
            }
        }
    }
}
