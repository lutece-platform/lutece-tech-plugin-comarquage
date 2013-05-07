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
package fr.paris.lutece.plugins.comarquage.util.cache.genericimpl;

import fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;


/**
 * Common abstract cache<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.timeout</code></b>&nbsp;: The timeout (in milliseconds) for client that wait a ressource already in search.</li>
 * </ul>
 */
public abstract class AbstractCache extends AbstractChainNode
{
    private static final String PROPERTY_FRAGMENT_TIMEOUT = ".timeout";
    private final Map<Serializable, Serializable> _mapRunningSearch;

    /**
     * The timeout (in milliseconds) for client that wait a ressource already in search.
     */
    private long _lTimeout;

    /**
     * The public constructor
     * @param strImplementationName the name of the implementation class
     * @param strImplementationDescription the description of the implementation
     */
    public AbstractCache( String strImplementationName, String strImplementationDescription )
    {
        super( strImplementationName, strImplementationDescription );
        _mapRunningSearch = new HashMap<Serializable, Serializable>(  );
        _lTimeout = Long.MAX_VALUE;
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
         * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

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
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#getObject(IContextChainManager, Serializable, Object)
     *
     * @param filterManager Context calling manager (needed to call some service or next element in filter's chain)
     * @param key The unique key of object
     * @param element The element to work (<code>null</code> if no element given)
     * @return The resolved object or <code>null</code> if no object found
     */
    public final Object getObject( IContextChainManager filterManager, Serializable key, Object element )
    {
        Object elt = element;

        if ( elt != null )
        {
            // Element already found... nothing to do, and an error in the manager?
            return elt;
        }

        Object keyLock = null;
        boolean keyReserved = false;

        try
        {
            // Lock this cache to search the element
            synchronized ( _mapRunningSearch )
            {
                // only one thread at a time can do this search
                elt = doSearch( filterManager, key );

                // element in cache, go on...
                if ( elt != null )
                {
                    return elt;
                }

                if ( _lTimeout == 0 )
                {
                    return null;
                }

                // check if the element search is running?
                if ( _mapRunningSearch.containsKey( key ) )
                {
                    // OK: wait on real key of the object!
                    keyLock = _mapRunningSearch.get( key );
                }
                else
                {
                    // KO: no running search, register as running search,
                    // and do search!
                    AppLogService.debug( "Locking access to key: " + key );
                    keyReserved = true;
                    _mapRunningSearch.put( key, key );
                }
            }

            // keyLock isn't null: search in progress, we must wait on the "searching cache key"
            // (not on our key)
            if ( keyLock != null )
            {
                return waitForKeyFreeing( keyLock, filterManager, key );
            }

            // keyLock is null: No search in progress, we realize the search
            // (not on our key)
            elt = filterManager.callNextFilter( key, elt );

            // Lock this cache to store the element
            if ( elt != null )
            {
                synchronized ( _mapRunningSearch )
                {
                    doStore( filterManager, key, elt );
                }
            }

            return elt;
        }
        finally
        {
            if ( keyReserved )
            {
                // to be sure that timeout "waiters" will be free
                synchronized ( key )
                {
                    AppLogService.debug( "Freeing access to key: " + key );
                    key.notifyAll(  );
                }

                synchronized ( _mapRunningSearch )
                {
                    _mapRunningSearch.remove( key );
                }
            }
        }
    }

    /**
     * Wait on the key to obtain the element already in search<br>
     *
     * @param sharedKeyLock The unique key of object searched by another thread
     * @param filterManager Context calling manager (needed to call some service or next element in filter's chain)
     * @param privateKey The unique key of object searched by the current search
     * @return The resolved object or <code>null</code> if no object find
     */
    private Object waitForKeyFreeing( Object sharedKeyLock, IContextChainManager filterManager, Serializable privateKey )
    {
        // wait until searcher find the object
        synchronized ( sharedKeyLock )
        {
            AppLogService.debug( Thread.currentThread(  ) + " => Entering critical section on key : " + privateKey );

            long lTimeout = _lTimeout;
            long lDateStart = System.currentTimeMillis(  );

            try
            {
                AppLogService.debug( getImplementationName(  ) + " go to sleep for: " + lTimeout + " ms for key " +
                    privateKey );
                sharedKeyLock.wait( lTimeout );
                //                sharedKeyLock.wait( );
                AppLogService.debug( getImplementationName(  ) + " slept during: " +
                    ( System.currentTimeMillis(  ) - lDateStart ) + " ms for key " + privateKey );
            }
            catch ( InterruptedException e )
            {
                AppLogService.debug( getImplementationName(  ) + " interrupted: " + " for key " + privateKey );
            }

            AppLogService.debug( Thread.currentThread(  ) + " => Leaving critical section on key : " + privateKey );
        }

        Object element;

        // Lock this cache to search the element a second time
        synchronized ( _mapRunningSearch )
        {
            element = doSearch( filterManager, privateKey );
        }

        if ( element != null )
        {
            AppLogService.debug( getImplementationName(  ) + " wait and hit key: " + privateKey );
        }
        else
        {
            AppLogService.debug( getImplementationName(  ) + " wait and miss key: " + privateKey );
        }

        return element;
    }

    /**
     * Realise the search of the object
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @return the element in cache
     */
    public abstract Object doSearch( IContextChainManager filterManager, Serializable key );

    /**
     * Ask to store an object (normally not present in cache)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @param element the element to store
     */
    public abstract void doStore( IContextChainManager filterManager, Serializable key, Object element );

    /**
     * Empty a cache
     *
     */
    public abstract void doFlush(  );

    /**
     * get the number of items in cache
     * @return the cache size
     */
    public abstract int getCacheSize(  );
}
