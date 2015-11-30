<?php

include_once('db_config.php');

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

function writeKeys($idVotation){
		
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