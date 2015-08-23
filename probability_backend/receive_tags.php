<?php
	require_once 'database.php';
	$factor = $_GET['factors'];
	$value = $_GET['values'];
	$tag = $_GET['tags'];
	$query = "INSERT INTO factors_to_tag values(NULL, '$factor', '$value', '$tag')";
	$result = mysqli_query($con,$query);
	if(!$result){
		header("Location: add_tags_front.php?error=1");
	}
	header("Location: add_tags_front.php?success=1");
?>