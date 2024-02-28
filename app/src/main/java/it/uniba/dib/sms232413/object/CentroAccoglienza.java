package it.uniba.dib.sms232413.object;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class CentroAccoglienza{

    private String nameId;
    private String nome;
    private Map<String, String> contatti, localita;
    private List<String> imgReference, rulesServices;
    public CentroAccoglienza(){
    }

    public CentroAccoglienza(String nameId, String nome, String infoutils, Map<String, String> contatti, Map<String,
            String> localita, List<String> imgReference, List<String>rulesServices) {
        this.nameId = nameId;
        this.nome = nome;
        this.contatti = contatti;
        this.localita = localita;
        this.imgReference = imgReference;
        this.rulesServices = rulesServices;
    }

    protected CentroAccoglienza(Parcel in) {
        nameId = in.readString();
        nome = in.readString();
        imgReference = in.createStringArrayList();
        rulesServices = in.createStringArrayList();
    }



    public String getNome() {
        return nome;
    }


    public List<String> getImgReference() {
        return imgReference;
    }

    public String getNameId() {
        return nameId;
    }

    public Map<String, String> getContatti() {
        return contatti;
    }

    public Map<String, String> getLocalita() {
        return localita;
    }

    public List<String> getRulesServices() {
        return rulesServices;
    }


    @NonNull
    @Override
    public String toString() {
        return nome;
    }
}
