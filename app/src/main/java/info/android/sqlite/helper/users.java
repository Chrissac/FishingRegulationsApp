package info.android.sqlite.helper;

/**
 * Created by csacripante on 27/07/2017.
 */

public class users {
    public int id;
    String userName;
    String email;
    String password;
    String created_at;
    public int dbId;

    public users() {
    }

    public users(String username,String email, String password, int db_id){
        this.userName = username;
        this.email  = email;
        this.password = password;
        this.dbId = db_id;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setdb_Id(int db_id) {
        this.dbId = db_id;
    }
    public void setUserName(String username){
        this.userName = username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password  =password;
    }
    public void setCreated_at(String createdat){
        this.created_at  = createdat;
    }
    //getters
    public long getId() {
        return this.id;
    }
    public long getdb_id() {
        return this.dbId;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public String getCreated_at(String created_at){
        return this.created_at;
    }
}
