package helper;

/**
 * @author ben
 * Static class designed to standardize the way text gets cleaned. 
 * This class cannot be extended and instantiated.
 */

public final class CleanText {
    
    private CleanText() {
        //empty private constructor. This cannot be instantiated.
    }
    
    public static String Standardize(String input) {
        String output = input.toLowerCase();
        return output;
    }
}
