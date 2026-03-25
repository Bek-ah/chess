package client;

import chess.*;

import java.nio.file.AccessDeniedException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws AccessDeniedException {
        var serverURL = "http://localhost:8080";
        if (args.length == 1){
            serverURL = args[0];
        }
        new LoggedOutClient(serverURL);//.run;

    }
}
