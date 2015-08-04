<?php

require_once 'database.php';

if(!(isset($_POST['domain']) && isset($_POST['range']))){
	die("Oops! No can do, babe.");
}

$domain = $_POST['domain'];
$range = $_POST['range'];

$arr_domain = explode("\n", $domain);
$arr_range = explode("\n", $range);

foreach($arr_domain as $value){
	foreach($arr_range as $value2){
		$query = "INSERT INTO model VALUES(NULL, '$value', '$value2', 0);";
		mysqli_query($con, $query) or die("Cannot Query: ". mysqli_error($con));
	}
}
echo "<a href='interface_insert_connections.php'>Done! Click here and go back!</a>";
