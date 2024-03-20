package com.example.mfpledonaf.hotel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setTitle("O texto do título da tela");
    }

    public void apaga(View v) {
        HotelHelper ch = null;
        SQLiteDatabase bdw = null;
        try {
            ch = new HotelHelper(getApplicationContext());
            bdw = ch.getWritableDatabase();
            EditText codhab = (EditText) findViewById(R.id.edcodhab);
            String cod = codhab.getText().toString();
            if (cod.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Por favor, digite o código da habitação.", Toast.LENGTH_LONG).show();
            } else {
                long id = bdw.delete("tbl_habitacoes", "codhabitacao =" + cod, null);
                if (id == 0) {
                    Toast.makeText(getApplicationContext(),
                            "\nNão foi possível eliminar. \nVerifique o código.\n",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Habitação eliminada com sucesso.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "\nErro processando o BD. \n",
                    Toast.LENGTH_LONG).show();
        } finally {
            if (bdw != null) bdw.close();
            if (ch != null) ch.close();
        }
    }

    public void inserir(View v) {
        int codigo=0, qtde=0; float preco=0.0f;
        //String c, q, p;
        EditText campoCod = findViewById(R.id.edcodhab);
        EditText campoQtde = findViewById(R.id.edqtdepessoas);
        EditText campoPreco = findViewById(R.id.edpreco);
        try {
            codigo = Integer.parseInt(campoCod.getText().toString());
            qtde = Integer.parseInt(campoQtde.getText().toString());
            preco = Float.parseFloat(campoPreco.getText().toString());
            if(codigo <= 0)throw new Exception("erro");
            //também funcionaria pegando os dados digitados como strings:
            //c = campoCod.getText().toString();
            //q = campoQtde.getText().toString();
            //p = campoPreco.getText().toString();
        } catch (Exception erro) {
            Snackbar.make(v, "Por favor, digite dados corretos!", Snackbar.LENGTH_LONG).show();
            return; //abandonamos a inserção
        }

        ContentValues valores = new ContentValues();
        valores.put("codhabitacao", codigo); //codhabitacao é o nome do campo da tabela
        valores.put("qtdepessoas", qtde); //qtdepessoas é o nome do campo da tabela
        valores.put("precodiaria", preco); //precodiaria é o nome do campo da tabela
        //ou com os comandos put:
        //valores.put("codhabitacao", c); //codhabitacao é o nome do campo da tabela
        //valores.put("qtdepessoas", q); //qtdepessoas é o nome do campo da tabela
        //valores.put("precodiaria", p); //precodiaria é o nome do campo da tabela
        HotelHelper helper = null;  //classe derivada de SQLiteOpenHelper
        SQLiteDatabase bdw = null;
        try {
            helper = new HotelHelper(getApplicationContext());
            bdw = helper.getWritableDatabase();
            long id = bdw.insert("tbl_habitacoes", "", valores);
            if (id == -1) {
                Snackbar.make(v, "Não foi possível inserir!", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(v, "Habitação inserida com sucesso.", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Snackbar.make(v, "Erro processando o BD!", Snackbar.LENGTH_LONG).show();
        } finally {
            if (bdw != null) bdw.close();
            if (helper != null) helper.close();
        }
    }

    public void altera(View v) {
        int codigo=0, qtde=0; float preco=0.0f;
        EditText campoCod = findViewById(R.id.edcodhab);
        EditText campoQtde = findViewById(R.id.edqtdepessoas);
        EditText campoPreco = findViewById(R.id.edpreco);
        try {
            codigo = Integer.parseInt(campoCod.getText().toString());
            qtde = Integer.parseInt(campoQtde.getText().toString());
            preco = Float.parseFloat(campoPreco.getText().toString());
            if(codigo == 0 || qtde == 0 || preco == 0) throw new Exception("erro");
        } catch (Exception erro) {
            Snackbar.make(v, "Por favor, digite dados corretos!", Snackbar.LENGTH_LONG).show();
            return; //abandonamos a alteração dos dados da habitação
        }
        ContentValues valores = new ContentValues();
        valores.put("qtdepessoas", qtde);
        valores.put("precodiaria", preco);
        HotelHelper helper = null;
        SQLiteDatabase bdw = null;
        try {
            helper = new HotelHelper(getApplicationContext());
            bdw = helper.getWritableDatabase();
            long id = bdw.update("tbl_habitacoes", valores,
                    "codhabitacao="+codigo, null);
            if (id == 0) {
                Snackbar.make(v, "Não foi possível alterar!", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(v, "Habitação alterada com sucesso.", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Snackbar.make(v, "Erro processando o BD!", Snackbar.LENGTH_LONG).show();
        } finally {
            if (bdw != null) bdw.close();
            if (helper != null) helper.close();
        }
    }

    public void listar(View v) {
        HotelHelper helper = null;  //classe derivada de SQLiteOpenHelper
        SQLiteDatabase bdr1 = null;
        Cursor cursor = null;
        String str = "\nHabitações cadastradas:\n\n";
        try {
            Context ctx = v.getContext();
            helper = new HotelHelper(ctx);
            bdr1 = helper.getReadableDatabase();
            cursor = bdr1.query("tbl_habitacoes", null, null,
                    null, null, null, "codhabitacao");
            //ou Cursor cursor = bdr1.rawQuery("select * from tbl_habitacoes", null);
            while (cursor.moveToNext()) {
                String cod = cursor.getString(0);
                String qt = cursor.getString(1);
                float pr = cursor.getFloat(2);
                str += "código: " + cod + ", pessoas: " + qt + ", diária R$ "
                        + String.format("%.2f", pr) + "\n\n";
            }
            ((TextView) findViewById(R.id.res)).setText(str);
        } catch (Exception ex) {
            Snackbar.make(v, "Erro processando o BD!", Snackbar.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
            if (bdr1 != null) bdr1.close();
            if (helper != null) helper.close();
        }
    }

}