<?php
header('Content-Type: text/html; charset=utf-8');

include_once('keysManagement.php');


if (isset($_REQUEST['id'])){
	$votation_id=$_REQUEST['id'];

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <h3>Database reading test</h3> 
</html>
<?php

	$keys = getBothKeys($votation_id);

	foreach ($keys as $results) {

		echo "Public key: ".$results['publicKey']."<br>";
		echo "Private key: ".$results['privateKey']."<br>";

	}

}else{
	echo "Votation id is mandatory. Please, check the URL format: http://egc.jeparca.com/readKeys.php?id=XXXXXXXXXX";
}

?>