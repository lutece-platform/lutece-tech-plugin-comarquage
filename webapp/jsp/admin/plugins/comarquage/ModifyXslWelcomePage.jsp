<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="comarquage" scope="session" class="fr.paris.lutece.plugins.comarquage.web.CoMarquageJspBean" />

<% comarquage.init( request, comarquage.RIGHT_COMARQUAGE_MANAGEMENT); %>
<%= comarquage.getModifyXslWelcomePage( request )%>

<%@ include file="../../AdminFooter.jsp" %>