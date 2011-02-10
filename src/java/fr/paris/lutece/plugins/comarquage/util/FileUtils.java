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
package fr.paris.lutece.plugins.comarquage.util;

import java.io.File;


/**
 * File utilities
 */
public final class FileUtils
{
    /**
     * Hidden constructor
     *
     */
    private FileUtils(  )
    {
    }

    /**
     * Delete the file
     * @param strFilePath the file to delete
     * @return <code>true</code> if the file as been deleted
     */
    public static boolean removeFile( File strFilePath )
    {
        return strFilePath.delete(  );
    }

    /**
     *
     * @param file the file
     * @return <code>true</code> if the file exist
     */
    public static boolean fileExists( File file )
    {
        return file.exists(  );
    }

    /**
     * Deletes all files and subdirs
     *
     * @param path The path to delete
     * @return <code>true</code> if the directory as been deleted
     */
    public static boolean deleteDirectory( File path )
    {
        if ( path.exists(  ) )
        {
            File[] files = path.listFiles(  );

            for ( File fileCurrent : files )
            {
                if ( fileCurrent.isDirectory(  ) )
                {
                    deleteDirectory( fileCurrent );
                }
                else
                {
                    fileCurrent.delete(  );
                }
            }
        }

        return path.delete(  );
    }
}
