<%@ include file="/WEB-INF/layouts/include.jsp"%>
<nav class="navbar navbar-expand-sm navbar-toggleable-sm navbar-light bg-white border-bottom box-shadow mb-3">
    <div class="container">
        <a class="navbar-brand" href="#">
            <img alt="MSU Logo" src="https://www.missouristate.edu/favicon.ico" />
        </a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target=".navbar-collapse" 
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="navbar-collapse collapse d-sm-inline-flex justify-content-between">
            <ul class="navbar-nav flex-grow-1">
                <li class="nav-item active">
                                  <a class="nav-link text-dark" href="<c:url value='/'/>">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-dark" href="<c:url value='/privacy'/>">Privacy</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
