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
package fr.paris.lutece.plugins.comarquage.util.cache.genericimpl;

import fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Abstract Key Adapter implements description basic<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.prefix</code></b>&nbsp;: The prefix to use for the generated string.</li>
 * <li><i>base</i><b><code>.suffix</code></b>&nbsp;: The suffix to use for the generated string.</li>
 * </ul>
 */
public abstract class AbstractKeyAdapter implements IKeyAdapter
{
    private static final String PROPERTY_FRAGMENT_PREFIX = ".prefix";
    private static final String PROPERTY_FRAGMENT_SUFFIX = ".suffix";

    /**
     * The description of the implementation class.
     */
    private final String _strDescription;

    /**
     * The prefix associated with this key adapter.
     */
    private String _strPrefix;

    /**
     * The suffix associated with this key adapter.
     */
    private String _strSuffix;

    /**
     * Construct the instance with the description of the implementation
     * class.
     *
     * @param strDescription The description of the implementation class
     */
    public AbstractKeyAdapter( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * @see IKeyAdapter#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        setPrefix( AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_PREFIX ) );
        setSuffix( AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_SUFFIX ) );
    }

    /**
     * @see IKeyAdapter#getDescription()
     *
     * @return a description of the implementation adapter
     */
    public final String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Return the prefix associated with this key adapter.
     *
     * @return the prefix associated with this key adapter.
     */
    public String getPrefix(  )
    {
        return _strPrefix;
    }

    /**
     * Return the suffix associated with this key adapter.
     *
     * @return the suffix associated with this key adapter.
     */
    public String getSuffix(  )
    {
        return _strSuffix;
    }

    /**
     * Set the prefix associated with this key adapter.
     *
     * @param prefix the prefix associated with this key adapter
     */
    public void setPrefix( String prefix )
    {
        _strPrefix = prefix;
    }

    /**
     * Set the suffix associated with this key adapter.
     *
     * @param suffix the prefix associated with this key adapter
     */
    public void setSuffix( String suffix )
    {
        _strSuffix = suffix;
    }
}
