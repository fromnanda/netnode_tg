package com.fatec.fernanda.appredes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fatec.fernanda.appredes.dao.FirebaseHelper;
import com.fatec.fernanda.appredes.R;
import com.fatec.fernanda.appredes.models.Topico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuTopicosActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    ListView topicosList;

    DatabaseReference databaseRef;
    DatabaseReference topicosConteudoRef;
    DatabaseReference topicosRef;
    DatabaseReference umTituloRef;

    FirebaseHelper helper;

    int idConteudo;

    ArrayList<String> arrayStringTopicos;
    ArrayList<String> titulosTopicos;

    Topico topico;

    ArrayList<Topico> arrayTopicos;


    //TODO ALTERAR PARAMETODO ONLOAD?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_topicos);

        topicosList = (ListView) findViewById(R.id.lstMenuTopicos);

        //RECEBENDO O ID DO CONTEUDO
        Intent originIntent = getIntent();
        idConteudo = originIntent.getExtras().getInt("idConteudo");

        //SETUP FIREBASE
        databaseRef = FirebaseDatabase.getInstance().getReference();

        topicosConteudoRef = databaseRef.child("conteudos").child(String.valueOf(idConteudo)).child("topicos");

        helper = new FirebaseHelper(topicosConteudoRef);

        topicosRef = databaseRef.child("topicos");


        ////TESTANDO MENU
        arrayTopicos = new ArrayList<>();
        arrayStringTopicos = new ArrayList<>();

        topicosConteudoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    arrayStringTopicos.add(ds.getKey().toString());
                }

                //PEGANDO TITULO
                titulosTopicos = new ArrayList<>();
                arrayTopicos = new ArrayList<>();

                for (String topicoCorrente : arrayStringTopicos) {
                    umTituloRef = topicosRef.child(topicoCorrente).child("tituloTopico");

                    umTituloRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            titulosTopicos.add(dataSnapshot.getValue(String.class));

                            topico = new Topico();
                            //topico.setId(Integer.parseInt(dataSnapshot.getKey()));
                            topico.setTitulo(dataSnapshot.getValue(String.class));
                            arrayTopicos.add(topico);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } //

                adapter = new ArrayAdapter<>(MenuTopicosActivity.this, android.R.layout.simple_list_item_1, titulosTopicos);
                topicosList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        topicosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent topicoIntent = new Intent(MenuTopicosActivity.this, TopicoActivity.class);


                //TODO enviar o nome "chave" do titulo

                topicoIntent.putExtra("idTopico", i);

                MenuTopicosActivity.this.startActivity(topicoIntent);


            }
        });
    }
}
