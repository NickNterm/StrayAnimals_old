<?php
$servername = "localhost";
$username = "root";
$password = "iqsoft";
$dbname = "StrayAnimals";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

if($_GET["task"] == "ReadAccountByToken"){
    $sql = "SELECT * FROM Login WHERE token = '" . $_GET["value"] . "'";
    $result = $conn->query($sql);
   
    if ($result->num_rows > 0) {
    // output data of each row
        while($row = $result->fetch_assoc()) {
            $Account->name = $row["name"];
            $Account->token = $row["token"];
            $Account->phone = $row["phone"];
            $Account->email = $row["email"];
            $Account->banned = $row["banned"];

            $myJSON = json_encode($Account);
            echo $myJSON;
        }
    } else {
    echo "null";
    }
}else if($_GET["task"] == "ReadAccountById"){
    $sql = "SELECT * FROM Login WHERE token IN " . $_GET["value"];
    $result = $conn->query($sql);
    $record = 0;
    if ($result->num_rows > 0) {
    // output data of each row
    echo "[";
        while($row = $result->fetch_assoc()) {
            $Account->name = $row["name"];
            $Account->token = $row["token"];
            $Account->phone = $row["phone"];
            $Account->email = $row["email"];
            $Account->banned = $row["banned"];

            $myJSON = json_encode($Account);

            $record++;
            if($record != $result->num_rows){
                echo $myJSON.",";
            }else{
                echo $myJSON;
            }
        }
        echo "]";
    } else {
    echo "null";
    }
}

else if($_GET["task"] == "SaveAccount"){
    $Account = json_decode($_GET["value"]);
    $sql = "INSERT INTO Login (token, name, phone, email, banned) VALUES ('".$Account->token."', '".$Account->name."', '".$Account->phone."', '".$Account->email."',0)";
    if ($conn->query($sql) === TRUE) {
        echo "ok";
    } else {
        echo "";
    }
}
$conn->close();
?>