package it.uniba.dib.sms232413.object;

public class Misurazioni {
    public String id, categoria, data, risultato, userid, userEmail;

    public Misurazioni(String id, String data, String categoria, String risultato, String userid, String userEmail) {
        this.id = id;
        this.data = data;
        this.categoria = categoria;
        this.risultato = risultato;
        this.userid = userid;
        this.userEmail = userEmail;
    }

    public Misurazioni(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRisultato() {
        return risultato;
    }

    public void setRisultato(String risultato) {
        this.risultato = risultato;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
