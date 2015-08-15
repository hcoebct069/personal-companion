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

$list;
$list2;
/*foreach($factors as $factor){
	//print_r($factor['name']);
	$list[] = $factor['name'];
	break;
}*/

//get all individual probabilities
$query ="SELECT * FROM probabilities_individual;";
$result = mysqli_query($con, $query) or die("Error: Cannot select from individual probability: ". mysqli_error($con));
while($row = mysqli_fetch_assoc($result)){
	$probability[$row['factor']][$row['value']] = $row['probability'];
}

//$query = "CREATE TABLE temp_probabilities AS SELECT * FROM probabilities;";
//mysqli_query($con, $query) or die("Cannot create temporary table: ". mysqli_error($con));



foreach($factors as $factor){
		//update the individual probability
		foreach($factor['probabilities'] as $values => $probabilities)
			$probability[$factor['name']][$values] = $probabilities;
			$list[] = $factor['name'];
		//continue; // :D
}

foreach($factors as $factor){
	$list2 = NULL;
	calculate_probabilities($factor);
}

function calculate_probabilities($single_factor_array){
	global $list, $list2;
	//valar morghulis - All functions must die
	/*if (isset($list)){
		//echo "Yes! Found it! :D \n";
		if(in_array($single_factor_array['name'], $list)) {
			//echo "\n\nFound in list\n\n";
			return;
		}

	}

	if(isset($list2)){
		if(in_array($single_factor_array['name'], $list2)) return;
	}*/

	//add  the current  factor in list
	//$list[] = $single_factor_array['name'];
	//echo "We went there!";
	//valar daheris - All functions must serve
	if(is_cause($single_factor_array['name'])){
		$f_s = $single_factor_array['name'];
		echo "'".$f_s . "' is a cause. Operating on it's immediate Effects! \n ";
		$f_v = $single_factor_array['probabilities'];
		operate_on_effects($f_s, $f_v);
	}

	//valar daheris
	//- All functions must serve
	if(is_effect($single_factor_array['name'])){
		$f_s = $single_factor_array['name'];
		echo "'".$f_s . "' is a effect. Operating on it's immediate Causes! \n";
		$f_v = $single_factor_array['probabilities'];
		operate_on_causes($f_s, $f_v);
	}
	return;
}

function operate_on_effects($factor_str, $factor_val){
	global $con, $list, $list2, $probability;
	$hold_current = NULL; //dummy
	$query = "SELECT effect from model WHERE cause='$factor_str';";
	$result = mysqli_query($con, $query) or die("Error Selecting Effects: ". mysqli_error($con));
	while($row = mysqli_fetch_assoc($result)){
		if(in_array($row['effect'], $list) || (isset($list2) && in_array($row['effect'], $list2)) ) continue;
		//needs amemndment here
		//
		//
		//
		//
		//
		//
		echo "  Operating on ".$row['effect']." \n";
		$effect['name'] = $row['effect'];
		$query2 = "SELECT value from probabilities_individual WHERE factor='".$row['effect']."';";
		$result2 = mysqli_query($con, $query2) or die("Error selecting vallues from a specific factor: ". mysqli_error($con));
		while($row2 = mysqli_fetch_assoc($result2)){
			$val = $row2['value'];
			echo "Operating on subvalue: ". $val . "\n\n";
			$p_e = $probability[$effect['name']][$val];
			//get model id
			$query3 = "SELECT id from model WHERE cause='$factor_str' AND effect='".$row['effect']."';";
			//echo $query3;
			//echo "\n";
			$result3 = mysqli_query($con, $query3) or die("Could not get model! ". mysqli_error($con));
			$model_id = mysqli_fetch_assoc($result3)['id'];
			$prob = 0;
			$count = 0;
			foreach($factor_val as $cause_value => $p_c){
				//echo "P_C should be: ". $factor_val[$cause_value];
				//echo "\n\n";
				$query4 = "SELECT probability_cause_effect as pce FROM probabilities WHERE model_id=$model_id AND value_cause='$cause_value' AND value_effect='$val';";
				$result4 = mysqli_query($con, $query4);
				$p_c_e = mysqli_fetch_assoc($result4)['pce'];

				$query5 = "SELECT probability_cause_effect as pce_2, value_effect as v_e FROM probabilities WHERE model_id=$model_id AND value_cause='$cause_value';";
				echo  $query5 . "\n";
				$result5 =mysqli_query($con, $query5) or die("Error in line 153 : ". mysqli_error($con));
				$sum = 0;
				while($row5 = mysqli_fetch_assoc($result5)){
					echo "--- pce_2=".$row5['pce_2']." | prob_independent: ".$probability[$effect['name']][$row5['v_e']]. "\n";
					$sum += ($row5['pce_2'] * $probability[$effect['name']][$row5['v_e']]);
				}
				//check for sanity if sum is still zero, don't f*** up!
				//
				if($sum == 0) {$prob += 0;}
				else{$prob += ($p_c_e * $p_e/$sum);}
				echo "probability: " . $prob . "\n\n";
				//$prob += $p_c_e * $p_e / $p_c ;
				$count++;
			}
			echo "-----------------". $count ."\n\n";

			//$query4  = "SELECT * FROM probabilities WHERE model_id=$model_id AND value_cau"
			if($prob == 0){
				$val_p = 0;
			} else {
				$val_p = $prob/$count;
			}
			echo "[][]FINALPROB:: ". $val_p;
			$probability_local[$effect['name']][$val] = $val_p; // $prob/$count;
			$effect['probabilities'][$val] = $val_p; //$prob/$count;
		}
		$probability[$effect['name']] = $probability_local[$effect['name']];
		$hold_current[] = $effect;
		$list2[] = $effect['name'];
		//calculate_probabilities($effect);
	}

	if($hold_current == NULL) return;
	foreach($hold_current as $eff){
		calculate_probabilities($eff);
	}

}

function operate_on_causes($factor_str, $factor_val){
	global $con, $list, $list2, $probability;
	$hold_current = NULL; //dummy
	$query = "SELECT cause from model WHERE effect='$factor_str';";
	$result = mysqli_query($con, $query) or die("Error Selecting Causes: ". mysqli_error($con));
	while($row = mysqli_fetch_assoc($result)){
		if(in_array($row['cause'], $list) || (isset($list2) && in_array($row['cause'], $list2)) ) continue;
		echo "  Operating on ".$row['cause']." \n";
		$cause['name'] = $row['cause'];
		$query2 = "SELECT value from probabilities_individual WHERE factor='".$row['cause']."';";
		$result2 = mysqli_query($con, $query2) or die("Error selecting vallues from a specific factor: ". mysqli_error($con));
		while($row2 = mysqli_fetch_assoc($result2)){
			$val = $row2['value'];
			$p_c = $probability[$cause['name']][$row2['value']];
			//get model id
			$query3 = "SELECT id from model WHERE effect='$factor_str' AND cause='".$row['cause']."';";
			$result3 = mysqli_query($con, $query3);
			$model_id = mysqli_fetch_assoc($result3)['id'];
			$count = 0;
			$prob = 0;
			//$prob_e = 0;
			foreach($factor_val as $effect_value => $p_e){

				//$query4 = "SELECT probability_effect_cause as pec FROM probabilities WHERE model_id=$model_id AND value_cause='$val' AND value_effect='$effect_value';";
				//echo $query4 . "\n";
				//echo "P_E should be: ". $factor_val[$effect_value];
				//echo "\n\n";
				//$result4 = mysqli_query($con, $query4);
				//$p_e_c = mysqli_fetch_assoc($result4)['pec'];
				//if($p_e == 0) $p_e = 0.00001; //normally when p_e is 0 p_e_c is 0
				//$prob += $p_e_c * $p_c / $p_e ;
				//$prob_e_c += $p_e_c;
				//$prob_e += $p_e;
				//$count++;

				$query4 = "SELECT probability_effect_cause as pec FROM probabilities WHERE model_id=$model_id AND value_cause='$val' AND value_effect='$effect_value';";
				$result4 = mysqli_query($con, $query4);
				$p_e_c = mysqli_fetch_assoc($result4)['pec'];

				$query5 = "SELECT probability_effect_cause as pec_2, value_cause as v_c FROM probabilities WHERE model_id=$model_id AND value_effect='$effect_value';";
				echo  $query5 . "\n";
				$result5 =mysqli_query($con, $query5) or die("Error in line 153 : ". mysqli_error($con));
				$sum = 0;
				while($row5 = mysqli_fetch_assoc($result5)){
					echo "--- pce_2=".$row5['pec_2']." | prob_independent: ".$probability[$cause['name']][$row5['v_c']]. "\n";
					$sum += ($row5['pec_2'] * $probability[$cause['name']][$row5['v_c']]);
				}
				//check for sanity if sum is still zero, don't f*** up!
				//
				if($sum == 0) {$prob += 0;}
				else{$prob += ($p_e_c * $p_c/$sum);}
				echo "probability: " . $prob . "\n\n";
				//$prob += $p_c_e * $p_e / $p_c ;
				$count++;
			}
			if($prob == 0){
				$val_p = 0;
			} else {
				$val_p = $prob/$count;
			}
			echo "[][]FINALPROB:: ". $val_p;
			$probability_local[$cause['name']][$val] = $val_p; // $prob/$count;
			$cause['probabilities'][$val] = $val_p; //$prob/$count;

			//$prob_e_c = $prob_e_c;
			//$prob_e = $prob_e;
			//if($prob_e_c == 0){
			//	$val_p = 0;
			//} else {
			//	$val_p = $prob_e_c * $p_c / $prob_e;
			//}
			//$probability[$cause['name']][$val] = $val_p; //$prob/$count;
			//$query4  = "SELECT * FROM probabilities WHERE model_id=$model_id AND value_cau"
			//$cause['probabilities'][$val] = $val_p; //$prob/$count;
		}
		$probability[$cause['name']] = $probability_local[$cause['name']];
		$hold_current[]  = $cause;
		$list2[] = $cause['name'];
		//calculate_probabilities($cause);
	}
	if($hold_current == NULL) return;
	foreach($hold_current as $caus){
		calculate_probabilities($caus);
	}

}



function is_cause($factor_str){
	global $con;
	$query = "SELECT * FROM model WHERE cause='$factor_str';";
	$result = mysqli_query($con, $query) or die("Exception in Finding if the factor is a cause: ". mysqli_error($con));
	if(mysqli_num_rows($result) < 1) return false;
	return true;
}

function is_effect($factor_str){
	global $con;
	$query = "SELECT * FROM model WHERE effect='$factor_str';";
	$result = mysqli_query($con, $query) or die("Exception in Finding if the factor is a cause: ". mysqli_error($con));
	if(mysqli_num_rows($result) < 1) return false;
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