<?php
$host="mysql.cs.iastate.edu";
$port=3306;
$socket="";
$user="dbu309la05";
$password="z8ndHcbY7wj";
$dbname="db309la05";

$con = new mysqli($host, $user, $password, $dbname, $port, $socket) 
		or die ('Could not connect to the database server' . mysqli_connect_error());
		
$query = "SELECT * FROM db309la05.users3";

$result = mysqli_query($con, $query) or die("Error in selelecting" / mysqli_error($con));

$tmpArr = array();
while ($row = mysqli_fetch_assoc($result))
{
	$tmpArr[] = $row;
}

echo json_encode($tmpArr);

$con->close();
?>