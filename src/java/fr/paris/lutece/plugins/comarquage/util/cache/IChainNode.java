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
package fr.paris.lutece.plugins.comarquage.util.cache;

import java.io.Serializable;


/**
 * A cache/filter that work to find/filter a ressource
 */
public interface IChainNode
{
    /**
     * Init the instance with params based on prefix <code>strBase</code>
     *
     * @param strBase The prefix of params to search
     */
    void init( String strBase );

    /**
     * Return the name of this filter's implementation
     * @return the name of this filter's implementation
     */
    String getImplementationName(  );

    /**
     * Return the description of this filter's implementation
     * @return the description of this filter's implementation
     */
    String getImplementationDescription(  );

    /**
     * Research an object in the cache section<br>
     *
     * If cache/filter find the object from a &quot;source&quot;, he
     * must call @link #doFilter(IContextChainManager, Serializable, Object)
     * before returning the object.
     *
     * @param filterManager Context calling manager (needed to call some service or next element in filter's chain)
     * @param key The unique key of object
     * @param element The element to work (<code>null</code> if no element given)
     * @return The resolved object or <code>null</code> if no object found
     */
    Object getObject( IContextChainManager filterManager, Serializable key, Object element );
}
