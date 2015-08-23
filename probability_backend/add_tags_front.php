<?php
	require_once 'database.php';

	$query = 'SELECT DISTINCT factor FROM probabilities_individual';
	$result = mysqli_query($con,$query);
	$factors;
	while($row = mysqli_fetch_assoc($result)){
		$factors[]=$row["factor"];
	}
 ?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Add Tags</title>
</head>
<body>
	<div id="sub-msg"><?php
	if(isset($_GET['error'])){
		//Error Message
		echo "Error!";
	}
	if(isset($_GET['success'])){
		//Success Message
		echo "Success!";
	}
	 ?></div>
	<form action="receive_tags.php" method="get" accept-charset="utf-8">

		<label for="factors">Factors</label>
		<select name="factors" id="factor_select">
			<option value='-1'>--Select Any one--</option>
			<?php foreach ($factors as $factor) {
				echo '<option value="'.$factor.'">'.$factor.'</option>';
			}?>
		</select><br><br>

		<label for="values">Values</label>
		<select name="values" id="values">
			<option value=""></option>
		</select><br><br>
		<label for="tags">Tags</label>
		<textarea name="tags" rows="10" cols="80"></textarea><br><br>
		<button type="submit">Add Factor</button>
	</form>
	<script type="text/javascript" charset="utf-8">
		function dgi(str){ return document.getElementById(str);}
		var Ajax = new XMLHttpRequest();
		Ajax.onreadystatechange = function(){
			if(Ajax.status == 200){
				//Change stuffs
				dgi('values').innerHTML = Ajax.responseText;

			}
		}
		var fs = document.getElementById('factor_select');
		fs.onchange = function(){
			Ajax.open("GET", "getValues.php?factor="+fs.value ,  true);
			Ajax.send();
		}
	</script>
</body>
</html>