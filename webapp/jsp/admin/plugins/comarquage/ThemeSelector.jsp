<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="comarquageLinkService" scope="session" class="fr.paris.lutece.plugins.comarquage.web.CoMarquageInsertServiceJspBean" />

<%= comarquageLinkService.getInsertServiceSelectorUI( request ) %>

