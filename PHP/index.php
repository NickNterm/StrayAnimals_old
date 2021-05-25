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

            $myJSON = json_encode($Account);

            echo $myJSON;
        }
    } else {
    echo "0 results";
    }
}
$conn->close();
?> 