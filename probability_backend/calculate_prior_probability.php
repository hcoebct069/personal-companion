<?php

require_once 'database.php';

//no any user input, all queries are assumed "safe" and not prepared

$query_get_factors = "DESCRIBE statistics";
$result_get_factors = mysqli_query($con, $query_get_factors) or die("Error: Cannot DESCRIBE statistics: ". mysqli_error($con));
//$factors_arr = Array([]);

while($row = mysqli_fetch_assoc($result_get_factors)){
	$factor = $row['Field'];
	if($factor === 'id') continue;
	$query_factor_distinct = "SELECT DISTINCT $factor FROM statistics ORDER BY $factor ASC;";
	$result_factor_distinct = mysqli_query($con, $query_factor_distinct) or die("Error: Cannot get DISTINCT $factor: ". mysqli_error($con));
	while($row2 = mysqli_fetch_assoc($result_factor_distinct)) {
		//$dynamic_query =  "SELECT COUNT($factor) FROM statistics WHERE $factor='".$row2[$factor]."'"
		$factors_arr[$factor][] = $row2[$factor];
	}
}

//var_dump($factors_arr);
//echo "<br>";


$query_get_model = "SELECT * FROM model;";
$result_get_model = mysqli_query($con, $query_get_model) or die("Error executing SELECT Query on `model` table: ". mysqli_error($con));

mysqli_query($con, "TRUNCATE TABLE probabilities;");
mysqli_query($con, "ALTER TABLE probabilities AUTO_INCREMENT 1;");

while($row1 = mysqli_fetch_assoc($result_get_model)){
	foreach($factors_arr[trim($row1['cause'])] as $cause){
		foreach($factors_arr[trim($row1['effect'])] as $evidence){
			$numerator = "SELECT COUNT(id) as numerator FROM statistics WHERE ". $row1['effect']. " = '$evidence' and ". $row1['cause']."= '$cause';";
			$denominator = "SELECT COUNT(id) as denominator FROM statistics WHERE ". $row1['cause'] . " = '$cause'";
			$denominator_2 = "SELECT COUNT(id) as denominator_2 FROM statistics WHERE ". $row1['effect']. " = '$evidence'";
			$result_numerator = mysqli_query($con, $numerator);
			$result_denominator = mysqli_query($con, $denominator);
			$result_denominator_2 = mysqli_query($con, $denominator_2);
			if(! $result_denominator || ! $result_numerator || !$result_denominator_2){
				die("Error in COUNT queries: ". mysqli_error($con));
			}
			$row_num = mysqli_fetch_assoc($result_numerator);
			$row_denum = mysqli_fetch_assoc($result_denominator);
			$row_denum_2 = mysqli_fetch_assoc($result_denominator_2);
			$numerator = $row_num['numerator'];
			$denominator = $row_denum['denominator'];
			$denominator_2 = $row_denum_2['denominator_2'];
			$prob = $numerator/$denominator;
			$prob_2 = $numerator/$denominator_2;
			echo "P(".$row1['effect']."=".$evidence." | ".$row1['cause']." = $cause) = ".$prob . "<br>";
			echo "P(".$row1['cause']."=".$cause." | ".$row1['effect']." = $evidence) = ".$prob_2 . "<br><hr>";
			$query_insert_prob = "INSERT INTO probabilities VALUES(NULL, ". $row1['id']. ", '$cause', '$evidence', $prob, $prob_2);";
			mysqli_query($con, $query_insert_prob) or die("Error INSERTING conditional Probabilities: ". mysqli_error($con));
			//Parse Float and divide and store in (separate table)
		}
	}
}

//Start Calculating Probabilities.
//Maybe do it in the above loop.