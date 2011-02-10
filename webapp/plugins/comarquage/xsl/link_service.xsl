<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:param name="selectedCdcCode"></xsl:param>
    <xsl:param name="pluginName"></xsl:param>
    <xsl:param name="adminPath"></xsl:param>
    <xsl:param name="linkServiceId"></xsl:param>
    
    <xsl:variable name="link" >
        jsp/admin/plugins/comarquage/ThemeSelector.jsp?plugin_name=<xsl:value-of select="$pluginName" />
    </xsl:variable>
    
    <!-- ************************************************************************ -->
    <!-- Variable globale : référence jusqu'au thème parent,
    utilisée pour construire les liens -->
    <xsl:variable name="lien_chemin">
        <xsl:value-of select="/fiche_localisee/infos_techniques/chemin/@id"/>
        <xsl:text>/</xsl:text>
    </xsl:variable>
    
    
    <!-- ************************************************************************ -->
    <!-- un tableau de 2 colonnes qui contient toutes les infos -->
    <xsl:template match="/fiche_localisee">
        
        
        
        <table>
            <tr>
                <td>
                    <xsl:apply-templates select="infos_techniques/chemin"/>
                </td>
            </tr>
        </table>
        
        <form name="fenvoi" method="post" action="jsp/admin/plugins/comarquage/DoInsertTheme.jsp">
            <input type="hidden" name="input" value="html_content" />
            <input type="hidden" name="plugin_name" value="{$pluginName}" />
            <table>
                
                <xsl:apply-templates select="fiche_pratique"/>
                
            </table>
            <p>
                <select name="target">
                    <option value="_self">Même fenêtre</option>
                    <option value="_blank">Nouvelle fenêtre</option>
                </select>
            </p>
            <p>
                <input type="submit" class="button" value="valider" />   &#160;
                <input type="button" class="button" onclick="javascript:history.go(-1);" value="Retour"/>
            </p>
        </form>
        
        
        
        
        
        
    </xsl:template>
    
    
    <!-- ************************************************************************ -->
    <!-- Colonne de gauche : contient la zone principale "vosdroits" dans une portlet,
    puis la zone "vosquestions" et les liens chacun dan une portlet -->
    <xsl:template match="fiche_pratique">
        <xsl:apply-templates select="vosdroits"/>
    </xsl:template>
    
    
    <!-- Template "vosdroits"
 contient les noeuds_fils (liens vers sous-thèmes/fiches ou lien dans la page)
 et les éléments "fiches" si la page est de type fiche -->
    <xsl:template match="vosdroits">
        
        <xsl:apply-templates select="noeuds_fils"/>
        
    </xsl:template>
    
    
    
    
    
    
    <!-- ************************************************************************ -->
    <!-- Template chemin
    Affiche la hiérarchie des thèmes jusqu'à l'élément courant, pour permettre la navigation -->
    <xsl:template match="infos_techniques/chemin">
        
        <xsl:for-each select="fiche_tech">
            
            
            <xsl:if test="@path='N0'">
                <xsl:text> > </xsl:text>
                <a href="{$link}&amp;insert_service_type={$linkServiceId}">Accueil Vos démarches</a>
            </xsl:if>
            <xsl:if test="@path!='N0'">
                <xsl:if test="position()!=last()">
                    <xsl:text> > </xsl:text>
                    <a href="{$link}&amp;id={@path}&amp;insert_service_type={$linkServiceId}"><xsl:value-of select="@titre"/></a>
                </xsl:if>
                
            </xsl:if>
            
        </xsl:for-each>
        
        <xsl:text> > </xsl:text>
        <xsl:value-of select="../../fiche_pratique/titre"/>
    </xsl:template>
    
    
    <xsl:template match="noeuds_fils">
        
        <xsl:apply-templates select="item"/>
        
    </xsl:template>
    
    
    
    
    <!-- Template "item" -->
    <xsl:template match="item">
        <xsl:choose>
            <xsl:when test="lien/@type='theme'">
                <tr>
                    <th>
                        <!-- <xsl:variable name="encoded_lien_chemin" select="replace(lien_chemin,'/', '_')" /> -->
                        <!-- <xsl:variable name="encoded_lien" select="lien" /> -->
                        <input type="hidden" name="theme_title_{$lien_chemin}{lien}" value="{titre}" />
                        <input type="radio" name="theme_id" value="{$lien_chemin}{lien}" />
                        <!-- 	<xsl:value-of select ="$lien_chemin" /> -  
				<xsl:value-of select ="lien" />  -->
				
                    </th>
                    <th>
                        <a href="{$link}&amp;id={$lien_chemin}{lien}&amp;insert_service_type={$linkServiceId}"><xsl:value-of select="titre"/></a>
                    </th>
                </tr>
            </xsl:when>
            <xsl:when test="lien/@type='fiche'">
            </xsl:when>
            <xsl:otherwise>
                <!--tr border="1">
			  <th valign="center" width="10">
			    	<input type="radio" name="themetitle" value="{titre}" onClick="javascript:envoi('{$lien_chemin}{lien}');window.close()"/>
			 </th>
  	 <th valign="center" align="left" width="190" >

	  	<xsl:value-of select="titre"/>

	   </th>
	</tr-->
            </xsl:otherwise>
            
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
