package dataaccess;

import model.UserData;

public class dataAccessMain {
    public static void main(String[] args){
        var db = new MemoryUserDAO();
        UserData u1 = new UserData("Anna", "password", "anna@byu.edu");
        db.createUser(u1);
        System.out.println(db);

        UserData u2 = new UserData("Kevin", "pswd", "kev@byu.edu");
        db.createUser(u2);
        System.out.println(db);

        UserData user = db.getUser("Kevin");
        System.out.println(user);

        db.clearUsers();
        System.out.println(db);
    }
}
