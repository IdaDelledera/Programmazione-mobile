package com.example.saleepepe.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.saleepepe.ListaVisualizzazione;
import com.example.saleepepe.Navigazione;
import com.example.saleepepe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class VisualizzaRicetta extends ListaVisualizzazione {

    private StorageReference storageReference;
    private List<String> ingredientiL = new ArrayList<>();
    private List<String> procedimentoL = new ArrayList<>();
    private OnFragmentInteractionListener mListener;


    ArrayAdapter<String> adapterIngredienti;
    ArrayAdapter<String> adapterProcedimento;

    FirebaseFirestore db;
    FirebaseStorage storage;

    ScrollView scrollView;
    TextView titolo, difficolta, costo, tempo, dosi;
    DocumentReference docref;
    ImageView fotopiatto;
    ListView procedimento, ingredienti;
    ImageButton tornaIdetro;

    String idRicetta, tipoPiatto;


    public VisualizzaRicetta() {
    }

    public static VisualizzaRicetta newInstance(String idRicetta, String tipoPiatto) {
        VisualizzaRicetta fragment = new VisualizzaRicetta();
        Bundle args = new Bundle();
        args.putString("idRicetta", idRicetta);
        args.putString("tipoPiatto", tipoPiatto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idRicetta = getArguments().getString("idRicetta");
            tipoPiatto = getArguments().getString("tipoPiatto");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_visualizza_ricetta, container, false);

        scrollView = root.findViewById(R.id.scrollvr);
        titolo = root.findViewById(R.id.titolodelpiatto);
        dosi = root.findViewById(R.id.dosi);
        costo = root.findViewById(R.id.costo);
        difficolta = root.findViewById(R.id.difficolta);
        tempo = root.findViewById(R.id.tempo);
        fotopiatto = root.findViewById(R.id.fotodelpiatto);
        procedimento = root.findViewById(R.id.visualizza_procedimento);
        ingredienti = root.findViewById(R.id.visualizza_ingredienti);
        tornaIdetro = root.findViewById(R.id.tornaIndietro);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        docref = db.collection(tipoPiatto).document(idRicetta);

        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        titolo.setText(document.get("titolo").toString());
                        costo.setText(document.get("costo").toString());
                        dosi.setText(document.get("dosi").toString());
                        difficolta.setText(document.get("difficoltà").toString());
                        tempo.setText(document.get("tempo").toString());
                        storageReference.child(tipoPiatto + "/" + idRicetta + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Glide.with(getContext()).load(imageUrl).into(fotopiatto);
                            }
                        });
                        ingredientiL = (List<String>) document.get("ingredienti");
                        adapterIngredienti = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1,
                                ingredientiL);
                        ingredienti.setAdapter(adapterIngredienti);
                        adapterIngredienti.notifyDataSetChanged();

                        procedimentoL = (List<String>) document.get("procedimento");
                        adapterProcedimento = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1,
                                procedimentoL);
                        procedimento.setAdapter(adapterProcedimento);
                        adapterProcedimento.notifyDataSetChanged();
                    }
                }
            }
        });

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

        tornaIdetro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getContext(), Navigazione.class);
                startActivity(back);
            }
        });
        return root;
    }
}