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

import fr.paris.lutece.plugins.comarquage.util.cache.IChainManager;
import fr.paris.lutece.plugins.comarquage.util.cache.IChainNode;
import fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * The main default cache/filter manager
 */
public class ChainManagerImpl implements IChainManager
{
    private final Map<String, IChainNode> _mapFilters;
    private final Map<String, String> _mapNextFilters;
    private final String _strPluginName;

    /**
     * Public constructor
     * @param strPluginName the plugin name
     */
    public ChainManagerImpl( String strPluginName )
    {
        _mapFilters = new HashMap<String, IChainNode>(  );
        _mapNextFilters = new HashMap<String, String>(  );
        _strPluginName = strPluginName;
    }

    /**
     * @see IChainManager#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        String strCacheFilterName;

        for ( int c = 0;
                ( strCacheFilterName = AppPropertiesService.getProperty( strBase + "." + c + ".name" ) ) != null;
                ++c )
        {
            final String strBaseName = strBase + "." + c;

            final String strClasscacheFilter = AppPropertiesService.getProperty( strBaseName + ".class" );

            if ( strClasscacheFilter == null )
            {
                throw new RuntimeException( strBaseName + ".class must be define." );
            }

            final String strNextCacheFilterName = AppPropertiesService.getProperty( strBaseName + ".next" );

            try
            {
                final Class classCacheFilter = Class.forName( strClasscacheFilter );
                final IChainNode cacheFilter = (IChainNode) classCacheFilter.newInstance(  );
                cacheFilter.init( strBaseName );

                addFilter( strCacheFilterName, cacheFilter, strNextCacheFilterName );
            }
            catch ( ClassNotFoundException e )
            {
                throw new RuntimeException( strBaseName + ".class (" + strClasscacheFilter +
                    ") define an unknown class (" + e.getMessage(  ) + ")" );
            }
            catch ( IllegalAccessException e )
            {
                throw new RuntimeException( strBaseName + ".class (" + strClasscacheFilter +
                    ") define an illegal access class (" + e.getMessage(  ) + ")" );
            }
            catch ( InstantiationException e )
            {
                throw new RuntimeException( strBaseName + ".class (" + strClasscacheFilter +
                    ") define a class with instanciation exception (" + e.getMessage(  ) + ")" );
            }
        }
    }

    /**
     * Add filter to map
     * @param strInstanceName the name of the concerned instance
     * @param filter the filter to add to the map
     */
    public void addFilter( String strInstanceName, IChainNode filter )
    {
        addFilter( strInstanceName, filter, null );
    }

    /**
     * Add filter and next filter to maps
     * @param strInstanceName the name of the concerned instance
     * @param filter the filter to add to the map of filters
     * @param strNextFilter the name of the filter to add to the map of next filters
     */
    public void addFilter( String strInstanceName, IChainNode filter, String strNextFilter )
    {
        _mapFilters.put( strInstanceName, filter );

        // We can put null in HashMap (for nextFilter)
        _mapNextFilters.put( strInstanceName, strNextFilter );
    }

    /**
     * Call a filter
     * @param strInstanceName the name of the concerned instance
     * @param key The unique key of object
     * @param element The element to work
     * @return The resolved object or <code>null</code> if no object found
     */
    protected Object callFilter( String strInstanceName, Serializable key, Object element )
    {
        AppLogService.debug( "Start call the cache/filter: " + strInstanceName + " for key " + key );

        final String strNextFilter = (String) _mapNextFilters.get( strInstanceName );
        final IChainNode filter = getFilter( strInstanceName );

        final IContextChainManager contextChainManager = new ContextChainManagerImpl( this, strInstanceName,
                strNextFilter, key );
        Object response = filter.getObject( contextChainManager, key, element );

        AppLogService.debug( "End call the cache/filter: " + strInstanceName + " for key " + key );

        return response;
    }

    /**
     * @see IChainManager#callFilter(java.lang.String, java.io.Serializable)
     *
     * @param strInstanceName Name of the filter
     * @param key Key of the object
     * @return Object searched or <code>null</code> if no object found
     */
    public Object callFilter( String strInstanceName, Serializable key )
    {
        return callFilter( strInstanceName, key, null );
    }

    /**
     * @see IChainManager#getFilter(String)
     *
     * @param strInstanceName Name of the filter
     * @return Cache/Filter searched
     */
    public IChainNode getFilter( String strInstanceName )
    {
        final IChainNode filter = (IChainNode) _mapFilters.get( strInstanceName );

        if ( filter == null )
        {
            throw new RuntimeException( "Filter '" + strInstanceName + "' undeclared!" );
        }

        return filter;
    }

    /**
     * @see IChainManager#getPluginName()
     *
     * @return the plugin name which the filter is associated
     */
    public String getPluginName(  )
    {
        return _strPluginName;
    }

    /**
     * Finds all the caches and flush them
     */
    public void emptyCaches(  )
    {
        Collection<IChainNode> filterList = _mapFilters.values(  );

        for ( IChainNode filter : filterList )
        {
            String strImplementationName = filter.getImplementationName(  );

            if ( strImplementationName.equals( "DiskAccessFilter" ) ||
                    strImplementationName.equals( "MemObjectAccessor" ) )
            {
                ( (AbstractCache) filter ).doFlush(  );
            }
        }
    }

    /**
     * get the sum of all cache sizes
     * @return the sum of all cache sizes
     */
    public int getCachesSize(  )
    {
        int nCachesSize = 0;
        Collection<IChainNode> filterList = _mapFilters.values(  );

        for ( IChainNode filter : filterList )
        {
            String strImplementationName = filter.getImplementationName(  );

            if ( strImplementationName.equals( "DiskAccessFilter" ) ||
                    strImplementationName.equals( "MemObjectAccessor" ) )
            {
                nCachesSize += ( (AbstractCache) filter ).getCacheSize(  );
            }
        }

        return nCachesSize;
    }
}
