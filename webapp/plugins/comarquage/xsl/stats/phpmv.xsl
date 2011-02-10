<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- ******************************************* -->
<!-- Statistiques phpMyVisites -->
<!-- ******************************************* -->

<xsl:template name="phpmv_code">
		<xsl:variable name="phpmv_root" select="/fiche_localisee/infos_techniques/abonne/variable[@id='phpmv.url']/text()"/>
		<xsl:variable name="phpmv_url"><xsl:value-of select="$phpmv_root"/>/phpmyvisites.php</xsl:variable>
		<xsl:variable name="phpmv_js_url"><xsl:value-of select="$phpmv_root"/>/phpmyvisites.js</xsl:variable>
		<xsl:variable name="phpmv_ref_organisme">

				<xsl:value-of select="/fiche_localisee/infos_techniques/abonne/organisme/@ref_organisme_xiti"/>
		</xsl:variable>
		<xsl:variable name="stats_enabled" select="number(/fiche_localisee/infos_techniques/abonne/variable[@id='phpmv.enable']/text())"/>
		<xsl:variable name="phpmv_id_spl" select="number(/fiche_localisee/infos_techniques/abonne/variable[@id='phpmv.idSPL']/text())"/>

		<xsl:variable name="phpmv_code_adherent">
				<xsl:value-of select="/fiche_localisee/infos_techniques/abonne/@id"/>
		</xsl:variable>
		<xsl:variable name="phpmv_groupe_adherent">
				<xsl:value-of select="/fiche_localisee/infos_techniques/site_niveau2/@chapitrage"/>

		</xsl:variable>
		<xsl:variable name="phpmv_type_organisme">
				<xsl:value-of select="/fiche_localisee/infos_techniques/abonne/type_organisme"/>
		</xsl:variable>
		<xsl:variable name="phpmv_groupe_national">
	  <xsl:value-of select="concat($phpmv_type_organisme,'/',$phpmv_groupe_adherent)"/>
		</xsl:variable>

		<xsl:if test="$stats_enabled and $stats_enabled='1' and $phpmv_groupe_adherent!=''">

				<!--  Marqueur niveau national -->
				<xsl:element name="script">
						<xsl:attribute name="type">text/javascript</xsl:attribute>
						<xsl:comment>
								var a_vars = Array();
								var pagename="<xsl:value-of select="$phpmv_groupe_national"/>";
								var phpmyvisitesSite = "<xsl:value-of select="$phpmv_id_spl"/>";
								var phpmyvisitesURL = "<xsl:value-of select="$phpmv_url"/>";
						</xsl:comment>
				</xsl:element>

				<xsl:element name="script">
						<xsl:attribute name="type">text/javascript</xsl:attribute>
						<xsl:attribute name="src"><xsl:value-of select="$phpmv_js_url"/></xsl:attribute>
						<xsl:text> </xsl:text>
				</xsl:element>
				<xsl:element name="noscript">
						<xsl:element name="div">
								<xsl:element name="img">

										<xsl:attribute name="src"><xsl:value-of select="$phpmv_url"/></xsl:attribute>
										<xsl:attribute name="alt">Statistiques</xsl:attribute>
										<xsl:attribute name="style">border:0</xsl:attribute>
								</xsl:element>
						</xsl:element>
				</xsl:element>

				<!--  Marqueur niveau adherent si on conna t l'id phpmv de son site -->

				<xsl:if test="$phpmv_ref_organisme!=''">
						<xsl:element name="script">
								<xsl:attribute name="type">text/javascript</xsl:attribute>
								<xsl:comment>
										var a_vars = Array();
										var pagename="<xsl:value-of select="$phpmv_groupe_adherent"/>";
										var phpmyvisitesSite = "<xsl:value-of select="$phpmv_ref_organisme"/>";
										var phpmyvisitesURL = "<xsl:value-of select="$phpmv_url"/>";
								</xsl:comment>
						</xsl:element>

						<xsl:element name="script">
								<xsl:attribute name="type">text/javascript</xsl:attribute>
								<xsl:attribute name="src"><xsl:value-of select="$phpmv_js_url"/></xsl:attribute>
								<xsl:text> </xsl:text>
						</xsl:element>
						<xsl:element name="noscript">
								<xsl:element name="div">
										<xsl:element name="img">

												<xsl:attribute name="src"><xsl:value-of select="$phpmv_url"/></xsl:attribute>
												<xsl:attribute name="alt">Statistiques</xsl:attribute>
												<xsl:attribute name="style">border:0</xsl:attribute>
										</xsl:element>
								</xsl:element>
						</xsl:element>
				</xsl:if>

		</xsl:if>
</xsl:template>
</xsl:stylesheet>