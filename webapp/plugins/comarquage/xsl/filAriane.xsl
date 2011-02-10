<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" indent="no"/>


<xsl:param name="pluginName"></xsl:param>
<xsl:param name="rootPathLabel"></xsl:param>




<!-- ************************************************************************ -->
<!-- un tableau de 2 colonnes qui contient toutes les infos -->
<xsl:template match="/fiche_localisee">
   		<xsl:apply-templates select="infos_techniques/chemin"/>
</xsl:template>

<!-- ************************************************************************ -->
<!-- Template chemin
Affiche la hiérarchie des thèmes jusqu'à l'élément courant, pour permettre la navigation -->
<xsl:template match="infos_techniques/chemin">		
		<xsl:for-each select="fiche_tech">
			
		<xsl:if test="@path='N0'">		
			&lt;page_link&gt;
               	&lt;page-url&gt;page=<xsl:value-of select="$pluginName"/>&lt;/page-url&gt;
                &lt;page-name&gt;&lt;![CDATA[<xsl:value-of disable-output-escaping="yes" select="$rootPathLabel"/>]]&gt;&lt;/page-name&gt;
			&lt;/page_link&gt;
		</xsl:if>
			
		<xsl:if test="@path!='N0'">
			&lt;page_link&gt;
	           	&lt;page-url&gt;page=<xsl:value-of select="$pluginName"/>&amp;amp;id=<xsl:value-of select="@path"/>&lt;/page-url&gt;
				&lt;page-name&gt;&lt;![CDATA[<xsl:value-of disable-output-escaping="yes" select="@titre"/>]]&gt;&lt;/page-name&gt;
			&lt;/page_link&gt;
		</xsl:if>                
		
		</xsl:for-each>

		<!--
		&lt;page_link&gt;
			&lt;page-name&gt;&lt;![CDATA[<xsl:value-of select="../../fiche_pratique/titre"/>]]&gt;&lt;/page-name&gt;
		&lt;/page_link&gt;
		-->
</xsl:template>


</xsl:stylesheet>