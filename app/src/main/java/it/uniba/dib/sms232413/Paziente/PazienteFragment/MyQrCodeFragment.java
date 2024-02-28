package it.uniba.dib.sms232413.Paziente.PazienteFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.Paziente;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MyQrCodeFragment extends Fragment {

    String currentLoggedPatienceId;
    ImageView myQrCode;
    TextView firstLetter, nameAndSurname;
    private Paziente pazienteCorrente;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        assert bundle != null;
        pazienteCorrente = Objects.requireNonNull(bundle.getParcelable("current_user"));

        setCurrentLoggedPatience(pazienteCorrente);
        return inflater.inflate(R.layout.my_qr_code_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myQrCode = view.findViewById(R.id.my_qr_code);
        firstLetter = view.findViewById(R.id.first_letter);
        nameAndSurname = view.findViewById(R.id.name_and_surname);

        firstLetter.setText(String.valueOf(pazienteCorrente.getName().toUpperCase().charAt(0)));
        nameAndSurname.setText(String.format("%s %s", pazienteCorrente.getName(), pazienteCorrente.getSurname()));

        createQrCode(view.getContext());
        // Disabilita i tocchi esterni al QR code
        View parentLayout = view.findViewById(R.id.fragment_qr_container);
        parentLayout.setOnTouchListener((v, event) -> {
            // Consuma l'evento di tocco per impedirne la propagazione
            return true;
        });
    }

    private void createQrCode(Context context){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(currentLoggedPatienceId, BarcodeFormat.QR_CODE, 700, 700);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(context, "Impossibile creare qrCode", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCurrentLoggedPatience(Paziente paziente) {
        this.currentLoggedPatienceId = paziente.getId();
    }
}