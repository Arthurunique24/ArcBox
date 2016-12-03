package com.example.arthur.arcboxv2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FormActivity extends Fragment implements View.OnClickListener{

    DBHelper dbHelper;

    Button btnAdd, btnRead, btnClean, btnUpdate, btnDel;
    EditText edId, edName, edWeight, edFrom, edTo, edFIO, edEmail, edPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_form, container, false);


        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        //btnAdd.setEnabled(true);
        btnRead = (Button) rootView.findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        btnClean = (Button) rootView.findViewById(R.id.btnClean);
        btnClean.setOnClickListener(this);

        btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnDel = (Button) rootView.findViewById(R.id.btnDel);
        btnDel.setOnClickListener(this);

        edId = (EditText) rootView.findViewById(R.id.edId);
        edName = (EditText) rootView.findViewById(R.id.edName);
        edWeight = (EditText) rootView.findViewById(R.id.edWeight);
        edFrom = (EditText) rootView.findViewById(R.id.edFrom);
        edTo = (EditText) rootView.findViewById(R.id.edTo);
        edFIO = (EditText) rootView.findViewById(R.id.edFIO);
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPhone = (EditText) rootView.findViewById(R.id.edPhone);

        getActivity();
        dbHelper = new DBHelper(getActivity());

        return rootView;
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
                    Toast.makeText(getActivity(), "Incorrect ", Toast.LENGTH_SHORT).show();
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
            case R.id.btnUpdate:
                if(id.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "incorrect id", Toast.LENGTH_SHORT).show();
                    break;
                }
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_WEIGHT, weight);
                contentValues.put(DBHelper.KEY_FROM, from);
                contentValues.put(DBHelper.KEY_TO, to);
                contentValues.put(DBHelper.KEY_FIO, fio);
                contentValues.put(DBHelper.KEY_EMAIL, email);
                contentValues.put(DBHelper.KEY_PHONE, phone);
                int updCount = database.update(DBHelper.TABLE_ORDER, contentValues, DBHelper.KEY_ID + "= ?", new String[] {id});
                Log.d("mLog", "Updates rows count = " + updCount);
                break;
            case R.id.btnDel:
                if (id.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "incorrect id", Toast.LENGTH_SHORT).show();
                    break;
                }
                int delCount = database.delete(DBHelper.TABLE_ORDER, DBHelper.KEY_ID + "= ?", new String[] {id});
                Log.d("mLog", "Deleted rows count = " + delCount);
        }

    }

}

