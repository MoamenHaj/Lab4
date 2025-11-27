package org.example.lab4;

public class User {
    private String username;
    private String password;
    private  int failedAttempts ; // --- track the failed attempts
    private long blockTimeEnd ; //-- track when the block expires


    // so this is a regex i took from the web for email verification called "magic regex pattern"
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9_%+-.]+@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,}$";
    // the constructor
    public User(String username,String password) throws Exception //for error handle
    {
        // this is the new variables
        this.failedAttempts = 0; // Start with 0 failures
        this.blockTimeEnd = 0;   // Not blocked
        //now we have to put the rules for validation
        //if the rules pass then we will set the fields:
        //this.username=username;
        //this.password=password;
        //username validation :
        if(username.length()>50){
            throw new Exception("Username is too long, try something shorter");
        }
        // email form check
        // i didn't get the regex that much but i understood the basic knowledge**
        if (!username.matches(EMAIL_REGEX)){
            // if the if is true then we throw exception else the email is valid
            throw new Exception("Please enter a valid Email as username ");
        }
        //======= email validation ends here ==================

        // ======password validation starts here 8888888*************888888
        if (password.length()<8){
            throw new Exception("Your password is too short, add more characters");
        }
        if (password.length()>12){
            throw new Exception("Your password is too long, try a shorter one");
        }
        //password check now
        //  allowed symbols
        String allowedSymbols = "!@#$%^&*()_+";
        // the old requirements was upper and lowe case with a symbol now its
        //  NEW requirements (letter, digit, symbol)

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            //  Check if the character is allowed
            if (Character.isLetter(c)) {
                hasLetter = true; // It's a letter
            } else if (Character.isDigit(c)) {
                hasDigit = true; // It's a digit
            } else if (allowedSymbols.indexOf(c) != -1) {
                // .indexOf() checks if the char 'c' is inside our allowedSymbols string
                hasSymbol = true; // It's one of the allowed symbols
            } else {
                // If it's not a letter, digit, or allowed symbol, it's invalid.
                //This matches the error
                throw new Exception("Please enter a valid password");
            }
        }

        //  After the loop, check if we found all required types
        // The new rule is "at least one letter, one digit, and one symbol"
        if (!hasLetter || !hasDigit || !hasSymbol) {
            throw new Exception("Please enter a valid password");
        }
        //now if all cheks (ifs) pass then:
        this.username = username;
        this.password = password;
    }
    // here we will put the getters for encapsulation
    public  String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public int getFailedAttempts() { return failedAttempts; }
    public void addFailedAttempt() {this.failedAttempts++;} // increment by 1 every failed attempt
    public void resetFailedAttempts() { // we have to reset the variables after success login
        this.failedAttempts = 0;
        this.blockTimeEnd = 0;
    }
    public boolean isBlocked() {
        // If the current time is BEFORE the blockTimeEnd, they are still blocked
        return System.currentTimeMillis() < blockTimeEnd; // we see if the time of blocked passed .
    }
    public void blockUser(int secondsToBlock) {
        // Current time + (seconds * 1000 to get milliseconds)
        this.blockTimeEnd = System.currentTimeMillis() + (secondsToBlock * 1000L);
    }
}
