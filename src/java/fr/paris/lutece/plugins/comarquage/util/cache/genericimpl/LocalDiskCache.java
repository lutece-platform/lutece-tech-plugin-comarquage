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
package fr.paris.lutece.plugins.comarquage.util.cache.genericimpl;

import fr.paris.lutece.plugins.comarquage.util.FileUtils;
import fr.paris.lutece.plugins.comarquage.util.cache.IContextChainManager;
import fr.paris.lutece.plugins.comarquage.util.cache.IKeyAdapter;
import fr.paris.lutece.plugins.comarquage.util.cache.IObjectTransform;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;


/**
 * Real Disk Access Filter<br/>
 *
 * <b>Properties read here:</b>
 * <ul>
 * <li><i>base</i><b><code>.basePath</code></b>&nbsp;: <b>Required</b> The filesystem path where cached files will be store.</li>
 * <li><i>base</i><b><code>.readOnly</code></b>&nbsp;: Mark the composent has readonly (source of documents access).</li>
 * <li><i>base</i><b><code>.keyAdapter.class</code></b>&nbsp;: <b>Required</b> The class used to implements key adapter (transform key to a valid relatif filesystem path).</li>
 * <li><i>base</i><b><code>.transform.class</code></b>&nbsp;: <b>Required</b> The class used to access informations stored in the disk.</li>
 * </ul>
 */
public class LocalDiskCache extends AbstractCache
{
    private static final String PROPERTY_FRAGMENT_BASE_PATH = ".basePath";
    private static final String PROPERTY_FRAGMENT_READ_ONLY = ".readOnly";
    private static final String PROPERTY_FRAGMENT_READ_ONLY_TRUE = "true";
    private static final String PROPERTY_BASE_KEY_ADAPTER = ".keyAdapter";
    private static final String PROPERTY_BASE_TRANSFORM = ".transform";

    //ancien	private static final int ERROR_NUMBER_COMARQUAGE_IO = 1001;
    private static final String ERROR_NUMBER_COMARQUAGE_IO = "1001";
    private IKeyAdapter _keyAdapter;
    private IObjectTransform _objTransform;
    private boolean _bReadOnly;
    private String _strPropertyBasePath;
    private int _nCacheSize;

    /**
     * Public constructor
     *
     */
    public LocalDiskCache(  )
    {
        super( "DiskAccessFilter", "Disk Access Filter" );
    }

    /**
     * @see fr.paris.lutece.plugins.comarquage.util.cache.IChainNode#init(String)
     *
     * @param strBase The prefix of params to search
     */
    public void init( String strBase )
    {
        super.init( strBase );

        _strPropertyBasePath = strBase + PROPERTY_FRAGMENT_BASE_PATH;

        final String readOnly = AppPropertiesService.getProperty( strBase + PROPERTY_FRAGMENT_READ_ONLY );

        if ( readOnly != null )
        {
            _bReadOnly = readOnly.equalsIgnoreCase( PROPERTY_FRAGMENT_READ_ONLY_TRUE );
        }

        _keyAdapter = readInitKeyAdapter( strBase + PROPERTY_BASE_KEY_ADAPTER );
        _objTransform = readInitObjectTransform( strBase + PROPERTY_BASE_TRANSFORM );
        _nCacheSize = 0;
    }

    /**
     * @see AbstractCache#doSearch(IContextChainManager, Serializable)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @return the element in cache
     */
    public Object doSearch( IContextChainManager filterManager, Serializable key )
    {
        final Object adaptedKey = _keyAdapter.adaptKey( key );

        String strPath = AppPathService.getPath( _strPropertyBasePath, (String) adaptedKey );
        final File file = new File( strPath );

        try
        {
            final InputStream in0 = new FileInputStream( file );
            final InputStream in = new BufferedInputStream( in0 );

            final byte[] buf = new byte[(int) file.length(  )];
            int len = 0;
            int pos = 0;

            do
            {
                len = in.read( buf, pos, buf.length - pos );
                pos += len;
            }
            while ( len > 0 );

            in.close(  );

            return _objTransform.transformToObject( buf );
        }
        catch ( FileNotFoundException e )
        {
            return null;
        }
        catch ( IOException e )
        {
            //ancien	throw new PhysicalException( ERROR_NUMBER_COMARQUAGE_IO, filterManager.getPluginName(  ), e );
            throw new AppException( ERROR_NUMBER_COMARQUAGE_IO, e );
        }
    }

    /**
     * @see AbstractCache#doStore(IContextChainManager, Serializable, Object)
     *
     * @param filterManager The context filter/cache manager, used to call next filter or ask parameters
     * @param key the key of the element
     * @param element the element to store
     */
    public void doStore( IContextChainManager filterManager, Serializable key, Object element )
    {
        if ( _bReadOnly )
        {
            return;
        }

        try
        {
            final Object adaptedKey = _keyAdapter.adaptKey( key );

            String strPath = AppPathService.getPath( _strPropertyBasePath, (String) adaptedKey );
            final File file = new File( strPath );

            // return true to indicate a directory creation, false if directory exist
            file.getParentFile(  ).mkdirs(  );

            // return true to indicate a file creation, false if file exist
            boolean bNewFile = file.createNewFile(  );

            if ( bNewFile )
            {
                _nCacheSize++;
            }

            final OutputStream out0 = new FileOutputStream( file );
            final OutputStream out = new BufferedOutputStream( out0 );

            final byte[] buf = _objTransform.transformToBinary( element );

            out.write( buf );

            out.close(  );
        }
        catch ( IOException e )
        {
            //ancien	throw new PhysicalException( ERROR_NUMBER_COMARQUAGE_IO, filterManager.getPluginName(  ), e );
            throw new AppException( ERROR_NUMBER_COMARQUAGE_IO, e );
        }
    }

    /**
     * Empty a cache
     *
     *
     */
    public void doFlush(  )
    {
        if ( _bReadOnly )
        {
            return;
        }

        String strPath;
        strPath = AppPathService.getPath( _strPropertyBasePath );

        File dirPath = new File( strPath );
        FileUtils.deleteDirectory( dirPath );

        _nCacheSize = 0;
    }

    /**
     * Get the number of items in cache
     * @return the cache size, ie the number of files in cache
     */
    public int getCacheSize(  )
    {
        if ( _bReadOnly )
        {
            return 0;
        }

        String strPath;
        strPath = AppPathService.getPath( _strPropertyBasePath );

        List<File> listFiles = null;

        try
        {
            listFiles = FileSystemUtil.getFiles( strPath, "" );
            listFiles.addAll( FileSystemUtil.getSubDirectories( strPath, "" ) );
            _nCacheSize = scanDirectoryCacheSize( listFiles );
        }
        catch ( DirectoryNotFoundException e )
        {
            AppLogService.debug( "Directory not found : " + strPath );
        }

        return _nCacheSize;
    }

    /**
     * Count files from disk cache
     * @param listFiles list of files (including directory)
     * @return number of files
     */
    private int scanDirectoryCacheSize( List<File> listFiles )
    {
        int nCache = 0;

        // loop through all the files of the list
        if ( listFiles.size(  ) == 0 )
        {
            return nCache;
        }

        for ( File file : listFiles )
        {
            if ( file.isDirectory(  ) && !file.getAbsolutePath(  ).endsWith( "CVS" ) )
            {
                nCache += scanDirectoryCacheSize( Arrays.asList( file.listFiles(  ) ) );
            }

            if ( !file.isFile(  ) )
            {
                continue;
            }

            nCache++;
        }

        return nCache;
    }
}
