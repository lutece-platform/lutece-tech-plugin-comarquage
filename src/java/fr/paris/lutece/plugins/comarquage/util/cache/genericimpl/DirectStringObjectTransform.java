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

import fr.paris.lutece.plugins.comarquage.util.cache.IObjectTransform;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.UnsupportedEncodingException;


/**
 * Simple code/decode a byte array into String with encoding or not<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.objToByteEncoding</code></b>&nbsp;: The encoding used to encode string into bytes array.</li>
 * <li><i>base</i><b><code>.byteToObjEncoding</code></b>&nbsp;: The encoding used to decode bytes array to string.</li>
 * </ul>
 */
public class DirectStringObjectTransform implements IObjectTransform
{
    private static final String PROPERTY_FRAGMENT_OBJ_TO_BYTE_ENCODING = ".objToByteEncoding";
    private static final String PROPERTY_FRAGMENT_BYTE_TO_OBJ_ENCODING = ".byteToObjEncoding";
    private String _strObjToByteEncoding;
    private String _strByteToObjEncoding;

    /**
     * Public constructor
     *
     */
    public DirectStringObjectTransform(  )
    {
    }

    /**
     * @see IObjectTransform#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        _strObjToByteEncoding = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_OBJ_TO_BYTE_ENCODING );
        _strByteToObjEncoding = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_BYTE_TO_OBJ_ENCODING );
    }

    /**
     * @see IObjectTransform#transformToBinary(Object)
     *
     * @param o the object to transform
     * @return the object into bytes array encoded
     */
    public byte[] transformToBinary( Object o )
    {
        if ( _strObjToByteEncoding == null )
        {
            return ( (String) o ).getBytes(  );
        }

        try
        {
            return ( (String) o ).getBytes( _strObjToByteEncoding );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e.getMessage(  ) );
        }
    }

    /**
     * @see IObjectTransform#transformToObject(byte[])
     *
     * @param b the bytes array to transform into object
     * @return the object coded
     */
    public Object transformToObject( byte[] b )
    {
        if ( _strByteToObjEncoding == null )
        {
            return new String( b );
        }

        try
        {
            return new String( b, _strByteToObjEncoding );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e.getMessage(  ) );
        }
    }
}
