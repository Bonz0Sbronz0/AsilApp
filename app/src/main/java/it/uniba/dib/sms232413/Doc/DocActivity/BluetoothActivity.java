package it.uniba.dib.sms232413.Doc.DocActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.ConnectedThread;
import it.uniba.dib.sms232413.object.Paziente;
import it.uniba.dib.sms232413.object.SensorHandler;

import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_SCAN = 4;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    static final int REQUEST_DISCOVERABLE = 3;
    private static final int PRIORITY_HIGH_ACCURACY = 5;
    private static final int REQUEST_CHECK_SETTINGS = 6;
    private final Context con = this;
    Button buttonSearch;
    BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> detectedAdapter;  // Adattatori per le ListView
    private BluetoothDevice bdDevice;  // Oggetto BluetoothDevice
    private ArrayList<BluetoothDevice> arrayListBluetoothDevices;  // ArrayList di dispositivi Bluetooth rilevati
    ListView listViewDetected;
    private BluetoothSocket mBTSocket = null;
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler mHandler; //Il nostro gestore principale che riceverà le notifiche di callback
    private ConnectedThread mConnectedThread;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;
    String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_bluetooth);

        Paziente paziente = getIntent().getParcelableExtra("paziente");
        userId = paziente.getId();
        userEmail = paziente.getEmail();

        // Inizializza Firebase Auth e Firebase Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listViewDetected = findViewById(R.id.listViewDetected);
        buttonSearch = findViewById(R.id.PairedBtn);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // Inizializzazione delle variabili
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();

        // Imposta gli adattatori per le ListView
        detectedAdapter = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_single_choice);
        listViewDetected.setAdapter(detectedAdapter);
        detectedAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bluetoothAdapter == null) {
            return;
        }
        // Verifica se il Bluetooth è abilitato
        if (!bluetoothAdapter.isEnabled()) {
            // Richiedi l'autorizzazione alla posizione prima di abilitare Bluetooth
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBluetoothPermission();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBluetoothPermission();
            }
        }
        buttonSearch.setOnClickListener(v -> {
            searchForDevices();
        });
        ListItemClicked listDetectDevice = new ListItemClicked();
        listViewDetected.setOnItemClickListener(listDetectDevice);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBondStateReceiver, filter);
    }

    private void showCustomPopup(final BluetoothDevice device) {
        // Creazione del layout per la schermata popup
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.custom_popup_layout, null);

        // Creazione del popup
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);

        // Imposta il flag per disabilitare il popup al di fuori del popup stesso
        builder.setCancelable(false);

        // Creazione del dialog
        final AlertDialog dialog = builder.create();

        // Troviamo il bottone nel layout del popup
        Button closeButton = popupView.findViewById(R.id.btn_close_popup);
        Button temperatura = popupView.findViewById(R.id.temperatura);
        Button pressione = popupView.findViewById(R.id.pressione);
        Button glicemia = popupView.findViewById(R.id.glicemia);
        Button battitocardiaco = popupView.findViewById(R.id.battitocardiaco);
        Button ipertensione = popupView.findViewById(R.id.ipertensione);
        Button diabete = popupView.findViewById(R.id.diabete);
        Button tremore = popupView.findViewById(R.id.tremore);

        // Gestione dell'evento onClick per la temperatura
        temperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccata la temperatura
                Toast.makeText(getApplicationContext(), "Temperatura cliccata", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestione dell'evento onClick per la pressione
        pressione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccata la pressione
                Toast.makeText(getApplicationContext(), "Pressione cliccata", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestione dell'evento onClick per la glicemia
        glicemia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccata la glicemia
                Toast.makeText(getApplicationContext(), "Glicemia cliccata", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestione dell'evento onClick per il battito cardiaco
        battitocardiaco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccato il battito cardiaco
                Toast.makeText(getApplicationContext(), "Battito cardiaco cliccato", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestione dell'evento onClick per l'ipertensione
        ipertensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccata l'ipertensione
                Toast.makeText(getApplicationContext(), "Ipertensione cliccata", Toast.LENGTH_SHORT).show();
            }
        });

        // Gestione dell'evento onClick per il diabete
        diabete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Azioni da eseguire quando viene cliccato il diabete
                Toast.makeText(getApplicationContext(), "Diabete cliccato", Toast.LENGTH_SHORT).show();
            }
        });

        tremore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Effettua la misurazione del tremore
                Toast.makeText(getApplicationContext(), "Misurazione in corso...", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Attendere 10 secondi", Toast.LENGTH_SHORT).show();
                SensorHandler sensorHandler = new SensorHandler(BluetoothActivity.this, new SensorHandler.SensorCallback() {
                    @Override
                    public void onResult(String result) {
                        // Visualizza il popup personalizzato per chiedere all'utente se desidera salvare la misurazione
                        showMeasurementPopup(result, device);
                        dialog.dismiss();
                    }
                });
                sensorHandler.startSensor();
            }

        });

        // Aggiungiamo un listener al bottone per chiudere il popup
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dissociateDevice(device, detectedAdapter, arrayListBluetoothDevices);
                dialog.dismiss();
                finish();
            }
        });
        // Mostrare il popup
        dialog.show();
    }

    public void showMeasurementPopup(final String result, final BluetoothDevice device) {
        // Infla il layout del popup personalizzato
        View popupView = getLayoutInflater().inflate(R.layout.custom_popup_result_sensor, null);

        // Costruisci il dialog personalizzato
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
        builder.setView(popupView);

        // Imposta il flag per disabilitare il popup al di fuori del popup stesso
        builder.setCancelable(false);

        // Mostra il dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Trova i tuoi elementi UI nel layout del popup personalizzato
        // E aggiungi loro il comportamento necessario
        Button btnAccept = popupView.findViewById(R.id.btn_accept);
        Button btnDecline = popupView.findViewById(R.id.btn_decline);
        TextView risultato = popupView.findViewById(R.id.risultato);
        risultato.setText("\"" + result + "\"");

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Genera un ID casuale per il documento
                String randomId = UUID.randomUUID().toString();

                String categoria = "Tremore";
                SimpleDateFormat date = new SimpleDateFormat("dd_MM_yyyy", Locale.ITALY);
                Date now = new Date();
                String date_DD_MM_YYYY = date.format(now);

                Map<String, Object> misurazione = new HashMap<>();
                misurazione.put("id", randomId);
                misurazione.put("risultato", result);
                misurazione.put("categoria", categoria);
                misurazione.put("data", date_DD_MM_YYYY);
                misurazione.put("userEmail", userEmail);
                misurazione.put("userId", userId);

                db.collection("misurazioni").document(randomId).set(misurazione);
                Toast.makeText(BluetoothActivity.this, R.string.succAddMeasurement, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Mostra il popup delle misurazioni nuovamente dopo la chiusura del popup di accettazione
                showCustomPopup(device);
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostra il popup delle misurazioni nuovamente dopo la chiusura del popup di accettazione
                showCustomPopup(device);
                dialog.dismiss();
            }
        });
    }



    // Classe interna per gestire il clic sugli elementi
    class ListItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent == listViewDetected) {
                // Gestione del clic sugli elementi rilevati
                handleDetectedItemClick(position);
            }
        }
    }

    /**
     *
     * Metodi che gestiscono la connessione con altri dispositivi
     *
     */

    @SuppressLint("MissingPermission")
    private void handleDetectedItemClick(int position) {
        if (position >= 0 && position < arrayListBluetoothDevices.size()) {
            bdDevice = arrayListBluetoothDevices.get(position);
            String deviceName = bdDevice.getName();
            try {
                if (bdDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // Il dispositivo è già accoppiato, mostra un messaggio di Toast
                    Toast.makeText(getApplicationContext(), "Attendere un momento!", Toast.LENGTH_SHORT).show();
                    dissociateDevice(bdDevice, detectedAdapter, arrayListBluetoothDevices);
                    Toast.makeText(getApplicationContext(), "Stiamo rieseguendo l'associazione!", Toast.LENGTH_SHORT).show();
                    connectToDeviceWithPairing(bdDevice);
                } else {
                    showToast(getString(R.string.pairing) + " " + deviceName);
                    connectToDeviceWithPairing(bdDevice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("ErrorArrayList", "Posizione non valida");
        }
    }

    // Dichiarazione del BroadcastReceiver per rilevare gli eventi di accoppiamento
    private final BroadcastReceiver mBondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED) {
                    // Dispositivo accoppiato con successo, ottenere il dispositivo e mostrare un popup
                    BluetoothDevice bondedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (bondedDevice != null) {
                        showCustomPopup(bondedDevice);
                    }
                }
            }
        }
    };

    private void connectToDeviceWithPairing(final BluetoothDevice device) {
        // Assume "0000" come PIN per l'esempio (sostituisci con il tuo PIN effettivo)
        final String pin = "0000";

        // Avvia un nuovo thread per eseguire l'operazione in modo asincrono
        new Thread(() -> {
            try {
                // Accoppiamento del dispositivo con il PIN fornito
                pairDevice(device, pin);

                // Crea un socket Bluetooth e connettiti al dispositivo
                mBTSocket = createBluetoothSocket(device, BT_MODULE_UUID, pin);

                // Gestisce la presa collegata
                if (mBTSocket != null) {
                    // Istanzia un nuovo thread per la comunicazione Bluetooth
                    mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                    mConnectedThread.start();
                    // Controlla le autorizzazioni per la connessione Bluetooth
                    if (ActivityCompat.checkSelfPermission(con, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            requestBluetoothPermission();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Gestisce gli errori di connessione
                Log.e("TAG", "Errore di collegamento al dispositivo: " + e.getMessage());
            }
        }).start(); // Avvia il thread
    }


    private void pairDevice(BluetoothDevice device, String pin) {
        try {
            // Associa il dispositivo al PIN fornito
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Verifica se è richiesta l'autorizzazione per la connessione Bluetooth
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Richiede l'autorizzazione se non è stata concessa
                        requestBluetoothPermission();
                    }
                }
                // Crea l'associazione con il dispositivo
                device.createBond();
            } else {
                Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN);

                byte[] pinBytes;
                if (pin != null && !pin.isEmpty()) {
                    // Converti il PIN in array di byte
                    pinBytes = pin.getBytes(StandardCharsets.UTF_8);
                    pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pinBytes);
                }

                // Verifica se è richiesta l'autorizzazione per la connessione Bluetooth
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Richiede l'autorizzazione se non è stata concessa
                        requestBluetoothPermission();
                    }
                }
                // Invia l'intento di accoppiamento
                sendBroadcast(pairingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Gestisce eventuali eccezioni durante il processo di accoppiamento
            Log.d("Exception", "Eccezione nell'accoppiamento del dispositivo");
        }
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, UUID uuid, String pin) throws IOException {
        try {
            // Accoppiare i dispositivi con il PIN fornito
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestBluetoothPermission();
                    }
                }
                device.createBond();
            } else {
                Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.PAIRING_VARIANT_PIN);

                byte[] pinBytes;
                if (pin != null && !pin.isEmpty()) {
                    pinBytes = pin.getBytes(StandardCharsets.UTF_8);
                    pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pinBytes);
                }

                sendBroadcast(pairingIntent);
            }

            // Crea una socket
            BluetoothSocket socket;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            } else {
                // Per i dispositivi più vecchi, utilizzare il metodo standard
                socket = device.createRfcommSocketToServiceRecord(uuid);
            }

            // Connessione al dispositivo server
            socket.connect();

            return socket;

        } catch (Exception e) {
            e.printStackTrace();
            // Gestire le eccezioni
            throw new IOException("Error creating Bluetooth socket: " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void dissociateDevice(BluetoothDevice device, ArrayAdapter<String> adapter, List<BluetoothDevice> devicesList) {
        try {
            Method method = device.getClass().getMethod("removeBond");
            method.invoke(device);
            // Dopo la rimozione del bond, rimuovi l'elemento dalla lista e aggiorna l'adapter
            devicesList.remove(device);
            adapter.remove(device.getName());
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Metodi che gestiscono i permessi e l'attivazione del bluetooth
     *
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Autorizzazione Bluetooth concessa, continuare con l'abilitazione Bluetooth
                enableBluetooth();
                enableLocation();
            }
        } else if (requestCode == REQUEST_SCAN) {
            // Verifica se le autorizzazioni sono state concesse
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Autorizzazione concessa, avviare la ricerca Bluetooth
                startBluetoothDiscovery();
            } else {
                // Autorizzazione non concessa, trattare di conseguenza
                showToast(getString(R.string.warningRequestPermissions));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                // Bluetooth è abilitato correttamente, rendere il dispositivo rilevabile
                showToast(getString(R.string.enableBluetoothSucc));
                makeDeviceDiscoverable();
            } else {
                // L'utente ha annullato o non è riuscito a abilitare Bluetooth
                showToast(getString(R.string.enableBluetoothFail));
            }
        } else if (requestCode == REQUEST_DISCOVERABLE) {
            if (resultCode == RESULT_OK) {
                // Il dispositivo è ora rilevabile
                showToast(getString(R.string.deviceDiscoverableSucc));
            } else {
                // L'utente ha annullato o non è riuscito a rendere il dispositivo rilevabile
                showToast(getString(R.string.enableBluetoothFail));
            }
        }else if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // L'utente ha attivato la localizzazione, esegui le azioni necessarie
                showToast("Localizzazione attiva");
            } else {
                // L'utente ha scelto di non attivare la localizzazione, gestisci di conseguenza
                showToast("Localizzazione disattivata, attivala per utilizzare il contenitore");
            }
        }
    }

    private void makeDeviceDiscoverable() {
        // Controllare se il dispositivo è già rilevabile
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestBluetoothPermission();
            }
        }
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            // Richiesta di rilevamento del dispositivo per 300 secondi (5 minuti)
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
        }
    }

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("TAG", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(con, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestBluetoothPermission();
                    }
                }
                // Ottieni il nome del dispositivo o utilizza un fallback se non disponibile
                String deviceName = (device.getName() != null) ? device.getName() : getString(R.string.unknownDevice);
                if (!deviceName.equals(getString(R.string.unknownDevice))) {
                    // Controlla se il dispositivo è già presente nella lista
                    if (!arrayListBluetoothDevices.contains(device)) {
                        // Se non è presente, aggiungilo alla lista e all'adapter
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.add(deviceName);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void searchForDevices() {
        // Verifica se il Bluetooth è abilitato
        if (bluetoothAdapter.isEnabled()) {
            // Cancella i dispositivi precedentemente rilevati
            arrayListBluetoothDevices.clear();
            detectedAdapter.clear();
            detectedAdapter.notifyDataSetChanged();
            Toast.makeText(con, "Ricerca dispositivi in corso", Toast.LENGTH_SHORT).show();

            try {
                // Verifica le autorizzazioni Bluetooth in fase di runtime
                // Autorizzazioni concesse, avviare la ricerca Bluetooth
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestBluetoothPermission();
                    }
                }
                bluetoothAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            } catch (Exception e) {
                Log.d("ErrorsearchForDevices", e.getMessage());
            }
        }
    }

    private void startBluetoothDiscovery() {
        // Avvia la scoperta del Bluetooth
        try {
            // Verifica delle autorizzazioni Bluetooth in fase di esecuzione
            // Autorizzazioni concesse, avviare la ricerca Bluetooth
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestBluetoothPermission();
                }
            }
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        } catch (Exception e) {
            Log.d("ErrorstartBluetoothDiscovery", e.getMessage());
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestBluetoothPermission() {
        // Verifica se l'app dispone di autorizzazioni Bluetooth
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Richiedi autorizzazioni Bluetooth
            requestPermissions(new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // Autorizzazione Bluetooth già concessa, abilitare Bluetooth
            enableBluetooth();
            enableLocation();
        }
    }

    private void enableLocation() {
        com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create()
                .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 secondi
                .setFastestInterval(5 * 1000); // 5 secondi

        com.google.android.gms.location.LocationSettingsRequest.Builder builder = new com.google.android.gms.location.LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // La localizzazione è già attivata, esegui le azioni necessarie
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // La localizzazione non è attivata, ma può essere risolta mostrando un prompt all'utente
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Errore durante l'avvio dell'intent
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}