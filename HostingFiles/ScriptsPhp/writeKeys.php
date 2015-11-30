<?php
header('Content-Type: text/html; charset=utf-8');

/***
*** Incluimos el fichero donde se encuentran las funciones de lectura y escritura
***/
include_once('keysManagement.php');

/***
*** Se lee desde la URL el id de la votación, la clave pública y la clave privada
*** que se quiere almacenar en la base de datos.
***/
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

	/***
	*** Hacemos la operación de escritura
	***/
	$operation = writeKeys($votation_id, $publicKey, $privateKey);

	/***
	*** Mostramos un mensaje de operación completada con éxito si ha sido así y, sino un error.
	***/
	if($operation){
		echo "Operation was completed successfully.";
	}else{
		echo "Ooops! An error happened while we was trying to write keys in database.";
	}
	
/***
*** Si no se ha proporcionado un id de votación, clave pública y clave privada en la URL o esta no sigue 
*** el formato indicado, se muestra un error.
***/
}else{
	echo "Votation id, public and private keys are mandatory. Please, check the URL format: http://egc.jeparca.com/writeKeys.php?id=XXXXXXXXXX&public=YYYYYYYYYY&private=ZZZZZZZZZZ";
}

?>