package nl.bramwinter.globus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 *
 * Parts of this code is taken/inspired from Googles firebase guide
 *
 */

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText email, pw, fName, lName;
    private Button cancel, login;
    private TextView status;

    private static final String TAG = "CreateUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        status = findViewById(R.id.textViewStatus);
        email = findViewById(R.id.editTextEmail);
        pw = findViewById(R.id.editTextPW);
        fName = findViewById(R.id.editTextFirstName);
        lName = findViewById(R.id.editTextLastName);
        cancel = findViewById(R.id.buttonCancel);
        login = findViewById(R.id.buttonLogin);

        findViewById(R.id.buttonCancel).setOnClickListener(this);
        findViewById(R.id.buttonCreateUser).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {

        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String eMail = email.getText().toString();
        if (TextUtils.isEmpty(eMail)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String password = pw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pw.setError("Required.");
            valid = false;
        } else {
            pw.setError(null);
        }

        return valid;
    }

    private void cancel(){
        this.finish();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateUserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonCreateUser) {
            createAccount(email.getText().toString(), pw.getText().toString());
        }
        else if (i == R.id.buttonCancel){
            cancel();
        }
        else if(i == R.id.buttonLogin){

        }
    }
}