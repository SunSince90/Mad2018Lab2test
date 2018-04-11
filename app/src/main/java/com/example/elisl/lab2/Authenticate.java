package com.example.elisl.lab2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class Authenticate extends AppCompatActivity {

    CallbackManager manager = null;

    private FirebaseAuth Auth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        Auth = FirebaseAuth.getInstance();

        manager = CallbackManager.Factory.create();
        LoginButton button = (LoginButton) findViewById(R.id.login_button);
        button.setReadPermissions("email", "public_profile");
        button.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FBLOGIN", "Login is successful:" + loginResult);
                onFacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FBLOGIN", "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FBLOGIN", "facebook:onError", error);
            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = Auth.getCurrentUser();

        if(currentUser == null) Log.d("FIREBASEAUTH", "User not authenticated");
        else Log.d("FIREBASEAUTH", "User is authenticated as " + currentUser.getDisplayName());
    }

    private void onFacebookLogin(AccessToken token) {
        Log.d("FBLOGIN", "Got token from facebook:" + token);

        GraphRequest gr = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {

            @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        Log.d("FBLOGIN", "GRAPHREQUEST COMPLETED!");
                        if(response.getError() != null )
                        {
                            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            String name = object.getString("birthday");
                            Log.d("FBLOGIN", "I Got the name, it is: " + name);
                        }
                        catch (JSONException j)
                        {
                            Toast.makeText(getApplicationContext(), "Exception!", Toast.LENGTH_SHORT).show();
                        }
                        //Log.d("FBLOGIN", "birthday " + object.getString("name"));


                        /*CharSequence text = "Hello toast!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();*/


                        //if(response.getError().getErrorCode() == 200)
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "public_profile");
        gr.setParameters(parameters);
        gr.executeAsync();
        //AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        /*Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FBLOGIN", "Login Successful,.............................");
                            FirebaseUser user = Auth.getCurrentUser();

                            Log.d("USER", "email: " + user.getEmail() + ", displayName: " + user.getDisplayName() + ", phone: " + user.getPhoneNumber());
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FBLOGIN", "Login not successful", task.getException());
                            Toast.makeText(Authenticate.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        manager.onActivityResult(requestCode, resultCode, data);
    }
}
