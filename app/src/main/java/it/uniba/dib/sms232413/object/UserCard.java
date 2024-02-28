package it.uniba.dib.sms232413.object;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.uniba.dib.sms232413.Doc.DocActivity.UserProfileDocSideActivity;
import it.uniba.dib.sms232413.R;


public class UserCard extends PopupWindow {

    static final int WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;
    static final int HEIGHT = ViewGroup.LayoutParams.MATCH_PARENT;
    static final boolean FOCUSABLE = true;
    private final View popupView;
    private Paziente currentPatient;

    private final Activity currentContext;

    ImageButton closeButton;
    TextView nomeTextView;
    TextView cognomeTextView;
    FloatingActionButton toUserButton;



    public UserCard(View popupView, Activity currentContext) {
        super(popupView, WIDTH, HEIGHT, FOCUSABLE);
        this.popupView =popupView;
        this.currentContext=currentContext;
        setPopupField(popupView);
        clickToClose();
        goToProfile();
    }


    private void setPopupField(View popupView) {
        nomeTextView = popupView.findViewById(R.id.nome_card);
        cognomeTextView = popupView.findViewById(R.id.cognome_card);
        toUserButton = popupView.findViewById(R.id.arrow_fab_toUser);
    }

    private void goToProfile() {
        toUserButton.setOnClickListener(view->{
            Intent goToUserProfile = new Intent(popupView.getContext(), UserProfileDocSideActivity.class);
            goToUserProfile.putExtra("user_selected",currentPatient);
            popupView.getContext().startActivity(goToUserProfile);
        });
    }


    private void clickToClose() {
        closeButton = popupView.findViewById(R.id.close_userCard_circle);
        closeButton.setOnClickListener(view-> closePopupWindow());
    }

    public void closePopupWindow() {
        dismiss();
        currentContext.findViewById(R.id.qr_imageView).setVisibility(View.VISIBLE);
    }

    public void showPopup(){
        setAnimationStyle(R.style.PopupAnimation);
        showAtLocation(currentContext.findViewById(R.id.container_qrcode), Gravity.CENTER, 0,0);
    }

    public TextView getNomeTextView() {
        return nomeTextView;
    }

    public TextView getCognomeTextView() {
        return cognomeTextView;
    }

    public void setCurrentPatient(Paziente currentPatient) {
        this.currentPatient = currentPatient;
    }

    public Paziente getCurrentPatient() {
        return currentPatient;
    }
}