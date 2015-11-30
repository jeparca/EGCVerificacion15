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

?>