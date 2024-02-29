/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Login;

/**
 *
 * @author Septian Galoh P
 */
public class users {
    
    public String username, status;

    public users(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public users() {
       username = username;
       status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String stat) {
        this.status = stat;
    }

   

}
