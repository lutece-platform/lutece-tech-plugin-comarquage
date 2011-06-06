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
package fr.paris.lutece.plugins.comarquage.util.parsers;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * DOM Parser
 * Provide parsing methods and override toString Method
 *
 */
public class DomParser
{
    private Document _document;

    /**
     * Constructor : get the source Stream and parse it
     * @param inSource The stream to parse
     */
    public DomParser( InputStream inSource )
    {
        try
        {
            // document factory creation
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(  );

            // Create a document constructor
            DocumentBuilder builder = factory.newDocumentBuilder(  );
            setDocument( builder.parse( inSource ) );
        }
        catch ( ParserConfigurationException pce )
        {
            AppLogService.error( "DOMParser configuration error during call to factory.newDocumentBuilder() : ", pce );
        }
        catch ( SAXException se )
        {
            AppLogService.error( "DOMParser error during call to builder.parse(inSource) : ", se );
        }
        catch ( IOException ioe )
        {
            AppLogService.error( "DOMParser I/O error during call to builder.parse(inSource) : ", ioe );
        }
    }

    /**
     * Return DOM document source in a byte array
     *
     * @return DOM document source
     */
    public byte[] toByteArray(  )
    {
        try
        {
            Source source = new DOMSource( getDocument(  ) );
            ByteArrayOutputStream out = new ByteArrayOutputStream(  );
            Result result = new StreamResult( out );
            TransformerFactory factory = TransformerFactory.newInstance(  );
            Transformer transformer = factory.newTransformer(  );
            transformer.transform( source, result );

            return out.toByteArray(  );
        }
        catch ( TransformerConfigurationException e )
        {
            AppLogService.error( "DOMParser toByteArray transform configuration error  : ", e );
        }
        catch ( TransformerException e )
        {
            AppLogService.error( "DOMParser toByteArray transform exception  : ", e );
        }

        return null;
    }

    /**
     * Get the document
     * @return the _document
     */
    public Document getDocument(  )
    {
        return _document;
    }

    /**
     * Set the document
     * @param _document the _document to set
     */
    private void setDocument( Document _document )
    {
        this._document = _document;
    }
}
