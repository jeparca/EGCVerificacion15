<?
header('Content-Type: application/json; charset=utf-8');
$host = $_SERVER['HTTP_HOST'];
setlocale(LC_TIME, "es_ES.utf8");
date_default_timezone_set('Europe/Madrid');

?>
<?
if (isset($_REQUEST['id'])){
	$idv=$_REQUEST['id'];
}

?>

<?

$user="jeparca_egc";
$pass="E?FPe#bl9k.?";
$server="localhost";
$db="jeparcac_egc";
$con=mysql_connect($server,$user,$pass);
mysql_select_db($db, $con);
$result = mysql_query("SELECT * FROM keysvotesAES where idvotation ='".$idv."'", $con);

echo "{\"Secretkey\":\"".mysql_result($result, 0, "secretKey")."\"}";


mysql_close($con);
?>