package com.example.tasktide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MinhaConta extends AppCompatActivity {

    private TextView txtNomeUsuario;
    private TextView txtHorasUsuario;
    private TextView txtCargoUsuario;
    private TextView txtEmailUsuario;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        txtHorasUsuario = findViewById(R.id.txtHorasUsuario);
        txtCargoUsuario = findViewById(R.id.txtCargoUsuario);
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario);
        imgView = findViewById(R.id.imgPerfilMinhaConta);

        carregarDadosUsuario();

    }

    private void carregarDadosUsuario() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String nome = prefs.getString("nome", "Nome não disponível");
        String horas = prefs.getString("horas", "0 horas contabilizadas");
        String cargo = prefs.getString("cargo", "Cargo não disponível");
        String email = prefs.getString("email", "Email não disponível");
        String uriString = prefs.getString("fotoPerfil", null);

        txtNomeUsuario.setText(nome);
        txtHorasUsuario.setText(horas + " contabilizadas");
        txtCargoUsuario.setText(cargo);
        txtEmailUsuario.setText(email);

        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            Glide.with(this).load(imageUri).into(imgView);
        } else {
            imgView.setImageResource(R.drawable.usuario_perfil);
        }
    }

    public void manual(View view) {
        Intent in = new Intent(this, Manual.class);
        startActivity(in);
    }


    public void meuscertificados(View view) {
        Intent in = new Intent(this, MeusCertificados.class);
        startActivity(in);
    }

    public void configuracoes(View view) {
        Intent in = new Intent(this, Configuracoes.class);
        startActivity(in);
    }

    public void sobrenos(View view) {
        Intent in = new Intent(this, SobreNos.class);
        startActivity(in);
    }

    public void inicial(View view) {
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }

    public void localizacao(View view) {
        Intent in = new Intent(this, Localizacao.class);
        startActivity(in);
    }

    public void addevento(View view) {
        Intent in = new Intent(this, CriarEvento.class);
        startActivity(in);
    }

    public void perfil(View view) {
        Intent in = new Intent(this, MinhaConta.class);
        startActivity(in);
    }

    public void meuseventos(View view) {
        Intent in = new Intent(this, MeusEventosCriador.class);
    }
}