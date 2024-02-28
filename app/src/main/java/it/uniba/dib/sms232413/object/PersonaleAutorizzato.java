package it.uniba.dib.sms232413.object;

import android.os.Parcel;

public class PersonaleAutorizzato extends Profilo {
    private String address;

    public PersonaleAutorizzato(String id, String name_center, String name, String gender, String surname,
                                String email, String password, String address, String phone) {
        super(id, name, surname, gender, name_center, email, phone, password, "doc");
            this.address = address;
    }

    public PersonaleAutorizzato(){super();}

    protected PersonaleAutorizzato(Parcel in) {
            super(in);
            address = in.readString();
    }


    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
            return 0;
    }

    public static final Creator<PersonaleAutorizzato> CREATOR = new Creator<>() {
        @Override
        public PersonaleAutorizzato createFromParcel(Parcel in) {
            return new PersonaleAutorizzato(in);
        }

        @Override
        public PersonaleAutorizzato[] newArray(int size) {
            return new PersonaleAutorizzato[size];
        }
    };

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }



}
