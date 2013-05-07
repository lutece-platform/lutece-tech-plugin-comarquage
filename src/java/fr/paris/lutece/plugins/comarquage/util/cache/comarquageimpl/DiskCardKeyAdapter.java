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
package fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl;

import fr.paris.lutece.plugins.comarquage.util.cache.genericimpl.AbstractKeyAdapter;

import java.io.File;
import java.io.Serializable;

import java.util.Iterator;


/**
 * Adapt a <code>CardKey</code> for the file system (Disk Accessor)<br/>
 *
 * <b>No properties read here.</b>
 */
public class DiskCardKeyAdapter extends AbstractKeyAdapter
{
    /**
     * Public constuctor
     *
     */
    public DiskCardKeyAdapter(  )
    {
        super( "Adapt a CardKey for the file system (Disk Accessor)" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        // Nothing to do
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter#adaptKey(Serializable)
     *
     * @param key the key to adapt
     * @return an adaptation of the key in one technologie / view...
     */
    public Object adaptKey( Serializable key )
    {
        final CardKey cardKey = (CardKey) key;

        final StringBuffer buf = new StringBuffer(  );

        if ( getPrefix(  ) != null )
        {
            buf.append( getPrefix(  ) );
        }

        buf.append( File.separator ).append( cardKey.getCDCCode(  ) );

        Iterator itId = cardKey.getPathCard(  ).iterator(  );

        while ( itId.hasNext(  ) )
        {
            final Object id = itId.next(  );

            buf.append( File.separator ).append( id );
        }

        if ( getSuffix(  ) != null )
        {
            buf.append( getSuffix(  ) );
        }

        return buf.toString(  );
    }
}
