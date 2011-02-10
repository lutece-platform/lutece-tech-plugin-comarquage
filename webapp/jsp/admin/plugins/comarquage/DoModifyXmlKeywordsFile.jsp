<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="comarquage" scope="session" class="fr.paris.lutece.plugins.comarquage.web.CoMarquageJspBean" />

<%
  		 comarquage.init( request, comarquage.RIGHT_COMARQUAGE_MANAGEMENT);	
         response.sendRedirect( comarquage.doModifyXmlKeywordsFile( request ) );
         
%>


