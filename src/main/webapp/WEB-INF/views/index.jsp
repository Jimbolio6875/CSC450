<%@ include file="/WEB-INF/layouts/include.jsp"%>

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Landing Page</title>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <!-- Bootsrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/styles.css'/>">
</head>

<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg ">
        <div class="container">
            <a class="navbar-brand" href="/">
                <!-- Brand here -->
                <img src="<c:url value='/resources/Socials/logo.png'/>" alt="#" id="logo">
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" href="index.html">Home</a>
                    </li>
                    <!--<li class="nav-item">
                        <a class="nav-link" href="#">Features</a>
                    </li>-->
                    <li class="nav-item">
                        <a class="nav-link me-5" href="socials.html">Socials</a>
                    </li>
                </ul>
                <a href="login" class="btn btn-outline-secondary shadow-sm d-sm d-block me-2">Login</a>
                <!--<a href="register.html" class="btn btn-outline-secondary shadow-sm d-sm d-block">Sign Up</a>-->
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="row">
            <div class="col-lg-6 col-sm-12 text-center">
                <div class="text mt-4 mx-4">
                    Connect to all your social media platforms and take control all in one place!
                </div>
                <div class="buttons">
                    <a href="register" class="btn btn-primary">Sign Up</a>
                </div>
            </div>
            <div class="col-lg-6 mt-3">
                <img src="<c:url value='/resources/Gifs/phone.gif'/>" alt="#" id="socials" class="w-100">
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>

</html>
