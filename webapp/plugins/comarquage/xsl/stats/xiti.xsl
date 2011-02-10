<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- ==============================================================
   Statistiques Xiti
   ============================================================== -->
 
 <xsl:template name="xiti_code">
  <xsl:variable name="xiti_url">http://logi8.xiti.com/hit.xiti</xsl:variable>
  <xsl:variable name="__xiti_subparam_site">185972</xsl:variable>
  <xsl:variable name="stats_xiti_enabled" select="number(/fiche_localisee/infos_techniques/abonne/variable[@id='xitiEnabled']/text())"/>
 
  <xsl:variable name="__xiti_subparam_subsite">2</xsl:variable>
 
  <xsl:variable name="__xiti_code_adherent"><xsl:value-of select="/fiche_localisee/infos_techniques/abonne/@id"/></xsl:variable>

  <xsl:variable name="__xiti_type_fiche"><xsl:choose>
		<!-- TODO : toBePrinted -->
	<xsl:when test="/fiche_localisee/infos_contenu/source/nom='Service-Public.fr' " >Fiche Nationale</xsl:when>
	<xsl:when test="/fiche_localisee/infos_contenu/source/nom!='Service-Public.fr' ">Fiche Locale</xsl:when>
	<xsl:otherwise> </xsl:otherwise>
	</xsl:choose></xsl:variable>
 
  <xsl:variable name="__xiti_theme_niveau_un"><xsl:value-of select="/fiche_localisee/infos_techniques/chemin/fiche_tech[position()=2]/@titre"/></xsl:variable>

  <xsl:variable name="__xiti_subparam_page"><xsl:value-of select="normalize-space($__xiti_code_adherent)"/>::<xsl:value-of select="normalize-space($__xiti_theme_niveau_un)"/>::<xsl:value-of select="normalize-space($__xiti_type_fiche)"/>::<xsl:value-of select="/fiche_localisee/infos_techniques/chemin/fiche_tech[position()=last()]/@id"/></xsl:variable>
 
  <xsl:variable name="xiti_param_spl">s=<xsl:value-of select="$__xiti_subparam_site"/><xsl:text disable-output-escaping="yes"><![CDATA[&]]>s2=</xsl:text><xsl:value-of select="$__xiti_subparam_subsite"/><xsl:text disable-output-escaping="yes"><![CDATA[&]]>p=</xsl:text><xsl:value-of select="normalize-space($__xiti_subparam_page)"/></xsl:variable> 
 
  <xsl:if test="$stats_xiti_enabled and $stats_xiti_enabled='1'">
   <xsl:element name="script">
    <xsl:attribute name="type">text/javascript</xsl:attribute>
    <xsl:comment>
     Xt_param_tmp = '<xsl:value-of select="$xiti_param_spl" disable-output-escaping="yes"/>';
     Xt_param = encodeURI(Xt_param_tmp);
     <xsl:text disable-output-escaping="yes">
      Xt_r = document.referrer;
      Xt_h = new Date();
      Xt_i = 'alt="Marqueur Xiti" src="</xsl:text><xsl:value-of select="$xiti_url"/><xsl:text disable-output-escaping="yes">?'+Xt_param;
      Xt_i += '<![CDATA[&]]>hl='+Xt_h.getHours()+'x'+Xt_h.getMinutes()+'x'+Xt_h.getSeconds();
      if ( parseFloat( navigator.appVersion ) >=4 )
        {
      Xt_s=screen;
      Xt_i+='<![CDATA[&]]>='+Xt_s.width+'x'+Xt_s.height+'x'+Xt_s.pixelDepth+'x'+Xt_s.colorDepth;
      }
      document.write('<![CDATA[<]]>img '+Xt_i+'<![CDATA[&]]>ref='+Xt_r.replace(/[<![CDATA[<]]><![CDATA[>]]>"]/g, '').replace(/<![CDATA[&]]>/g, '$')+'" /<![CDATA[>]]>');
     </xsl:text>
    </xsl:comment>
   </xsl:element>
  </xsl:if>
 </xsl:template>
</xsl:stylesheet>