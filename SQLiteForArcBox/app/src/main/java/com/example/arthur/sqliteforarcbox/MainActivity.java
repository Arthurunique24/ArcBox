package com.example.arthur.sqliteforarcbox;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DBHelper dbHelper;

    Button btnAdd, btnRead, btnClean, btnUpdate, btnDel;
    EditText edId, edName, edWeight, edFrom, edTo, edFIO, edEmail, edPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRead = (Button)findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClean = (Button)findViewById(R.id.btnClean);
        btnClean.setOnClickListener(this);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnDel = (Button) findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        edId = (EditText) findViewById(R.id.edId);
        edName = (EditText)findViewById(R.id.edName);
        edWeight = (EditText)findViewById(R.id.edWeight);
        edFrom = (EditText) findViewById(R.id.edFrom);
        edTo = (EditText) findViewById(R.id.edTo);
        edFIO = (EditText) findViewById(R.id.edFIO);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPhone = (EditText) findViewById(R.id.edPhone);

        dbHelper = new DBHelper(this);

    }

    @Override
    public void onClick(View v) {
        String id = edId.getText().toString();
        String name = edName.getText().toString();
        String weight = edWeight.getText().toString();
        String from = edFrom.getText().toString();
        String to = edTo.getText().toString();
        String fio = edFIO.getText().toString();
        String email = edEmail.getText().toString();
        String phone = edPhone.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //Add items
        ContentValues contentValues = new ContentValues();

        switch (v.getId()){
            case R.id.btnAdd:
                if(name.equalsIgnoreCase("") || weight.equalsIgnoreCase("") || from.equalsIgnoreCase("")
                        || to.equalsIgnoreCase("") || fio.equalsIgnoreCase("") || email.equalsIgnoreCase("")
                        || phone.equalsIgnoreCase("")){
                    Toast.makeText(MainActivity.this, "Incorrect ", Toast.LENGTH_SHORT).show();
                    break;
                }
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_WEIGHT, weight);
                contentValues.put(DBHelper.KEY_FROM, from);
                contentValues.put(DBHelper.KEY_TO, to);
                contentValues.put(DBHelper.KEY_FIO, fio);
                contentValues.put(DBHelper.KEY_EMAIL, email);
                contentValues.put(DBHelper.KEY_PHONE, phone);
                database.insert(DBHelper.TABLE_ORDER, null, contentValues);
                break;

            case R.id.btnRead:
                Cursor cursor = database.query(DBHelper.TABLE_ORDER, null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    int weightIndex = cursor.getColumnIndex(DBHelper.KEY_WEIGHT);
                    int fromIndex = cursor.getColumnIndex(DBHelper.KEY_FROM);
                    int toIndex = cursor.getColumnIndex(DBHelper.KEY_TO);
                    int fioIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
                    int emailIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
                    int phoneIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
                        do {
                            Log.d("mLog", "ID = " + cursor.getInt(idIndex)
                                    + ", Description = " + cursor.getString(nameIndex)
                                    + ", Weight = " + cursor.getString(weightIndex)
                                    + ", From = " + cursor.getString(fromIndex)
                                    + ", To = " + cursor.getString(toIndex)
                                    + ", FIO = " + cursor.getString(fioIndex)
                                    + ", Email = " + cursor.getString(emailIndex)
                                    + ", Phone = " + cursor.getString(phoneIndex));

                        } while (cursor.moveToNext());
                } else Log.d("mLog", "0 rows");
                cursor.close();
                break;

            case R.id.btnClean:
                database.delete(DBHelper.TABLE_ORDER, null, null);
                break;
        }

    }
}
