package com.example.saleepepe.ui.aggiunta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.saleepepe.Navigazione;
import com.example.saleepepe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class AggiuntaFragment extends Fragment implements AdapterView.OnItemSelectedListener, NumberPicker.OnScrollListener{

    FirebaseStorage storage;

    private StorageReference storageReference;


    ArrayList<String> ingredientiAl = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayList<String> procedimentoAl = new ArrayList<String>();
    ArrayAdapter<String> adapter2;

    final String[] piatti = {"ANTIPASTO", "PRIMO PIATTO", "SECONDO PIATTO", "DESSERT"};
    String id = AggiuntaFragment.GenerateRandomString.randomString(20);

    TextInputEditText titolo;
    TextInputLayout titoloL;
    ImageButton inserisciImmagine, tornaIndietro;
    Uri imageUri;
    ImageView fotopiatto;
    Button aggiuntaIngrediente, aggiuntaProcedimento, salvaRicetta;
    ListView aggiungiIngrediente, aggiungiProcedimento;
    String difficolta = null, costo = null, tipo_piatto = null, dosiS = null;
    int tempo;
    Spinner spinnerDosi, spinnerDifficolta, spinnerCosto;
    NumberPicker numberPicker;

    public AggiuntaFragment() {
    }


    public static AggiuntaFragment newInstance() {
        AggiuntaFragment fragment = new AggiuntaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_aggiunta_ricetta, container, false);
        inserisciImmagine = root.findViewById(R.id.bottonemodificaimmagine);
        titolo = root.findViewById(R.id.titolotext);
        titoloL = root.findViewById(R.id.titolotextL);
        fotopiatto = root.findViewById(R.id.fotoricetta);
        aggiuntaIngrediente = root.findViewById(R.id.button6);
        aggiungiIngrediente = root.findViewById(R.id.aggiungi_ingredienti);
        aggiuntaProcedimento = root.findViewById(R.id.button7);
        aggiungiProcedimento = root.findViewById(R.id.aggiungi_procedimento);
        aggiuntaProcedimento = root.findViewById(R.id.button7);
        spinnerDosi = root.findViewById(R.id.spinner_dosi);
        spinnerDifficolta = root.findViewById(R.id.spinner_difficolta);
        spinnerCosto = root.findViewById(R.id.spinner_costo);
        numberPicker = root.findViewById(R.id.numberPicker);
        salvaRicetta = root.findViewById(R.id.button8);

        numberPicker.setMaxValue(250);
        numberPicker.setMinValue(0);
        numberPicker.setTextSize(50);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, piatti);

        AlertDialog.Builder scelta = new AlertDialog.Builder(getContext());
        scelta.setCancelable(false);
        scelta.setTitle("Scegli il tipo di piatto");
        Spinner sp = new Spinner(getContext());
        sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sp.setAdapter(adp);
        sp.setOnItemSelectedListener(this);
        scelta.setView(sp);
        scelta.setPositiveButton("SALVA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Tipo di piatto salvato correttamente", Toast.LENGTH_SHORT).show();
            }
        });
        scelta.create().show();


        inserisciImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaImmagine();
            }
        });

        aggiuntaIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText ingrediente = new EditText(v.getContext());
                AlertDialog.Builder inserisciIngrediente = new AlertDialog.Builder(v.getContext());
                inserisciIngrediente.setMessage("INSERISCI IL NUOVO INGREDIENTE");
                inserisciIngrediente.setView(ingrediente);
                inserisciIngrediente.setPositiveButton("INSERISCI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ing = ingrediente.getText().toString().trim();
                        ingredientiAl.add(ing);
                        adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1,
                                ingredientiAl);
                        aggiungiIngrediente.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Ingrediente aggiunto", Toast.LENGTH_SHORT).show();
                    }
                });
                inserisciIngrediente.create().show();
            }
        });

        aggiuntaProcedimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText procedimento = new EditText(v.getContext());
                final AlertDialog.Builder inserisciProcedimento = new AlertDialog.Builder(v.getContext());
                inserisciProcedimento.setMessage("INSERISCI IL PROCEDIMENTO");
                inserisciProcedimento.setView(procedimento);
                inserisciProcedimento.setPositiveButton("INSERISCI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String proc = procedimento.getText().toString();
                        procedimentoAl.add(proc);
                        adapter2 = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_list_item_1,
                                procedimentoAl);
                        aggiungiProcedimento.setAdapter(adapter2);
                        adapter2.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Procedimento aggiunto", Toast.LENGTH_SHORT).show();
                    }
                });
                inserisciProcedimento.create().show();
            }
        });

        aggiungiProcedimento.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int elementoSelezionato = position;

                new AlertDialog.Builder(getContext()).setMessage("Sicuro di voler eliminare l'elemento selezionato?").
                        setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                procedimentoAl.remove(elementoSelezionato);
                                adapter2.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Procedimento eliminato correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("NO", null).create().show();

                return true;
            }
        });

        aggiungiIngrediente.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int elementoSelezionato = position;

                new AlertDialog.Builder(getContext()).setMessage("Sicuro di voler eliminare l'elemento selezionato?").
                        setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ingredientiAl.remove(elementoSelezionato);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Ingrediente eliminato correttamente", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("NO", null).create().show();

                return true;
            }
        });

        spinnerDosi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dosiS = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "Occorre selezionare la dose", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerDifficolta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficolta = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "Occorre selezionare la difficoltà", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCosto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                costo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "Occorre selezionare il costo", Toast.LENGTH_SHORT).show();
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tempo = picker.getValue();
            }
        });


        salvaRicetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titoloString = titolo.getText().toString().trim();

                if (TextUtils.isEmpty(titoloString)) {
                    if (ingredientiAl.isEmpty()) {
                        if (procedimentoAl.isEmpty()) {
                            titoloL.setError("E' richiesto il titolo");
                        }
                        Toast.makeText(getContext(), "Inserire gli ingredienti", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), "Inserire il procedimento", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> ricettaMap = new HashMap<>();
                ricettaMap.put("titolo", titoloString);
                ricettaMap.put("difficoltà", difficolta);
                ricettaMap.put("costo", costo);
                ricettaMap.put("tempo", tempo);
                ricettaMap.put("dosi", dosiS);
                ricettaMap.put("ingredienti", ingredientiAl);
                ricettaMap.put("procedimento", procedimentoAl);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(tipo_piatto).document(id).set(ricettaMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Ricetta salvata correttamente", Toast.LENGTH_LONG).show();
                        Intent back = new Intent(getContext(), Navigazione.class);
                        startActivity(back);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Ricetta non salvata", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        String item = parent.getItemAtPosition(pos).toString();

        if (item.equals("ANTIPASTO")) {
            tipo_piatto = "antipasti";
        } else if (item.equals("PRIMO PIATTO")) {
            tipo_piatto = "primi";
        } else if (item.equals("SECONDO PIATTO")) {
            tipo_piatto = "secondi";
        } else {
            tipo_piatto = "desserts";
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Occorre selezionare il tipo di piatto", Toast.LENGTH_SHORT).show();
    }

    private void cambiaImmagine() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void caricaImmagine() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Caricamento immagine...");
        pd.show();
        StorageReference imageRef = storageReference.child(tipo_piatto + "/" + id + ".jpg");

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            fotopiatto.setImageURI(imageUri);
            caricaImmagine();
        }
    }

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String backText);
    }


    //Inner class
    public static class GenerateRandomString {
        public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static Random RANDOM = new Random();

        public static String randomString(int len) {
            StringBuilder sb = new StringBuilder(len);

            for (int i = 0; i < len; i++) {
                sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
            }

            return sb.toString();
        }
    }
}