<?php
    $con = mysqli_connect("localhost", "id2289309_id2276682_mysql", "password", "id2289309_fishingregulationsdb");
    
    $email = $_POST["email"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM users WHERE email = ?");
    mysqli_stmt_bind_param($statement, "s", $email);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $username,$email,$password);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
		
        $response["success"] = true;  
        $response["username"] = $username;
		$response["email"] = $email;
        $response["password"] = $password;
		
    }
    
    echo json_encode($response);
?>
