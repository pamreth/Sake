package com.pamela.sake.entity;




import com.pamela.sake.database.AppDatabase;
import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.query.Select;

import java.util.List;


@Table(name = "Notes", database = AppDatabase.class)
public class Person extends Model {

    @PrimaryKey
    private Long id;

    @Column
    public String name;

    @Column(name = "phone")
    public String phone;

    public Person(Long id, String name, String phone) {
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public static Boolean isExistPerson(String id){
        Person person = Select.from(Person.class).where("id = ? ", Long.parseLong(id)).fetchSingle();
        return person != null;
    }
    public Person() {
        super();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public static List<Person> getPersons(){
        return Select.from(Person.class).fetch();
    }
}
