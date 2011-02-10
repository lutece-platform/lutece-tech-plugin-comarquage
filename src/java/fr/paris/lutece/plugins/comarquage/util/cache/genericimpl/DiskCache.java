/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
import fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter;
import fr.paris.lutece.portal.service.cache.CacheService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.io.Serializable;


/**
 * Disk Access Filter<br/>
 * Wraps an Ehcache Cache.<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.name</code></b>&nbsp;: <b>Required</b> Unique name for the cache
 * <li><i>base</i><b><code>.readOnly</code></b>&nbsp;: Mark the composent has readonly (source of documents access).</li>
 * <li><i>base</i><b><code>.keyAdapter.class</code></b>&nbsp;: <b>Required</b> The class used to implements key adapter (transform key to a valid relatif filesystem path).</li>
 * </ul>
 */
public class DiskCache extends AbstractCache
{
    private static final String PROPERTY_FRAGMENT_NAME = ".name"; // Name should be unique
    private static final String PROPERTY_FRAGMENT_READ_ONLY = ".readOnly";
    private static final String PROPERTY_FRAGMENT_READ_ONLY_TRUE = "true";
    private static final String PROPERTY_BASE_KEY_ADAPTER = ".keyAdapter";
    private Cache _cache;
    private IKeyAdapter _keyAdapter;
    private boolean _bReadOnly;

    /**
     * Public constructor
     *
     */
    public DiskCache(  )
    {
        super( "DiskAccessFilter", "Disk Access Filter" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        //_strPropertyBasePath = strBase + PROPERTY_FRAGMENT_BASE_PATH;
        final String readOnly = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_READ_ONLY );

        if ( readOnly != null )
        {
            _bReadOnly = readOnly.equalsIgnoreCase( PROPERTY_FRAGMENT_READ_ONLY_TRUE );
        }

        _keyAdapter = readInitKeyAdapter( strBase + PROPERTY_BASE_KEY_ADAPTER );

        _cache = CacheService.getInstance(  )
                             .createCache( AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_NAME ) );
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
        final Object adaptedKey = _keyAdapter.adaptKey( key );

        Element element = _cache.get( adaptedKey );

        if ( element == null )
        {
            return null;
        }

        return element.getObjectValue(  );
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
        if ( _bReadOnly )
        {
            return;
        }

        final Object adaptedKey = _keyAdapter.adaptKey( key );
        _cache.put( new Element( adaptedKey, element ) );
    }

    /**
     * Empty a cache
     *
     *
     */
    public void doFlush(  )
    {
        if ( _bReadOnly )
        {
            return;
        }

        _cache.removeAll(  );
    }

    /**
     * Get the number of items in cache
     * @return the cache size, ie the number of files in cache
     */
    public int getCacheSize(  )
    {
        if ( _bReadOnly )
        {
            return 0;
        }

        return _cache.getSize(  );
    }
}
