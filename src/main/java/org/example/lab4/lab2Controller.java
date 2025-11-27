package org.example.lab4;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class lab2Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;


    // This variable will hold the list of users we get from lab2Application
    private ArrayList<User> userList;
    // now we have the valid users in the arraylist.

    /**
     * This method is called by lab2Application.java
     * to or pass in the list of valid users.
     */
    public void setUserList(ArrayList<User> users) {
        this.userList = users;
        System.out.println("Controller now has " + this.userList.size() + " users."); // we will print the number of users in the userlist
    }

    //=========
    private int maxRetries; // this new variables for lab 3
    private int blockTimeInSeconds;

    public void setLoginSettings(int n, int t) {
        this.maxRetries = n;
        this.blockTimeInSeconds = t;
        System.out.println("Settings loaded: Max Tries=" + maxRetries + ", Block time=" + blockTimeInSeconds + "s");
    }

    /**
     * This method handles the login logic using Threads as required in lab 3
     */
    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText(); //getter
        String password = passwordField.getText(); //getter

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        // 1. Find the user in our list
        User foundUser = null;
        for (User user : userList) {
            if (user.getUsername().equals(username)) {  // we need a valid user to start counting retries and to block
                foundUser = user;
                break;
            }
        }

        if (foundUser == null) {
            errorLabel.setText("User not found.");
            return;
        }

        // We need a 'final' variable to use inside the threads
        final User targetUser = foundUser;


        // =================================================================
        // SCENARIO A: PASSWORD IS CORRECT
        // Requirement b: Thread checks if user is blocked
        // =================================================================
        if (targetUser.getPassword().equals(password)) {

            Thread checkBlockThread = new Thread(() -> {
                // This runs in the background
                if (targetUser.isBlocked()) {
                    // User is correct, BUT they are currently blocked
                    Platform.runLater(() -> {
                        errorLabel.setText("You are BLOCKED! Please wait.");
                    });
                } else {
                    // User is correct AND not blocked -> SUCCESS!
                    targetUser.resetFailedAttempts(); // Reset counter

                    Platform.runLater(() -> {
                        // This code runs on the UI thread to open the window
                        openWelcomeScreen();
                    });
                }
            });
            checkBlockThread.start(); // Start the thread

        }
        // =================================================================
        // SCENARIO B: PASSWORD IS WRONG
        // Requirement : Thread updates failed attempts
        // =================================================================
        else {
            Thread updateFailureThread = new Thread(() -> {
                // 1. Check if they are ALREADY blocked before counting a new failure
                if (targetUser.isBlocked()) {
                    Platform.runLater(() -> errorLabel.setText("You are BLOCKED! Please wait."));
                    return;
                }
                // If we are here, the user is NOT blocked.
                // But if their failures are still at the max, it means the timer Just expired.
                // We must give them a fresh start (0 tries) before counting this new failure.
                if (targetUser.getFailedAttempts() >= maxRetries) {
                    targetUser.resetFailedAttempts();
                }
                // 2. Add a failure
                targetUser.addFailedAttempt();
                int currentFailures = targetUser.getFailedAttempts();

                // 3. Check if they reached the limit
                if (currentFailures >= maxRetries) {
                    // BLOCK THEM!
                    targetUser.blockUser(blockTimeInSeconds);
                    Platform.runLater(() -> {
                        errorLabel.setText("Max attempts reached! Blocked for " + blockTimeInSeconds + "s");
                    });
                } else {
                    // Just a wrong guess
                    int triesLeft = maxRetries - currentFailures;
                    Platform.runLater(() -> {
                        errorLabel.setText("Wrong password. " + triesLeft + " attempts left.");
                    });
                }
            });
            updateFailureThread.start(); // Start the thread
        }
    }

    // Helper method to keep the code clean
    private void openWelcomeScreen() {
        try {
            errorLabel.setText("Login Successful!");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Welcome-view.fxml"));
            Scene welcomeScene = new Scene(fxmlLoader.load(), 333, 333);
            Stage welcomeStage = new Stage();
            welcomeStage.setTitle("Welcome");
            welcomeStage.setScene(welcomeScene);

            // Close login window
            Stage loginStage = (Stage) errorLabel.getScene().getWindow();
            loginStage.close();

            welcomeStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



