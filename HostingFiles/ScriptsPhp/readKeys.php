<?php
header('Content-Type: text/html; charset=utf-8');

/***
*** Incluimos el fichero donde se encuentran las funciones de lectura y escritura
***/
include_once('keysManagement.php');

/***
*** Se lee desde la URL el id de la votación para el que se quieren consultar las claves
***/
if (isset($_REQUEST['id'])){
	$votation_id=$_REQUEST['id'];

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <h3>Database reading test</h3> 
</html>
<?php

	/***
	*** Obtenemos las claves para el id proporcionado
	***/
	$keys = getBothKeys($votation_id);

	/***
	*** Recorremos los resultados obtenidos y los mostramos por pantalla.
	***/
	foreach ($keys as $results) {

		echo "Public key: ".$results['publicKey']."<br>";
		echo "Private key: ".$results['privateKey']."<br>";

	}

/***
*** Si no se ha proporcionado un id de votación en la URL o esta no sigue 
*** el formato indicado, se muestra un error.
***/
}else{
	echo "Votation id is mandatory. Please, check the URL format: http://egc.jeparca.com/readKeys.php?id=XXXXXXXXXX";
}

?>