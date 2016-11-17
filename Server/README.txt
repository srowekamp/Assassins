The Java servlet projects go here

Useful server functions:
	ssh into proj-309-la-05.cs.iastate.edu with your netid
		Stop service: sudo service tomcat stop
		Start service: sudo service tomcat start
		View one type of log: sudo journalctl -u tomcat.service
		Other logs located in: /var/log/tomcat/
	.WAR files go here: /var/lib/tomcat/webapps/

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

1) Create Account: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateAccount?username=admin&password=password&real_name=Name&b64_jpg=(Base64 encoded JPEG)
	parameters:
		username: user-entered username
		password: user-entered password
		real_name: user-entered real name
		b64_jpg: user-selected jpg encoded as a Base64 String. No size restrictions, but try to make them square up to 500x500 px
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in CreateAccount.java in the Assassins src
	Also within the response is a JSON object with key "account" holding all user account info in database (look at UserAccount.java)

2) Login: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Login?username=admin&password=password
	parameters:
		username: user-entered username
		password: user-entered password
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in Login.java and CreateAccount.java in the Assassins servlet src
	Also within the response is a JSON object with key "account" holding all user account info in database (look at UserAccount.java)
	
3) Create Game: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/CreateGame?gameid=test1&password=password&xcenter=0.0&ycenter=0.0&radius=1000&hostid=4&duration=1200
	parameters:
		gameid: user-entered name of the game to be displayed to clients
		password: user-entered password to join this game
		xcenter: The longitude of the center of this game (double value)
		ycenter: The latitude of the center of this game (double value)
		radius: The radius of this game's playable area in meters (int value)
		hostid: The id of the player who created this game (int value)
		duration: The time in seconds that this game will last (int value)
	Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in CreateGame.java in the Assassins src
	Also within the response is a JSON object with key "game" holding all database info for this game (look in Game.java)
	
4) Join Game: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/JoinGame?gameid=test1&id=1&password=password
    parameters:
        id: ID of the player trying to join the game. (int)
        gameid: ID of the game that you are trying to join. (String)
        password: password of the game you are trying to join. (String)
    Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in JoinGame.java in the Assassins servlet src
	Also within the response is a JSON object with key "game" holding all database info in this game (look at Game.java)

5) GetPlayers: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/GetPlayers?gameid=test1&id=4&x_location=-93.647220&y_location=42.02588
	Called by all players in the lobby while waiting for game to start. Game is started for non-host players when end_time and players_alive
		are no longer null in the response (only when host has called GameStart).
	parameters:
		gameid: The gameID of this game (String)
		id: The id of the player making the request (int)
		x_location: The longitude of the last GPS location reported from the user's device (double)
		y_location: The latitude of the last GPS location reported from the user's device (double)
	Return: JSONObject
		keys:
			result: A value indictating what happened. Result values can be found in GetPlayers.java in the Assassins src
			game: A JSONObject representing the current game. Once end_time and and players_alive have been set via GameStart by host, 
					the game has started and everyone should move to the game view and start calling UpdateGame
			num_players: A value indicating the number of players in the game (int)
			Player x: (x = 0 to num_players - 1) One JSONObject UserAccount for each player in the game
			
6) GameStart: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/GameStart?gameid=test1&start_time=235959
	Called when the host starts the game
	parameters:
		gameid: The gameID of this game (String)
		start_time: The time when the game was started (String : "hhmmss" in 24 hour time)
	Returns: JSONObject
		keys:
			result: A value indictating what happened. Result values can be found in GameStart.java in the Assassins src
			game: A JSONObject representing the current game.
			target: A UserAccount object of the host's target

7) UpdateGame: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/UpdateGame?gameid=test1&id=4&x_location=-93.647220&y_location=42.025870
	parameters:
		gameid: The gameID of this game (String)
		id: The id of the player making this request (int)
		x_location: The longitude of the last GPS location reported from the user's device (double)
		y_location: The latitude of the last GPS location reported from the user's device (double)
	Returns: JSONObject
		keys:
			result: A value indictating what happened. Result values can be found in UpdateGame.java in the Assassins src
			game: A JSONObject representing the current game.
			istop: A boolean value encoded as a String ("true" or "false") indicating whether or not this player is the top of the list. Top player calls EndGame
			target: A UserAccount object of the player's target
			
8) Kill: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Kill?gameid=test1&id=6
	Called by the player making a kill.
	parameters:
		gameid: The gameID of this game (String)
		id: The id of the player who made the kill (int)
	Returns: JSONObject
		keys:
			result: A value indictating what happened. Result values can be found in Kill.java in the Assassins src

9) EndGame: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/EndGame?gameid=test1
	Called by the player at the top of the alive_players list (istop flag in update game) when game is over. 
	parameters:
		gameid: The gameID of this game (String)
	Returns: JSONObject
		keys:
			result: A value indictating what happened. Result values can be found in UpdateGame.java in the Assassins src
			game: A JSONObject representing the current game.
			
10) LeaveGame: http://proj-309-la-05.cs.iastate.edu:8080/Assassins/LeaveGame?gameid=test1&id=1
	Called by a player who wants to leave a game
	parameters:
        id: ID of the player trying to leave the game. (int)
        gameid: ID of the game that you are trying to leave. (String)
    Returns a JSON object with key "result" with one value indicating what happened
		Result values can be found in LeaveGame.java in the Assassins servlet src

11) Output users table: http://proj-309-la-05.cs.iastate.edu/users.php
	Outputs all data in the users table from the database
	
12) Output active_games table: http://proj-309-la-05.cs.iastate.edu/active_games.php
	Outputs all data in the active_games table from the database
	
13) User Photos: http://proj-309-la-05.cs.iastate.edu:8080/userImages/x.jpg
	int x = id of user in database