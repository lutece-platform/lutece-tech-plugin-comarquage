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
package fr.paris.lutece.plugins.comarquage.business;


/**
 * This class represents the object Card
 */
public class Card
{
    //	masks
    private static final String MASK_THEME_LOCAL = "LN";
    private static final String MASK_THEME_PUBLIC = "N";

    // type name for a theme (node)
    private static final String TYPE_NAME_THEME = "theme";

    //  type name for a card
    private static final String TYPE_NAME_CARD = "fiche";

    // private attributes
    private String _strId;
    private String _strTitle;
    private String _strType;

    /**
    * Constructor.
    * Creates a new Card object.
    */
    public Card(  )
    {
    }

    /**
     * Constructor.
     * Creates a new Card object of given id and title.
     * @param strId the id of the object
     * @param strTitle the title of the object
     */
    public Card( String strId, String strTitle )
    {
        _strId = strId;
        _strTitle = strTitle;

        // set the type fiche/theme
        if ( _strId.startsWith( MASK_THEME_LOCAL ) || _strId.startsWith( MASK_THEME_PUBLIC ) )
        {
            setType( TYPE_NAME_THEME );
        }
        else
        {
            setType( TYPE_NAME_CARD );
        }
    }

    /**
    * Getter for _strId
    * @return the id
    */
    public String getId(  )
    {
        return _strId;
    }

    /**
     * Getter for _strTitle
     * @return the title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Setter for _strId
     * @param strId the id
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * Setter for _strTitle
     * @param strTitle the title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Getter for _strType
     * @return the type
     */
    public String getType(  )
    {
        return _strType;
    }

    /**
     * Setter for _strType
     * @param strType the type
     */
    private void setType( String strType )
    {
        _strType = strType;
    }
}
