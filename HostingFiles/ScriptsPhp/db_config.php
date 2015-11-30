<?php
header('Content-type: text/html; charset=utf-8');

/***
*** FUNCIÓN DE CONEXIÓN CON LA BASE DE DATOS
***
*** Esta función conecta con la base de datos donde se almacenan
*** las claves que se crean durante la creación de la votación,
*** de modo que se puede conectar tanto para extraer como para
*** almacenar la información en la base de datos.
*** Esta función incorpora los siguientes parámetros internos
*** para hacer la conexión a la base de datos:
*** $user: usuario de conexión a la base de datos
*** $pass: contraseña de conexión a la base de datos
*** $server: servidor donde se encuentra alojada la base de datos
*** $port: puerto donde escucha el servidor de base de datos
*** $db_name: nombre de la base de datos
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