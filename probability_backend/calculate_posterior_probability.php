<?php

// GET or POST
//	set factor's probability
//	array -> factor: array(value: probability)
//	foreach factor
//	check if this is cause, if it is
//		set the cause's probability different from the lookup table
//	end this foreach and start new one
//	foreach factor
//	check if this is evidence (effect) or not,
//		if yes, calculate P(it's all causes | effect)
//		for each of its causes which are also effect
//			do the same thing (recursive)
//	end foreach

// input :
// [
// 	{
// 		"name" : "factor_name",
// 		"value": "value",
// 		"probability":probability
// 	},
//
//  	{
// 		"name" : "factor_name",
// 		"value": "value",
// 		"probability":probability
// 	}
//
// ]

require_once 'database.php';

$query_get_factors = "DESCRIBE statistics;";
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


$input = $_POST['json_input'];
$factors = json_decode($input);


//get all individual probabilities
$query ="SELECT * FROM probabilities_individual;";
$result = mysqli_query($con, $query) or die("Error: Cannot select from individual probability: ". mysqli_error($con));
while($row = mysqli_fetch_assoc($result)){
	$probability[$row['factor']] = $row['probability'];
}

foreach($factors as $factor){
		//update the individual probability
		$probability[$factor['name']] = $factor['probability'];
		//continue; // :D
}

//calculate P(Cause | Evidence)for all

foreach($factors as $factor){
	$query = "SELECT * FROM model where effect='".$factor['name']."';";
	mysqli_query($con,$query) or die("Error: Cannot Query cause. : ". mysqli_error($con));
	if(mysqli_affected_rows($con) >= 1){
		//update the conditional probability
		$query_get_model = "SELECT * FROM model;";
		$result_get_model = mysqli_query($con, $query_get_model) or die("Error executing SELECT Query on `model` table: ". mysqli_error($con));
		while($row1 = mysqli_fetch_assoc($result_get_model)){
			foreach($factors_arr[trim($row1['cause'])] as $cause){
				foreach($factors_arr[trim($row1['effect'])] as $evidence){
					/*$numerator = "SELECT COUNT(id) as numerator FROM statistics WHERE ". $row1['effect']. " = '$evidence' and ". $row1['cause']."= '$cause';";
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
					$prob = $numerator/$denominator;*/
					$query_get_probability = "SELECT probability_effect_cause WHERE model_id=".$row1['id']." and value_cause='$cause' AND value_effect='$evidence' LIMIT 1";
					$result_get_probability = mysqli_query($con, $query_get_probability) or die("Error executing probability error ". mysqli_error($con));
					$row_probs = mysqli_fetch_assoc($result_get_probability);
					$prob = $row_probs['probability_effect_cause'] * $probability[$row['cause']];
					$final_probability[] = new final_probability($row['cause'], $cause, $row['effect'], $evidence);
					//$probabilityCauseEffect[$row1['cause']."=".$cause."|".$row1['effect']."=".$evidence] = $row_probs['probability_effect_cause'] * probabilities[]/probabilities[];
					//echo "P(".$row1['effect']."=".$evidence." | ".$row1['cause']." = $cause) = ".$prob . "<br>";
					//$query_insert_prob = "INSERT INTO probabilities VALUES(NULL, ". $row1['id']. ", '$cause', '$evidence', $prob);";
					//mysqli_query($con, $query_insert_prob) or die("Error INSERTING conditional Probabilities: ". mysqli_error($con));
					//Parse Float and divide and store in (separate table)
				}
			}
		}
	}
}

class final_probability{
	public $cause, $cause_value, $effect, $effect_value, $probability_cause_effect;
	public function __construct($c, $c_v, $e, $e_v, $p){
		$this->cause = $c;
		$this->cause_value = $c_v;
		$this->effect = $e;
		$this->effect_value = $e_v;
		$this->probability_cause_effect = $p;
	}
}



//return all non-zero probabilities.
