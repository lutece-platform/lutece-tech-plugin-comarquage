<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:param name="pluginName"></xsl:param>

<xsl:variable name="linkId" >
	jsp/site/Portal.jsp?page=<xsl:value-of select="$pluginName" />&amp;id
</xsl:variable>


<xsl:variable name="puce" >
	<img src="images/local/skin/bullet.gif" align="middle" /><xsl:text> </xsl:text>
</xsl:variable>


<xsl:template match="/">


<div align="center">
	<h3>Grand Public</h3>
</div>


<div id="three-zones-first-and-second">

 	<div id="three-zones-first">
	<div id="three-zones-first-content">


				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19803">Argent</a><br />(Epargne / Assurance / Impôts...)
				</p>
				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19805">Famille</a><br />(Allocations familiales / Naissance / Mariage / PACS / Scolarité...)
				</p>
				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19807">Justice</a><br />(Casier judiciaire / Médiation / Porter plainte / Procès civil...)
				</p>
				
				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19809">Loisirs</a><br />(Aide pour les vacances...)
				</p>
				
				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19811">Social - Santé</a><br />(Personnes âgées / Personnes handicapées / RSA...)
				</p>
				
				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N20">Vie associative</a><br />(Création / Dissolution / Bénévolat...)
				</p>
				
	</div>

	</div>
	<div id="three-zones-second">
	<div id="three-zones-second-content">

				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19804">Etrangers en France - Europe</a><br />(Français à l'étranger / Titres de séjour / Nationalité française...)
				</p>

				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19806">Formation - Travail</a><br />(Etudes supérieures / CDD / Accès à la fonction publique / Chômage...)
				</p>

				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19808">Logement</a><br />(Construction / Allocations logement / Location...)
				</p>

				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19810">Papiers - Citoyenneté</a><br />(Etat-civil / Passeport / Elections...)
				</p>

				<p>
					<xsl:copy-of select="$puce" />
					<a href="{$linkId}=N0/N19812">Transports</a><br />(Carte grise / Permis de conduire / Contrôle technique...)
				</p>

	</div>
	</div>
</div>

</xsl:template>



</xsl:stylesheet>