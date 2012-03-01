/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.comarquage.service.search;

import fr.paris.lutece.plugins.comarquage.util.parsers.CoMarquagePublicParser;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.lucene.document.Document;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * Indexer service for public cards (comarquage)
 */
public class CoMarquagePublicIndexer implements SearchIndexer
{
    // Constants
    private static final String INDEXER_NAME = "ComarquagePublicIndexer";
    private static final String INDEXER_DESCRIPTION = "Indexer service for public cards";
    private static final String INDEXER_VERSION = "1.0.0";
    private static final String PROPERTY_INDEXER_ENABLE = "comarquage.indexing.publicIndexer.enable";
    private static final String JSP_SEARCH_COMARQUAGE = "jsp/site/Portal.jsp?page=comarquage";
    public static final String INDEX_TYPE_COMARQUAGE = "comarquage.indexing.publicType";

    /**
     * {@inheritDoc}
     */
    public List<Document> getDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        // Parses the XML keyword file of the public cards (CDC)
        CoMarquagePublicParser publicParser = new CoMarquagePublicParser(  );

        // Gets the list of lucene documents (to add to the index)
        List<Document> listDocuments = publicParser.getPublicDocuments(  );

        return listDocuments;
    }

    /**
     * {@inheritDoc}
     */
    public List<Document> getDocuments( String strIdDocument )
        throws IOException, InterruptedException, SiteMessageException
    {
        return null;
    }

    /**
     * Index all documents
     * @throws IOException exception
     * @throws InterruptedException exception
     * @throws SiteMessageException exception
     */
    public void indexDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        // Parses the XML keyword file of the public cards (CDC)
        CoMarquagePublicParser publicParser = new CoMarquagePublicParser(  );

        // Gets the list of lucene documents (to add to the index)
        List<Document> listDocuments = publicParser.getPublicDocuments(  );

        for ( Document doc : listDocuments )
        {
            IndexationService.write( doc );
        }
    }

    /**
    * {@inheritDoc}
    */
    public String getName(  )
    {
        return INDEXER_NAME;
    }

    /**
    * {@inheritDoc}
    */
    public String getVersion(  )
    {
        return INDEXER_VERSION;
    }

    /**
    * {@inheritDoc}
    */
    public String getDescription(  )
    {
        return INDEXER_DESCRIPTION;
    }

    /**
    * {@inheritDoc}
    */
    public boolean isEnable(  )
    {
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE, "true" );

        return ( strEnable.equalsIgnoreCase( "true" ) );
    }

    /**
     * {@inheritDoc}
     */
	public List<String> getListType(  )
	{
		List<String> listType = new ArrayList<String>(  );
		listType.add( AppPropertiesService.getProperty( INDEX_TYPE_COMARQUAGE ) );
		
		return listType;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSpecificSearchAppUrl(  )
	{
		return JSP_SEARCH_COMARQUAGE;
	}
}
