package it.polimi.ingsw.common.util;

/** Contains useful functions for strings */
public class StringUtils
{
    /** Returns whether or not a string is all composed of digits
     * @param string Input string
     * @return Whether or not the string is composed of all digits
     */
    public static boolean isDigitString(String string)
    {
        boolean isAllDigits = !string.isBlank();

        for(char c : string.toCharArray())
        {
            if(!isAllDigits) break;

            if(!Character.isDigit(c)) isAllDigits = false;
        }

        return isAllDigits;
    }
}
