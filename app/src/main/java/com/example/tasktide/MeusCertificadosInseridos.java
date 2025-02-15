package com.example.tasktide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tasktide.DAO.DAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MeusCertificadosInseridos extends Activity {

    private static final int PICK_PDF_REQUEST = 1;
    private EditText editTextNomeCertificado, editTextHoras, editTextData;
    private Spinner spinnerTipoCertificado;
    private TextView txtSelectedFile;
    private String selectedPdfPath = null;
    private DAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_certificados_inseridos);

        editTextNomeCertificado = findViewById(R.id.editTextNomeCertificado);
        editTextHoras = findViewById(R.id.editTextHoras);
        editTextData = findViewById(R.id.editTextData);
        spinnerTipoCertificado = findViewById(R.id.spinnerTipoCertificado);
        txtSelectedFile = findViewById(R.id.txt_selected_file);

        Button btnSelecionarPDF = findViewById(R.id.btnSelecionarPDF);
        Button btnAdicionarCertificado = findViewById(R.id.btnAdicionarCertificado);

        dao = new DAO(this);

        editTextData.addTextChangedListener(new IncluirMascara(editTextData));
        editTextHoras.addTextChangedListener(new HorarioTextWatcher(editTextHoras));

        btnSelecionarPDF.setOnClickListener(v -> selectPdfFile());

        btnAdicionarCertificado.setOnClickListener(v -> insertCertificate());

        editTextData.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nenhuma ação necessária aqui
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nenhuma ação necessária aqui
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                isUpdating = true;

                String cleanText = s.toString().replaceAll("[^\\d]", ""); // Remove caracteres não numéricos
                String formattedText = "";

                // Aplica formatação progressiva para data
                int length = cleanText.length();
                if (length >= 2) {
                    formattedText = cleanText.substring(0, 2) + "/";
                    if (length >= 4) {
                        formattedText += cleanText.substring(2, 4) + "/";
                        if (length > 4) {
                            formattedText += cleanText.substring(4, Math.min(length, 8));
                        }
                    } else {
                        formattedText += cleanText.substring(2);
                    }
                } else {
                    formattedText = cleanText;
                }

                // Atualiza o campo com o texto formatado
                currentText = formattedText;
                editTextData.setText(formattedText);
                editTextData.setSelection(formattedText.length()); // Coloca o cursor no final
                isUpdating = false;
            }
        });
    }

    private void selectPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Selecione um arquivo PDF"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedPdfPath = uri.getPath();
            txtSelectedFile.setText(selectedPdfPath != null ? selectedPdfPath : "Arquivo não identificado");
        }
    }

    private void insertCertificate() {
        String nomeCertificado = editTextNomeCertificado.getText().toString().trim();
        String tipoCertificado = spinnerTipoCertificado.getSelectedItem().toString();
        String dataEmissao = editTextData.getText().toString().trim();
        String horasCertificado = editTextHoras.getText().toString().trim();

        // Usando formatarData() aqui
        String dataFormatada = formatarData(dataEmissao);

        // Verifica se a data foi formatada corretamente
        if (dataFormatada.equals("Data inválida")) {
            Toast.makeText(this, "Formato de data inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nomeCertificado.isEmpty() || tipoCertificado.isEmpty() || dataEmissao.isEmpty() || horasCertificado.isEmpty() || selectedPdfPath == null) {
            Toast.makeText(this, "Preencha todos os campos e selecione um arquivo PDF!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Valida e formata a data para o formato "dd/MM/yyyy"
        SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdfEntrada.parse(dataFormatada);  // Tenta parsear a data
            if (date == null) {
                throw new Exception("Data inválida");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Formato de data inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Agora lidamos com as horas, conforme a modificação que fizemos anteriormente
        double horasInseridas = 0.0;

        // Validar se é no formato HH:mm
        if (horasCertificado.contains(":")) {
            String[] partes = horasCertificado.split(":");
            if (partes.length == 2) {
                try {
                    int horas = Integer.parseInt(partes[0]);
                    int minutos = Integer.parseInt(partes[1]);

                    // Validar intervalo de horas e minutos
                    if (horas < 0 || horas > 23 || minutos < 0 || minutos > 59) {
                        Toast.makeText(this, "Horas ou minutos fora do intervalo permitido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    horasInseridas = horas + (minutos / 60.0); // Conversão para decimal
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Formato de horas inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Formato de horas inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        } else { // Validar se é decimal
            try {
                horasInseridas = Double.parseDouble(horasCertificado);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato de horas inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Calcular horas limitadas e salvar como decimal
        String horasValidadas = String.valueOf(calcularHorasComplementares(tipoCertificado, horasInseridas));

        // Agora, insere o certificado no banco com as horas limitadas
        insertCertificateToDatabase(nomeCertificado, tipoCertificado, dataFormatada, horasValidadas);

        // Salva o nome do arquivo PDF
        savePdfName(selectedPdfPath);

        // Exibe uma mensagem de sucesso
        Toast.makeText(this, "Certificado inserido com sucesso!", Toast.LENGTH_SHORT).show();

        // Atualiza as horas totais
        atualizarHorasTotais();

        // Volta para a tela de Meus Certificados
        ArrayList<Integer> certificados = new ArrayList<>();
        Intent intent = new Intent(this, MeusCertificados.class);
        intent.putIntegerArrayListExtra("certificados", certificados);
        startActivity(intent);
        finish();
    }


    private double calcularHorasComplementares(String selectedItem, double horasInseridas) {
        double maxHorasPermitidas;

        switch (selectedItem) {
            case "Atividade cultural":
                maxHorasPermitidas = 3.0;
                break;
            case "Atividade esportiva":
                maxHorasPermitidas = 4.0;
                break;
            case "Colóquio":
            case "Conferência":
            case "Congresso":
            case "Encontro":
            case "Fórum":
                maxHorasPermitidas = 5.0;
                break;
            case "Mesa-redonda":
            case "Palestra":
                maxHorasPermitidas = 2.0;
                break;
            case "Seminário":
            case "Visita técnica":
            case "Workshop":
                maxHorasPermitidas = 5.0;
                break;
            default:
                maxHorasPermitidas = 0.0;
                break;
        }

        // Limitar horas ao máximo permitido
        return Math.min(horasInseridas, maxHorasPermitidas);
    }

    private void insertCertificateToDatabase(String nomeCertificado, String tipoCertificado, String dataEmissao, String horasCertificado) {
        SQLiteDatabase db = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome_certificado", nomeCertificado);
        values.put("tipo_certificado", tipoCertificado);
        values.put("data_emissao", dataEmissao);
        values.put("horas_certificado", horasCertificado);  // Armazenar horas no formato decimal

        long resultado = db.insert("Certificados", null, values);

        if (resultado != -1) {
            Log.i("DAO", "Certificado inserido com sucesso. ID: " + resultado);
        } else {
            Log.e("DAO", "Erro ao inserir certificado.");
        }

        db.close();
    }

    private void savePdfName(String pdfName) {
        if (pdfName == null || pdfName.trim().isEmpty()) {
            Log.e("DAO", "O nome do PDF está vazio ou nulo. Operação cancelada.");
            return;
        }

        SQLiteDatabase db = null;

        try {
            db = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nome_pdf", pdfName);

            long resultado = db.insert("tabela_pdfs", null, values);

            if (resultado != -1) {
                Log.i("DAO", "PDF salvo com sucesso com ID: " + resultado);
            } else {
                Log.e("DAO", "Erro ao salvar o PDF.");
            }
        } catch (Exception e) {
            Log.e("DAO", "Erro ao salvar o PDF: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private double calcularHorasTotais() {
        double totalHoras = 0.0;
        SQLiteDatabase db = dao.getReadableDatabase();

        // Seleciona todas as horas dos certificados
        String query = "SELECT horas_certificado FROM Certificados";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Recupera as horas de cada certificado
                @SuppressLint("Range") String horas = cursor.getString(cursor.getColumnIndex("horas_certificado"));
                try {
                    totalHoras += Double.parseDouble(horas);
                } catch (NumberFormatException e) {
                    Log.e("DAO", "Erro ao parsear horas: " + e.getMessage());
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return totalHoras;
    }

    private void atualizarHorasTotais() {
        double horasTotais = calcularHorasTotais();  // Chama o método que calcula as horas totais
        TextView txtHoras = findViewById(R.id.txtHoras);
        txtHoras.setText(String.format(Locale.getDefault(), "%.2f horas totais", horasTotais));  // Formata o texto
    }


    private String formatarData(String data) {
        if (data == null || data.isEmpty()) {
            return "Data inválida";
        }
        try {
            // Verifique se o formato da data é "dd/MM/yyyy"
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdfEntrada.parse(data);  // Parse a data no formato esperado

            SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdfSaida.format(date);  // Retorne a data formatada
        } catch (Exception e) {
            e.printStackTrace();
            return "Data inválida";
        }
    }
}

