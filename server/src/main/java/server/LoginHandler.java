package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.Auth;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import service.ChessService;

import java.nio.file.AccessDeniedException;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;

public class LoginHandler {
    public LoginHandler(){}
    public Auth login(String body, DataAccess dataAccess) throws NoSuchElementException, AccessDeniedException, MissingFormatArgumentException {
        ChessService service = new ChessService(dataAccess);
        User user = new Gson().fromJson(body, User.class);
        String securePass = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        if (user.password()==null || user.username()==null){
            throw new MissingFormatArgumentException("Missing password or username");
        }
        User foundUser = dataAccess.getUserbyUsername(user.username());
        if(foundUser==null){
            throw new NoSuchElementException("User not found"); //username doesn't exist
        } else if (!BCrypt.checkpw(user.password(), foundUser.password())){
            throw new AccessDeniedException("Username or password is incorrect");
        } else {
            return service.createAuth(foundUser.username());
        }
    }
}
