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
package fr.paris.lutece.plugins.comarquage.util.cache.genericimpl;

import fr.paris.lutece.plugins.comarquage.util.cache.IChainNode;
import fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter;
import fr.paris.lutece.plugins.comarquage.util.cache.IObjectTransform;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Common abstract filter<br/>
 *
 * <b>No properties read here.</b>
 */
public abstract class AbstractChainNode implements IChainNode
{
    private static final String PROPERTY_FRAGMENT_CLASS = ".class";
    private final String _strImplementationName;
    private final String _strImplementationDescription;

    /**
     * The public constructor
     * @param strImplementationName the name of the implementation class
     * @param strImplementationDescription the description of the implementation
     */
    public AbstractChainNode( String strImplementationName, String strImplementationDescription )
    {
        _strImplementationName = strImplementationName;
        _strImplementationDescription = strImplementationDescription;
    }

    /**
     * @see IChainNode#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        // Nothing to do
    }

    /**
     * @see IChainNode#getImplementationDescription( )
     *
     * @return the description of this filter's implementation
     */
    public final String getImplementationDescription(  )
    {
        return _strImplementationDescription;
    }

    /**
     * @see IChainNode#getImplementationName( )
     *
     * @return the name of this filter's implementation
     */
    public final String getImplementationName(  )
    {
        return _strImplementationName;
    }

    /**
     * Read and init a <code>IObjectTransform</code> object
     *
     * @param strBase Base of the <code>IObjectTransform</code> properties
     * @return the initialized <code>IObjectTransform</code> object
     *
     * @see IObjectTransform
     */
    protected IObjectTransform readInitObjectTransform( String strBase )
    {
        IObjectTransform objTransform = (IObjectTransform) readNewInstanceInit( strBase );
        objTransform.init( strBase );

        return objTransform;
    }

    /**
     * Read and init a <code>IKeyAdapter</code> object
     *
     * @param strBase Base of the <code>IKeyAdapter</code> properties
     * @return the initialized <code>IKeyAdapter</code> object
     *
     * @see IKeyAdapter
     */
    protected IKeyAdapter readInitKeyAdapter( String strBase )
    {
        IKeyAdapter keyAdapter = (IKeyAdapter) readNewInstanceInit( strBase );
        keyAdapter.init( strBase );

        return keyAdapter;
    }

    /**
     * Create a new instance based on class <code><i>strBase</i>.class</code>
     *
     * @param strBase Base of the properties to instanciate the object
     * @return the new instance
     */
    private Object readNewInstanceInit( String strBase )
    {
        final String strClass = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_CLASS );

        if ( strClass == null )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_CLASS + " must be define." );
        }

        try
        {
            final Class klass = Class.forName( strClass );
            Object obj = klass.newInstance(  );

            return obj;
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_CLASS + " (" + strClass +
                ") define an unknown class (" + e.getMessage(  ) + ")" );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_CLASS + " (" + strClass +
                ") define an illegal access class (" + e.getMessage(  ) + ")" );
        }
        catch ( InstantiationException e )
        {
            throw new RuntimeException( strBase + PROPERTY_FRAGMENT_CLASS + " (" + strClass +
                ") define a class with instanciation exception (" + e.getMessage(  ) + ")" );
        }
    }
}
