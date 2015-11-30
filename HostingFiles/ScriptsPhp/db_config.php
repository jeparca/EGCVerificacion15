<?php
header('Content-type: text/html; charset=utf-8');

function getConnector() {

	$user = "jeparcac_egc";
	$pass = "kqPTE8dLz3GVtks";

	$host = "mysql:host=localhost;port=3306;dbname=jeparcac_egc;charset=utf8";

	try {
		$con = new PDO($host, $user, $pass);
		$con -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

		return $con;

	} catch( PDOException $e ) {

		echo 'Connection error: ' . $e -> GetMessage();
	}
}

?>