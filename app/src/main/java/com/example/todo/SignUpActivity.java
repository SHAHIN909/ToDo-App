package com.example.todo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Base64;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    CircleImageView dp;
    int SELECT_PICTURE = 200;
    CheckBox remember;
    boolean rem = false, chooseimg = false;
    Button signup;
    EditText email, pass;
    String id = "";
    private FirebaseAuth mAuth;
    SharedPreferences sh;
    SharedPreferences.Editor e;


    protected  void onStart(){
          super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
             startActivity(new Intent(SignUpActivity.this,TodoList.class));
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = findViewById(R.id.femail);
        pass = findViewById(R.id.fpass);
        dp = findViewById(R.id.dpimg);
        remember = findViewById(R.id.ckbRem);
        signup = findViewById(R.id.btnsignup);
        mAuth = FirebaseAuth.getInstance();
        sh = getSharedPreferences("imagedb", MODE_PRIVATE);
        e = sh.edit();
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
                chooseimg = true;
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String Pass = pass.getText().toString().trim();
                String err = "";
                if (!isValid(Email)) err += "Invalid email!\n";
                if (!validPass(Pass)) err += "Invalid Password!\n";
                if (remember.isChecked() == true) rem = true;
                if (chooseimg == false) err += "Pleasae Select an Image!\"";

                if (!err.isEmpty()) {

                    showErrorDialog(err);

                } else {
                    id += System.currentTimeMillis();

                    mAuth.createUserWithEmailAndPassword(Email, Pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                                        startActivity(new Intent(SignUpActivity.this, TodoList.class));

                                        Toast.makeText(SignUpActivity.this,"User Created !!",Toast.LENGTH_SHORT).show();
                                     } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }


            }
        });
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    boolean validPass(String pass) {
        boolean ok = true;
        int n = pass.length();
        ok &= (n > 3 && n < 9);
        ok &= pass.matches("[0-9]+");
        return ok;
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    dp.setImageURI(selectedImageUri);
                    try {
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
                        // initialize byte stream
                        ByteArrayOutputStream stream=new ByteArrayOutputStream();
                        // compress Bitmap
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                        // Initialize byte array
                        byte[] bytes=stream.toByteArray();
                        // get base64 encoded string
                       String sImage="";
                       sImage =Base64.encodeToString(bytes,Base64.DEFAULT);
                        // set encoded text on textview
                        e.putString("image",sImage);
                        e.commit();
                        Toast.makeText(SignUpActivity.this,"BITMAP IMAGE "+sImage.length(),Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Bitmap uriToBitmap(Uri imageUri) {
        try {
            // Get the ContentResolver
            ContentResolver contentResolver = getContentResolver();

            // Open an InputStream from the Uri using the ContentResolver
            InputStream inputStream = contentResolver.openInputStream(imageUri);

            // Decode the InputStream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Close the InputStream
            inputStream.close();

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle errors gracefully
        }
    }
}