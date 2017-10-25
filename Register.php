<?php
    $con = mysqli_connect("localhost", "id2289309_id2276682_mysql", "password", "id2289309_fishingregulationsdb");

    $username = $_POST["username"];
	$email = $_POST["email"];
    $password = $_POST["password"];

    $statement = mysqli_prepare($con, "INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
    mysqli_stmt_bind_param($statement, "sss", $username, $email, $password);
    mysqli_stmt_execute($statement);
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>
