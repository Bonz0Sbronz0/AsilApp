package it.uniba.dib.sms232413.object;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public abstract class Profilo implements Parcelable {
    private String id, name, surname, gender, email, phone, password, type, receptionceter;

    public Profilo(){
    }

    @Override
    public String toString() {
        return "Profilo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public Profilo(String id, String name, String surname, String gender,
                   String receptionceter, String email, String phone, String password, String type){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.receptionceter = receptionceter;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.type = type;
    }

    public Profilo(Parcel in){
        id = in.readString();
        receptionceter = in.readString();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        password = in.readString();

        gender = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(receptionceter);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(gender);
        dest.writeString(phone);
    }
    public String getType(){
        return type;
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReceptionceter() {
        return receptionceter;
    }

    public void setReceptionceter(String receptionceter) {
        this.receptionceter = receptionceter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId(){
        return id;
    }

}