package it.uniba.dib.sms232413.object;

public class Documento {
    private String nome;
    private byte[] bytes; // array di byte per memorizzare i dati del documento

    // Costruttore vuoto
    public Documento() {
        // Costruttore vuoto
    }

    // Costruttore con parametri
    public Documento(String nome, byte[] bytes) {
        this.nome = nome;
        this.bytes = bytes;
    }

    // Metodi getter e setter per il campo "nome"
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Metodi getter e setter per il campo "bytes"
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
