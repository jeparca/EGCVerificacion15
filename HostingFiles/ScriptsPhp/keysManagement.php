<?php

/***
*** Incluimos el archivo de conexión a la base de datos.
***/
include_once('db_config.php');

/***
*** FUNCIÓN DE EXTRACCIÓN DE CLAVES DE LA BASE DE DATOS
***
*** Esta función conecta con la base de datos donde se almacenan
*** las claves que se crean durante la creación de la votación,
*** de modo que se puede obtener la clave pública y privada.
*** En un principio, esta función está diseñada como función de
*** testing para poder recuperar ambas claves, pero será modificada
*** debido al grave problema de seguridad que ello supone.
*** Esta función incorpora los siguientes parámetros externos
*** para obtener las claves:
*** $idVotation: es el id de la votación para la cual se quieren conocer las claves
***/
function getBothKeys($idVotation){
		
	try{

 		$con = getConnector();
 	
		$query = "SELECT * FROM keysvotes WHERE idvotation = ".$idVotation;
		$sentencia = $con->query($query);
		$rows = $sentencia->fetchAll();
		$con=null;
	
		return $rows;
 	}catch(PDOException $e){
		echo "Error: ".$e->GetMessage();
	}
}

/***
*** FUNCIÓN DE ESCRITURA DE CLAVES EN LA BASE DE DATOS
***
*** Esta función conecta con la base de datos donde se almacenan
*** las claves que se crean durante la creación de la votación,
*** de modo que se pueden escribir nuevas claves pasadas por parámetros.
*** En un principio, esta función está diseñada como función de
*** testing para poder escribir ambas claves, pero será modificada
*** debido al grave problema de seguridad que ello supone.
*** Esta función incorpora los siguientes parámetros externos
*** para obtener las claves:
*** $idVotation: es el id de la votación para la cual se quieren conocer las claves
*** $publicKey: clave pública a incorporar para el id proporcionado
*** $privateKey: clave privada a incorporar para el id proporcionado
***/
function writeKeys($idVotation, $publicKey, $privateKey){
		
	try{

 		$con = getConnector();
 	
		$query = "INSERT INTO keysvotes (idvotation, publicKey, privateKey) VALUES (:id,:public,:private)";
		$sentencia = $con->prepare($query);
		$sentencia->bindParam(':id',$idVotation);
		$sentencia->bindParam(':public',$publicKey);
		$sentencia->bindParam(':private',$privateKey);

		$insert = $sentencia->execute();
		$con=null;

		return $insert;
 	}catch(PDOException $e){
		echo "Error: ".$e->GetMessage();
	}
}

?>