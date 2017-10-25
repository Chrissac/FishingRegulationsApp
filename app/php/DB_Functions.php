<?php
class DB_Functions {
 
    private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
 
        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
	public function storeRecoveryCode($email,$recoveryCode){
		
		$stmt = $this->conn->prepare("UPDATE users SET recoveryCode = ? WHERE email = ?");
        $stmt->bind_param("ss", $recoveryCode,$email);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT email FROM users WHERE email = ? AND recoveryCode is not null");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            return $user;
        } else {
            return false;
        }
	}
	public function updatePasswordFromRecoveryCode($email,$password){
		
		$stmt = $this->conn->prepare("UPDATE users SET password = ? WHERE email = ?");
        $stmt->bind_param("ss", $password,$email);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT email FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
			
			
			$stmt2 = $this->conn->prepare("UPDATE users SET recoveryCode = null WHERE email = ?");
            $stmt2->bind_param("s", $email);
            $stmt2->execute();
            $stmt2->close();
 
            return $user;
        } else {
            return false;
        }
	}
	
	public function isValidRecoveryCode($email,$recoveryCode){
		
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ? AND recoveryCode = ?");
        $stmt->bind_param("ss", $email,$recoveryCode);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) { 
            return true;
        } else {
            return false;
        }
	}
 
    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
		$stmt = $this->conn->prepare("SELECT email,username,password FROM users WHERE email = ? AND password = ?");
        $stmt->bind_param("ss", $email,$password);
        $result = $stmt->execute();
		$user = $stmt->get_result()->fetch_assoc();
        $stmt->close();
		return $user;
    }
	public function getUserByEmail($email) {
		$stmt = $this->conn->prepare("SELECT id,email,username,password FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $result = $stmt->execute();
		$user = $stmt->get_result()->fetch_assoc();
        $stmt->close();
		return $user;
    }
 
    /**
     * Check user is existed or not
     */
    public function isUserEmailExisted($email) {
		
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
	
	public function isUserNameExisted($username) {
		
        $stmt = $this->conn->prepare("SELECT email from users WHERE username = ?");
 
        $stmt->bind_param("s", $username);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }

}
?>