<?php

	require_once 'Config.php';
	require_once 'DB_Functions.php';
	$db = new DB_Functions();
    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);


    $username = $_POST["username"];
	$email = $_POST["email"];
    $password = $_POST["password"];
	$loginType = $_POST["loginType"];
	
		//Check to see if the users already exists
	if ($db->isUserEmailExisted($email) && $loginType!="FB") {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "An account is already registered with that email: " . $email;
        echo json_encode($response);
    } else if($db->isUserEmailExisted($email) && $loginType=="FB"){
		$user = $db->getUserByEmail($email);
		if ($user) {
			$response["success"] = true;
			$response["userexistsfb"] = true;			
			$response["user"]["id"] = $user["id"];
            $response["user"]["email"] = $user["email"];
			$response["user"]["username"] = $user["username"];
			$response["user"]["password"] = $user["password"];
            echo json_encode($response);
        } 
		else{
				$response["error"] = TRUE;
				$response["error_msg"] = "Something Went wrong ";
				echo json_encode($response);
		}
	}
	else if($loginType=="FB"){
		//insert the new facebook user
			$statement = mysqli_prepare($con, "INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
			mysqli_stmt_bind_param($statement, "sss", $username, $email, $password);
			mysqli_stmt_execute($statement);
			
		$user = $db->getUserByEmail($email);
		if ($user) {
			$response["success"] = true;
			$response["userexistsfb"] = true;			
			$response["user"]["id"] = $user["id"];
            $response["user"]["email"] = $user["email"];
			$response["user"]["username"] = $user["username"];
			$response["user"]["password"] = $user["password"];
            echo json_encode($response);
        } 
		else{
				$response["error"] = TRUE;
				$response["error_msg"] = "Something Went wrong ";
				echo json_encode($response);
		}
	
	}
	
	if ($db->isUserNameExisted($username)) {
        // user already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "An account is already registered with user name: " . $username;
        echo json_encode($response);
    } 
			//insert the user
		if($db->isUserEmailExisted($email)){

		}
		else{
			$statement = mysqli_prepare($con, "INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
			mysqli_stmt_bind_param($statement, "sss", $username, $email, $password);
			mysqli_stmt_execute($statement);
			
			$response = array();
			$response["success"] = true;  
			
			echo json_encode($response);
		}


?>