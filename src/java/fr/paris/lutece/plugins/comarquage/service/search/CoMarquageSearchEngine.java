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
package fr.paris.lutece.plugins.comarquage.service.search;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * CoMarquageSearchEngine
 */
public class CoMarquageSearchEngine implements SearchEngine
{
    // Property
    private static final String PROPERTY_SEARCH_TYPE = "comarquage.search.type";

    /**
     * Return search results
     * @param strQuery The search query
     * @param request The HTTP request
     * @return Results as a collection of SearchResult
     */
    public List<SearchResult> getSearchResults( String strQuery, HttpServletRequest request )
    {
        ArrayList<SearchItem> listResults = new ArrayList<SearchItem>(  );
        Searcher searcher = null;

        try
        {
            searcher = new IndexSearcher( IndexationService.getDirectoryIndex(  ), true );

            Collection<String> queries = new ArrayList<String>(  );
            Collection<String> fields = new ArrayList<String>(  );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>(  );

            // Contents
            if ( ( strQuery != null ) && !strQuery.equals( "" ) )
            {
                Query queryContents = new TermQuery( new Term( SearchItem.FIELD_CONTENTS, strQuery ) );
                queries.add( queryContents.toString(  ) );
                fields.add( SearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            // Type
            String strSearchType = AppPropertiesService.getProperty( PROPERTY_SEARCH_TYPE );
            Query queryType = new TermQuery( new Term( SearchItem.FIELD_TYPE, strSearchType ) );
            queries.add( queryType.toString(  ) );
            fields.add( SearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            Query multiQuery = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION, (String[]) queries.toArray( new String[queries.size(  )] ),
                    (String[]) fields.toArray( new String[fields.size(  )] ),
                    (BooleanClause.Occur[]) flags.toArray( new BooleanClause.Occur[flags.size(  )] ),
                    IndexationService.getAnalyser(  ) );

            // Get results documents
            TopDocs topDocs = searcher.search( multiQuery, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (int i = 0; i < hits.length; i++)
            {
            	int docId = hits[i].doc;
                Document document = searcher.doc(docId);
                SearchItem si = new SearchItem( document );
                listResults.add( si );
            }

            searcher.close(  );
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return convertList( listResults );
    }

    /**
     * Convert a list of Lucene items into a list of generic search items
     * @param listSource The list of Lucene items
     * @return A list of generic search items
     */
    private List<SearchResult> convertList( List<SearchItem> listSource )
    {
        List<SearchResult> listDest = new ArrayList<SearchResult>(  );

        for ( SearchItem item : listSource )
        {
            SearchResult result = new SearchResult(  );
            result.setId( item.getId(  ) );

            try
            {
                result.setDate( DateTools.stringToDate( item.getDate(  ) ) );
            }
            catch ( ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle(  ) + "\" : " +
                    e.getMessage(  ) );
            }

            result.setUrl( item.getUrl(  ) );
            result.setTitle( item.getTitle(  ) );
            result.setSummary( item.getSummary(  ) );
            result.setType( item.getType(  ) );
            listDest.add( result );
        }

        return listDest;
    }
}
