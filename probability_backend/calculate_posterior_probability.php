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
// 		"probabilities": {
// 					"value1":probability,
// 					"value2":probability
// 				}
// 	},
//  	{
// 		"name" : "factor_name",
// 		"probabilities": {
// 					"value1":probability,
// 					"value2":probability
// 				}
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
$factors = json_decode($input, true); //true lets us use assoc. arrays

foreach($factors as $factor){
	//print_r($factor['name']);
	$list[] = $factor['name'];
}

//get all individual probabilities
$query ="SELECT * FROM probabilities_individual;";
$result = mysqli_query($con, $query) or die("Error: Cannot select from individual probability: ". mysqli_error($con));
while($row = mysqli_fetch_assoc($result)){
	$probability[$row['factor']][$row['value']] = $row['probability'];
}



foreach($factors as $factor){
		//update the individual probability
		foreach($factor['probabilities'] as $values => $probabilities)
			$probability[$factor['name']][$values] = $probabilities;
		calculate_probabilities($factor);
		//continue; // :D
}

function calculate_probabilities($single_factor_array){
	global $list;
	//valar morghulis - All functions must die
	if(in_array($single_factor_array['name'], $list)) return;

	//valar daheris - All functions must serve
	if(is_cause($single_factor_array['name'])){
		$f_s = $single_factor_array['name'];
		$f_v = $single_factor_array['probabilities'];
		operate_on_effects($f_s, $f_v);
	}

	//valar daheris - All functions must serve
	if(is_effect($single_factor_array['name'])){
		$f_s = $single_factor_array['name'];
		$f_v = $single_factor_array['probabilities'];
		operate_on_causes($f_s, $f_v);
	}
	return;
}

function operate_on_effects($factor_str, $factor_val){
	$query = "SELECT effect from model WHERE cause='$factor_str';";
	$result = mysqli_query($con, $query) or die("Error Selecting Effects: ". mysqli_error($con));
	while($row = mysqli_fetch_assoc($result)){
		$effect['name'] = $row['effect'];
		$query2 = "SELECT value from probabilities_individual WHERE factor='".$row['effect']."';";
		$result2 = mysqli_query($con, $query2) or die("Error selecting vallues from a specific factor: ". mysqli_error($con));
		while($row2 = mysqli_fetch_assoc($result2)){
			$val = $row2['value'];
			$p_e = $row2['probability'];
			//get model id
			$query3 = "SELECT id from model WHERE cause='$factor_str' AND effect=".$row['effect']."';";
			$result3 = mysqli_query($con, $query3);
			$model_id = mysqli_fetch_assoc($result3)['id'];
			$count = 0;
			foreach($factor_val as $cause_value => $p_c){
				$query4 = "SELECT probability_cause_effect as pce FROM probabilities WHERE model_id=$model_id AND value_cause='$cause_value' AND value_effect='$val';";
				$result4 = mysqli_query($con, $query4);
				$p_c_e = mysqli_fetch_assoc($result4)['pce'];
				$prob = $p_c_e * $p_e / $p_c ;
				$count++;
			}

			//$query4  = "SELECT * FROM probabilities WHERE model_id=$model_id AND value_cau"
			$probability[$effect['name']][$val] = $prob/$count;
			$effect['probabilities'][$val] = $prob/$count;
		}
	}
	calculate_probabilities($effect);
}

function operate_on_causes($factor_str, $factor_val){
	$query = "SELECT cause from model WHERE effect='$factor_str';";
	$result = mysqli_query($con, $query) or die("Error Selecting Causes: ". mysqli_error($con));
	while($row = mysqli_fetch_assoc($result)){
		$cause['name'] = $row['cause'];
		$query2 = "SELECT value from probabilities_individual WHERE factor='".$row['effect']."';";
		$result2 = mysqli_query($con, $query2) or die("Error selecting vallues from a specific factor: ". mysqli_error($con));
		while($row2 = mysqli_fetch_assoc($result2)){
			$val = $row2['value'];
			$p_c = $row2['probability'];
			//get model id
			$query3 = "SELECT id from model WHERE effect='$factor_str' AND cause=".$row['effect']."';";
			$result3 = mysqli_query($con, $query3);
			$model_id = mysqli_fetch_assoc($result3)['id'];
			$count = 0;
			foreach($factor_val as $effect_value => $p_e){
				$query4 = "SELECT probability_effect_cause as pec FROM probabilities WHERE model_id=$model_id AND value_cause='$val' AND value_effect='$effect_value';";
				$result4 = mysqli_query($con, $query4);
				$p_e_c = mysqli_fetch_assoc($result4)['pec'];
				$prob = $p_e_c * $p_c / $p_e ;
				$count++;
			}
			$probability[$cause['name']][$val] = $prob/$count;
			//$query4  = "SELECT * FROM probabilities WHERE model_id=$model_id AND value_cau"
			$cause['probabilities'][$val] = $prob/$count;
		}
	}
	calculate_probabilities($effect);
}



function is_cause($factor_str){
	$query = "SELECT * FROM model WHERE cause='$factor_str';";
	$result = mysqli_query($con, $query) or die("Exception in Finding if the factor is a cause: ". mysqli_error($con));
	if(mysqli_affected_rows($con) < 1) return false;
	return true;
}

function is_effect($factor_str){
	$query = "SELECT * FROM model WHERE effect='$factor_str';";
	$result = mysqli_query($con, $query) or die("Exception in Finding if the factor is a cause: ". mysqli_error($con));
	if(mysqli_affected_rows($con) < 1) return false;
	return true;
}

print_r($probability);
//--ignore after below --
/*


//calculate P(Cause | Evidence)for all

//Structure for storing final probability
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

$final_probability = Array(); //dummy declaration <- for visibility
foreach($factors as $factor){
	$query = "SELECT * FROM model where effect='".$factor['name']."';";
	echo $query;
	mysqli_query($con,$query) or die("Error: Cannot Query cause. : ". mysqli_error($con));
	if(mysqli_affected_rows($con) >= 1){
		//update the conditional probability
		$query_get_model = "SELECT * FROM model;";
		$result_get_model = mysqli_query($con, $query_get_model) or die("Error executing SELECT Query on `model` table: ". mysqli_error($con));
		while($row1 = mysqli_fetch_assoc($result_get_model)){
			foreach($factors_arr[trim($row1['cause'])] as $cause){
				foreach($factors_arr[trim($row1['effect'])] as $evidence){
					$query_get_probability = "SELECT probability_effect_cause WHERE model_id=".$row1['id']." and value_cause='$cause' AND value_effect='$evidence' LIMIT 1";
					$result_get_probability = mysqli_query($con, $query_get_probability) or die("Error executing probability error ". mysqli_error($con));
					$row_probs = mysqli_fetch_assoc($result_get_probability);
					$prob = $row_probs['probability_effect_cause'] * $probability[$row['cause']][$cause] / $probability[$row['effect']][$effect];
					$final_probability[] = new final_probability($row['cause'], $cause, $row['effect'], $evidence);
					//Parse Float and divide and store in (separate table)
				}
			}
		}
	}
}

print_r($final_probability);

//iterate through $final_probability and return only probabilities that are greatest in a factor?

/* */ //<-- Ends any opened comment