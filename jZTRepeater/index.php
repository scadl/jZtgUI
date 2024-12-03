<?php

// curl -H X-ZT1-Auth:-token- http://localhost:9993/controller/network
// http://localhost/jZTRepeater/?apiRq=controller/network

$ch = curl_init();

$token = "";
$fh = fopen('C:\ProgramData\ZeroTier\One\authtoken.secret','r');
while ($line = fgets($fh)) {
	$token .= $line;
}
fclose($fh);

curl_setopt($ch, CURLOPT_URL,"http://localhost:9993/".$_GET["apiRq"]);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
	'X-ZT1-Auth: '.$token
]);
//curl_setopt($ch, CURLOPT_POST, 1);
//curl_setopt($ch, CURLOPT_POSTFIELDS, "postvar1=value1&postvar2=value2&postvar3=value3");

print(rtrim(curl_exec($ch),"1"));

?>