<?php

	require_once 'Config.php';
	require_once 'DB_Functions.php';
	$db = new DB_Functions();
    $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

	$email = $_POST["email"];
    $password = $_POST["password"];
	$UserExists = False;
	
	$response = array("error" => FALSE);
	
	
		//Check to see if the users already exists
	if ($db->isUserEmailExisted($email)) {
		$UserExists = true;
    }
	else{
		// user does not exist. Tell them to sign up
        $response["error"] = TRUE;
        $response["error_msg"] = "Account does not exist. Please register an account. ";
        echo json_encode($response);
	}
	
	
	if($UserExists){
	    $user = $db->getUserByEmailAndPassword($email, $password);
        if ($user) {
			$response["success"] = true;  
            $response["user"]["email"] = $user["email"];
			$response["user"]["username"] = $user["username"];
			$response["user"]["password"] = $user["password"];
            echo json_encode($response);
        } 
		else{
				$response["error"] = TRUE;
				$response["error_msg"] = "Password is incorrect Please try again or recover your password ";
				echo json_encode($response);
		}
	
	}
	
?>