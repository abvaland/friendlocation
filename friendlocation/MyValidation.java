package com.example.ajay.friendlocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ajay on 01-02-2017.
 * fuctionality : provide validation
 */

public class MyValidation {

    static boolean  isEmpty(String s)
    {
        if(s.equals(""))
        {
            return true;
        }
        return false;
    }
    static boolean isValidEmail(String email)
    {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    static boolean isValidPassword(String pass)
    {
        if(pass!=null && pass.length()>=6)
        {
            return true;
        }
        return false;
    }
    static boolean isValidPincode(String pincode)
    {
        if(pincode.matches("[0-9]*") && pincode.length()>=6)
        {
            return true;
        }
        return false;
    }
    static boolean isValidMobile(String mobile)
    {
        if(mobile.length()==10 && mobile.matches("[0-9]*"))
        {
            return true;
        }
        return false;
    }

}
