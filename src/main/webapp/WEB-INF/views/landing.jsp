<%@ include file="/WEB-INF/layouts/include.jsp"%>

<!doctype HTML>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Landing Page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body>
	<h1 id="customerH1">Welcome ${sessionScope.firstName }</h1>
	<p>Landing page</p>
	<a id="signOut" href="<c:url value='/signOut'/>">Sign Out</a>
</body>
</html>

<script>
	
</script>