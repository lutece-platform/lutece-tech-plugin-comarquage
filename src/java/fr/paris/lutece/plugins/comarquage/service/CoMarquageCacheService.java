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
package fr.paris.lutece.plugins.comarquage.service;

import fr.paris.lutece.plugins.comarquage.util.CoMarquageUtils;
import fr.paris.lutece.plugins.comarquage.util.cache.IChainManager;
import fr.paris.lutece.portal.service.content.ContentService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;

import javax.servlet.http.HttpServletRequest;


/**
 * This class delivers articles to web componants. It handles XML tranformation to HTML and provides a cache feature in
 * order to reduce the number of tranformations.
 */
public class CoMarquageCacheService extends ContentService
{
    private static boolean _bCache = true;
    private static final String CONTENT_SERVICE_NAME = "CoMarquageCacheService";

    /**
    * Returns the HTML (or XML) code for a page for a given mode. The Service should use request parameters to
    * identify the page content to provide.
    * Actually this method is not used : code generation for a comarquage page is always handled by CoMarquageApp.getPage
    *
    * @param request The HTTP request containing content parameters
    * @param nMode The current mode.
    * @return The HTML (or XML) code of the page.
    * @throws UserNotSignedException the UserNotSignedException
    * @see fr.paris.lutece.plugins.comarquage.web.CoMarquageApp#getPage(HttpServletRequest, int, Plugin)
    */
    public String getPage( HttpServletRequest request, int nMode )
        throws UserNotSignedException
    {
        return null;
    }

    /**
     * Analyzes request's parameters to see if the request should be handled by the current Content Service
     * Should always return false in this case to prevent the getPage method from this service to be used.
     *
     * @param request The HTTP request
     * @return false always
     */
    public boolean isInvoked( HttpServletRequest request )
    {
        // must be false not to invoked getPage from this service
        return false;
    }

    /**
     * Returns the Content Service name
     *
     * @return The name as a String
     */
    public String getName(  )
    {
        return CONTENT_SERVICE_NAME;
    }

    /**
     * Gets the current cache status.
     *
     * @return true if enable, otherwise false
     */
    public boolean isCacheEnable(  )
    {
        return _bCache;
    }

    /**
     * Gets the number of item currently in the cache.
     *
     * @return the number of item currently in the cache.
     */
    public int getCacheSize(  )
    {
        int nCacheSize = 0;

        IChainManager chainManager = CoMarquageUtils.getChainManagerByPluginName( getPluginName(  ) );
        nCacheSize += chainManager.getCachesSize(  );

        //}
        return nCacheSize;
    }

    /**
     * Reset the caches.
     */
    public void resetCache(  )
    {
        IChainManager chainManager = CoMarquageUtils.getChainManagerByPluginName( getPluginName(  ) );
        chainManager.emptyCaches(  );
        AppLogService.info( "Reset comarquage cache done for instance named : " + getPluginName(  ) );
    }
}
