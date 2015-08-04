<?php

define('USER', 'root');
define('PASS', 'toor');
define('HOST', 'localhost');
define('DB', 'personality_model');
$con = mysqli_connect(HOST, USER, PASS, DB);
if(!$con){
	die("Error while connecting to database: ". mysqli_error($con));
}
