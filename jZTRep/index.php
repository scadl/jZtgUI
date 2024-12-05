<?php

/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/

// curl -H X-ZT1-Auth:-token- http://localhost:9993/controller/network
// http://localhost/jZTRepeater/?apiRq=controller/network

header('Content-Type: application/json');

$ch = curl_init();

if ($_GET["apiRq"]=="controller/network_members") {

	$api_rq = "controller/network/" . $_GET["netID"] . "/member";
	curl_setopt($ch, CURLOPT_URL, "http://localhost:9993/" . $api_rq);
	curl_setopt($ch, CURLOPT_HTTPHEADER, [
		'X-ZT1-Auth: ' . $_GET['token']
	]);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	// CURLOPT_RETURNTRANSFER (int)	true to return the transfer as a string of the return value of curl_exec() instead of outputting it directly.
	$netS = json_decode(curl_exec($ch), true);
	curl_close($ch);

	$output = array();
	print("[");
	foreach($netS as $nKey => $nVal){
		$ch2 = curl_init();
		curl_setopt($ch2, CURLOPT_URL, "http://localhost:9993/controller/network/" . $_GET["netID"] . "/member/" . $nKey);
		curl_setopt($ch2, CURLOPT_HTTPHEADER, [
			'X-ZT1-Auth: ' . $_GET['token']
		]);
		curl_setopt($ch2, CURLOPT_RETURNTRANSFER, false);
		curl_exec($ch2); // will print output automaticaly, because of CURLOPT_RETURNTRANSFER=false
		curl_close($ch2);
	}
	print("]");
	//print(json_encode($output, JSON_HEX_AMP));

} else {
	curl_setopt($ch, CURLOPT_URL, "http://localhost:9993/" . $_GET["apiRq"]);
	curl_setopt($ch, CURLOPT_HTTPHEADER, [
		'X-ZT1-Auth: ' . $_GET['token']
	]);
	//curl_setopt($ch, CURLOPT_POST, 1);
	//curl_setopt($ch, CURLOPT_POSTFIELDS, "postvar1=value1&postvar2=value2&postvar3=value3");
	curl_exec($ch); // will print output automaticaly, because of CURLOPT_RETURNTRANSFER=false
	//print(rtrim(curl_exec($ch), "1"));
	curl_close($ch);
}

