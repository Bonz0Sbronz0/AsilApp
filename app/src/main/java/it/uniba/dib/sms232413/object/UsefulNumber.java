package it.uniba.dib.sms232413.object;

public class UsefulNumber {
    private String name;
    private String phoneNumber;
    private String iconResourceName; // Nome del file dell'icona

    public UsefulNumber(String name, String phoneNumber, String iconResourceName) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.iconResourceName = iconResourceName;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getIconResourceName() {
        return iconResourceName;
    }
}
