<%@ include file="/WEB-INF/layouts/include.jsp"%>

<!doctype HTML>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>Login Page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<!-- Bootstrap -->
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<body>
	<h1>Login Page</h1>

	<form id="login-form">
	  <div class="form-group">
	    <label for="usernameInput">Username</label>
	    <input type="text" class="form-control" id="usernameInput" name="username" placeholder="Username">
	  </div>
	  <div class="form-group">
	    <label for="passwordInput">Password</label>
	    <input type="password" class="form-control" id="passwordInput" name="password" placeholder="Password">
	  </div>
  <button class="btn btn-primary" id="submitLoginBtn">Submit</button>
</form>

<!-- TODO: move script to separate js file -->
<script>
	
	const loginForm = document.getElementById("login-form");
	const loginBtn = document.getElementById("submitLoginBtn");
	const usernameInput = document.getElementById("usernameInput");
	const passwordInput = document.getElementById("passwordInput");
	
	loginBtn.addEventListener("click", function(e){
		try {
		      e.preventDefault(); // Prevent Form From Submitting "normally" (Internet Explorer)
		      let data = {};
		      data.username = usernameInput.value;
		      data.password = passwordInput.value;
		      
		      //TODO: we might want to hash this? Or encrypt this? idk. Sending pw in plaintext is bad

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
		      }).catch(function(error) {
		        
		      });
		    } catch (err) {
		      console.log("error");
		    }
		  
	});
	
	
</script>

<!-- Move to template -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>


</html>