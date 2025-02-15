package com.example.tasktide;

import static com.example.tasktide.DAO.DAO.TABELA_EVENTO;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tasktide.DAO.DAO;
import com.example.tasktide.Objetos.Atividade;
import com.example.tasktide.Objetos.Cronograma;
import com.example.tasktide.Objetos.Evento;
import com.example.tasktide.Objetos.Informacoes;
import com.example.tasktide.Objetos.Usuario;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisaoGeral extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;
    private TableLayout tableLayoutCronograma;
    private ImageView imgBanner;
    private EditText txtMostraNomeDoEvento, edtxtMostraDescricao;
    private EditText txtMostraLocalDoEvento;
    private EditText txtMostraDataDoEvento;
    private EditText txtMostraHoraDeInicioEvento;
    private EditText txtMostraHoraDeTerminoEvento;
    private EditText txtMostraTipoDoEvento, edtxtMostraPrazoInscricao, txtMostraHorasComplementaresEvento, edtxtMostrarValorDoEvento;
    private ImageButton imgbtnEditarValorEvento, imgbtnEditarDescricao;
    private TextView  txtHoraDeInicioEvento, txtHoraDeTerminoEvento, txtValorDoEvento,txtDescricaoVG;
    private ImageButton imgbtnAlterarNome, imgbtnAlterarLocal, imgbtnAlterarData, imgbtnAlterarHorarioInicio, imgbtnEditarHoraDeTermino, imgbtnEditarTipoEvento, imgButtonEditarPrazoIncricao;
    private Button btnInscrever, btnComprarIngresso;
    private DAO dao;
    private long idEvento;
    private long usuarioId;
    private boolean isEditingNome = false;
    private boolean isEditingLocal = false;
    private boolean isEditingHorarioInicio = false;
    private boolean isEditingHorarioTermino = false;
    private boolean isEditingPrazoInscricao = false;
    private boolean isEditingValorEvento = false;
    private boolean isEditingDescricao = false;
    private boolean isEditingData = false;

    private String conteudoRelatorio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_geral);

        imgBanner = findViewById(R.id.imgBanner);
        ImageView btnMudarBanner = findViewById(R.id.imgbtnMudarBanner);
        ImageButton imgbtnCriarCronograma = findViewById(R.id.imgbtnCriarCronograma);

        ImageButton imgbtnMudarBanner = findViewById(R.id.imgbtnMudarBanner);

        txtDescricaoVG = findViewById(R.id.txtDescricaoVG);
        imgbtnEditarDescricao = findViewById(R.id.imgbtnEditarDescricao);
        edtxtMostraDescricao = findViewById(R.id.edtxtMostraDescricao);
        edtxtMostraPrazoInscricao = findViewById(R.id.edtxtMostraPrazoInscricao);
        txtHoraDeInicioEvento = findViewById(R.id.txtHoraDeInicioEvento);
        txtHoraDeTerminoEvento = findViewById(R.id.txtHoraDeTerminoEvento);
        txtMostraLocalDoEvento = findViewById(R.id.txtMostraLocalDoEvento);
        txtMostraNomeDoEvento = findViewById(R.id.txtMostraNomeDoEvento);
        txtMostraDataDoEvento = findViewById(R.id.txtMostraDataDoEvento);
        txtMostraHoraDeInicioEvento = findViewById(R.id.txtMostraHoraDeInicioEvento);
        txtMostraHoraDeTerminoEvento = findViewById(R.id.txtMostraHoraDeTerminoEvento);
        txtMostraTipoDoEvento = findViewById(R.id.txtMostraTipoDoEvento);
        txtMostraHorasComplementaresEvento = findViewById(R.id.txtMostraHorasComplementaresEvento);

        imgbtnAlterarNome = findViewById(R.id.imgbtnAlterarNome);
        imgbtnAlterarLocal = findViewById(R.id.imgbtnAlterarLocal);
        imgbtnAlterarData = findViewById(R.id.imgbtnAlterarData);
        imgbtnAlterarHorarioInicio = findViewById(R.id.imgbtnAlterarHorarioInicio);
        imgbtnEditarHoraDeTermino = findViewById(R.id.imgbtnEditarHoraDeTermino);
        imgbtnEditarTipoEvento = findViewById(R.id.imgbtnEditarTipoEvento);
        imgButtonEditarPrazoIncricao = findViewById(R.id.imgButtonEditarPrazoIncricao);

        txtValorDoEvento = findViewById(R.id.txtValorDoEvento);
        edtxtMostrarValorDoEvento = findViewById(R.id.edtxtMostrarValorDoEvento);
        imgbtnEditarValorEvento = findViewById(R.id.imgbtnEditarValorEvento);
        btnInscrever = findViewById(R.id.btnInscrever);
        btnComprarIngresso = findViewById(R.id.btnComprarIngresso);

        dao = new DAO(this);

        Intent intent = getIntent();
        long eventoId = intent.getLongExtra("evento_id", -1);

        usuarioId = getUsuarioId();

        Evento evento = dao.buscarEventoPorId(eventoId);
        if (evento != null) {
            configurarCamposDeHora(eventoId);
            configurarDescricaoDoEvento(eventoId);

            txtMostraNomeDoEvento.setText(evento.getNomeEvento());
            edtxtMostraDescricao.setText(evento.getDescricao());
            txtMostraTipoDoEvento.setText(evento.getTipoEvento());
            txtMostraHorasComplementaresEvento.setText(evento.getHorasComplementares());

            String[] informacoes = dao.buscarInformacoesPorEvento(eventoId);

            txtMostraDataDoEvento.setText("De " + formatarData(informacoes[0]) + " até " + formatarData(informacoes[1]));
            txtMostraLocalDoEvento.setText(!informacoes[2].isEmpty() ? informacoes[2] : "Local não definido");
            edtxtMostraPrazoInscricao.setText(formatarData(informacoes[3]));

            try {
                double valorEvento = Double.parseDouble(informacoes[4].isEmpty() ? "0.00" : informacoes[4]);
                edtxtMostrarValorDoEvento.setText(String.format("%.2f", valorEvento));
                configurarCamposValor(valorEvento);
            } catch (NumberFormatException e) {
                edtxtMostrarValorDoEvento.setText("0.00");
            }

            txtMostraHoraDeInicioEvento.setText(!informacoes[5].isEmpty() ? informacoes[5] : "Não definido");
            if (informacoes[6] != null && !informacoes[6].isEmpty()) {
                txtHoraDeTerminoEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeTerminoEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeTerminoEvento.setText(informacoes[6]);
            } else {
                txtHoraDeTerminoEvento.setVisibility(View.GONE);
                txtMostraHoraDeTerminoEvento.setVisibility(View.GONE);
            }
            carregarBanner(eventoId);

            String nomeEvento = evento.getNomeEvento(); // Nome do evento correto
            String dataPrevista = informacoes[0]; // Data prevista do evento
            long eventoData = formatarDataParaLong(dataPrevista); // Convertendo a data para timestamp

            // Agendando a notificação com o nome do evento
            agendarNotificacao(eventoData, nomeEvento);

        } else {
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        boolean vemDaTelaInicial = intent.getBooleanExtra("VEM_DA_TELA_INICIAL", false);

        if (vemDaTelaInicial) {
            imgbtnCriarCronograma.setVisibility(View.GONE);
            imgbtnEditarDescricao.setVisibility(View.GONE);
            imgbtnEditarTipoEvento.setVisibility(View.GONE);
            imgbtnEditarValorEvento.setVisibility(View.GONE);
            imgButtonEditarPrazoIncricao.setVisibility(View.GONE);
            imgbtnAlterarData.setVisibility(View.GONE);
            imgbtnAlterarLocal.setVisibility(View.GONE);
            imgbtnAlterarNome.setVisibility(View.GONE);
            imgbtnAlterarHorarioInicio.setVisibility(View.GONE);
            imgbtnEditarHoraDeTermino.setVisibility(View.GONE);
            imgbtnMudarBanner.setVisibility(View.GONE);
            configurarVisibilidadeBotoes();
        }

        btnMudarBanner.setOnClickListener(v -> showImageSizeWarningDialog());

        // Dentro do método do seu ImageButton ou onde for disparado o código
        ImageButton btnPopupMenu = findViewById(R.id.btnPopupMenu);
        btnPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(VisaoGeral.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.voltar) {
                            onBackPressed();
                            return true;
                        } else if (itemId == R.id.relatorio) {
                            mostrarRelatorio(VisaoGeral.this, eventoId);
                            return true;
                        } else if (itemId == R.id.visualizarCronograma) {
                            // Criando o AlertDialog para mostrar o cronograma
                            AlertDialog.Builder builder = new AlertDialog.Builder(VisaoGeral.this);
                            builder.setTitle("Cronograma");

                            // Infla o layout correto para o cronograma (tabela.xml)
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.tabela, null);  // Certifique-se de que 'tabela.xml' existe

                            // Agora, você acessa o TableLayout no layout inflado
                            TableLayout tableLayout = view.findViewById(R.id.tableLayoutCronograma);

                            // Verificando se o TableLayout foi encontrado
                            if (tableLayout != null) {
                                // Preenche a tabela com os dados do cronograma
                                preencherTabelaCronograma(tableLayout, eventoId);
                            } else {
                                Log.e("VisaoGeral", "TableLayout não encontrado no layout inflado.");
                            }

                            // Define a View do AlertDialog
                            builder.setView(view);

                            // Adiciona um botão para fechar o AlertDialog
                            builder.setPositiveButton("Fechar", null);

                            // Cria e exibe o AlertDialog
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            popup.dismiss();

                            return true; // Indica que o item foi processado
                        }
                        return false; // Se não for nenhum dos itens acima
                    }
                });

                popup.show();
            }
        });


        imgbtnCriarCronograma.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.adicionar_atividades, null);

            EditText edtNomeAtividade = dialogView.findViewById(R.id.edtNomeAtividade);
            EditText edtLocalAtividade = dialogView.findViewById(R.id.edtLocalAtividade);
            EditText edtResponsavelAtividade = dialogView.findViewById(R.id.edtResponsavelAtividade);
            Button btnEscolherDataHorario = dialogView.findViewById(R.id.btnEscolherDataHorario);
            Button btnAdicionarAtividade = dialogView.findViewById(R.id.btnAdicionarAtividade);

            btnEscolherDataHorario.setOnClickListener(v1 -> escolherDataEHoras(btnEscolherDataHorario));

            btnAdicionarAtividade.setOnClickListener(v1 -> {
                String nomeAtividade = edtNomeAtividade.getText().toString();
                String localAtividade = edtLocalAtividade.getText().toString();
                String responsavelAtividade = edtResponsavelAtividade.getText().toString();
                String dataHora = btnEscolherDataHorario.getText().toString();

                if (nomeAtividade.isEmpty() || localAtividade.isEmpty() || responsavelAtividade.isEmpty() || dataHora.isEmpty()) {
                    Toast.makeText(VisaoGeral.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    dao.adicionarAtividade(eventoId, nomeAtividade, dataHora, responsavelAtividade, localAtividade, "");
                    preencherTabelaCronograma(tableLayoutCronograma, eventoId);

                    edtNomeAtividade.setText("");
                    edtLocalAtividade.setText("");
                    edtResponsavelAtividade.setText("");
                    btnEscolherDataHorario.setText("Escolher Data e Horário");

                    Toast.makeText(VisaoGeral.this, "Atividade adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(VisaoGeral.this);
            builder.setView(dialogView);
            builder.setCancelable(true);
            builder.show();
        });


        imgbtnAlterarNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingNome) {
                    txtMostraNomeDoEvento.setEnabled(true);
                    imgbtnAlterarNome.setImageResource(R.drawable.botao_salvar);
                } else {
                    String nomeAlterado = txtMostraNomeDoEvento.getText().toString();
                    dao.atualizarNomeEvento(nomeAlterado, eventoId);
                    txtMostraNomeDoEvento.setText(nomeAlterado);

                    txtMostraNomeDoEvento.setEnabled(false);
                    imgbtnAlterarNome.setImageResource(R.drawable.editarinformacoes);
                    Toast.makeText(getApplicationContext(), "Nome do evento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingNome = !isEditingNome;
            }
        });


        imgbtnAlterarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingLocal) {
                    txtMostraLocalDoEvento.setEnabled(true);
                    imgbtnAlterarLocal.setImageResource(R.drawable.botao_salvar);
                } else {
                    String localAlterado = txtMostraLocalDoEvento.getText().toString();
                    dao.atualizarLocalEvento(localAlterado, eventoId);

                    txtMostraLocalDoEvento.setText(localAlterado);
                    txtMostraLocalDoEvento.setEnabled(false);
                    imgbtnAlterarLocal.setImageResource(R.drawable.editarinformacoes);

                    Toast.makeText(getApplicationContext(), "Local do evento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingLocal = !isEditingLocal;
            }
        });


        imgbtnAlterarHorarioInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingHorarioInicio) {
                    txtHoraDeInicioEvento.setEnabled(true);
                    imgbtnAlterarHorarioInicio.setImageResource(R.drawable.botao_salvar);
                } else {
                    String horarioInicioAlterado = txtHoraDeInicioEvento.getText().toString();
                    dao.atualizarHorarioInicioEvento(horarioInicioAlterado, eventoId);

                    txtHoraDeInicioEvento.setText(horarioInicioAlterado);
                    txtHoraDeInicioEvento.setEnabled(false);
                    imgbtnAlterarHorarioInicio.setImageResource(R.drawable.editarinformacoes);

                    Toast.makeText(getApplicationContext(), "Horário de início do evento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingHorarioInicio = !isEditingHorarioInicio;
            }
        });


        imgbtnEditarHoraDeTermino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingHorarioTermino) {
                    txtHoraDeTerminoEvento.setEnabled(true);
                    imgbtnEditarHoraDeTermino.setImageResource(R.drawable.botao_salvar);
                } else {
                    String horarioTerminoAlterado = txtHoraDeTerminoEvento.getText().toString();
                    dao.atualizarHorarioTerminoEvento(horarioTerminoAlterado, eventoId);

                    txtHoraDeTerminoEvento.setText(horarioTerminoAlterado);
                    txtHoraDeTerminoEvento.setEnabled(false);
                    imgbtnEditarHoraDeTermino.setImageResource(R.drawable.editarinformacoes);

                    Toast.makeText(getApplicationContext(), "Hora de término do evento atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingHorarioTermino = !isEditingHorarioTermino;
            }
        });

        imgbtnAlterarData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingData) {
                    // Habilita a edição das duas datas em um único campo de texto
                    txtMostraDataDoEvento.setEnabled(true);
                    imgbtnAlterarData.setImageResource(R.drawable.botao_salvar);
                } else {
                    // Obtém a nova string com a data alterada (formato "De dataPrevista até dataFim")
                    String dataAlterada = txtMostraDataDoEvento.getText().toString();

                    // Extrai as datas individuais da string para enviar ao banco
                    String[] datas = dataAlterada.split(" até ");
                    if (datas.length == 2) {
                        String dataPrevistaAlterada = datas[0].replace("De ", "").trim();
                        String dataFimAlterada = datas[1].trim();

                        // Atualiza no banco de dados
                        dao.atualizarDatasEvento(dataPrevistaAlterada, dataFimAlterada, eventoId);

                        // Atualiza o TextView com a nova data no formato correto
                        txtMostraDataDoEvento.setText("De " + dataPrevistaAlterada + " até " + dataFimAlterada);
                        txtMostraDataDoEvento.setEnabled(false);
                        imgbtnAlterarData.setImageResource(R.drawable.editarinformacoes);

                        // Exibe uma mensagem de sucesso
                        Toast.makeText(getApplicationContext(), "Datas do evento atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
                // Alterna o estado de edição
                isEditingData = !isEditingData;
            }
        });


        imgButtonEditarPrazoIncricao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingPrazoInscricao) {
                    edtxtMostraPrazoInscricao.setEnabled(true);
                    imgButtonEditarPrazoIncricao.setImageResource(R.drawable.botao_salvar);
                } else {
                    String prazoInscricaoAlterado = edtxtMostraPrazoInscricao.getText().toString();
                    dao.atualizarPrazoInscricaoEvento(prazoInscricaoAlterado, eventoId);

                    edtxtMostraPrazoInscricao.setText(prazoInscricaoAlterado);
                    edtxtMostraPrazoInscricao.setEnabled(false);
                    imgButtonEditarPrazoIncricao.setImageResource(R.drawable.editarinformacoes);

                    Toast.makeText(getApplicationContext(), "Prazo de inscrição do evento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingPrazoInscricao = !isEditingPrazoInscricao;
            }
        });


        imgbtnEditarValorEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingValorEvento) {
                    txtValorDoEvento.setEnabled(true);
                    imgbtnEditarValorEvento.setImageResource(R.drawable.botao_salvar); // Troca o ícone para salvar
                } else {
                    String valorEventoAlterado = txtValorDoEvento.getText().toString();
                    dao.atualizarValorEvento(Double.parseDouble(valorEventoAlterado), eventoId); // Atualiza no banco de dados

                    txtValorDoEvento.setText(valorEventoAlterado);
                    txtValorDoEvento.setEnabled(false);
                    imgbtnEditarValorEvento.setImageResource(R.drawable.editarinformacoes); // Troca o ícone para editar

                    Toast.makeText(getApplicationContext(), "Valor do evento atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingValorEvento = !isEditingValorEvento;
            }
        });

        imgbtnEditarDescricao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditingDescricao) {
                    edtxtMostraDescricao.setEnabled(true);
                    imgbtnEditarDescricao.setImageResource(R.drawable.botao_salvar);
                } else {
                    String descricaoEventoAlterada = edtxtMostraDescricao.getText().toString();
                    dao.atualizarDescricaoEvento(descricaoEventoAlterada, eventoId);

                    edtxtMostraDescricao.setText(descricaoEventoAlterada);
                    edtxtMostraDescricao.setEnabled(false);
                    imgbtnEditarDescricao.setImageResource(R.drawable.editarinformacoes);

                    Toast.makeText(getApplicationContext(), "Descrição do evento atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                }
                isEditingDescricao = !isEditingDescricao;
            }
        });


        imgbtnEditarTipoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VisaoGeral.this);
                builder.setTitle("Escolha o Tipo de Evento");

                String[] tiposDeEvento = {"Atividade cultural", "Atividade esportiva", "Colóquio",
                        "Conferência", "Congresso", "Encontro", "Fórum",
                        "Mesa-redonda", "Palestra", "Seminário", "Visita técnica", "Workshop"};

                builder.setItems(tiposDeEvento, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String novoTipoEvento = tiposDeEvento[which];
                        txtMostraTipoDoEvento.setText(novoTipoEvento);

                        String novasHorasComplementares = calcularHorasComplementares(novoTipoEvento);
                        txtMostraHorasComplementaresEvento.setText(novasHorasComplementares);

                        dao.atualizarTipoEhorasNoBanco(novoTipoEvento, novasHorasComplementares, eventoId);

                        Toast.makeText(VisaoGeral.this, "Tipo do evento e horas complementares atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });

        btnComprarIngresso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acessar a Intent e obter o ID do evento diretamente
                Intent intent = getIntent();
                long eventoId = intent.getLongExtra("evento_id", -1);  // Recuperando o evento_id da Intent

                // Verificar se o eventoId é válido
                if (eventoId != -1) {
                    // Recupera o valor do evento usando o DAO
                    Informacoes informacoes = dao.getInformacoesById(eventoId);
                    if (informacoes != null) {
                        double valorEvento = informacoes.getValorEvento();

                        // Passa o valor do evento para a próxima tela
                        Intent pagamentoIntent = new Intent(VisaoGeral.this, PagamentoEvento.class);
                        pagamentoIntent.putExtra("VALOR_EVENTO", valorEvento);
                        pagamentoIntent.putExtra("evento_id", eventoId);
                        startActivity(pagamentoIntent);
                    } else {
                        Toast.makeText(VisaoGeral.this, "Informações do evento não encontradas.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VisaoGeral.this, "ID do evento inválido.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnInscrever.setEnabled(false);

                Intent intent = getIntent();
                long eventoId = intent.getLongExtra("evento_id", -1);

                if (eventoId != -1) {
                    if (dao.verificarInscricao(usuarioId, eventoId)) {
                        Toast.makeText(VisaoGeral.this, "Você já está inscrito neste evento!", Toast.LENGTH_SHORT).show();
                    } else {
                        dao.inscreverNoEvento(usuarioId, eventoId);
                        Toast.makeText(VisaoGeral.this, "Inscrição realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VisaoGeral.this, "Evento não encontrado!", Toast.LENGTH_SHORT).show();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnInscrever.setEnabled(true);
                    }
                }, 1000);
            }
        });
    }


    private String formatarData(String data) {
        if (data == null || data.isEmpty()) {
            return "Data inválida";
        }
        try {
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdfEntrada.parse(data);


            SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdfSaida.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Data inválida";
        }
    }

    private long formatarDataParaLong(String data) {
        if (data == null || data.isEmpty()) {
            return 0; // Retorna 0 caso a data seja inválida
        }
        try {
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdfEntrada.parse(data);

            // Retorna o timestamp da data (em milissegundos)
            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // Caso ocorra erro na conversão
    }

    private long getUsuarioId() {
        String usuarioEmail = getEmailUsuario();

        if (usuarioEmail == null) {
            return -1;
        }

        Usuario usuario = dao.buscarUsuarioPorEmail(usuarioEmail);

        if (usuario != null) {
            return usuario.getId();
        } else {
            return -1;
        }
    }

    private String getEmailUsuario() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("email", null);
    }

    private void configurarVisibilidadeBotoes() {
        // Acessar a Intent e obter o ID do evento diretamente
        Intent intent = getIntent();
        long eventoId = intent.getLongExtra("evento_id", -1);  // Recuperando o evento_id da Intent

        // Verificar se o eventoId foi recuperado corretamente
        if (eventoId == -1) {
            Toast.makeText(this, "ID do evento inválido", Toast.LENGTH_SHORT).show();
            return; // Se o ID for inválido, não faz sentido continuar
        }

        // Verificar se o evento é pago ou gratuito usando o DAO
        boolean eventoPago = dao.EventoPago(eventoId); // Método que verifica se o evento é pago no banco de dados

        Button btnComprarIngresso = findViewById(R.id.btnComprarIngresso);
        Button btnInscrever = findViewById(R.id.btnInscrever);

        if (eventoPago) {
            btnComprarIngresso.setVisibility(View.VISIBLE);
            btnInscrever.setVisibility(View.GONE);
        } else {
            btnComprarIngresso.setVisibility(View.GONE);
            btnInscrever.setVisibility(View.VISIBLE);
        }
    }


    private void configurarDescricaoDoEvento(long idEvento) {
        String descricao = dao.obterDescricaoEvento(idEvento);

        EditText edtxtMostraDescricao = findViewById(R.id.edtxtMostraDescricao);
        ImageButton imgbtnEditarDescricao = findViewById(R.id.imgbtnEditarDescricao);
        TextView txtDescricaoVG = findViewById(R.id.txtDescricaoVG);

        if (descricao != null && !descricao.isEmpty()) {

            edtxtMostraDescricao.setText(descricao);
            edtxtMostraDescricao.setVisibility(View.VISIBLE);
            imgbtnEditarDescricao.setVisibility(View.VISIBLE);
            txtDescricaoVG.setVisibility(View.VISIBLE);

            edtxtMostraDescricao.setMinLines(1);
            edtxtMostraDescricao.setMaxLines(10);
        } else {
            edtxtMostraDescricao.setVisibility(View.GONE);
            imgbtnEditarDescricao.setVisibility(View.GONE);
            txtDescricaoVG.setVisibility(View.GONE);
        }
    }

    private String calcularHorasComplementares(String selectedItem) {
        switch (selectedItem) {
            case "Atividade cultural":
                return "3 horas";
            case "Atividade esportiva":
                return "4 horas";
            case "Colóquio":
            case "Conferência":
            case "Congresso":
            case "Encontro":
            case "Fórum":
                return "5 horas";
            case "Mesa-redonda":
            case "Palestra":
                return "2 horas";
            case "Seminário":
            case "Visita técnica":
            case "Workshop":
                return "5 horas";
            default:
                return "";
        }
    }

    private void configurarCamposValor(Double valorEvento) {
        if (valorEvento != null && valorEvento > 0) {
            txtValorDoEvento.setVisibility(View.VISIBLE);
            edtxtMostrarValorDoEvento.setVisibility(View.VISIBLE);
            imgbtnEditarValorEvento.setVisibility(View.VISIBLE);
            edtxtMostrarValorDoEvento.setText(String.format("%.2f", valorEvento));
        } else {
            txtValorDoEvento.setVisibility(View.GONE);
            edtxtMostrarValorDoEvento.setVisibility(View.GONE);
            imgbtnEditarValorEvento.setVisibility(View.GONE);
        }
    }

    private void configurarCamposDeHora(long idEvento) {
        Informacoes informacoes = dao.getInformacoesById(idEvento);

        if (informacoes != null) {
            String horaInicio = informacoes.getHorarioInicio();
            String horarioTermino = informacoes.getHorarioTermino();

            // Define os textos dos campos de hora
            if (horaInicio != null && !horaInicio.isEmpty()) {
                txtHoraDeInicioEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeInicioEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeInicioEvento.setText(horaInicio);
                // Tornar visível o botão de editar horário de início
                ImageButton imgbtnAlterarHorarioInicio = findViewById(R.id.imgbtnAlterarHorarioInicio);
                imgbtnAlterarHorarioInicio.setVisibility(View.VISIBLE);
            } else {
                txtHoraDeInicioEvento.setVisibility(View.GONE);
                txtMostraHoraDeInicioEvento.setVisibility(View.GONE);
            }

            if (horarioTermino != null && !horarioTermino.isEmpty()) {
                txtHoraDeTerminoEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeTerminoEvento.setVisibility(View.VISIBLE);
                txtMostraHoraDeTerminoEvento.setText(horarioTermino);
                // Tornar visível o botão de editar horário de término
                ImageButton imgbtnEditarHoraDeTermino = findViewById(R.id.imgbtnEditarHoraDeTermino);
                imgbtnEditarHoraDeTermino.setVisibility(View.VISIBLE);
            } else {
                txtHoraDeTerminoEvento.setVisibility(View.GONE);
                txtMostraHoraDeTerminoEvento.setVisibility(View.GONE);
            }
        }
    }

    // Método para exibir o relatório
    private void mostrarRelatorio(Context context, long eventoId) {
        Evento evento = dao.buscarEventoPorId(eventoId);
        Informacoes informacoes = dao.getInformacoesById(eventoId);
        String[] participantes = dao.buscarParticipantes(eventoId);

        // Construa o relatório como String
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Nome: ").append(evento.getNomeEvento()).append("\n");
        relatorio.append("Tipo: ").append(evento.getTipoEvento()).append("\n");
        relatorio.append("Horas Complementares: ").append(evento.getHorasComplementares()).append("\n");
        relatorio.append("Descrição: ").append(evento.getDescricao()).append("\n");
        relatorio.append("Modalidade: ").append(evento.getModalidade()).append("\n");
        relatorio.append("Categoria: ").append(evento.getCategoria()).append("\n\n");

        if (informacoes != null) {
            relatorio.append("Informações Adicionais:\n");
            relatorio.append("Local: ").append(informacoes.getLocal()).append("\n");
            relatorio.append("Data de Início: ").append(informacoes.getDataPrevista()).append("\n");
            relatorio.append("Data de Término: ").append(informacoes.getDataFim()).append("\n");
            relatorio.append("Horário: ").append(informacoes.getHorarioInicio()).append(" - ").append(informacoes.getHorarioTermino()).append("\n");
            relatorio.append("Valor: R$ ").append(informacoes.getValorEvento()).append("\n\n");
        }

        relatorio.append("Participantes:\n");
        for (String participante : participantes) {
            relatorio.append("- ").append(participante).append("\n");
        }

        // Exibir o relatório em um AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Relatório do Evento");
        builder.setMessage(relatorio.toString());
        builder.setPositiveButton("Fechar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Métodos para notificação
    public void agendarNotificacao(long eventoData, String nomeEvento) {
        // Obtém o AlarmManager do sistema
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager != null) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Se a permissão não foi concedida, solicita ao usuário para permitir
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        // Verificar se o evento é no futuro
        Calendar calendarioAtual = Calendar.getInstance(); // Data e hora atual
        long hojeMillis = calendarioAtual.getTimeInMillis(); // Tempo atual em milissegundos
        Log.d("Data Atual", "Hoje é: " + calendarioAtual.getTime().toString()); // Verificando a data de hoje

        // Definir a data do evento a partir do timestamp fornecido
        Calendar calendarioEvento = Calendar.getInstance();
        calendarioEvento.setTimeInMillis(eventoData); // Data do evento
        long eventoMillis = calendarioEvento.getTimeInMillis(); // Tempo do evento em milissegundos
        Log.d("Evento", "Data do Evento: " + calendarioEvento.getTime().toString()); // Verificando a data do evento

        if (eventoMillis <= hojeMillis) {
            // Se o evento for no passado ou no presente, não agendar a notificação
            Log.e("Erro", "A data do evento deve ser no futuro!");
            return; // Não agendar a notificação
        }

        // Subtrai 1 dia (24 horas) da data do evento para agendar a notificação para o dia anterior
        calendarioEvento.add(Calendar.DAY_OF_MONTH, -1);  // Subtrai 1 dia da data do evento
        long notificacaoTempo = calendarioEvento.getTimeInMillis();  // Tempo para a notificação
        Log.d("Notificacao", "Notificação será enviada para: " + calendarioEvento.getTime().toString()); // Verificando o tempo da notificação

        // Criação de Intent para não disparar nenhuma activity
        Intent intent = new Intent(); // Nenhuma ação será disparada

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        // Agendar o alarme para enviar a notificação
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificacaoTempo, pendingIntent);
            Log.d("Alarme", "Notificação agendada para: " + calendarioEvento.getTime().toString()); // Confirmando o agendamento
        }

        // Envia a notificação imediatamente, se desejado
        enviarNotificacao(nomeEvento);
    }

    public void enviarNotificacao(String nomeEvento) {
        int notificationId = 1;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "evento_notificacao_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificação de Evento";
            String description = "Canal para notificações de evento";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Mensagem com o nome do evento
        String mensagem = "Seu evento \"" + nomeEvento + "\" será amanhã!";

        // Intent para abrir a tela de detalhes do evento
        Intent intent = new Intent(); // Não há necessidade de abrir nenhuma Activity aqui

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        // Cria a notificação
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.icone_tasktide_foreground)
                .setContentTitle("Lembrete de Evento")
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationId, notification);
    }

    //Métodos para cronograma
    private void preencherTabelaCronograma(TableLayout tableLayout, long eventoId) {
        // Verifique se o TableLayout e a lista de atividades são válidos
        if (tableLayout == null) {
            Log.e("VisaoGeral", "TableLayout é nulo.");
            return; // Retorna sem fazer nada se o TableLayout for nulo
        }

        List<Cronograma> atividades = dao.buscarCronogramaPorEvento(eventoId);

        if (atividades == null || atividades.isEmpty()) {
            Log.w("VisaoGeral", "Nenhuma atividade encontrada para o evento com ID " + eventoId);
            return; // Retorna se a lista de atividades for nula ou vazia
        }

        // Remove as linhas antigas da tabela, preservando a primeira (cabeçalho)
        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));

        // Adiciona as novas linhas com dados das atividades
        for (Cronograma atividade : atividades) {
            TableRow row = new TableRow(this);

            // Adiciona a coluna de Data e Hora
            TextView txtDataHora = new TextView(this);
            txtDataHora.setText(atividade.getData() + " " + atividade.getHorario());
            txtDataHora.setGravity(Gravity.CENTER);
            txtDataHora.setPadding(8, 8, 8, 8);

            // Adiciona a coluna de Atividade
            TextView txtAtividade = new TextView(this);
            txtAtividade.setText(atividade.getNomeAtividade());
            txtAtividade.setGravity(Gravity.CENTER);
            txtAtividade.setPadding(8, 8, 8, 8);

            // Adiciona a coluna de Palestrante (Responsável)
            TextView txtResponsavel = new TextView(this);
            txtResponsavel.setText(atividade.getPalestrante());
            txtResponsavel.setGravity(Gravity.CENTER);
            txtResponsavel.setPadding(8, 8, 8, 8);

            // Adiciona a coluna de Local
            TextView txtLocal = new TextView(this);
            txtLocal.setText(atividade.getLocal());
            txtLocal.setGravity(Gravity.CENTER);
            txtLocal.setPadding(8, 8, 8, 8);

            // Adiciona as views à linha
            row.addView(txtDataHora);
            row.addView(txtAtividade);
            row.addView(txtResponsavel);
            row.addView(txtLocal);

            // Adiciona a linha à tabela
            tableLayout.addView(row);
        }
    }



    private void escolherDataEHoras(Button btnEscolherDataHorario) {
        Locale locale = new Locale("pt", "BR");
        Locale.setDefault(locale);

        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(VisaoGeral.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anoEscolhido, int mesEscolhido, int diaEscolhido) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(VisaoGeral.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int horaEscolhida, int minutoEscolhido) {
                        // Formatação da data e horário
                        String dataHorarioEscolhido = String.format("%02d/%02d/%d %02d:%02d", diaEscolhido, mesEscolhido + 1, anoEscolhido, horaEscolhida, minutoEscolhido);
                        btnEscolherDataHorario.setText(dataHorarioEscolhido);
                    }
                }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        }, ano, mes, dia);
        datePickerDialog.show();
    }


    // Métodos de Imagem
    private void showImageSizeWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Aviso de Imagem")
                .setMessage("A imagem deve ter preferencialmente 310 x 160 pixels. Deseja continuar?")
                .setPositiveButton("Continuar", (dialog, which) -> openImageChooser())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Lógica para atualizar o banner (já existente)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgBanner.setImageBitmap(bitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                long eventoId = getIntent().getLongExtra("evento_id", -1);
                if (eventoId != -1) {
                    dao.atualizarBannerEvento(eventoId, imageBytes);
                    Toast.makeText(this, "Banner atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                    editor.putString("bannerImagem", imageUri.toString());
                    editor.apply();

                    Toast.makeText(this, "Banner salvo com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "ID do evento inválido!", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void carregarBanner(long eventoId) {
        // Verifica se o eventoId é válido
        if (eventoId != -1) {
            byte[] bannerBytes = dao.obterBannerEvento(eventoId);

            if (bannerBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bannerBytes, 0, bannerBytes.length);
                imgBanner.setImageBitmap(bitmap);
            }
        }
    }

    //Métodos de Navegação
    public void IrTelaPagar(View view) {
        Intent in = new Intent(VisaoGeral.this, PagamentoEvento.class);
        startActivity(in);
    }

    public void inicialVG(View view){
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }

    public void localizacaoVG(View view){
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }

    public void criarVG(View view){
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }

    public void meuseventosVG(View view){
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }

    public void perfilVG(View view){
        Intent in = new Intent(this, TelaInicial.class);
        startActivity(in);
    }
}