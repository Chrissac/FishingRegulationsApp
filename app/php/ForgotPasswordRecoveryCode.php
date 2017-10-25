<?php
 
require_once 'DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
   
if (isset($_POST['email'])) {
 
    $email = $_POST['email'];
    $recoverycode = $_POST['recoverycode'];
 
    // check if user is already existed with the same email
    if ($db->isUserEmailExisted($email)) {
        
		//Insert the recoveryCode into db.
        $user = $db->storeRecoveryCode($email, $recoverycode);
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
        $response["error_msg"] = "No email found with: " . $email;
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required input email is missing!";
    echo json_encode($response);
}
?>