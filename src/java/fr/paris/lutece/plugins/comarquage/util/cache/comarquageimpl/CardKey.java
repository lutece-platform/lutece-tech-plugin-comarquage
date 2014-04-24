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
package fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Card Key
 */
public class CardKey implements Serializable
{
    private static final long serialVersionUID = 8792056053291999157L;
    private final String _strCDCCode;
    private final List<String> _listPathCard;

    /**
     * Public constuctor
     * @param strCDCCode the CDC Code for the card
     */
    public CardKey( String strCDCCode )
    {
        _strCDCCode = strCDCCode;
        _listPathCard = new ArrayList<String>(  );
    }

    /**
     * Public constuctor
     * @param strCDCCode the CDC Code for the node/card
     * @param strPath the path to the node/card
     * @param cSep the separator character
     */
    public CardKey( String strCDCCode, String strPath, char cSep )
    {
        this( strCDCCode );

        StringTokenizer tokenizer = new StringTokenizer( strPath, String.valueOf( cSep ) );

        while ( tokenizer.hasMoreTokens(  ) )
        {
            addPath( tokenizer.nextToken(  ) );
        }
    }

    /**
     * Adds the path
     * @param strCardId the id of the card
     */
    private void addPath( String strCardId )
    {
        _listPathCard.add( strCardId );
    }

    /**
     * Returns the CDC Code
     * @return the CDC code
     */
    public String getCDCCode(  )
    {
        return _strCDCCode;
    }

    /**
     * Returns the path of the card
     * @return the path of the card
     */
    public List<String> getPathCard(  )
    {
        return Collections.unmodifiableList( _listPathCard );
    }

    /**
     * Retruns a string representation
     * @return the string representation
     */
    public String toString(  )
    {
        final StringBuffer buf = new StringBuffer(  );

        buf.append( "ID=" ).append( getCDCCode(  ) );

        for ( Iterator itId = getPathCard(  ).iterator(  ); itId.hasNext(  ); )
        {
            final Object id = itId.next(  );

            buf.append( '/' ).append( id );
        }

        return buf.toString(  );
    }

    /**
     * Returns the hashcode
     * @return the hashcode
     */
    public int hashCode(  )
    {
        return toString(  ).hashCode(  );
    }

    /**
     * Returns true if this is equal to thatn false otherwise
     * @param that the object to compare with this
     * @return true if this is equal to thatn false otherwise
     */
    public boolean equals( Object that )
    {
        if ( that == null )
        {
            return false;
        }

        if ( that == this )
        {
            return true;
        }

        try
        {
            final CardKey cardKey = (CardKey) that;

            if ( ( _strCDCCode == null ) && ( cardKey._strCDCCode != null ) )
            {
                return false;
            }

            if ( ( _strCDCCode != null ) && !_strCDCCode.equals( cardKey._strCDCCode ) )
            {
                return false;
            }

            return _listPathCard.equals( cardKey._listPathCard );
        }
        catch ( ClassCastException e )
        {
            return false;
        }
    }
}
