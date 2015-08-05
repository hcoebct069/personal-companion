<?php

require_once 'database.php';

//no any user input, all queries are assumed "safe" and not prepared

$query_get_factors = "DESCRIBE statistics";
$result_get_factors = mysqli_query($con, $query_get_factors) or die("Error: Cannot DESCRIBE statistics: ". mysqli_error($con));
//$factors_arr = Array([]);

$query_total_population = "SELECT COUNT(id) as tp FROM statistics;";
$result_total_population = mysqli_query($con, $query_total_population) or die("Error: Cannot COUNT population: ". mysqli_error('$con'));
$total_population = mysqli_fetch_assoc($result_total_population)['tp'];

while($row = mysqli_fetch_assoc($result_get_factors)){
	$factor = $row['Field'];
	if($factor === 'id') continue;
	$query_factor_distinct = "SELECT DISTINCT $factor FROM statistics ORDER BY $factor ASC;";
	$result_factor_distinct = mysqli_query($con, $query_factor_distinct) or die("Error: Cannot get DISTINCT $factor: ". mysqli_error($con));
	while($row2 = mysqli_fetch_assoc($result_factor_distinct)) {
		$dynamic_query =  "SELECT COUNT(id) as cp FROM statistics WHERE $factor='".$row2[$factor]."';";
		$result_dynamic_query = mysqli_query($con, $dynamic_query) or die("Error in calculating probability: ". mysqli_error($con));
		$factors_arr[$factor][$row2[$factor]] = mysqli_fetch_assoc($result_dynamic_query)['cp']/$total_population;
	}
}

var_dump($factors_arr);
//echo "<br>";


/*$query_get_model = "SELECT * FROM model;";
$result_get_model = mysqli_query($con, $query_get_model) or die("Error executing SELECT Query on `model` table: ". mysqli_error($con));

while($row1 = mysqli_fetch_assoc($result_get_model)){
	foreach($factors_arr[trim($row1['cause'])] as $cause){
		foreach($factors_arr[trim($row1['effect'])] as $evidence){
			$numerator = "SELECT COUNT(id) as numerator FROM statistics WHERE ". $row1['effect']. " = '$evidence' and ". $row1['cause']."= '$cause';";
			$denominator = "SELECT COUNT(id) as denominator FROM statistics WHERE ". $row1['cause'] . " = '$cause'";
			$result_numerator = mysqli_query($con, $numerator);
			$result_denominator = mysqli_query($con, $denominator);
			if(! $result_denominator || ! $result_numerator){
				die("Error in COUNT queries: ". mysqli_error($con));
			}
			$row_num = mysqli_fetch_assoc($result_numerator);
			$row_denum = mysqli_fetch_assoc($result_denominator);
			$numerator = $row_num['numerator'];
			$denominator = $row_denum['denominator'];
			$prob = $numerator/$denominator;
			echo "P(".$row1['effect']."=".$evidence." | ".$row1['cause']." = $cause) = ".$prob . "<br>";
			$query_insert_prob = "INSERT INTO probabilities VALUES(NULL, ". $row1['id']. ", '$cause', '$evidence', $prob);";
			mysqli_query($con, $query_insert_prob) or die("Error INSERTING conditional Probabilities: ". mysqli_error($con));
			//Parse Float and divide and store in (separate table)
		}
	}
}
*/
//Start Calculating Probabilities.
//Maybe do it in the above loop.