package it.uniba.dib.sms232413.object;

import com.journeyapps.barcodescanner.ScanOptions;

public class QrCodeReader extends ScanOptions{
    public void setQrCodeReader(){
        this.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        this.setPrompt("SCAN PATIENT QRCODE");
        this.setCameraId(0);
        this.setBeepEnabled(false);
        this.setOrientationLocked(false);
        this.setBarcodeImageEnabled(true);
    }
}
