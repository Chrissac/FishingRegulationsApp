<?php
 
require_once 'DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
   
if (isset($_POST['email']) && isset($_POST['recoverycode'])&& isset($_POST['password'])) {
 
    $email = $_POST['email'];
    $recoverycode = $_POST['recoverycode'];
	$password = $_POST['password'];
	$isMatchedRecoveryCode = false;
	$isValidEmail = false;
    // check if user is already existed with the same email
    if ($db->isUserEmailExisted($email) ) {
        
		//Insert the recoveryCode into db.
		$isValidEmail = true;

    } else {
        $response["error"] = TRUE;
        $response["error_msg"] = "No email found with: " . $email;
        echo json_encode($response);
    }
	if ($db->isValidRecoveryCode($email,$recoverycode) ) {
        $isMatchedRecoveryCode = true;
		//Insert the recoveryCode into db.
    } else {
        $response["error"] = TRUE;
        $response["error_msg"] = "Recovery passwords do not match. Please copy the code from your email and paste it. ";
        echo json_encode($response);
    }
	
	if($isMatchedRecoveryCode && $isValidEmail){
	    $user = $db->updatePasswordFromRecoveryCode($email, $password);
        if ($user) {
			$response["success"] = true;  
            $response["user"]["email"] = $user["email"];
            echo json_encode($response);
        } else {
            // user does not exist
            $response["error"] = TRUE;
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
        }
	
	} else {
		$response["error"] = TRUE;
		$response["error_msg"] = "Unknown error occurred in registration!";
		echo json_encode($response);
	}
}
?>