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
package fr.paris.lutece.plugins.comarquage.web;

import fr.paris.lutece.plugins.comarquage.util.CoMarquageUtils;
import fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl.CardKey;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 */
public class CoMarquageInsertServiceJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    /*templates*/
    private static final String TEMPLATE_THEME_SELECTOR = "admin/plugins/comarquage/linkservice/manage_link_service.html";

    /*properties*/
    private static final String PROPERTY_ENTRY_CDC_LINKSERVICE_FRAGMENT = ".entry.cdcLinkService";

    /*MArks*/
    private static final String MARK_PORTAL_URL = "url_portal";
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_THEME_ID = "theme_id";
    private static final String PARAMETER_THEME_TITLE = "theme_title_";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_INPUT = "input";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_ID = "id";

    /**
     * return the UI for insert service selector
     *
     * @param request the Http request
     * @return the html of UI
     */
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        String strPluginName = request.getParameter( PARAMETER_PLUGIN_NAME );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        // get the id from the parameters in the request
        String strId = CoMarquageUtils.getId( request );
        AppLogService.debug( "id " + strId );

        String strData = null;

        String strPropertyCoMarquageCode = strPluginName + CoMarquageConstants.PROPERTY_COMARQUAGE_CODE_FRAGMENT;

        if ( strId == null )
        {
            strId = CoMarquageConstants.ROOT_NODE_ID;
        }

        CardKey cardKey = new CardKey( AppPropertiesService.getProperty( strPropertyCoMarquageCode ), strId, '/' );
        AppLogService.debug( cardKey.toString(  ) );

        String strPropertyEntryCdcLinkService = strPluginName + PROPERTY_ENTRY_CDC_LINKSERVICE_FRAGMENT;
        String strLinkServiceEntry = AppPropertiesService.getProperty( strPropertyEntryCdcLinkService );

        strData = CoMarquageUtils.callChainManagerByPluginName( strPluginName, strLinkServiceEntry, cardKey );

        if ( strData == null )
        {
            String strPropertyMessageNoPage = strPluginName + CoMarquageConstants.PROPERTY_MESSAGE_NO_PAGE_FRAGMENT;
            strData = AppPropertiesService.getProperty( strPropertyMessageNoPage );
        }

        //	substitute the parents hierarchy code previously generated in the main template
        model.put( CoMarquageConstants.MARK_COMARQUAGE_DATA, strData );

        // subsitute the plugin name in the main template
        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );

        //substitute the portal url
        model.put( MARK_PORTAL_URL, AppPathService.getBaseUrl( request ) );

        // Gets the locale of the user
        Locale locale = AdminUserService.getLocale( request );

        // get the main template
        HtmlTemplate tThemeSelector = AppTemplateService.getTemplate( TEMPLATE_THEME_SELECTOR, locale, model );

        return tThemeSelector.getHtml(  );
    }

    /**
     *
     * @param request the Http request
     * @return the service to insert
     */
    public String doInsertTheme( HttpServletRequest request )
    {
        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strId = request.getParameter( PARAMETER_THEME_ID );
        String strTitle = request.getParameter( PARAMETER_THEME_TITLE + strId );
        String strInput = request.getParameter( PARAMETER_INPUT );
        String strPluginName = request.getParameter( PARAMETER_PLUGIN_NAME );

        // Check mandatory fields
        if ( ( strId == null ) || ( strTitle == null ) || strId.equals( "" ) || strTitle.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        UrlItem urlXPage = new UrlItem( AppPathService.getPortalUrl(  ) );
        urlXPage.addParameter( PARAMETER_PAGE, strPluginName );
        urlXPage.addParameter( PARAMETER_ID, strId );

        AppLogService.debug( "url " + urlXPage.getUrlWithEntity(  ) );

        String strLink = buildLink( strTitle, urlXPage.getUrl(  ), strTitle, strTarget );
        AppLogService.debug( "strLink " + strLink );

        return insertUrl( request, strInput, StringEscapeUtils.escapeJavaScript( strLink ) );
    }
}
