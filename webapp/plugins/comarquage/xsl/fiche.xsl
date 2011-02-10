<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:lutece="luteceComarquage">

<xsl:output method="xml" indent="yes" encoding="UTF-8" version="1.0" omit-xml-declaration="yes"/>

<!-- Formatage en chapitre, paragraphe, etc. inclus dans le fichier texte.xsl -->
<xsl:include href="texte.xsl"/>
<xsl:include href="stats/phpmv.xsl"/>
<xsl:include href="stats/xiti.xsl"/>

<!-- Paramètres -->
<xsl:param name="selectedCdcCode"/>
<xsl:param name="pluginName"/>

<!-- Noms des styles à utiliser -->
<xsl:variable name="portletStyle" select="'portlet'"/>
<xsl:variable name="portletHeaderStyle" select="'portlet-header'"/>
<xsl:variable name="portletContentStyle" select="'portlet-content'"/>

<!-- Nom du répertoire courant dans l'aborescence cdc -->
<xsl:variable name="cdcBaseUrl">
	<xsl:text>http://www.servicepubliclocal.com/spl</xsl:text>
</xsl:variable>

<!-- Référence jusqu'au thème parent, utilisée pour construire les liens -->
<xsl:variable name="lien_chemin">
	<xsl:value-of select="/fiche_localisee/infos_techniques/chemin/@id"/>
	<xsl:text>/</xsl:text>
</xsl:variable>

<!-- ********************************************* -->
<!-- Template "fiche_localisee" : tableau de deux  -->
<!-- colonnes qui contient toutes les informations -->
<!-- ********************************************* -->
<xsl:template match="/fiche_localisee">

	<p style="text-align:left; margin-left:10px">
		<xsl:apply-templates select="infos_techniques/chemin"/>
	</p>
	
	<p style="text-align:right; margin-right:10px">
		<a href="JavaScript:print()">
			<img src="images/local/skin/buttons/b_print.gif" border="0" alt="Imprimer"/>
		</a>
	</p>
	
	<!-- Flash info -->
	<div id="one-zone-first">
		<div id="one-zone-first-content">
			<xsl:apply-templates select="fiche_pratique/flash"/>
		</div>
	</div>
	
	<!-- 1ère colonne (contenu principal) -->
   	<div id="two-zones-first">
		<div id="two-zones-first-content">
			<xsl:apply-templates select="fiche_pratique"/>
		</div>
	</div>
	
	<!-- 2ème colonne -->
	<div id="two-zones-second">
		<div id="two-zones-second-content">
			<xsl:call-template name="ressources"/>
			<xsl:call-template name="source"/>
		</div>
	</div>
	<xsl:call-template name="phpmv_code"/>
	<xsl:call-template name="xiti_code"/>

</xsl:template>


<!-- ********************************************************** -->
<!-- Template "fiche_pratique" : colonne de gauche, contient la -->
<!-- zone principale "vosdroits" dans une portlet, puis la zone -->
<!-- "vosquestions" et les liens chacun dans une portlet        -->
<!-- ********************************************************** -->
<xsl:template match="fiche_pratique">
	
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:value-of select="titre"/>
		</h3>
		
		<div class="{$portletContentStyle}">
			<!-- Affiche les noeuds (liens) -->
			<xsl:apply-templates select="//infos_thematique/item"/>
			
			<!-- Affiche les ancres -->
			<xsl:apply-templates select="vosdroits/noeuds_fils/item"/>
			
			<!-- Espacement entre les items et les fiches -->
			<br/>
			<xsl:if test="((count(//infos_thematique/item)!=0)
					or (count(vosdroits/noeuds_fils/item)!=0))
					and (count(vosdroits/fiches)!=0)">
				<hr/>
				<br/>
			</xsl:if>
			
			<!-- Affichage des fiches -->
			<xsl:apply-templates select="vosdroits/fiches"/>
		</div>
	</div>
	
	<xsl:apply-templates select="vosquestions"/>
	<xsl:call-template name="liens"/>
	
</xsl:template>


<!-- ************************************************************************* -->
<!-- Template "source" : permet de créer la portlet en haut à droite qui donne -->
<!-- les informations sur la source des pages et leur date de mise à jour      -->
<!-- ************************************************************************* -->
<xsl:template name="source">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Source</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<a href="http://www.service-public.fr/" title="Site service-public.fr">
				<img alt="Lien vers le site service-public.fr" border="0"
					src="images/local/skin/plugins/comarquage/logo_service_public.jpg"/>
			</a>
			<br/>
			<xsl:text>Dernière mise à jour le </xsl:text>
			<xsl:value-of select="infos_techniques/date_maj_coperia"/>
		</div>
	</div>
</xsl:template>


<!-- ************************************************************ -->
<!-- Template "chemin" : affiche la hiérarchie des thèmes jusqu'à -->
<!-- l'élément courant, pour permettre la navigation              -->
<!-- ************************************************************ -->
<xsl:template match="chemin">
	<font class="path">
		<xsl:for-each select="fiche_tech">
			<xsl:if test="position()=2 and position()!=last()">
				<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={@path}">
					<xsl:value-of select="@titre"/>
				</a>
			</xsl:if>
		</xsl:for-each>
	</font>
</xsl:template>


<!-- *************************************************** -->
<!-- Template "ressources" : les infos qui sont ajoutées -->
<!-- dans la colonne de droite sous forme de portlet     -->
<!-- *************************************************** -->
<xsl:template name="ressources">

	<!-- Complément d'information locale -->
	<xsl:call-template name="coordonnees_locales"/>
	
	<!-- Téléprocédures -->
	<xsl:apply-templates select="fiche_pratique/tele_procedures"/>
	
	<!-- Téléservices (démarches en ligne parisiennes) -->
	<xsl:call-template name="teleservices"/>
	<!-- Téléservices (démarches en ligne nationales) -->
	<xsl:apply-templates select="fiche_pratique/tele_services"/>
	
	<!-- Formulaires en ligne -->
	<xsl:call-template name="teleformulaires"/>
	
	<!-- Pour accomplir votre démarche & Pour vous informer -->
	<xsl:apply-templates select="infos_loc"/>
	
	<!-- Lettres types -->
	<xsl:apply-templates select="fiche_pratique/lettres"/>
	
</xsl:template>


<!-- ********************** -->
<!-- Template "formulaires" -->
<!-- ********************** -->
<xsl:template match="formulaires">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Formulaires</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<xsl:apply-templates select="formulaire"/>
		</div>
	</div>
</xsl:template>


<!-- ********************* -->
<!-- Template "formulaire" -->
<!-- ********************* -->
<xsl:template match="formulaire">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
	</p>
</xsl:template>


<!-- ************************** -->
<!-- Template "tele_procedures" -->
<!-- ************************** -->
<xsl:template match="tele_procedures">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Téléprocédures</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<xsl:apply-templates select="tele_procedure"/>
		</div>
	</div>
</xsl:template>


<!-- ************************* -->
<!-- Template "tele_procedure" -->
<!-- ************************* -->
<xsl:template match="tele_procedure">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
	</p>
</xsl:template>


<!-- ************************ -->
<!-- Template "tele_services" -->
<!-- ************************ -->
<xsl:template match="tele_services">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Téléservices</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<xsl:apply-templates select="tele_service"/>
		</div>
	</div>
</xsl:template>


<!-- *********************** -->
<!-- Template "tele_service" -->
<!-- *********************** -->
<xsl:template match="tele_service">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
	</p>
</xsl:template>


<!-- ********************* -->
<!-- Template "references" -->
<!-- ********************* -->
<xsl:template match="references">
	<h3>
		<xsl:text>Textes de référence :</xsl:text>
	</h3>
	<xsl:apply-templates select="reference"/>
</xsl:template>


<!-- ******************** -->
<!-- Template "reference" -->
<!-- ******************** -->
<xsl:template match="reference">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
	</p>
</xsl:template>


<!-- ********************* -->
<!-- Template "definition" -->
<!-- ********************* -->
<xsl:template match="definition">
	<span>
		<div class="{$portletStyle}">
			<h3 class="{$portletHeaderStyle}">
				<xsl:text>Définition</xsl:text>
			</h3>
			<div class="{$portletContentStyle}">
				<p id="{@id}">
					<xsl:copy-of select="$puce"/>
					<b>
						<xsl:value-of select="titre"/>
					</b>
					<xsl:apply-templates select="description"/>
				</p>
			</div>
		</div>
	</span>
</xsl:template>


<!-- ****************** -->
<!-- Template "lettres" -->
<!-- ****************** -->
<xsl:template match="lettres">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Lettres types</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<xsl:apply-templates select="lettre"/>
		</div>
	</div>
</xsl:template>


<!-- ***************** -->
<!-- Template "lettre" -->
<!-- ***************** -->
<xsl:template match="lettre">
	<p>
		<xsl:copy-of select="$puce"/>
		<b>
			<xsl:value-of select="titre"/>
		</b>
		<xsl:apply-templates select="description"/>
	</p>
</xsl:template>


<!-- *********************** -->
<!-- Template "vosquestions" -->
<!-- *********************** -->
<xsl:template match="vosquestions">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Questions / réponses</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<xsl:apply-templates select="item"/>
		</div>
	</div>
</xsl:template>


<!-- **************** -->
<!-- Template "flash" -->
<!-- **************** -->
<xsl:template match="flash">
	<div class="{$portletStyle}">
		<h3 class="{$portletHeaderStyle}">
			<xsl:text>Flash-infos</xsl:text>
		</h3>
		<div class="{$portletContentStyle}">
			<p id="{@id}">
				<xsl:copy-of select="$puce"/>
				<b>
					<xsl:value-of select="titre"/>
				</b>
				<xsl:apply-templates select="description"/>
			</p>
		</div>
	</div>
</xsl:template>


<!-- ****************************** -->
<!-- Template "coordonnees_locales" -->
<!-- ****************************** -->
<xsl:template name="coordonnees_locales">
	<xsl:if test="count(//details_competence[not(contains(../nom, 'Nouveau Service'))
			and (.!='')])&gt;0">
		<div class="{$portletStyle}">
			<h3 class="{$portletHeaderStyle}">
				<xsl:text>Complément d'information locale</xsl:text>
			</h3>
			<div class="{$portletContentStyle}">
				<xsl:apply-templates select="//details_competence"/>
			</div>
		</div>
	</xsl:if>
</xsl:template>


<!-- **************** -->
<!-- Template "liens" -->
<!-- **************** -->
<xsl:template name="liens">
	<xsl:if test="((count(liens)&gt;0) or (count(references)&gt;0))">
		<div class="{$portletStyle}">
			<h3 class="{$portletHeaderStyle}">
				<xsl:text>Pour en savoir plus</xsl:text>
			</h3>
			<div class="{$portletContentStyle}">
				<!-- Textes de référence -->
				<xsl:apply-templates select="references"/>
				
				<!-- Sites Web -->
				<xsl:if test="count(liens/web)&gt;0">
					<h3>
						<xsl:text>Sites Web :</xsl:text>
					</h3>
					<xsl:apply-templates select="liens/web/item"/>
				</xsl:if>
				
				<!-- Minitel -->
				<xsl:if test="count(liens/minitel)&gt;0">
					<h3>
						<xsl:text>Minitel :</xsl:text>
					</h3>
					<xsl:apply-templates select="liens/minitel/item"/>
				</xsl:if>
			</div>
		</div>
	</xsl:if>
</xsl:template>


<!-- ********************** -->
<!-- Template "description" -->
<!-- ********************** -->
<xsl:template match="description">
	<br/>
	<xsl:value-of select="."/>
</xsl:template>


<!-- ******************** -->
<!-- Template "lien_desc" -->
<!-- ******************** -->
<xsl:template match="lien_desc">
	<a href="{@href}" target="{@target}">
		<xsl:value-of select="."/>
	</a>
	<br/>
</xsl:template>


<!-- ************************************************ -->
<!-- Template "noeuds_fils/item" (affiche les ancres) -->
<!-- ************************************************ -->
<xsl:template match="vosdroits/noeuds_fils/item">
	<xsl:choose>
		<xsl:when test="starts-with(//fiche_pratique/@url, 'L') and titre!=''">
			<p>
				<xsl:copy-of select="$puce"/>
				
				<xsl:choose>
					<xsl:when test="count(lien)!=0">
						<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}{lien}">
							<xsl:value-of select="titre"/>
						</a>
					</xsl:when>
					
					<xsl:otherwise>
						<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}">
							<xsl:value-of select="titre"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:apply-templates select="description"/>
				<br/>
				<xsl:apply-templates select="Texte"/>
			</p>
		</xsl:when>
		
		<xsl:when test="starts-with(lien, 'L') and titre!=''">
			<p>
				<xsl:copy-of select="$puce"/>
				
				<xsl:choose>
					<xsl:when test="count(lien)!=0">
						<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}{lien}">
							<xsl:value-of select="titre"/>
						</a>
					</xsl:when>
					
					<xsl:otherwise>
						<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}">
							<xsl:value-of select="titre"/>
						</a>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:apply-templates select="description"/>
				<br/>
				<xsl:apply-templates select="Texte"/>
			</p>
		</xsl:when>
		
		<xsl:when test="count(ancre)!=0 and titre!=''">
			<p>
				<xsl:copy-of select="$puce"/>
				<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}{ancre}">
					<xsl:value-of select="titre"/>
				</a>
				<xsl:apply-templates select="description"/>
				<br/>
				<xsl:apply-templates select="Texte"/>
			</p>
		</xsl:when>
	</xsl:choose>
</xsl:template>


<!-- **************************************************** -->
<!-- Template "infos_thematique/item" (affiche les liens) -->
<!-- **************************************************** -->
<xsl:template match="infos_thematique/item">
	<xsl:if test="(starts-with(lien, 'N') or starts-with(lien, 'F')
			or starts-with(lien, $selectedCdcCode)) and titre!=''">
		<p>
			<xsl:copy-of select="$puce"/>
			
			<xsl:choose>
				<xsl:when test="count(lien)!=0">
					<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}{lien}">
						<xsl:value-of select="titre"/>
					</a>
				</xsl:when>
				
				<xsl:otherwise>
					<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}">
						<xsl:value-of select="titre"/>
					</a>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:apply-templates select="description"/>
			<br/>
			<xsl:apply-templates select="Texte"/>
		</p>
	</xsl:if>
</xsl:template>


<!-- **************************** -->
<!-- Template "vosquestions/item" -->
<!-- **************************** -->
<xsl:template match="vosquestions/item">
	<p>
		<xsl:copy-of select="$puce"/>
		<a href="jsp/site/Portal.jsp?page={$pluginName}&amp;id={$lien_chemin}{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
		<br/>
		<xsl:apply-templates select="Texte"/>
	</p>
</xsl:template>


<!-- ************************* -->
<!-- Template "liens/web/item" -->
<!-- ************************* -->
<xsl:template match="liens/web/item">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{lien}">
			<xsl:value-of select="titre"/>
		</a>
		<xsl:apply-templates select="description"/>
		<br/>
		<xsl:apply-templates select="Texte"/>
	</p>
</xsl:template>


<!-- ******************** -->
<!-- Template "infos_loc" -->
<!-- ******************** -->
<xsl:template match="infos_loc">

	<!-- Pour accomplir votre démarche -->
	<xsl:apply-templates select="services_accomplir"/>
	
	<!-- Pour vous informer -->
	<xsl:apply-templates select="services_informer"/>

</xsl:template>


<!-- ***************************** -->
<!-- Template "services_accomplir" -->
<!-- ***************************** -->
<xsl:template match="services_accomplir">

	<xsl:if test="count(service/*/nom[not(contains(., 'Nouveau Service'))])&gt;0
			and count(service[not(teleservice)])&gt;0">
		
		<!-- Count the number of service that will be shown -->
		<xsl:variable name="nShow">
			<xsl:value-of select="count(service[not(@lutece:code_cdc)
					or not(normalize-space(@lutece:code_cdc))
					or @lutece:code_cdc=$selectedCdcCode])"/>
		</xsl:variable>
		
		<!-- If we have something to show, display the content box -->
		<xsl:if test="not($nShow=0)">
			<div class="{$portletStyle}">
				<h3 class="{$portletHeaderStyle}">
					<xsl:text>Pour accomplir votre démarche</xsl:text>
				</h3>
				<div class="{$portletContentStyle}">
					<xsl:apply-templates select="service"/>
				</div>
			</div>
		</xsl:if>
	</xsl:if>

</xsl:template>


<!-- **************************** -->
<!-- Template "services_informer" -->
<!-- **************************** -->
<xsl:template match="services_informer">

	<xsl:if test="count(service/*/nom[not(contains(., 'Nouveau Service'))])&gt;0
			and count(service[not(teleservice)])&gt;0">
	
		<!-- Count the number of service that will be shown -->
		<xsl:variable name="nShow">
			<xsl:value-of select="count(service[not(@lutece:code_cdc)
					or not(normalize-space(@lutece:code_cdc))
					or @lutece:code_cdc=$selectedCdcCode])"/>
		</xsl:variable>
		
		<!-- If we have something to show, display the content box -->
		<xsl:if test="not($nShow=0)">
			<!-- "count" test is not effecient : test generated content -->
			<xsl:variable name="toDisplay"><xsl:apply-templates select="service"/></xsl:variable>
			<xsl:if test="not($toDisplay='')">
				<div class="{$portletStyle}">
					<h3 class="{$portletHeaderStyle}">
						<xsl:text>Pour vous informer</xsl:text>
					</h3>
	
					<div class="{$portletContentStyle}">
						<xsl:apply-templates select="service"/>
					</div>
				</div>
			</xsl:if>
		</xsl:if>
   </xsl:if>

</xsl:template>


<!-- ******************* -->
<!-- Templates "service" -->
<!-- ******************* -->
<!-- If no code_cdc given, display -->
<xsl:template match="service[not(@lutece:code_cdc)]">
	<xsl:apply-templates select="point_d_accueil"/>
	<xsl:apply-templates select="numero_d_appel"/>
	<xsl:apply-templates select="contact_usager"/>
	<xsl:apply-templates select="a_savoir"/>
</xsl:template>

<!-- If code_cdc empty, display -->
<xsl:template match="service[not(normalize-space(@lutece:code_cdc))]">
	<xsl:apply-templates select="point_d_accueil"/>
	<xsl:apply-templates select="numero_d_appel"/>
	<xsl:apply-templates select="contact_usager"/>
	<xsl:apply-templates select="a_savoir"/>
</xsl:template>

<!-- If code_cdc empty is the same has the one given, display -->
<xsl:template match="service[@lutece:code_cdc=$selectedCdcCode]">
	<xsl:apply-templates select="point_d_accueil"/>
	<xsl:apply-templates select="numero_d_appel"/>
	<xsl:apply-templates select="contact_usager"/>
	<xsl:apply-templates select="a_savoir"/>
</xsl:template>

<!-- Other cases : dont't display -->
<xsl:template match="service"/>


<!-- *********************** -->
<!-- Template "teleservices" -->
<!-- *********************** -->
<xsl:template name="teleservices">
	<xsl:if test="count(//teleservice)&gt;0">
		<div class="{$portletStyle}">
			<h3 class="{$portletHeaderStyle}">
				<xsl:text>Téléservices</xsl:text>
			</h3>
			<div class="{$portletContentStyle} cadrefond">
				<xsl:apply-templates select="//teleservice"/>
			</div>
		</div>
	</xsl:if>
</xsl:template>


<!-- ************************** -->
<!-- Template "teleformulaires" -->
<!-- ************************** -->
<xsl:template name="teleformulaires">
	<xsl:if test="count(//teleformulaire)+count(//formulaires/formulaire)&gt;0">
		<div class="{$portletStyle}">
			<h3 class="{$portletHeaderStyle}">
				<xsl:text>Formulaires en ligne</xsl:text>
			</h3>
			<div class="{$portletContentStyle}">
				<xsl:apply-templates select="//teleformulaire"/>
				<xsl:apply-templates select="//formulaire"/>
			</div>
		</div>
	</xsl:if>
</xsl:template>


<!-- ********************** -->
<!-- Template "teleservice" -->
<!-- ********************** -->
<xsl:template match="teleservice">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{url}">
			<xsl:value-of select="nom"/>
		</a>
		<xsl:apply-templates select="description_url"/>
		<xsl:apply-templates select="organisme/source"/>
	</p>
</xsl:template>


<!-- ************************* -->
<!-- Template "teleformulaire" -->
<!-- ************************* -->
<xsl:template match="teleformulaire">
	<p>
		<xsl:copy-of select="$puce"/>
		<a target="_blank" href="{url}">
			<xsl:value-of select="nom"/>
		</a>
		<xsl:apply-templates select="description_url"/>
		<xsl:apply-templates select="organisme/source"/>
	</p>
</xsl:template>


<!-- ************************** -->
<!-- Template "description_url" -->
<!-- ************************** -->
<xsl:template match="description_url">
	<br/>
	<xsl:call-template name="substitute_retour_ligne">
		<xsl:with-param name="chaine" select="."/>
	</xsl:call-template>
</xsl:template>


<!-- ************************* -->
<!-- Template "contact_usager" -->
<!-- ************************* -->
<xsl:template match="contact_usager">
	<ul>
		<li>
			<xsl:text>Contact</xsl:text>
			<br/>
			<xsl:apply-templates select="nom"/>
			<xsl:apply-templates select="telephone"/>
			<xsl:apply-templates select="telecopie"/>
			<xsl:apply-templates select="bureau"/>
			<xsl:apply-templates select="etage"/>
		</li>
	</ul>
</xsl:template>


<!-- ************** -->
<!-- Template "nom" -->
<!-- ************** -->
<xsl:template match="nom">
	<b>
		<xsl:call-template name="substitute_retour_ligne">
			<xsl:with-param name="chaine" select="."/>
		</xsl:call-template>
	</b>
</xsl:template>


<!-- ******************** -->
<!-- Template "telephone" -->
<!-- ******************** -->
<xsl:template match="telephone">
	<br/>
	<xsl:text>Tél. : </xsl:text>
	<xsl:call-template name="format_number">
		<xsl:with-param name="chaine" select="."/>
	</xsl:call-template>
</xsl:template>


<!-- ******************** -->
<!-- Template "telecopie" -->
<!-- ******************** -->
<xsl:template match="telecopie">
	<br/>
	<xsl:text>Fax : </xsl:text>
	<xsl:call-template name="format_number">
		<xsl:with-param name="chaine" select="."/>
	</xsl:call-template>
</xsl:template>


<!-- ***************** -->
<!-- Template "bureau" -->
<!-- ***************** -->
<xsl:template match="bureau">
	<br/>
	<xsl:text>Bureau : </xsl:text>
	<xsl:value-of select="."/>
</xsl:template>


<!-- **************** -->
<!-- Template "etage" -->
<!-- **************** -->
<xsl:template match="etage">
	<br/>
	<xsl:text>Etage : </xsl:text>
	<xsl:value-of select="."/>
</xsl:template>


<!-- ******************* -->
<!-- Template "a_savoir" -->
<!-- ******************* -->
<xsl:template match="a_savoir">
	<ul>
		<li>
			<xsl:text>A savoir</xsl:text>
			<br/>
			<xsl:value-of select="."/>
		</li>
	</ul>
</xsl:template>


<!-- ***************** -->
<!-- Template "source" -->
<!-- ***************** -->
<xsl:template match="source">
	<br/>
	<xsl:if test="url_logo_medaillon">
		<br/>
		<a target="_blank" href="{url}">
			<img src="{$cdcBaseUrl}{url_logo_medaillon}" border="0"/>
		</a>
	</xsl:if>
	<br/>
	<xsl:text>Source : </xsl:text>
	<xsl:value-of select="nom"/>
</xsl:template>


<!-- ************************** -->
<!-- Template "point_d_accueil" -->
<!-- ************************** -->
<xsl:template match="point_d_accueil">

	<xsl:variable name="tester_nom">
	      <xsl:value-of select="nom"/>
	</xsl:variable>
	
	<xsl:variable name="nouveau" select="'Nouveau Service'"/>
	
	<xsl:if test="not(contains($tester_nom, $nouveau))">
		<p>
			<xsl:copy-of select="$puce"/>
			<xsl:apply-templates select="organisme/nom"/>
			<br/>
			<xsl:apply-templates select="nom"/>
			
			<xsl:if test="normalize-space(adresse_postale)">
				<br/>
				<xsl:apply-templates select="adresse_postale"/>
			</xsl:if>
			
			<xsl:if test="normalize-space(coord_num)">
				<xsl:apply-templates select="coord_num"/>
				<br/>
			</xsl:if>
			
			<xsl:if test="count(ouverture)&gt;0">
				<br/>
	    		<b>
	    			<xsl:text>Horaires</xsl:text>
	    		</b>
	    		<br/>
	    		<xsl:apply-templates select="ouverture[not(condition_ouverture)]"/>
	    		<xsl:apply-templates select="ouverture[condition_ouverture]"/>
	    	</xsl:if>
	    	
			<xsl:if test="normalize-space(coord_num/url)">
				<xsl:apply-templates select="coord_num/url"/>
			</xsl:if>
			
			<xsl:apply-templates select="organisme/source"/>
		</p>
		<br/>
	</xsl:if>
	
</xsl:template>


<!-- ************************** -->
<!-- Template "adresse_postale" -->
<!-- ************************** -->
<xsl:template match="adresse_postale">
	<br/>
	<xsl:apply-templates select="position_voie"/>
	<xsl:apply-templates select="complement_adresse"/>
	<xsl:apply-templates select="bp"/>
	<xsl:apply-templates select="cp"/>
	<xsl:apply-templates select="ville"/>
	<xsl:apply-templates select="cedex"/>
</xsl:template>


<!-- ************************ -->
<!-- Template "position_voie" -->
<!-- ************************ -->
<xsl:template match="position_voie">
	<xsl:value-of select="."/>
	<br/>
</xsl:template>


<!-- ***************************** -->
<!-- Template "complement_adresse" -->
<!-- ***************************** -->
<xsl:template match="complement_adresse">
	<xsl:value-of select="."/>
	<br/>
</xsl:template>


<!-- ************* -->
<!-- Template "bp" -->
<!-- ************* -->
<xsl:template match="bp">
	<xsl:if test="current()!=''">
		<xsl:value-of select="."/>
		<br/>
	</xsl:if>
</xsl:template>


<!-- ************* -->
<!-- Template "cp" -->
<!-- ************* -->
<xsl:template match="cp">
	<xsl:value-of select="."/>
	<xsl:text> </xsl:text>
</xsl:template>


<!-- **************** -->
<!-- Template "ville" -->
<!-- **************** -->
<xsl:template match="ville">
	<xsl:value-of select="."/>
	<xsl:text> </xsl:text>
</xsl:template>


<!-- **************** -->
<!-- Template "cedex" -->
<!-- **************** -->
<xsl:template match="cedex">
	<xsl:value-of select="."/>
</xsl:template>


<!-- ************************* -->
<!-- Template "url_plan_acces" -->
<!-- ************************* -->
<xsl:template match="url_plan_acces">
	<br/>
	<a target="_blank" href="{.}">
		<xsl:text>Plan d'accès</xsl:text>
	</a>
</xsl:template>


<!-- ******************** -->
<!-- Template "coord_num" -->
<!-- ******************** -->
<xsl:template match="coord_num">
	<xsl:apply-templates select="telephone"/>
	<xsl:apply-templates select="telecopie"/>
	<xsl:apply-templates select="minitel_loc"/>
	<xsl:apply-templates select="email"/>
	<xsl:apply-templates select="wap"/>
</xsl:template>


<!-- ********************** -->
<!-- Template "minitel_loc" -->
<!-- ********************** -->
<xsl:template match="minitel_loc">
 	<br/>
 	<xsl:text>Minitel : </xsl:text>
 	<xsl:value-of select="."/>
</xsl:template>


<!-- ************** -->
<!-- Template "url" -->
<!-- ************** -->
<xsl:template match="url">
	<br/>
	<xsl:text>Site internet : </xsl:text>
 	<a target="_blank" href="{.}">
 		<xsl:choose>
 			<xsl:when test="not(contains(substring(., 1, 25), '-'))
 					and string-length(.)&gt;=25">
	 			<xsl:value-of select="substring(., 1, 25)"/>
	 			<xsl:text>...</xsl:text>
 			</xsl:when>
 			<xsl:otherwise>
 				<xsl:value-of select="."/>
 			</xsl:otherwise>
 		</xsl:choose>
 	</a>
</xsl:template>


<!-- **************** -->
<!-- Template "email" -->
<!-- **************** -->
<xsl:template match="email">
	<br/>
	<xsl:text>Email : </xsl:text>
 	<a href="mailto:{.}">
 		<xsl:choose>
 			<xsl:when test="not(contains(substring(., 1, 25), '-'))
 					and string-length(.)&gt;=25">
	 			<xsl:value-of select="substring(., 1, 25)"/>
	 			<xsl:text>...</xsl:text>
 			</xsl:when>
 			<xsl:otherwise>
 				<xsl:value-of select="."/>
 			</xsl:otherwise>
 		</xsl:choose>
 	</a>
</xsl:template>


<!-- ************** -->
<!-- Template "wap" -->
<!-- ************** -->
<xsl:template match="wap">
 	<br/>
 	<xsl:text>Wap : </xsl:text>
 	<xsl:value-of select="."/>
</xsl:template>


<!-- ******************** -->
<!-- Template "ouverture" -->
<!-- ******************** -->
<xsl:template match="ouverture">
	<xsl:apply-templates select="condition_ouverture"/>
	<xsl:apply-templates select="type_ouverture"/>
	<xsl:apply-templates select="plage_j"/>
</xsl:template>


<!-- ****************************** -->
<!-- Template "condition_ouverture" -->
<!-- ****************************** -->
<xsl:template match="condition_ouverture">
 	<xsl:value-of select="."/>
 	<xsl:text>, </xsl:text>
</xsl:template>


<!-- ************************* -->
<!-- Template "type_ouverture" -->
<!-- ************************* -->
<xsl:template match="type_ouverture">
 	<xsl:value-of select="."/>
 	<xsl:text>, </xsl:text>
</xsl:template>


<!-- ****************** -->
<!-- Template "plage_j" -->
<!-- ****************** -->
<xsl:template match="plage_j">

 	<xsl:choose>
	 	<xsl:when test="@j_debut_num!=@j_fin_num">
		 	<xsl:text>du </xsl:text>
		 	<xsl:value-of select="@j_debut"/>
		 	<xsl:text> au </xsl:text>
		 	<xsl:value-of select="@j_fin"/>
	 	</xsl:when>
	 	
	 	<xsl:otherwise>
	 	    <xsl:text>le </xsl:text>
	 		<xsl:value-of select="@j_debut"/>
	 	</xsl:otherwise>
 	</xsl:choose>
 	
 	<xsl:apply-templates select="plage_h"/>
 	<br/>
 	
</xsl:template>


<!-- ******************* -->
<!-- Templates "plage_h" -->
<!-- ******************* -->
<xsl:template match="plage_h[position()=1]">
 	<xsl:text> de </xsl:text>
 	<xsl:value-of select="@h_debut"/>
 	<xsl:text>h</xsl:text>
 	
 	<xsl:if test="string-length(@min_debut)=1">
 		<xsl:text>0</xsl:text>
 	</xsl:if>
 	
 	<xsl:value-of select="@min_debut"/>
	<xsl:text> à </xsl:text>
 	<xsl:value-of select="@h_fin"/>
 	<xsl:text>h</xsl:text>
 	
 	<xsl:if test="string-length(@min_fin)=1">
 		<xsl:text>0</xsl:text>
 	</xsl:if>
 	
 	<xsl:value-of select="@min_fin"/>
</xsl:template>

<xsl:template match="plage_h[position()&gt;1]">
 	<xsl:text> et de </xsl:text>
 	<xsl:value-of select="@h_debut"/>
 	<xsl:text>h</xsl:text>
 	
 	<xsl:if test="string-length(@min_debut)=1">
 		<xsl:text>0</xsl:text>
 	</xsl:if>
 	
 	<xsl:value-of select="@min_debut"/>
	<xsl:text> à </xsl:text>
 	<xsl:value-of select="@h_fin"/>
 	<xsl:text>h</xsl:text>
 	
 	<xsl:if test="string-length(@min_fin)=1">
 		<xsl:text>0</xsl:text>
 	</xsl:if>
 	
 	<xsl:value-of select="@min_fin"/>
</xsl:template>


<!-- ***************************** -->
<!-- Template "details_competence" -->
<!-- ***************************** -->
<xsl:template match="details_competence">

	<xsl:apply-templates select="info_locale"/>
	<xsl:apply-templates select="page_locale"/>
	<xsl:apply-templates select="complement_info"/>
	<xsl:apply-templates select="cout"/>
	<xsl:apply-templates select="delai"/>
	<xsl:apply-templates select="contact"/>
	
</xsl:template>


<!-- ************************** -->
<!-- Template "complement_info" -->
<!-- ************************** -->
<xsl:template match="complement_info">
 	<br/>
 	<xsl:value-of select="."/>
</xsl:template>


<!-- *************** -->
<!-- Template "cout" -->
<!-- *************** -->
<xsl:template match="cout">
 	<br/>
 	<xsl:text>Coût : </xsl:text>
 	<xsl:value-of select="."/>
</xsl:template>


<!-- **************** -->
<!-- Template "delai" -->
<!-- **************** -->
<xsl:template match="delai">
 	<br/>
 	<xsl:text>Délai : </xsl:text>
 	<xsl:value-of select="@quantite"/>
 	<xsl:text> </xsl:text>
 	<xsl:value-of select="@unite"/>
</xsl:template>


<!-- ****************** -->
<!-- Template "contact" -->
<!-- ****************** -->
<xsl:template match="contact">
 	<br/>
 	<xsl:text>Contact : </xsl:text>
 	<xsl:value-of select="."/>
</xsl:template>


<!-- ********************** -->
<!-- Template "page_locale" -->
<!-- ********************** -->
<xsl:template match="page_locale">
	<xsl:if test="not(contains(../../nom, 'Nouveau Service'))">
		<br/>
		<a target="_blank" href="{url}">
			<xsl:value-of select="titre_url"/>
		</a>
		<xsl:apply-templates select="description_url"/>
	</xsl:if>
</xsl:template>


<!-- ************************** -->
<!-- Template "description_url" -->
<!-- ************************** -->
<xsl:template match="description_url[ancestor::page_locale]">
	<xsl:text> </xsl:text>
	<xsl:value-of select="."/>
</xsl:template>


<!-- ********************** -->
<!-- Template "info_locale" -->
<!-- ********************** -->
<xsl:template match="info_locale">
	<xsl:if test="not(contains(../../nom, 'Nouveau Service'))">
		<p>
			<xsl:apply-templates select="titre_info"/>
			<xsl:apply-templates select="contenu_info"/>
		</p>
	</xsl:if>
</xsl:template>


<!-- ********************* -->
<!-- Template "titre_info" -->
<!-- ********************* -->
<xsl:template match="titre_info">
	<xsl:if test="current() and normalize-space(current())">
	 	<xsl:copy-of select="$puce"/>
	 	<b>
		 	<xsl:call-template name="substitute_retour_ligne">
				<xsl:with-param name="chaine" select="."/>
			</xsl:call-template>
		</b>
	</xsl:if>
</xsl:template>


<!-- *********************** -->
<!-- Template "contenu_info" -->
<!-- *********************** -->
<xsl:template match="contenu_info">
 	<p>
	 	<xsl:call-template name="substitute_retour_ligne">
			<xsl:with-param name="chaine" select="."/>
		</xsl:call-template>
	</p>
</xsl:template>


<!-- ************************* -->
<!-- Template "numero_d_appel" -->
<!-- ************************* -->
<xsl:template match="numero_d_appel">
	<xsl:apply-templates/>
</xsl:template>

<!-- **************************** -->
<!-- Template "url_logo_banniere" -->
<!-- **************************** -->
<xsl:template match="url_logo_banniere[ancestor::numero_d_appel]"/>


<!-- ***************************** -->
<!-- Template "url_logo_medaillon" -->
<!-- ***************************** -->
<xsl:template match="url_logo_medaillon[ancestor::numero_d_appel]"/>


<!-- ***************************** -->
<!-- Template "description_numero" -->
<!-- ***************************** -->
<xsl:template match="description_numero">
	<br/>
	<xsl:value-of select="."/>
</xsl:template>


<!-- ********************** -->
<!-- Template "cout_numero" -->
<!-- ********************** -->
<xsl:template match="cout_numero">
	<xsl:apply-templates select="cout"/>
	<xsl:apply-templates select="type_tarification"/>
</xsl:template>


<!-- **************************** -->
<!-- Template "type_tarification" -->
<!-- **************************** -->
<xsl:template match="type_tarification">
	<br/>
	<xsl:text>Tarification : </xsl:text>
	<xsl:value-of select="."/>
</xsl:template>


<!-- *************************** -->
<!-- Template "infos_techniques" -->
<!-- *************************** -->
<xsl:template match="/fiche_localisee/infos_techniques"/>


<!-- ********************** -->
<!-- Template "infos_reloc" -->
<!-- ********************** -->
<xsl:template match="/fiche_localisee/infos_reloc"/>


<!-- ********************************************** -->
<!-- Template "substitute_retour_ligne" : permet de -->
<!-- remplacer les /n d'une string par des <br/>    -->
<!-- ********************************************** -->
<xsl:template name="substitute_retour_ligne">

	<xsl:param name="chaine"/>
	<xsl:param name="changer" select="'\n'"/>
	
	<xsl:choose>
		<xsl:when test="contains($chaine, $changer )">
			<xsl:variable name="avant" select="substring-before($chaine, $changer)"/>
			<xsl:variable name="apres" select="substring-after($chaine, $changer)"/>
			<xsl:value-of select="$avant"/>
			<br/>
			<xsl:choose>
				<xsl:when test="contains($apres, $changer)">
					<xsl:call-template name="substitute_retour_ligne">
						<xsl:with-param name="chaine" select="$apres"/>
						<xsl:with-param name="changer" select="$changer"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$apres"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$chaine"/>
		</xsl:otherwise>
	</xsl:choose>
	
</xsl:template>


<!-- ******************************************* -->
<!-- Template "format_number" : permet d'ajouter -->
<!-- des espaces dans un numéro de téléphone     -->
<!-- ******************************************* -->
<xsl:template name="format_number">

	<xsl:param name="chaine"/>
	<xsl:param name="ajouter" select="' '"/>
	
	<xsl:choose>
		<xsl:when test="string-length($chaine)&gt;2">
			<xsl:variable name="avant" select="substring($chaine, 1, 2)"/>
			<xsl:variable name="apres" select="substring($chaine, 3)"/>
			<xsl:value-of select="$avant"/>
			<xsl:value-of select="$ajouter"/>
			<xsl:choose>
				<xsl:when test="string-length($apres)&gt;2">
					<xsl:call-template name="format_number">
						<xsl:with-param name="chaine" select="$apres"/>
						<xsl:with-param name="ajouter" select="$ajouter"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="string-length($apres)=2">
					<xsl:value-of select="$apres"/>
				</xsl:when>
				<xsl:when test="string-length($apres)=1">
					<xsl:value-of select="$apres"/>
				</xsl:when>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$chaine"/>
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>

</xsl:stylesheet>
