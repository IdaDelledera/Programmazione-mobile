package com.example.saleepepe;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.example.saleepepe.ui.VisualizzaRicetta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;



public class ListaVisualizzazione extends Fragment {


    FirebaseFirestore db;
    ListAdapter listAdapter = new ListAdapter(this.getContext());

    ArrayAdapter<String> listafiltrata;

    ArrayList<String> titoli = new ArrayList<>();
    ArrayList<String> idL = new ArrayList<>();

    ArrayList<String> idResultData = new ArrayList<>();

    ArrayList<String> resultsData = new ArrayList<>();

    ListView visualizzaTitoli;
    EditText ricerca;
    String tipoPiatto, titolo, id, idRicetta;
    private OnFragmentInteractionListener mListener;

    public ListaVisualizzazione() {

    }

    public static ListaVisualizzazione newInstance(String tipoPiatto) {
        ListaVisualizzazione fragment = new ListaVisualizzazione();
        Bundle args = new Bundle();
        args.putString("tipoPiatto", tipoPiatto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipoPiatto = getArguments().getString("tipoPiatto");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_lista_visualizzazione, container, false);

        visualizzaTitoli = root.findViewById(R.id.listview);
        ricerca = root.findViewById(R.id.ricerca);

        db = FirebaseFirestore.getInstance();


        db.collection(tipoPiatto).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        titolo = (String) doc.get("titolo");
                        id = doc.getId();
                        titoli.add(titolo);
                        idL.add(id);
                    }
                    visualizzaTitoli.setAdapter(listAdapter);
                }
            }
        });

        visualizzaTitoli.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idRicetta = idL.get(position);
                openFragment(idRicetta, tipoPiatto);
            }

        });

        ricerca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                listAdapter.getFilter().filter(s.toString());
            }
        });

        return root;
    }

    private void openFragment(String id, String tipoPiatto) {
        VisualizzaRicetta fragment = VisualizzaRicetta.newInstance(id, tipoPiatto);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.visualizzaRicettaFragment, fragment, "VISUALIZZARICETTA_FRAGMENT").commit();
    }


    public void sendBack(String backText) {
        if (mListener != null) {
            mListener.onFragmentInteraction(backText);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String backText);
    }


    public class ListAdapter extends BaseAdapter implements Filterable {

        Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return titoli.size();
        }

        @Override
        public Object getItem(int position) {
            return titoli.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.textviewlist, null);
            TextView titoloRicetta = view.findViewById(R.id.titoloricetta);
            titoloRicetta.setText(titoli.get(position));
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = titoli;
                        results.count = resultsData.size();
                        resultsData.clear();
                        resultsData.addAll(titoli);
                    } else {
                        idResultData.clear();
                        String searchStr = constraint.toString().toUpperCase();
                        for (String o : titoli) {
                            if (o.toUpperCase().startsWith(searchStr)) {
                                if(resultsData.contains(o)){
                                    if(ricerca.length()==0)
                                        results.values = titoli;

                                    resultsData.clear();
                                    break;
                                }
                                resultsData.add(o);
                                idResultData.add(idL.get(titoli.indexOf(o)));
                                results.values = o;
                                results.count = resultsData.size();
                            }
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    listafiltrata = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1,
                            resultsData);
                    visualizzaTitoli.setAdapter(listafiltrata);
                    listafiltrata.notifyDataSetChanged();
                    notifyDataSetChanged();

                    visualizzaTitoli.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            idRicetta = idResultData.get(position);
                            openFragment(idRicetta, tipoPiatto);
                        }

                    });
                }
            };
        }
    }

}