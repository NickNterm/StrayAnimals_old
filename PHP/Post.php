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

if($_GET["task"] == "ReadPosts"){
    $sql = "SELECT * FROM Posts ORDER BY id DESC";
    $result = $conn->query($sql);
    $record = 0;
    if ($result->num_rows > 0) {
    // output data of each row
    echo "[";
        while($row = $result->fetch_assoc()) {
            $Post->accountId = $row["account_id"];
            $Post->photoLocation = $row["photo"];
            $Post->moreInfo = $row["more_info"];

            $myJSON = json_encode($Post);
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
}else if($_GET["task"] == "ReadPostsFromToken"){
    $sql = "SELECT * FROM Posts WHERE account_id = '" . $_GET["value"] . "' ORDER BY id DESC";
    $result = $conn->query($sql);
    $record = 0;
    if ($result->num_rows > 0) {
    // output data of each row
    echo "[";
        while($row = $result->fetch_assoc()) {
            $Post->accountId = $row["account_id"];
            $Post->photoLocation = $row["photo"];
            $Post->moreInfo = $row["more_info"];

            $myJSON = json_encode($Post);
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
}else if($_GET["task"] == "CreatePost"){
    $Post = json_decode($_GET["value"]);
    $sql = "INSERT INTO Posts (account_id, photo, more_info) VALUES ('".$Post->account_id."', '".$Post->photo."', '".$Post->more_info."')";
    if ($conn->query($sql) === TRUE) {
        echo "ok";
    } else {
        echo "";
    }
}
else if($_SERVER['REQUEST_METHOD'] == 'POST')
{
    $DefaultId = 0;
    $ImageData = $_POST['image'];
    $Description = $_POST['description'];
    $Token = $_POST['token'];
    echo "token is $Token";
    $GetOldIdSQL ="SELECT id FROM Posts ORDER BY id ASC";
    $Query = mysqli_query($conn,$GetOldIdSQL);
    while($row = mysqli_fetch_array($Query)){
        $DefaultId = $row['id'];
    }
    $ImagePath = "images/$DefaultId.png";
    $InsertSQL = "INSERT INTO Posts(account_id, photo, more_info) VALUES ('$Token', '$ImagePath', '$Description')";
    if(mysqli_query($conn, $InsertSQL)){
        file_put_contents("/var/www/html/StrayAnimals/$ImagePath", base64_decode($ImageData));
        echo "Your Image Has Been Uploaded.";
    }
    mysqli_close($conn);
}else{
    echo "Not Uploaded";
}
$conn->close();
?>