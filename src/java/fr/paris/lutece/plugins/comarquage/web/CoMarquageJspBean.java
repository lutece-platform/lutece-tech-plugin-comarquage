/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.comarquage.util.FileUtils;
import fr.paris.lutece.plugins.comarquage.util.localnodes.LocalXmlFilesUtils;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage CoMarquage features
 */
public class CoMarquageJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_COMARQUAGE_MANAGEMENT = "COMARQUAGE_MANAGEMENT";
    private static final String JSP_DO_REMOVE_XML_LOCAL_FILE = "jsp/admin/plugins/comarquage/DoRemoveXmlLocalFile.jsp";
    private static final String JSP_DO_OVERRIDE_XML_LOCAL_FILE = "jsp/admin/plugins/comarquage/DoOverrideXmlLocalFile.jsp";

    /* parameters */
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_FILE_NAME = "file_name";
    private static final String PARAMETER_KEYWORDS_FILE = "keywords_file";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_FILE_SOURCE_NAME = "file_source";

    /* templates for the plugin management page */
    private static final String TEMPLATE_MANAGE_COMARQUAGE = "admin/plugins/comarquage/manage_comarquage.html";
    private static final String TEMPLATE_MANAGE_XML_LOCAL_FILES = "admin/plugins/comarquage/manage_xml_local_files.html";
    private static final String TEMPLATE_MANAGE_XSL_FILES = "admin/plugins/comarquage/manage_xsl_welcome_page.html";
    private static final String TEMPLATE_MODIFY_XSL_FILES = "admin/plugins/comarquage/modify_xsl_welcome_page.html";
    private static final String TEMPLATE_CREATE_COMARQUAGE = "admin/plugins/comarquage/create_xml_local_file.html";
    private static final String TEMPLATE_MANAGE_KEYWORDS_FILES = "admin/plugins/comarquage/manage_keywords_files.html";
    private static final String TEMPLATE_MODIFY_KEYWORDS_FILE = "admin/plugins/comarquage/modify_keywords_file.html";

    /* bookmarks for the plugin management page */
    private static final String MARK_COMARQUAGE_CODE = "comarquage_code";
    private static final String MARK_XML_LOCAL_FILE_LIST = "file_list";
    private static final String MARK_ROLE_NAME = "role_name";
    private static final String MARK_KEYWORDS_FILE = "keywords_file";

    /* properties */
    private static final String PROPERTY_LOCALS_XML_PATH_FRAGMENT = ".path.xml.locals";
    private static final String PROPERTY_LOCALS_XSL_PATH_FRAGMENT = ".path.xsl";
    private static final String PROPERTY_INDEX_CDC_INDEX_FRAGMENT = ".indexing.";
    private static final String PROPERTY_INDEXING_XML_DIRECTORY = ".path.xml";
    private static final String PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT = ".filename.xsl.accueil";
    private static final String PROPERTY_PAGE_TITLE_COMARQUAGE_MANAGER = "comarquage.comarquage_manager.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_COMARQUAGE_LOCAL_FILE = "comarquage.comarquage_local_file.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_COMARQUAGE_WELCOME_PAGE = "comarquage.comarquage_welcome_page.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_COMARQUAGE_KEYWORDS_FILES = "comarquage.comarquage_keywords_files.pageTitle";
    private static final String PROPERTY_LIST_CDC_INDEX_KEYS_FRAGMENT = ".indexing.listCdcIndexKeys";

    /* JSPs */
    private static final String MANAGE_XML_LOCAL_FILES = "ManageXmlLocalFiles.jsp";
    private static final String MANAGE_XSL_WELCOME_PAGE = "ManageXslWelcomePages.jsp";
    private static final String MANAGE_KEYWORDS_FILES_PAGE = "ManageXmlKeywordsFiles.jsp";
    private static final String CONFIRM_OVERRIDE_XML_LOCAL_FILE = "OverrideXmlLocalFile.jsp";
    private static final String MESSAGE_MANDATORY_FIELDS_UPLOAD = "comarquage.message.mandatory.fields.upload";
    private static final String MESSAGE_CONFIRM_REMOVE_XML_LOCAL_FILE = "comarquage.message.confirmRemoveXmlLocalFile";
    private static final String MESSAGE_CONFIRM_OVERRIDE_XML_LOCAL_FILE = "comarquage.message.confirmOverrideXmlLocalFile";

    /* Utils */
    private static final String SEPARATOR = "/";
    private static final String EXTENSION_OLD = ".old";

    /**
     * Creates a new CoMarquageJspBean object.
     */
    public CoMarquageJspBean(  )
    {
    }

    /**
     * Returns the manage page of comarquage
     *
     * @param request the Http request
     * @return The HTML page
     */
    public String getManageCoMarquage( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_MANAGER );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        // the service code to display
        String strCode = AppPropertiesService.getProperty( getPlugin(  ).getName(  ) +
                CoMarquageConstants.PROPERTY_COMARQUAGE_CODE_FRAGMENT );

        model.put( MARK_COMARQUAGE_CODE, strCode );
        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, getPlugin(  ).getName(  ) );

        //	Get the main template
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_COMARQUAGE, getLocale(  ), model );

        // Return the HTML page
        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the manage page of xml local files
     *
     * @param request the Http request
     * @return The HTML page
     */
    public String getManageXmlLocalFiles( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_LOCAL_FILE );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        model.put( MARK_XML_LOCAL_FILE_LIST, getXmlLocalFilesList( strPluginName ) );
        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_XML_LOCAL_FILES, getLocale(  ), model );

        // Return the HTML page
        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the create form of a new local xml file with the upload field
     * @param request the http request
     * @return the html code for the create form of a co-marquage card
     */
    public String getCreateXmlLocalFile( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_LOCAL_FILE );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_COMARQUAGE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes the creation form of a new card by recovering the parameters
     * in the http request
     * @param request the http request
     * @return The Jsp URL of the process result
     * @throws FileNotFoundException
     */
    public String doCreateXmlLocalFile( HttpServletRequest request )
    {
        try
        {
            String strPluginName = getPlugin(  ).getName(  );

            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;

            FileItem fileFrom = null;
            fileFrom = multi.getFile( PARAMETER_FILE_SOURCE_NAME );

            if ( ( fileFrom == null ) || fileFrom.getName(  ).equals( "" ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELDS_UPLOAD,
                    AdminMessage.TYPE_STOP );
            }

            //          Getting downloadFile's name
            String strNameFile = FileUploadService.getFileNameOnly( fileFrom );

            // Clean name
            String strClearName = UploadUtil.cleanFileName( strNameFile );

            String strXmlPath = AppPathService.getPath( strPluginName + PROPERTY_LOCALS_XML_PATH_FRAGMENT, strClearName );

            File destFile = new File( strXmlPath );

            if ( destFile.exists(  ) )
            {
                String strXmlOldPath = AppPathService.getPath( strPluginName + PROPERTY_LOCALS_XML_PATH_FRAGMENT,
                        strClearName + EXTENSION_OLD );

                File tmpOldFile = new File( strXmlOldPath );
                destFile.renameTo( tmpOldFile );

                File tmpFile = new File( strXmlPath );

                try
                {
                    FileOutputStream fos = new FileOutputStream( tmpFile );
                    fos.flush(  );
                    fos.write( fileFrom.get(  ) );
                    fos.close(  );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e.getMessage(  ), e );

                    if ( tmpFile.exists(  ) )
                    {
                        tmpFile.delete(  );
                    }

                    tmpOldFile.renameTo( destFile );
                }

                // Displays the confirmation screen
                return CONFIRM_OVERRIDE_XML_LOCAL_FILE + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin(  ).getName(  ) +
                "&" + PARAMETER_FILE_NAME + "=" + strClearName;
            }
            else
            {
                FileOutputStream fos = new FileOutputStream( destFile );
                fos.flush(  );
                fos.write( fileFrom.get(  ) );
                fos.close(  );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( FileNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        //Displays the list of the Co-Marquage files
        return MANAGE_XML_LOCAL_FILES + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin(  ).getName(  );
    }

    /**
     * Get the confirmation form when trying to override an existing file
     * @param request the http request
     * @return the html code for the confirmation form
     */
    public String getOverrideXmlLocalFile( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_OVERRIDE_XML_LOCAL_FILE );
        url.addParameter( PARAMETER_FILE_NAME, request.getParameter( PARAMETER_FILE_NAME ) );
        url.addParameter( PARAMETER_FILE_SOURCE_NAME, request.getParameter( PARAMETER_FILE_SOURCE_NAME ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_OVERRIDE_XML_LOCAL_FILE, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Override  an existing xml local file with the one uploaded (after confirmation)
     * @param request the http request
     * @return The Jsp URL of the process result
     */
    public String doOverrideXmlLocalFile( HttpServletRequest request )
    {
        String strPluginName = getPlugin(  ).getName(  );
        String strFileName = request.getParameter( PARAMETER_FILE_NAME );
        String strXmlOldPath = AppPathService.getPath( strPluginName + PROPERTY_LOCALS_XML_PATH_FRAGMENT,
                strFileName + EXTENSION_OLD );

        File oldFile = new File( strXmlOldPath );

        if ( oldFile.exists(  ) )
        {
            oldFile.delete(  );
        }

        return MANAGE_XML_LOCAL_FILES + "?" + PARAMETER_PLUGIN_NAME + "=" + strPluginName;
    }

    /**
     * Returns the remove confirmation page of a new local xml file
     * @param request the http request
     * @return the html code for the remove confirmation form of a co-marquage card
     */
    public String getRemoveXmlLocalFile( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_REMOVE_XML_LOCAL_FILE );
        url.addParameter( PARAMETER_FILE_NAME, request.getParameter( PARAMETER_FILE_NAME ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_XML_LOCAL_FILE, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes the removal form of a xml local file
     * @param request the http request
     * @return The Jsp URL of the process result
     */
    public String doRemoveXmlLocalFile( HttpServletRequest request )
    {
        String strFileName = request.getParameter( PARAMETER_FILE_NAME );
        String strRootDirectory = AppPathService.getPath( getPlugin(  ).getName(  ) +
                PROPERTY_LOCALS_XML_PATH_FRAGMENT );

        File strFilePath = new File( strRootDirectory + SEPARATOR + strFileName );
        FileUtils.removeFile( strFilePath );

        //Displays the list of the Co-Marquage files
        return MANAGE_XML_LOCAL_FILES + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin(  ).getName(  );
    }

    /**
     * Returns the xml file list
     * @param strPluginName the plugin
     * @return the html code to diplay the list of stylesheets
     */
    private Collection<String[]> getXmlLocalFilesList( String strPluginName )
    {
        String strRootDirectory = AppPathService.getWebAppPath(  );
        String strLocalDirectoryName = AppPropertiesService.getProperty( strPluginName +
                PROPERTY_LOCALS_XML_PATH_FRAGMENT );
        Collection<String> listFileNames = createFileList( strRootDirectory, strLocalDirectoryName, strPluginName );
        Collection<String[]> listFiles = new ArrayList<String[]>(  );

        for ( String strFileName : listFileNames )
        {
            String[] arrayFileInfo = new String[2];
            arrayFileInfo[0] = strFileName;
            arrayFileInfo[1] = LocalXmlFilesUtils.getTitleFromXmlFile( strFileName, strPluginName );
            listFiles.add( arrayFileInfo );
        }

        return listFiles;
    }

    /**
     * Process the listing, fills the hashmap of the (id, collection of local themes/cards)
     * @param strRootDirectory the name of the root directory where to find the directory to scan
     * @param strLocalDirectoryName the name of the directory to scan
     * @param strPluginName the plugin
     * @return The result of this process (strLogs)
     */
    public static synchronized Collection<String> createFileList( String strRootDirectory,
        String strLocalDirectoryName, String strPluginName )
    {
        // initialise a temporary hashmap that will be filled
        Collection<String> colTemp = new ArrayList<String>(  );

        List<File> listFiles = null;

        try
        {
            listFiles = FileSystemUtil.getFiles( strRootDirectory, strLocalDirectoryName );
            listFiles.addAll( FileSystemUtil.getSubDirectories( strRootDirectory, strLocalDirectoryName ) );
        }
        catch ( DirectoryNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        scanDirectoryFile( colTemp, listFiles );

        return colTemp;
    }

    /**
     * Scans the directory
     * @param col the collection of files where to add elements found during the scan
     * @param listFiles the list of files where to scan
     */
    private static void scanDirectoryFile( Collection<String> col, List<File> listFiles )
    {
        // loop through all the files of the list
        if ( listFiles.size(  ) == 0 )
        {
            return;
        }

        for ( File file : listFiles )
        {
            if ( file.isDirectory(  ) && !file.getAbsolutePath(  ).endsWith( "CVS" ) )
            {
                scanDirectoryFile( col, Arrays.asList( file.listFiles(  ) ) );
            }

            if ( !file.isFile(  ) )
            {
                continue;
            }

            col.add( file.getName(  ) );
        }
    }

    /**
    * Returns the manage page of xsl welcome files
    *
    * @param request the Http request
    * @return The HTML page
    */
    public String getManageXslWelcomePages( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_WELCOME_PAGE );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_XSL_FILES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the modification form for xsl welcome file :  upload field
     * @param request the http request
     * @return the html code for the modification form of a xsl welcome file
     */
    public String getModifyXslWelcomePage( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_WELCOME_PAGE );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        String strRoleName = request.getParameter( CoMarquageConstants.PARAMETER_ROLE_NAME );
        String strTitle = request.getParameter( PARAMETER_TITLE );

        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );
        model.put( MARK_ROLE_NAME, strRoleName );
        model.put( CoMarquageConstants.MARK_TITLE, strTitle );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_XSL_FILES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes the modification of the xsl welcome file
     * @param request the http request
     * @return The Jsp URL of the management page
     */
    public String doModifyXslWelcomePage( HttpServletRequest request )
    {
        try
        {
            String strPluginName = getPlugin(  ).getName(  );

            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;

            String strRoleName = multi.getParameter( CoMarquageConstants.PARAMETER_ROLE_NAME );

            String strXslFile = null;

            if ( ( strRoleName == null ) || ( strRoleName.trim(  ).equals( "" ) ) )
            {
                strXslFile = AppPropertiesService.getProperty( strPluginName + PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT );
            }
            else
            {
                strXslFile = AppPropertiesService.getProperty( strPluginName + PROPERTY_XSL_FILENAME_WELCOME_FRAGMENT +
                        "." + strRoleName );
            }

            String strXslPath = AppPathService.getPath( strPluginName + PROPERTY_LOCALS_XSL_PATH_FRAGMENT, strXslFile );

            //Remove the old file
            FileItem fileFrom = null;
            fileFrom = multi.getFile( PARAMETER_FILE_SOURCE_NAME );

            //test of the existence of the object
            if ( ( fileFrom == null ) || fileFrom.getName(  ).equals( "" ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELDS_UPLOAD,
                    AdminMessage.TYPE_STOP );
            }

            //delete old xslfile
            File fileTo = new File( strXslPath );

            if ( fileTo.exists(  ) )
            {
                fileTo.delete(  );
            }

            //write new xsl file
            File destFile = new File( strXslPath );
            FileOutputStream fos = new FileOutputStream( destFile );
            fos.flush(  );
            fos.write( fileFrom.get(  ) );
            fos.close(  );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( FileNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        //Displays the list of the Co-Marquage files
        return MANAGE_XSL_WELCOME_PAGE + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin(  ).getName(  );
    }

    /**
    * Returns the manage page of xml keyword files used for indexing
    *
    * @param request the Http request
    * @return The HTML page
    */
    public String getManageXmlKeywordsFiles( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_KEYWORDS_FILES );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        // Gets the list of CDC index keys
        String strCdcIndexKeys = AppPropertiesService.getProperty( strPluginName +
                PROPERTY_LIST_CDC_INDEX_KEYS_FRAGMENT );
        List<String> listCdcIndexKeys = new ArrayList<String>(  );
        String[] splitKeys = strCdcIndexKeys.split( "," );

        for ( int i = 0; i < splitKeys.length; i++ )
        {
            listCdcIndexKeys.add( splitKeys[i] );
        }

        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );
        model.put( "list_keys", listCdcIndexKeys );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_KEYWORDS_FILES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the modification form for xml keywords file :  upload field
     * @param request the http request
     * @return the html code for the modification form of a xml keywords file
     */
    public String getModifyXmlKeywordsFile( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_COMARQUAGE_KEYWORDS_FILES );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        String strPluginName = getPlugin(  ).getName(  );

        String strKeywordFile = request.getParameter( PARAMETER_KEYWORDS_FILE );
        String strTitle = request.getParameter( PARAMETER_TITLE );

        model.put( CoMarquageConstants.MARK_PLUGIN_NAME, strPluginName );
        model.put( MARK_KEYWORDS_FILE, strKeywordFile );
        model.put( CoMarquageConstants.MARK_TITLE, strTitle );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_KEYWORDS_FILE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Processes the modification of the xml keyword file
     * @param request the http request
     * @return The Jsp URL of the management page
     */
    public String doModifyXmlKeywordsFile( HttpServletRequest request )
    {
        try
        {
            String strPluginName = getPlugin(  ).getName(  );

            MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;

            // Gets the XML index file path
            String strKeywordsFileType = multi.getParameter( PARAMETER_KEYWORDS_FILE );
            String strXmlFile = AppPropertiesService.getProperty( strPluginName + PROPERTY_INDEX_CDC_INDEX_FRAGMENT +
                    strKeywordsFileType );
            String strXmlPath = AppPathService.getPath( strPluginName + PROPERTY_INDEXING_XML_DIRECTORY, strXmlFile );

            //Remove the old file
            FileItem fileFrom = null;
            fileFrom = multi.getFile( PARAMETER_FILE_SOURCE_NAME );

            //test of the existence of the object
            if ( ( fileFrom == null ) || fileFrom.getName(  ).equals( "" ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELDS_UPLOAD,
                    AdminMessage.TYPE_STOP );
            }

            File fileTo = new File( strXmlPath );

            if ( fileTo.exists(  ) )
            {
                fileTo.delete(  );
            }

            File destFile = new File( strXmlPath );
            FileOutputStream fos = new FileOutputStream( destFile );
            fos.flush(  );
            fos.write( fileFrom.get(  ) );
            fos.close(  );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( FileNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        //Displays the list of the Co-Marquage files
        return MANAGE_KEYWORDS_FILES_PAGE + "?" + PARAMETER_PLUGIN_NAME + "=" + getPlugin(  ).getName(  );
    }
}
