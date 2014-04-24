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
package fr.paris.lutece.plugins.comarquage.web;

import fr.paris.lutece.plugins.comarquage.util.CoMarquageUtils;
import fr.paris.lutece.plugins.comarquage.util.FileUtils;
import fr.paris.lutece.plugins.comarquage.util.cache.comarquageimpl.CardKey;
import fr.paris.lutece.portal.service.html.EncodingService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides comarquage pages.
 */
public class CoMarquageApp implements XPageApplication
{
    // Templates
    private static final String TEMPLATE_XPAGE_COMARQUAGE = "skin/plugins/comarquage/page_comarquage.html";
    private static final String TEMPLATE_RESULTS_ENTRY = "skin/plugins/comarquage/search/search_results_entry.html";
    private static final String TEMPLATE_RESULTS = "skin/plugins/comarquage/search/search_results.html";

    // MARKs
    private static final String MARK_SEARCH_HEADER = "search_header";
    private static final String MARK_RESULTS_LIST = "results_list";
    private static final String MARK_QUERY = "query";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ERROR = "error";

    // Properties
    private static final String PROPERTY_PATHLABEL_FRAGMENT = ".xpage.pagePathLabel";
    private static final String PROPERTY_PAGETITLE_FRAGMENT = ".xpage.pageTitle";
    private static final String PROPERTY_ENTRY_CDC_ACCUEIL_FRAGMENT = ".entry.cdcHtmlAccueil";
    private static final String PROPERTY_ENTRY_CDC_THEME_FRAGMENT = ".entry.cdcHtmlTheme";
    private static final String PROPERTY_RESULTS_PER_PAGE = ".indexing.nbDocsPerPage";
    private static final String PROPERTY_ENTRY_CDC_ACCUEIL_PERSO_FRAGMENT = ".entry.cdcHtmlAccueilPerso.";
    private static final String PROPERTY_ENTRY_CDC_PAGE_LINK_FRAGMENT = ".entry.cdcPageLink";
    private static final String PROPERTY_LOCALS_XSL_PATH_FRAGMENT = ".path.xsl";
    private static final String PROPERTY_PATH_BASE = "lutece.portal.path";
    private static final String PROPERTY_ENCODE_URI_ENCODING = "search.encode.uri.encoding";

    // Parameters
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_XPAGE_APP = "page";

    // Default values
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";
    private static final String DEFAULT_PAGE_INDEX = "1";

    // Message (error)
    private static final String MESSAGE_INVALID_SEARCH_TERMS = ".message.invalidSearchTerms";

    // Bean
    private static final String BEAN_SEARCH_ENGINE = "comarquage.comarquageSearchEngine";

    // Strings
    private static final String STRING_EQUAL = "=";
    private static final String STRING_PAGE = "page";
    private static final String STRING_QUESTION = "?";

    /**
    * Creates a new CoMarquageApp object
    */
    public CoMarquageApp(  )
    {
    }

    /**
    * Returns the CoMarquage XPage content depending on the request
    * parameters and the current mode.
    *
    * @param request The HTTP request.
    * @param nMode The current mode.
    * @param plugin the plugin
    * @return The page content.
    */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
    {
        XPage page = new XPage(  );

        //	records the plugin for futher use
        String strPluginName = plugin.getName(  );

        // set the title of the xpage 
        page.setTitle( I18nService.getLocalizedString( strPluginName + PROPERTY_PAGETITLE_FRAGMENT,
                request.getLocale(  ) ) );

        String strHtmlCode = "";

        // get the id parameter
        String strId = CoMarquageUtils.getId( request );

        // get the query paramter
        String strQuery = request.getParameter( PARAMETER_QUERY );

        // get basic path of the xpage
        String strPathLabel = I18nService.getLocalizedString( strPluginName + PROPERTY_PATHLABEL_FRAGMENT,
                request.getLocale(  ) );

        // if the query parameter is not null, load the search page
        // else if no id parameter is found, load the welcome page,
        // otherwise load the theme page corresponding to the given id
        if ( ( strQuery != null ) && ( strQuery.length(  ) > 0 ) )
        {
            // set the path of the xpage (built the normal pathLabel)
            page.setPathLabel( strPathLabel );

            strHtmlCode = getSearchPage( request, strPluginName );
        }
        else if ( ( strId == null ) || ( strId.equals( "" ) ) )
        {
            // set the path of the xpage (built the normal pathLabel)
            page.setPathLabel( strPathLabel );

            // get the welcome page html code
            strHtmlCode = getWelcomePage( request, strPluginName );
        }
        else
        {
            String strName = request.getParameter( PARAMETER_XPAGE_APP );
            String strPropertyCoMarquageCode = strName + CoMarquageConstants.PROPERTY_COMARQUAGE_CODE_FRAGMENT;
            CardKey cardKey = new CardKey( AppPropertiesService.getProperty( strPropertyCoMarquageCode ), strId, '/' );

            // get the entry for the chain manager corresponding to the page Link
            String strPropertyEntryPageLink = strName + PROPERTY_ENTRY_CDC_PAGE_LINK_FRAGMENT;
            String strPageLinkEntry = AppPropertiesService.getProperty( strPropertyEntryPageLink );

            // call the chain manager to retrieve the xml data that gives the list of items of the extended page path
            String strPathLabelItemsXml = CoMarquageUtils.callChainManagerByPluginName( strName, strPageLinkEntry,
                    cardKey );

            if ( ( strPathLabelItemsXml != null ) && ( strPathLabelItemsXml.length(  ) != 0 ) )
            {
                // set the extended page path
                page.setXmlExtendedPathLabel( strPathLabelItemsXml );
            }
            else
            {
                // set the path of the xpage (built the normal pathLabel)
                page.setPathLabel( strPathLabel );
            }

            // get the page html code
            strHtmlCode = getThemePage( request, strId, strPluginName );
        }

        // set the page content
        page.setContent( strHtmlCode );

        return page;
    }

    /**
     * Returns the Html code for the welcome page of the comarquage plugin
     *
     * @param request the http request
     * @param strPluginName the plugin name
     * @return the html code for tha welcome page
     */
    private String getWelcomePage( HttpServletRequest request, String strPluginName )
    {
        HashMap<String, String> model = new HashMap<String, String>(  );

        String strData = null;

        String strPropertyCoMarquageCode = strPluginName + CoMarquageConstants.PROPERTY_COMARQUAGE_CODE_FRAGMENT;
        CardKey cardKey = new CardKey( AppPropertiesService.getProperty( strPropertyCoMarquageCode ),
                CoMarquageConstants.ROOT_NODE_ID, '/' );

        String strRoleName = request.getParameter( CoMarquageConstants.PARAMETER_ROLE_NAME );

        String strPropertyEntryCdcAccueil;
        String strPropertyFileXslWelcomePage;

        if ( strRoleName != null )
        {
            strPropertyEntryCdcAccueil = strPluginName + PROPERTY_ENTRY_CDC_ACCUEIL_PERSO_FRAGMENT + strRoleName;
            strPropertyFileXslWelcomePage = strPluginName + CoMarquageConstants.PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT +
                "." + strRoleName;
        }
        else
        {
            strPropertyEntryCdcAccueil = strPluginName + PROPERTY_ENTRY_CDC_ACCUEIL_FRAGMENT;
            strPropertyFileXslWelcomePage = strPluginName + CoMarquageConstants.PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT;
        }

        String strXslFileName = AppPropertiesService.getProperty( strPropertyFileXslWelcomePage );

        String strFilePath = AppPathService.getPath( strPluginName + PROPERTY_LOCALS_XSL_PATH_FRAGMENT, strXslFileName );

        File file = new File( strFilePath );
        boolean btestXslWelcomePagePresent = FileUtils.fileExists( file );

        if ( btestXslWelcomePagePresent )
        {
            String strAccueilEntry = AppPropertiesService.getProperty( strPropertyEntryCdcAccueil );
            strData = CoMarquageUtils.callChainManagerByPluginName( strPluginName, strAccueilEntry, cardKey );
        }
        else
        {
            String strPropertyEntryCdcTheme = strPluginName + PROPERTY_ENTRY_CDC_THEME_FRAGMENT;
            String strThemeEntry = AppPropertiesService.getProperty( strPropertyEntryCdcTheme );
            strData = CoMarquageUtils.callChainManagerByPluginName( strPluginName, strThemeEntry, cardKey );
        }

        if ( strData == null )
        {
            String strPropertyMessageNoPage = strPluginName + CoMarquageConstants.PROPERTY_MESSAGE_NO_PAGE_FRAGMENT;
            strData = AppPropertiesService.getProperty( strPropertyMessageNoPage );
        }

        model.put( CoMarquageConstants.MARK_COMARQUAGE_DATA, strData );

        String strSearchHeader = getHeader( request, "" );
        model.put( MARK_SEARCH_HEADER, strSearchHeader );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_COMARQUAGE, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Returns the Html code for a theme page of the comarquage plugin
     *
     * @param request The HTTP request
     * @param strId the �ge id
     * @param strPluginName the plugin name
     * @return the html code for the card whose id is given in request
     */
    private String getThemePage( HttpServletRequest request, String strId, String strPluginName )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strData = null;

        String strPropertyCoMarquageCode = strPluginName + CoMarquageConstants.PROPERTY_COMARQUAGE_CODE_FRAGMENT;
        CardKey cardKey = new CardKey( AppPropertiesService.getProperty( strPropertyCoMarquageCode ), strId, '/' );

        String strPropertyEntryCdcTheme = strPluginName + PROPERTY_ENTRY_CDC_THEME_FRAGMENT;
        String strThemeEntry = AppPropertiesService.getProperty( strPropertyEntryCdcTheme );

        strData = CoMarquageUtils.callChainManagerByPluginName( strPluginName, strThemeEntry, cardKey );

        if ( strData == null )
        {
            String strPropertyMessageNoPage = strPluginName + CoMarquageConstants.PROPERTY_MESSAGE_NO_PAGE_FRAGMENT;
            strData = AppPropertiesService.getProperty( strPropertyMessageNoPage );
        }

        model.put( CoMarquageConstants.MARK_COMARQUAGE_DATA, strData );

        String strSearchHeader = getHeader( request, "" );
        model.put( MARK_SEARCH_HEADER, strSearchHeader );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_COMARQUAGE, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Returns the Html code for the search page
     * @param request the http request
     * @param strPluginName the plugin name
     * @return the Html code for the search page
     */
    private String getSearchPage( HttpServletRequest request, String strPluginName )
    {
        String strQuery = request.getParameter( PARAMETER_QUERY );
        String strBasePath = AppPropertiesService.getProperty( PROPERTY_PATH_BASE );
        String strSearchPageUrl = strBasePath + STRING_QUESTION + STRING_PAGE + STRING_EQUAL + strPluginName;
        String strError = "";
        Locale locale = request.getLocale(  );

        // Check XSS characters
        if ( ( strQuery != null ) && ( StringUtil.containsXssCharacters( strQuery ) ) )
        {
            strError = I18nService.getLocalizedString( strPluginName + MESSAGE_INVALID_SEARCH_TERMS, locale );
            strQuery = "";
        }

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( strPluginName + PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;

        int nNbItemsPerPage = Integer.parseInt( strNbItemPerPage );
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );
        strCurrentPageIndex = ( strCurrentPageIndex != null ) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;

        SearchEngine engine = (SearchEngine) SpringContextService.getPluginBean( strPluginName, BEAN_SEARCH_ENGINE );
        List<SearchResult> listResults = engine.getSearchResults( strQuery, request );
        
        String strQueryForPaginator = EncodingService.encodeUrl( strQuery, PROPERTY_ENCODE_URI_ENCODING );

        UrlItem url = new UrlItem( strSearchPageUrl );
        url.addParameter( PARAMETER_QUERY, strQueryForPaginator );
        url.addParameter( PARAMETER_NB_ITEMS_PER_PAGE, nNbItemsPerPage );

        Paginator paginator = new Paginator( listResults, nNbItemsPerPage, url.getUrl(  ), PARAMETER_PAGE_INDEX,
                strCurrentPageIndex );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_RESULTS_LIST, paginator.getPageItems(  ) );
        model.put( MARK_QUERY, strQuery );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, strNbItemPerPage );
        model.put( MARK_ERROR, strError );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULTS, locale, model );

        return template.getHtml(  );
    }

    /**
     * Returns the search header
     *
     * @param request the http request
     * @param strQuery the query
     * @return the header
     */
    private String getHeader( HttpServletRequest request, String strQuery )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( strQuery == null )
        {
            model.put( MARK_QUERY, "" );
        }
        else
        {
            model.put( MARK_QUERY, strQuery );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RESULTS_ENTRY, request.getLocale(  ), model );

        return template.getHtml(  );
    }
}
