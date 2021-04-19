package com.example.saleepepe.ui.profilo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.saleepepe.Login;
import com.example.saleepepe.R;
import com.example.saleepepe.ui.aggiunta.AggiuntaFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class ProfiloFragment extends Fragment implements AggiuntaFragment.OnFragmentInteractionListener{


    public static final int CAMERA_PERM_CODE = 101;

    FirebaseFirestore db;
    FirebaseStorage storage;
    Uri imageUri;
    ImageView fotoprofilo;
    Button modificaPassword;
    ImageButton modificaImmagine, bottonelogout, scattaImmagine;
    TextView nomeutente, email;
    String id;

    private StorageReference storageReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        fotoprofilo = root.findViewById(R.id.fotoprofilo);
        modificaImmagine = root.findViewById(R.id.bottonemodifica);
        scattaImmagine = root.findViewById(R.id.scattafoto);
        bottonelogout = root.findViewById(R.id.bottonelogout);
        nomeutente = root.findViewById(R.id.nomeutente);
        email = root.findViewById(R.id.email);
        modificaPassword = root.findViewById(R.id.modifica_password);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.getEmail().equals(document.getId())) {
                                    id = document.getId();
                                    email.setText(document.getId());
                                    nomeutente.setText(document.get("username").toString());
                                    storageReference.child("images/" + id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            Glide.with(getContext()).load(imageUrl).into(fotoprofilo);
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


        bottonelogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(getContext(), Login.class));
                Toast.makeText(getContext(), "Logout effettuato", Toast.LENGTH_SHORT).show();
            }
        });

        modificaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaImmagine();

            }
        });

        scattaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chiediPermessiFotocamera();
            }


        });

        modificaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());
                AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
                passwordreset.setTitle("MODIFICA PASSWORD");
                passwordreset.setMessage("Inserisci la nuova password: ");
                passwordreset.setView(resetPassword);
                passwordreset.setPositiveButton("SALVA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String psw = resetPassword.getText().toString();
                        user.updatePassword(psw).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Password modificata correttamente.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                Toast.makeText(getContext(), "Password non modificata.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), "Effettua nuovamente il login e riprova.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                                startActivity(new Intent(getContext(), Login.class));
                            }
                        });

                    }
                });
                passwordreset.create().show();
            }
        });

        return root;
    }

    private void chiediPermessiFotocamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            apriFotocamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                apriFotocamera();
            } else {
                Toast.makeText(getContext(), "Richiesta autorizzazione fotocamera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void apriFotocamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_PERM_CODE);
    }


    private void cambiaImmagine() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        Toast.makeText(getContext(), "Immagine del profilo modificata", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            fotoprofilo.setImageURI(imageUri);
            caricaImmagine();
        }

        if (requestCode == CAMERA_PERM_CODE) {
            assert data != null;
            Bitmap image = (Bitmap) data.getExtras().get("data");
            fotoprofilo.setImageBitmap(image);
            uploadImageToFirebase(image);
        }
    }

    private void uploadImageToFirebase(Bitmap image) {
        final StorageReference imageReference = storageReference.child("images/" + id + ".jpg");
        fotoprofilo.setDrawingCacheEnabled(true);
        fotoprofilo.buildDrawingCache();
        image = ((BitmapDrawable) fotoprofilo.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Immagine non modificata", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Immagine modificata correttamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void caricaImmagine() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Caricamento immagine...");
        pd.show();

        StorageReference imageRef = storageReference.child("images/" + id + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Impossibile caricare immagine", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onFragmentInteraction(String backText) {
        getActivity().onBackPressed();
    }

}


