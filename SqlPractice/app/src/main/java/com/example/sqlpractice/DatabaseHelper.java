package com.example.sqlpractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static java.sql.DriverManager.println;


public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //데이터베이스를 처음 생성해주는 경우(기존에 사용자가 데이터베이스를 사용하지 않았던 경우)
        println("createCreate() 호출됨");
        String tableName = "customer";
        // 이 함수에서 데이터베이스는 매개변수인 db를 써야한다.
        // 테이블 생성할때 if not exists라는 조건문을 넣어줄 수 있다.(존재하지 않을 때 테이블 생성)
        // _id는 SQLite에서 내부적으로 관리되는 내부 id이다.
        String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age text, phone text)";
        db.execSQL(sql);

        println("테이블 생성됨.");
    }

    //Alter로 수정을 할 수도 있지만 코드엔 생략.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //사용자가 디비를 사용하고있어서 그걸 업데이트 해주는 경우
        println("onUpgrade() 호출됨: " + oldVersion + ", " + newVersion);

        if(newVersion > 1) {
            String tableName = "customer";
            db.execSQL("drop table if exists " + tableName);
            println("테이블 삭제함");

            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age text, phone text)";
            db.execSQL(sql);

            println("테이블 생성됨.");
        }
    }
}
