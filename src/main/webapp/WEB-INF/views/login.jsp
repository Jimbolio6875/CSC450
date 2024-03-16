<%@ include file="/WEB-INF/layouts/include.jsp"%>
<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login Page</title>
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
                <img src="<c:url value='/resources/Gifs/interactions.gif'/>"class="img-fluid" alt="Image">
            </div>

            <div class="col-md-6 d-flex align-items-center justify-content-center">
            	<p id="messageBox"></p>
                <form id="login-form">
                    <div class="top">
                        <a href="/"><img src="<c:url value='/resources/Socials/logo.png'/>" class="img-fluid" id="loginlogo"></a>
                        <header>Sign in</header>
                    </div>
                    <div class="mb-3">
                        <!--<label for="exampleInputEmail1" class="form-label">Email Address</label>-->
                        <input type="text" class="form-control" id="usernameInput" aria-describedby="emailHelp"
                            placeholder="Username">
                        <!--<div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>-->
                    </div>
                    <div class="mb-3">
                        <!--<label for="exampleInputPassword1" class="form-label">Password</label>-->
                        <input type="password" class="form-control" id="passwordInput" placeholder="Password">
                    </div>
                    <div class="two-col" style="width: 320px;">
                        <div class="mb-3 form-check">
                            <input type="checkbox" id="login-check">
                            <label class="login-check" for="exampleCheck1">Remember Me</label>
                        </div>
                        <div class="two">
                            <label><a href="#">Forgot password?</a></label>
                        </div>
                    </div>
                    <button type="submit" class="submit" id="submitLoginBtn">Sign In</button>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
            <!-- TODO: move script to separate js file -->
<script>
	
	const loginForm = document.getElementById("login-form");
	const loginBtn = document.getElementById("submitLoginBtn");
	const usernameInput = document.getElementById("usernameInput");
	const passwordInput = document.getElementById("passwordInput");
	const messageBox = document.getElementById("messageBox");
	
	loginBtn.addEventListener("click", function(e){
		try {
		      e.preventDefault(); // Prevent Form From Submitting "normally" (Internet Explorer)
		      let data = {};
		      data.username = usernameInput.value;
		      data.password = passwordInput.value;

		      // Make AJAX call to the server along with a message
		      fetch("<c:url value='/login' />", {
		        method: "POST",
			body: JSON.stringify(data),
		        headers: {
		          "Content-Type": "application/json"
		        }
		      }).then(function(response) { 
		        if (response.ok) {
		          let res = response.json();
		          return res;
		        }
		      }).then(function(response) { 
		    	  console.log(response);
		    	  console.log(response.messageType);
		        if (response.messageType === "danger"){
		        	messageBox.innerText = response.message;
		        }
		        else{
		        	window.location.replace("<c:url value='/landing' />");
		        }
			  }).catch(function(error) {
		        
		      });
		    } catch (err) {
		      console.log("error");
		    }
		  
	});
	
	
</script>
</body>

</html>