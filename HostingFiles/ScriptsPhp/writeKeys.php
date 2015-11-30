<?php
header('Content-Type: text/html; charset=utf-8');

include_once('keysManagement.php');
if (isset($_REQUEST['id']) & isset($_REQUEST['public']) & isset($_REQUEST['private'])){
	$votation_id = $_REQUEST['id'];
	$publicKey = $_REQUEST['public'];
	$privateKey = $_REQUEST['private'];


?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <h3>Database writing test</h3> 
</html>

<?php

	$operation = writeKeys($votation_id, $publicKey, $privateKey);

	if($operation){
		echo "Operation was completed successfully.";
	}else{
		echo "Ooops! An error happened while we was trying to write keys in database.";
	}

}else{
	echo "Votation id, public and private keys are mandatory. Please, check the URL format: http://egc.jeparca.com/writeKeys.php?id=XXXXXXXXXX&public=YYYYYYYYYY&private=ZZZZZZZZZZ";
}

?>