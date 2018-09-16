package com.kvm.automaticattendancemarker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kvm.automaticattendancemarker.utilities.crypt.AESCrypt;
import com.kvm.automaticattendancemarker.vipers.tasks.SaveQrOnCard;

public class LoginPageChooseProfile extends AppCompatActivity
{
    //////////////////////////////////////////////////////////////////////////////// Google Stuff
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount mGoogleAccount;
    private int RC_SIGN_IN = 13;
    //////////////////////////////////////////////////////////////////////////////// Google Stuff

    //////////////////////////////////////////////////////////////////////////////// Page Elements
    private LinearLayout chooseLayout;
    private LinearLayout retryLayout;
    //////////////////////////////////////////////////////////////////////////////// Page Elements

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page_choose_profile);

        toAgencyFB();
        setRefsAndListeners();


        //////////////////////////////////////// It's Google time
        setUpSignIn();
        runSignIn();
        //////////////////////////////////////// It's Google time
    }





    View.OnClickListener onChoosingProfile = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent move;
            String sharedPrefFileName = getResources().getString(R.string.shared_pref_app_pref);
            String appMode = getResources().getString(R.string.shared_pref_app_pref_mode);
            SharedPreferences sharedPref = getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();


            if(v.getId() == R.id.login_page_choose_professor)
            {
                sharedPrefEditor.putString(appMode, "professor");
                sharedPrefEditor.apply();
                move = new Intent(LoginPageChooseProfile.this, ProfessorMenu.class); ////////// Change to new class
                move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(move);
            }

            if(v.getId() == R.id.login_page_choose_manager)
            {
                Toast.makeText(LoginPageChooseProfile.this, "Feature Coming Soon...", Toast.LENGTH_SHORT).show();

//                sharedPrefEditor.putString(appMode, "manager");
//                sharedPrefEditor.apply();
//                move = new Intent(LoginPageChooseProfile.this, ManagerMenu.class);
//                move.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(move);
            }
        }
    };











    //////////////////////////////////////////////////////////////////////////////// Google Stuff
    private void setUpSignIn()
    {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    private void runSignIn()
    {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result)
    {
        System.out.println("SignIn Success: " + result.isSuccess());
        if(result.isSuccess())
        {
            try
            {
                mGoogleAccount = result.getSignInAccount();
                getDataFromAccountAndStore();
                String nitroNation = "ViperGTSR2015";
                String stringQR = buildStringFromGoogleData();
                String nitroQR = AESCrypt.encrypt(nitroNation, stringQR);
                new SaveQrOnCard().execute(nitroQR);
                //////////////////////////////////////// Animate chooseLayout and setvisible
                chooseLayout.setVisibility(View.VISIBLE);
                retryLayout.setVisibility(View.GONE);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //////////////////////////////////////// Animate chooseLayout and setvisible
            chooseLayout.setVisibility(View.GONE);
            retryLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromAccountAndStore()
    {
        System.out.println("//////////////////////////////////////////////////");
        System.out.println(mGoogleAccount.getId());
        System.out.println(mGoogleAccount.getDisplayName());
        System.out.println(mGoogleAccount.getEmail());
        System.out.println(mGoogleAccount.getPhotoUrl());
        System.out.println("//////////////////////////////////////////////////");

        String sharedPrefFileName = getResources().getString(R.string.shared_pref_google_pref);
        String id = getResources().getString(R.string.shared_pref_google_pref_id);
        String photourl = getResources().getString(R.string.shared_pref_google_pref_photourl);
        String displayname = getResources().getString(R.string.shared_pref_google_pref_displayname);
        String email = getResources().getString(R.string.shared_pref_google_pref_email);

        SharedPreferences sharedPref = getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        try
        {
            editor.putString(photourl, mGoogleAccount.getPhotoUrl().toString());
        }
        catch (NullPointerException e)
        {
            editor.putString(photourl, "");
        }
        finally
        {
            editor.putString(id, mGoogleAccount.getId());
            editor.putString(displayname, mGoogleAccount.getDisplayName());
            editor.putString(email, mGoogleAccount.getEmail());
            editor.apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    //////////////////////////////////////////////////////////////////////////////// Google Stuff










    //////////////////////////////////////////////////////////////////////////////// Save
    private String buildStringFromGoogleData()
    {
        String tempString = "{";
        tempString += "\"displayname\":\"" + mGoogleAccount.getDisplayName() + "\",";
        tempString += "\"email\":\"" + mGoogleAccount.getEmail() + "\",";
        try
        {
            tempString += "\"photourl\":\"" + mGoogleAccount.getPhotoUrl().toString() + "\"";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            tempString += "\"photourl\":\"\"";
        }
        finally
        {
            tempString += "}";
        }

        System.out.println(tempString);

        return tempString;
    }
    //////////////////////////////////////////////////////////////////////////////// Save












    private void setRefsAndListeners()
    {
        chooseLayout = (LinearLayout) findViewById(R.id.login_page_choose_layout_choose);
        retryLayout = (LinearLayout) findViewById(R.id.login_page_choose_layout_retry);

        Button chose_professor = (Button) findViewById(R.id.login_page_choose_professor);
        Button chose_manager = (Button) findViewById(R.id.login_page_choose_manager);
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_page_choose_retry_signin_button);

        chose_professor.setOnClickListener(onChoosingProfile);
        chose_manager.setOnClickListener(onChoosingProfile);
        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                runSignIn();
            }
        });
    }

    private void toAgencyFB()
    {
        TextView lbl1 = (TextView) findViewById(R.id.login_page_choose_lbl_header);
        TextView lbl3 = (TextView) findViewById(R.id.login_page_choose_professor);
        TextView lbl4 = (TextView) findViewById(R.id.temp_id_1);
        TextView lbl5 = (TextView) findViewById(R.id.temp_id_2);
        TextView lbl31 = (TextView) findViewById(R.id.login_page_choose_manager);


        Typeface agencyfb = Typeface.createFromAsset(getAssets(), "agency_fb.ttf");
        lbl1.setTypeface(agencyfb);
        lbl3.setTypeface(agencyfb);
        lbl4.setTypeface(agencyfb);
        lbl5.setTypeface(agencyfb);
        lbl31.setTypeface(agencyfb);
    }
}