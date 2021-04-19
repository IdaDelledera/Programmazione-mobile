package com.example.saleepepe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.saleepepe.ListaVisualizzazione;
import com.example.saleepepe.R;

public class HomeFragment extends Fragment{


    CardView antipasti, primi, secondi, dessert;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        antipasti = root.findViewById(R.id.antipastiC);
        primi = root.findViewById(R.id.primiC);
        secondi = root.findViewById(R.id.secondiC);
        dessert = root.findViewById(R.id.dessertsC);

        antipasti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("antipasti");
            }
        });

       primi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("primi");
            }
        });

        secondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("secondi");
            }
        });

       dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("desserts");
            }
        });
       return root;
    }

    private void openFragment(String tipoPiatto) {
        ListaVisualizzazione fragment = ListaVisualizzazione.newInstance(tipoPiatto);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.listaVisualizzazione, fragment, "LISTAVISUALIZZAZIONE_FRAGMENT").commit();
    }

}