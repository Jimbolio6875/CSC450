<%@ include file="/WEB-INF/layouts/include.jsp"%>
<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registration Page</title>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
    <!-- Bootsrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/startupStyles.css'/>">
</head>

<body>
    <div class="container-fluid">
        <div class="row">

            <div class="col-md-6">
                <img src="<c:url value='/resources/Gifs/people.gif'/>" class="img-fluid" alt="Image">
            </div>

            <div class="col-md-6 d-flex align-items-center justify-content-center">
                <form id="register-form">
                    <div class="top">
                        <a href="/"><img src="<c:url value='/resources/Socials/logo.png'/>" class="img-fluid" id="loginlogo"></a>
                        <header>Register</header>
                    </div>
                    <div class="two-forms">
                        <div class="mb-3">
                            <input type="text" class="form-control" placeholder="First Name">
                        </div>
                        <div class="mb-3">
                            <input type="text" class="form-control" placeholder="Last Name">
                        </div>
                    </div>
                    <div class="mb-3">
                        <!--<label for="exampleInputEmail1" class="form-label">Email Address</label>-->
                        <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                            placeholder="Email Address">
                        <!--<div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>-->
                    </div>
                    <div class="mb-3">
                        <!--<label for="exampleInputPassword1" class="form-label">Password</label>-->
                        <input type="password" class="form-control" id="exampleInputPassword1" placeholder="Password">
                    </div>
                    <div class="two-col">
                        <div class="mb-3 form-check">
                            <input type="checkbox" id="login-check">
                            <label class="login-check" for="exampleCheck1">Remember Me</label>
                        </div>
                        <div class="two">
                            <label><a href="#">Terms & Conditions</a></label>
                        </div>
                    </div>
                    <button type="submit" onclick="redirectToSocials(event)" class="submit">Register</button>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
</body>

</html>