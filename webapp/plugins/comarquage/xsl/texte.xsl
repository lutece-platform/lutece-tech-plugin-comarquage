<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Variable d'affichage de la puce pour une liste -->
<xsl:variable name="puce">
	<img src="images/local/skin/bullet.gif" alt=""/>
	<xsl:text> </xsl:text>
</xsl:variable>

<!-- Noms des styles à utiliser (les styles correspondants -->
<!-- sont dans la feuille de style css "customize.css") -->
<xsl:variable name="tableStyle" select="'comarquage-table'"/>
<xsl:variable name="tableRowOddStyle" select="'comarquage-table-odd-row'"/>
<xsl:variable name="tableRowEvenStyle" select="'comarquage-table-even-row'"/>
<xsl:variable name="tableHeaderStyle" select="'comarquage-table-header'"/>
<xsl:variable name="tableFooterStyle" select="'comarquage-table-footer'"/>

<xsl:variable name="highlightStyle" select="'comarquage-highlight'"/>
<xsl:variable name="amountStyle" select="'comarquage-amount'"/>
<xsl:variable name="quotationStyle" select="'comarquage-quotation'"/>

<!-- ********************************************** -->
<!-- Formatage de fiche, chapitre, paragraphe, etc. -->
<!-- ********************************************** -->

<!-- *** Template "fiche" *** -->
<xsl:template match="fiche">
	<h1 id="Debut{@id}">
		<xsl:value-of select="titre"/>
	</h1>
	<xsl:apply-templates select="Chapitre"/>
	<xsl:if test="contains(@id, 'Chap')">
		<p>
			<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}">
				<xsl:text>Retour en haut de page</xsl:text>
			</a>
		</p>
	</xsl:if>
	<br />
</xsl:template>


<!-- *** Template "Texte" *** -->
<xsl:template match="Texte">
	<xsl:apply-templates/>
</xsl:template>


<!-- *** Template "Chapitre" *** -->
<xsl:template match="Chapitre">
	<xsl:apply-templates/>
</xsl:template>


<!-- *** Template "Titre" *** -->
<xsl:template match="Titre">

	<xsl:if test="name(parent::node())='Chapitre'">
		<h3>
			<xsl:copy-of select="$puce"/>
			<xsl:apply-templates select="Paragraphe" mode="NotFormatted"/>
		</h3>
	</xsl:if>

	<xsl:if test="name(parent::node())='SousChapitre'">
		<h3>
			<xsl:apply-templates select="Paragraphe" mode="NotFormatted"/>
		</h3>
	</xsl:if>

</xsl:template>


<!-- *** Template "SousChapitre" *** -->
<xsl:template match="SousChapitre">
	<xsl:apply-templates/>
</xsl:template>


<!-- *** Template "Liste" *** -->
<xsl:template match="Liste">
	<ul>
		<xsl:apply-templates select="Item"/>
	</ul>
</xsl:template>


<!-- *** Template "Item" *** -->
<xsl:template match="Item">
	<li>
		<xsl:apply-templates select="Paragraphe" mode="NotFormatted"/>
		<xsl:apply-templates select="Liste"/>
	</li>
</xsl:template>


<!-- *** Template "Tableau" *** -->
<xsl:template match="Tableau">
	<center>
		<table class="{$tableStyle}">
			<xsl:apply-templates select="Rangée"/>
		</table>
	</center>
</xsl:template>


<!-- *** Template "Rangée" *** -->
<xsl:template match="Rangée">
	<xsl:choose>

		<xsl:when test="@type='header'">
			<tr class="{$tableHeaderStyle}">
				<xsl:apply-templates select="Cellule"/>
			</tr>
		</xsl:when>

		<xsl:when test="@type='footer'">
			<tr class="{$tableFooterStyle}">
				<xsl:apply-templates select="Cellule"/>
			</tr>
		</xsl:when>

		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="position() mod 2 = 0">
					<tr class="{$tableRowEvenStyle}">
						<xsl:apply-templates select="Cellule"/>
					</tr>
				</xsl:when>
				<xsl:otherwise>
					<tr class="{$tableRowOddStyle}">
						<xsl:apply-templates select="Cellule"/>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>

	</xsl:choose>
</xsl:template>


<!-- *** Template "Cellule" *** -->
<xsl:template match="Cellule">
	<td>
		<xsl:apply-templates select="Paragraphe" mode="NotFormatted"/>
		<xsl:apply-templates select="Liste"/>
	</td>
</xsl:template>


<!-- Pour un paragraphe à formater (chapitre, sous-chapitre) -->
<xsl:template match="Paragraphe">
	<p align="justify">
		<xsl:apply-templates/>
	</p>
</xsl:template>


<!-- Pour les listes, cellules, titres, etc. -->
<xsl:template match="Paragraphe" mode="NotFormatted">
	<xsl:apply-templates/>
</xsl:template>


<!-- ************************ -->
<!-- Formatage de paragraphes -->
<!-- ************************ -->

<!-- *** Template "MiseEnEvidence" *** -->
<xsl:template match="MiseEnEvidence">
	<span class="{$highlightStyle}">
		<xsl:value-of select="."/>
	</span>
</xsl:template>


<!-- *** Template "LienExterne" *** -->
<xsl:template match="LienExterne">
	<a target="_blank" href="{@URL}">
		<xsl:value-of select="."/>
	</a>
</xsl:template>


<!-- *** Template "Citation" *** -->
<xsl:template match="Citation">
	<span class="{$quotationStyle}">
		<xsl:value-of select="."/>
	</span>
</xsl:template>


<!-- *** Template "Montant" *** -->
<xsl:template match="Montant">
	<span class="{$amountStyle}">
		<xsl:value-of select="."/>
		<xsl:text> EUR</xsl:text>
	</span>
</xsl:template>


<!-- *** Template "LienInterne" *** -->
<xsl:template match="LienInterne">
	<a class="definition" href="javascript:void(0)">
		<xsl:value-of select="."/>
		<xsl:apply-templates select="//definition[@id=current()/@LienPublication]"/>
	</a>
	<xsl:text>&#160;</xsl:text>
</xsl:template>

</xsl:stylesheet>
