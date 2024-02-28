package it.uniba.dib.sms232413.object;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;
    public final static int MESSAGE_READ = 2;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Ottieni i flussi di input e output, utilizzando oggetti temporanei perché
        // i flussi dei membri sono definitivi
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];  // memoria buffer per il flusso
        int bytes; // byte restituiti da read()
        // Continua ad ascoltare InputStream finché non si verifica un'eccezione
        while (true) {
            try {
                // Leggi da InputStream
                bytes = mmInStream.available();
                if(bytes != 0) {
                    buffer = new byte[1024];
                    SystemClock.sleep(100); //mettere in pausa e attendere il resto dei dati. Regolare questo a seconda della velocità di invio.
                    bytes = mmInStream.available(); // quanti byte sono pronti per essere letti?
                    bytes = mmInStream.read(buffer, 0, bytes); // registrare quanti byte abbiamo effettivamente letto
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget(); // Invia i byte ottenuti all'attività dell'interfaccia utente
                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }
    }

    // Chiamato dall'attività principale per inviare i dati al dispositivo remoto
    public void write(String input) {
        byte[] bytes = input.getBytes();           //converte la stringa inserita in byte
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    // Chiamato dall'attività principale per spegnere la connessione
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
