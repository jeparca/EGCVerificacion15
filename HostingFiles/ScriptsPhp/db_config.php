<?php
header('Content-type: text/html; charset=utf-8');

/***
*** FUNCIÓN DE CONEXIÓN CON LA BASE DE DATOS
***
***
***
***
***
***/
function getConnector() {

	$user = "jeparcac_egc";
	$pass = "kqPTE8dLz3GVtks";
	$server = "localhost";
	$port = "3306";
	$db_name = "jeparcac_egc";

	$host = "mysql:host=$server;port=$port;dbname=$db_name;charset=utf8";

	try {
		$con = new PDO($host, $user, $pass);
		$con -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

		return $con;

	} catch( PDOException $e ) {

		echo 'Connection error: ' . $e -> GetMessage();
	}
}

?>