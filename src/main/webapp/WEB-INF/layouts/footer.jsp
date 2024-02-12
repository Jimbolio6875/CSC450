<%@ include file="/WEB-INF/layouts/include.jsp"%>
<footer class="border-top footer text-muted">
  <div class="container mx-auto">
    <%
      java.util.Calendar cal = new java.util.GregorianCalendar();
      cal.setTime(new java.util.Date());
      Integer year = cal.get(java.util.Calendar.YEAR);
    %>
    &copy; <%=year %> - Example Page - <a href="<c:url value='/privacy' />">Privacy</a>
  </div>
</footer>
