<?php
	require_once 'database.php';

	if(!isset($_GET['factor'])){
		die("No babe! That's not how you do it!");
	}

	$query = "SELECT value FROM probabilities_individual WHERE factor='".$_GET['factor']."';";
	$result = mysqli_query($con, $query) or die("Error: ". mysqli_error($con));
	while($row = mysqli_fetch_assoc($result)){
		echo "<option value='".$row['value']."'>".$row['value']."</option>\n";
	}

 ?>