package com.pamela.sake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pamela.sake.database.AppDatabase;
import com.pamela.sake.entity.Person;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;
import com.reactiveandroid.query.Delete;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.query.Update;

import java.util.List;

public class PersonActivity extends AppCompatActivity {

    Button btnGuardar, btnBuscar, btnBorrar, btnActualizar;
    EditText etid, etNombres, etTelefono, etlistPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDatabase.class)
                .addModelClasses(Person.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .build());

        setContentView(R.layout.activity_person);

        btnGuardar = findViewById(R.id.BtnGuardar);
        btnBuscar = findViewById(R.id.BtnBuscar);
        btnBorrar = findViewById(R.id.BtnBorrar);
        btnActualizar = findViewById(R.id.BtnActualizar);

        etid = findViewById(R.id.etId);
        etNombres = findViewById(R.id.etNombres);
        etTelefono = findViewById(R.id.ettelefono);
        etlistPerson = findViewById(R.id.txtListPersons);

        fillListPeople();


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etid.getText().toString();
                if (!Person.isExistPerson(id)) {
                    Person person = new Person();
                    person.setName(etNombres.getText().toString());
                    person.setPhone(etTelefono.getText().toString());
                    person.setId(Long.parseLong(id));
                    person.save();
                    Person personReturn = Select.from(Person.class).where("id = ? ", Long.parseLong(etid.getText().toString())).fetchSingle();
                    fillListPeople();
                    cleanFealds();
                    Toast.makeText(getApplicationContext(), "Se guardó el dato: " + personReturn.getPhone(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ya existe una persona con id: " + id, Toast.LENGTH_LONG).show();
                }


            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etid.getText().toString();
                try {
                    Person person = Select.from(Person.class).where("id = ? ", Long.parseLong(id)).fetchSingle();
                    etNombres.setText(person.getName());
                    etTelefono.setText(person.getPhone());
                    etid.setText(String.valueOf(person.getId()));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No se encontró la persona", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etid.getText().toString();
                if (!id.equals("")) {
                    try {
                        Delete.from(Person.class).where("id = ? ", Long.parseLong(id)).execute();
                        cleanFealds();
                        fillListPeople();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al eliminar la persona", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Digite el id de la persona a buscar", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etid.getText().toString();
                if (Person.isExistPerson(id)) {
                    Update.table(Person.class).set("name = ? ", etNombres.getText().toString()).where("id = ? ", Long.parseLong(id)).execute();
                    Update.table(Person.class).set("phone = ? ", etTelefono.getText().toString()).where("id = ? ", Long.parseLong(id)).execute();
                    Person person = Select.from(Person.class).where("id = ? ", Long.parseLong(id)).fetchSingle();
                    etNombres.setText(person.getName());
                    etTelefono.setText(person.getPhone());
                    etid.setText(String.valueOf(person.getId()));
                    fillListPeople();
                }
            }
        });
    }

    private void cleanFealds() {
        etNombres.setText("");
        etTelefono.setText("");
        etid.setText("");
    }

    private void fillListPeople() {
        String listPersons = "";
        List<Person> people = Select.from(Person.class).fetch();
        int index = 1;
        for (Person p : people) {
            listPersons += index + " - " + p.getId() + " - " + p.getName() + " - " + p.getPhone() + ".\n";
            index++;
        }
        etlistPerson.setText(listPersons);
    }
}
