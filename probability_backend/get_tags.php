<?php
	//curl calculate_posterior_probability
	//get json_input, pass that untouched to calculate_posteri=psor_probability.

	//json is returned - decode it
	//for each factors, find the value that has max probability
	//at the end of it: you'd have factor-value pair
	//see the database and store tags in array.
	//json_encode the tags and return.

	$inp = $_POST['json_input'];

	$data = array("json_input"=>$inp);

	$c  = curl_init();

	curl_setopt($c, CURLOPT_URL, 'http://localhost/probability_backend/calculate_posterior_probability.php');
	curl_setopt($c, CURLOPT_POST, true);
	curl_setopt($c, CURLOPT_POSTFIELDS,  $data);
	curl_setopt($c, CURLOPT_RETURNTRANSFER, 1);

	$output = curl_exec($c);
	//print_r($output);
	$output_json = json_decode($output, true);
	//var_dump($output_json);
	//echo "a";
	//print_r($output_json);
	$factor_value;
	foreach($output_json as $factorKey => $factor){
		$max = 0;
		reset($factor);
		//Assign the first key
		$factor_value[$factorKey] = key($factor);
		foreach($factor as $key => $val){
			//echo $val . "\n";
			if($val > $max){
				$factor_value[$factorKey] = $key;
				$max = $val;
			}
		}
	}

	//print_r($factor_value);
	//You've an key-value pair now in $factor_value

	require_once 'database.php';

	$tags = "";
	foreach($factor_value as $key => $val){
		$query = "SELECT tag FROM factors_to_tag WHERE factor='$key' AND value='$val';";
		$res = mysqli_query($con, $query) or die("Whoops! ");
		$tags = $tags . mysqli_fetch_assoc($res)['tag'];
	}

	print_r($tags);

 ?>