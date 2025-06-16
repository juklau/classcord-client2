package fr.classcord.model;


public class User {
    private String username;
    private String status;



    // Constructeurs

    public User(){

    }

    public User(String username, String status){
        this.username = username;
        this.status = status;
    }

    // getters/setters
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    //methodes

    //toujours faire toString
    @Override
    public String toString(){
        return "User{" +
            "username = '" + username + '\'' +
            ",status = '" + status + '\'' +
        '}';
    }
    
}