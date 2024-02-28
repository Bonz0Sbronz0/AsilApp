package it.uniba.dib.sms232413.object;

import android.os.Parcel;

import it.uniba.dib.sms232413.Database.DBUtenti;

public class Paziente extends Profilo {
    private String birthdate, placeBirth, nationality,emailDoc, idDoc, docFoldRef;



    public Paziente(String id, String name, String surname, String gender, String birthdate, String placeBirth,
                    String nationality, String receptioncenter, String email,
                    String telefono, String emailDoc, String idDoc, String password) {

        super(id, name, surname, gender, receptioncenter, email, telefono, password, "user");
        this.birthdate = birthdate;
        this.placeBirth = placeBirth;
        this.nationality = nationality;
        this.emailDoc= emailDoc;
        this.idDoc = idDoc;
       setDocFoldRef(new DBUtenti().createDefaultPatientDocFolder(id));
    }

    public Paziente(){super();}

    protected Paziente(Parcel in) {
        super(in);
        birthdate = in.readString();
        placeBirth = in.readString();
        nationality = in.readString();
        emailDoc = in.readString();
        idDoc = in.readString();
        docFoldRef = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(birthdate);
        dest.writeString(placeBirth);
        dest.writeString(nationality);
        dest.writeString(emailDoc);
        dest.writeString(idDoc);
        dest.writeString(docFoldRef);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Paziente> CREATOR = new Creator<>() {
        @Override
        public Paziente createFromParcel(Parcel in) {
            return new Paziente(in);
        }

        @Override
        public Paziente[] newArray(int size) {
            return new Paziente[size];
        }
    };


    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPlaceBirth() {
        return placeBirth;
    }

    public void setPlaceBirth(String placeBirth) {
        this.placeBirth = placeBirth;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmailDoc() {
        return emailDoc;
    }

    public void setEmailDoc(String emailDoc) {
        this.emailDoc = emailDoc;
    }

    public String getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc;
    }

    public String getDocFoldRef() {
        return docFoldRef;
    }

    public void setDocFoldRef(String docFoldRef) {
        this.docFoldRef = docFoldRef;
    }
}
