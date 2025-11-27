package org.example.lab4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// --- Import classes needed for file reading for users file.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class lab2Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // HERE WE WILL ADD THE LAB 3 CODE TO READ FROM COMMAND LINE THE ARGUMENTS
        // first lets put the default value for max attempt and block time
        int n=3;
        int t=60;

        // lets get the arguments to pass for the program
        List<String> args = getParameters().getRaw();
        // i will add a line to debug if not needed i will remove it or make it a note
        //System.out.println("DEBUG: I found " + args.size() + " arguments: " + args);

        if(args.size()>= 2){
            try {
                n = Integer.parseInt(args.get(0)); //first arg is the num of attempts
                t = Integer.parseInt(args.get(1)); // second arg is the blocking time
            } catch (NumberFormatException e){
                System.out.println("Error: Arguments must be integers. Using defaults (3, 60).");            }
        }else {
            System.out.println("Warning: No arguments provided. Using defaults (3, 60).");
        }

        ArrayList<User> validUsers = new ArrayList<>();
        try {
            // This will look for "users.txt" in the main project folder
            File userFile = new File("users.txt"); // the users file have to be in the same folder as the project.
            Scanner fileReader = new Scanner(userFile);
            System.out.println("Reading from users.txt.....");

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                try {
                    String[] parts = line.split("\\s+"); // Split by one or more spaces
                    if (parts.length == 2) {
                        validUsers.add(new User(parts[0], parts[1]));
                    }
                } catch (Exception e) {
                    // Ignore bad lines, just like lab 1 but without printing the error to console
                    System.out.println(" non-valid line: " + line);
                }
            }
            fileReader.close(); // Don't forget to close the scanner!!! if not it will keep going.=bugs
            System.out.println("Loaded " + validUsers.size() + " valid users."); // to see how much valid lines in the arraylist

        } catch (FileNotFoundException e) {
            System.out.println(" error : users.txt file not found!!!!");
            System.out.println("Please place users.txt in the " + System.getProperty("user.dir") + " folder."); // to print the directory the project in or user in of course we want user in the project.
            // If we can't load users, we must stop the app.
            Platform.exit();
            return;
        }


        // LOAD THE FXML (like before)
        FXMLLoader fxmlLoader = new FXMLLoader(lab2Application.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 333, 444); // Set window size


        // here we will pass the user to the controller..
        // This is what connects data to  UI
        lab2Controller controller = fxmlLoader.getController();
        controller.setUserList(validUsers); // We pass the list we just loaded! the arraylist/validUsers

        // now we will add lab 3 settings (n and t) to the controller
        controller.setLoginSettings(n,t);

        stage.setTitle("Login Window"); // title of the window
        stage.setScene(scene);
        stage.show();

        // This makes the 'x' button close the program or else it will not close.
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args); // to pass the args
    }
}