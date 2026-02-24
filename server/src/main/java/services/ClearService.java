package services;

public class ClearService {
    public void clear(){
        db.clearAuth();
        db.clearUsers();
        db.clearGames();
    }
}
