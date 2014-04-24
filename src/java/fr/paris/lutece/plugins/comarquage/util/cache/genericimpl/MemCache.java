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
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * Memory Object Accessor<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.delay</code></b>&nbsp;: The delay (in ms) between two scan in cache to reject old object.</li>
 * <li><i>base</i><b><code>.maxSize</code></b>&nbsp;: The maximum size of the cache before flush.</li>
 * </ul>
 */
public class MemCache extends AbstractCache
{
    private static final String PROPERTY_FRAGMENT_DELAY = ".delay";
    private static final String PROPERTY_FRAGMENT_MAX_SIZE = ".maxSize";
    private final Map<Serializable, Object> _mapCache;
    private final Set<Serializable> _setLastAccess;
    private long _lDelay;
    private int _nMaxSize;
    private long _lNextCheck;

    /**
     * Public constructor
     *
     */
    public MemCache(  )
    {
        super( "MemObjectAccessor", "Mem Object Accessor" );
        _mapCache = new WeakHashMap<Serializable, Object>(  );
        _setLastAccess = new HashSet<Serializable>(  );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        final String strDelay = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_DELAY );

        if ( strDelay != null )
        {
            try
            {
                _lDelay = Long.parseLong( strDelay );
            }
            catch ( NumberFormatException e )
            {
                throw new RuntimeException( strDelay + PROPERTY_FRAGMENT_DELAY + " must be a valid integer value (' " +
                    strDelay + "')." );
            }
        }

        final String strMaxSize = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_MAX_SIZE );

        if ( strMaxSize != null )
        {
            try
            {
                _nMaxSize = Integer.parseInt( strMaxSize );
            }
            catch ( NumberFormatException e )
            {
                throw new RuntimeException( strDelay + PROPERTY_FRAGMENT_MAX_SIZE +
                    " must be a valid integer value (' " + strMaxSize + "')." );
            }
        }

        if ( _lDelay >= 0 )
        {
            _lNextCheck = System.currentTimeMillis(  ) + _lDelay;
        }
        else
        {
            _lNextCheck = Long.MAX_VALUE;
        }
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
        Object element = _mapCache.get( key );
        _setLastAccess.remove( key );

        // Return null if no element finded
        return element;
    }

    /**
     * @see AbstractCache#doStore(IContextChainManager, Serializable, Object)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @param element the element to store
     */
    public void doStore( IContextChainManager filterManager, Serializable key, Object element )
    {
        _mapCache.put( key, element );

        int cacheSize = _mapCache.size(  );

        if ( ( _lNextCheck < System.currentTimeMillis(  ) ) || ( ( _nMaxSize > 0 ) && ( _nMaxSize < cacheSize ) ) )
        {
            for ( Serializable keyCurrent : _setLastAccess )
            {
                _mapCache.remove( keyCurrent );
            }

            _setLastAccess.clear(  );
            _setLastAccess.addAll( _mapCache.keySet(  ) );

            if ( _lDelay >= 0 )
            {
                _lNextCheck = System.currentTimeMillis(  ) + _lDelay;
            }
        }
    }

    /**
     * Empty a cache
     *
     */
    public void doFlush(  )
    {
        _mapCache.clear(  );
    }

    /**
     * Get the number of items in cache
     * @return the cache size, ie the size of the map of cards
     */
    public int getCacheSize(  )
    {
        return _mapCache.size(  );
    }
}
