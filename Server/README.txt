The Java servlet projects go here

How to work on server files :

1) Download "Eclipse IDE for Java EE Developers." I'm using Eclipse Neon 

2) Download & Install Apache Tomcat 7.0 http://tomcat.apache.org/download-70.cgi

3) Download & Install latest Java JRE if you don't have one

4) Open the "Assasins" project in eclipse

5) Import the installed JRE into eclipse if needed

6) Import the Tomcat runtime into eclipse
	Window > Preferences > Server > Runtime Environments > 
		Add > Apache > Apache Tomcat 7.0 > Next > Browse to the intallation directory 
			Then select the imported JRE and click finish
			
6a) You may also have to add the JRE and Apache Tomcat to the project build path
	Right click on the Assassins Project > Build Path > Configure Build Path
		Libraries Tab > Add Library > Then add both the JRE System Library and Server Runtime

Current functions of the server (needs VPN): 

1) Login: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Login
	parameters:
		username: user-entered username
		password: user-entered password
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in Login.java and CreateAccount.java in the Assassins servlet src
	Also within the response is a JSON object with key "account" holding all user account info in database (look at UserAccount.java)
		
2) Create Account: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateAccount
	parameters:
		username: user-entered username
		password: user-entered password
		real_name: user-entered real name
		b64_jpg: user-selected jpg encoded as a Base64 String. No size restrictions, but try to make them square up to 500x500 px
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in CreateAccount.java in the Assassins src
	Also within the response is a JSON object with key "account" holding all user account info in database (look at UserAccount.java)
		
3) Output Users table: http://proj-309-la-05.cs.iastate.edu/connect.php
	Outputs all data in the users table from the database
	Just for testing. Don't use this in code. 
	
4) User Photos: http://proj-309-la-05.cs.iastate.edu:8080/userImages/x.jpg
	int x = id of user in database