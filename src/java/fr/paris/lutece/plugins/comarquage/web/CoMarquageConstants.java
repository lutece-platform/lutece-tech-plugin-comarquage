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
package fr.paris.lutece.plugins.comarquage.web;


/**
 *
 * @author jSteve
 * This class content the utils constants for the package web of comarquage
 *
 */
public abstract class CoMarquageConstants
{
    //	 Properties
    public static final String PROPERTY_COMARQUAGE_CODE_FRAGMENT = ".code";
    public static final String PROPERTY_MESSAGE_NO_PAGE_FRAGMENT = ".message.error.nopage";
    public static final String PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT = ".filename.xsl.accueil";

    // parameters
    public static final String PARAMETER_ROLE_NAME = "role_name";

    //  MARKs	
    public static final String MARK_COMARQUAGE_DATA = "comarquage_data";
    public static final String MARK_PLUGIN_NAME = "plugin_name";
    public static final String MARK_TITLE = "title";

    //
    public static final String ROOT_NODE_ID = "N0";
    
    /**
     * Private constructor
     */
    private CoMarquageConstants(  )
    {
    	// nothing
    }
}
