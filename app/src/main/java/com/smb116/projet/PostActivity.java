package com.smb116.projet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.DatabaseMetaData;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;

    private ImageButton selectedImage;
    private EditText title;
    private EditText content;

    private Uri imageUri = null;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        selectedImage = (ImageButton) findViewById(R.id.imageButton);
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        progressDialog = new ProgressDialog(this);
    }

    public void onClickImage(View v){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            selectedImage.setImageURI(imageUri);
        }
    }

    public void onClickSend(View v){
        post();
    }

    private void post() {
        progressDialog.setMessage("Envoi en cours...");
        progressDialog.show();
        final String titleText = title.getText().toString().trim();
        final String contentText = content.getText().toString().trim();

        if(!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(contentText)){
            StorageReference filePath = storageReference.child("Blog_Images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Task downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    downloadUrl.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            DatabaseReference newPost = databaseReference.push();  //creates unique random id
                            newPost.child("title").setValue(titleText);
                            newPost.child("content").setValue(contentText);
                            newPost.child("imageUrl").setValue(downloadUrl.getResult().toString());
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "envoi réussi", Toast.LENGTH_LONG).show();

                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });

                }
            });
        }
    }
}
