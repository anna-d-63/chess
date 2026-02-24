package AnnasTests;

import model.UserData;
import org.junit.jupiter.api.Test;

public class dataAccessTests {
    @Test
    public void insertUser(){
        UserData u = new UserData("Anna", "password", "anna@byu.edu");

    }
}
