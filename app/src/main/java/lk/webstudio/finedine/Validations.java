package lk.webstudio.finedine;

public class Validations {
    public boolean isValidSriLankanMobile(String number) {
        return number.matches("^(070|071|072|074|075|076|077|078)\\d{7}$");
    }

}
