The Java servlet projects go here

Current functions of the server: 

1) Login: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/LoginBasic?username=admin&password=password
	(replace admin/password with user input)
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in Login.java and CreateAccount.java in the Assassins src
		
2) Create Account: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateAccountBasic?username=admin&password=password
	(replace admin/password with user input)
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in CreateAccount.java in the Assassins src
		
3) Output Users table: http://proj-309-la-05.cs.iastate.edu/connect.php
	Outputs all data in the users table from the database