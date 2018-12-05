package nl.bramwinter.globus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FrontpageActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    static final String TAG = "authentification";

    private static int RC_SIGN_IN = 9001;
    private static int REQUEST_CODE_CREATE_USER = 9002;

    private TextView statusTextView;
    private EditText emailField;
    private EditText passwordField;
    private Button logIn;
    private Button createUser;
    private SignInButton googleLogin;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);

        auth = FirebaseAuth.getInstance();

        statusTextView = findViewById(R.id.textView3);
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        logIn = findViewById(R.id.buttonLogin);
        createUser = findViewById(R.id.buttonCreate);
        googleLogin = findViewById(R.id.googlelogin);

        findViewById(R.id.googlelogin).setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void updateUIGoo(FirebaseUser user) {

        if (user != null) {

        } else {
            statusTextView.setText(R.string.signed_out);

            findViewById(R.id.googlelogin).setVisibility(View.VISIBLE);
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            ((TextView) findViewById(R.id.textView3)).setText(
                    "User ID: " + user.getUid());
            Intent intent = new Intent(this, OverviewActivity.class);
                    startActivity(intent);
                    finish();
        } else {
            ((TextView) findViewById(R.id.textView3)).setText(
                    "Error: sign in fucking failed.");
        }
    }

    public void createAccount(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FrontpageActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.googlelogin) {
            signIn();
        }
        else if(i == R.id.buttonCreate){
            createUser();
        }
        else if (i == R.id.buttonLogin){}
    }

    private void createUser(){
        Intent intent = new Intent(FrontpageActivity.this, CreateUserActivity.class);
        //startActivityForResult(intent, 9002);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
      Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

      AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
      auth.signInWithCredential(credential)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          // Sign in success, update UI with the signed-in user's information
                          Log.d(TAG, "signInWithCredential:success");
                          FirebaseUser user = auth.getCurrentUser();
                          updateUI(user);
                      } else {
                          // If sign in fails, display a message to the user.
                          Log.w(TAG, "signInWithCredential:failure", task.getException());
                          Snackbar.make(findViewById(R.id.textView3), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                          updateUI(null);
                      }

                      // ...
                  }
              });
  }

  private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        else if(requestCode == REQUEST_CODE_CREATE_USER){

        }
    }

}
