/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.comarquage.util.cache;

import java.io.Serializable;


/**
 * Filter manager, manage call in filter's chain
 */
public interface IChainManager
{
    /**
     * Init the instance with params based on prefix <code>strBase</code>
     *
     * @param strBase The prefix of params to search
     */
    void init( String strBase );

    /**
     * Call the <code>instanceName</code> filter/cache
     *
     * @param strInstanceName Name of the filter
     * @param key Key of the object
     * @return Object searched or <code>null</code> if no object found
     */
    Object callFilter( String strInstanceName, Serializable key );

    /**
     * Return the filter named <code>instanceName</code><br>
     *
     * A <code>RuntimeException</code> will be throw if no cache/filter exist with
     * this name.
     *
     * @param strInstanceName Name of the filter
     * @return Cache/Filter searched
     */
    IChainNode getFilter( String strInstanceName );

    /**
     * Return the plugin name which the filter is associated
     *
     * @return the plugin name which the filter is associated
     */
    String getPluginName(  );

    /**
     * Finds all the caches and flush them
     */
    void emptyCaches(  );

    /**
     * get the sum of all cache sizes
     * @return the cumuled caches size
     */
    int getCachesSize(  );
}
