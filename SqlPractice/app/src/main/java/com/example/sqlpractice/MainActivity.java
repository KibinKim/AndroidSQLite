package com.example.sqlpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    EditText edit_open, edit_mktbl, edit_name, edit_age, edit_phone;

    TextView textView;

    Button btn_open, btn_mktbl, btn_data, btn_search;

    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_open = (EditText)findViewById(R.id.edit_open);
        edit_mktbl = (EditText)findViewById(R.id.edit_mktable);
        edit_name = (EditText)findViewById(R.id.edit_name);
        edit_age = (EditText)findViewById(R.id.edit_age);
        edit_phone = (EditText)findViewById(R.id.edit_phoneNum);

        textView = (TextView)findViewById(R.id.textView);

        btn_open = (Button)findViewById(R.id.btn_open);
        btn_mktbl = (Button)findViewById(R.id.btn_mktable);
        btn_data = (Button)findViewById(R.id.btn_plusData);
        btn_search = (Button)findViewById(R.id.btn_search);


        //데이터베이스 오픈
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databaseName = edit_open.getText().toString(); //데이터베이스 이름 설정
                openDatabase(databaseName);
            }
        });

        //테이블 생성
        btn_mktbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = edit_mktbl.getText().toString(); //테이블 이름 설정
                createTable(tableName);
            }
        });

        //데이터 추가
        btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //넣을 데이터 값
                //trim()을 해줌으로 공백으로 인한 에러방지 (데이터푸시시 꼭 해줄 것)
                String name = edit_name.getText().toString().trim();
                String age = edit_age.getText().toString().trim();
                String phone = edit_phone.getText().toString().trim();

                /**
                 * 밑에 주석처리한 문장은 왜 써야하는지 모르겠음..
                 */
//                int ageNum = -1;
//                try {
//                    Integer.parseInt(age);
//                } catch (Exception e) {
//                }
                insertData(name, age, phone);
            }
        });

        //데이터 조회
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = edit_mktbl.getText().toString(); //테이블 이름설정
                selectData(tableName);
            }
        });
    }

    public void openDatabase(String databaseName) {
//        Toast.makeText(getApplicationContext(), "openDatabase() 호출됨", Toast.LENGTH_SHORT).show();
        println("openDatabase() 호출됨");
        DatabaseHelper helper = new DatabaseHelper(this, databaseName, null, 3); //helper 생성
        database = helper.getWritableDatabase(); //데이터베이스에 쓸 수 있는 권한을 리턴해줌
    }

    public void createTable(String tableName) {
//        Toast.makeText(getApplicationContext(), "createTable() 호출됨", Toast.LENGTH_SHORT).show();
        println("createTable() 호출됨");
        if(database != null) {
            String sql = "create table " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age text, phone text)";
            database.execSQL(sql);

//            Toast.makeText(getApplicationContext(), "테이블 생성됨", Toast.LENGTH_SHORT).show();
            println("테이블 생성됨");
        } else {
//            Toast.makeText(getApplicationContext(), "데이터베이스를 먼저 오픈하세요.", Toast.LENGTH_SHORT).show();
            println("데이터베이스를 먼저 오픈하세요");
        }
    }

    public void dropTable(String tableName) {
        println("deleteTable() 호출됨");
        String sql = "drop table " + tableName;
        database.execSQL(sql);
        println(tableName + "이 삭제되었습니다.");
    }

    public void insertData(String name, String age, String phone) {
//        Toast.makeText(getApplicationContext(), "insertData() 호출됨", Toast.LENGTH_SHORT).show();
        println("insertData() 호출됨");

        /**
         * insert 구문 변경해서 오류 뜰 수 있음. 원문이랑 확인하면서 수정할 것.
         * 다시 원문대로 바꿔 줌 DatabaseHelper클래스에서 코드가 꼬이기때문...
         */
        if(database != null) {
            String sql = "insert into customer(name, age, phone) values (?,?,?)";
            Object[] params = {name, age, phone};
            database.execSQL(sql, params); //두번째 파라미터를 이런식으로 객체를 전달하면 sql문의 ?를 이 params에 있는 데이터를 물음표를 대체해준다.
//            Toast.makeText(getApplicationContext(), "데이터베이스 추가함", Toast.LENGTH_SHORT).show();
            println("데이터베이스 추가함");
        } else {
//            Toast.makeText(getApplicationContext(), "데이터베이스를 먼저 오픈하세요.", Toast.LENGTH_SHORT).show();
            println("데이터베이스를 먼저 오픈하세요");
        }
    }

    public void selectData(String tableName) {
//        Toast.makeText(getApplicationContext(), "selectData() 호출됨", Toast.LENGTH_SHORT).show();
        println("selectData() 호출됨");
        if(database != null) {
            String sql = "select name, age, phone from " + tableName;
            /**
             * Cursor클래스에 관해 검색해볼 것.
             */
            Cursor cursor = database.rawQuery(sql, null); //파라미터는 없으니까 null값 넣어주면된다.
//            Toast.makeText(getApplicationContext(), "조회된 데이터 개수: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
            println("조회된 데이터개수: " + cursor.getCount());
            for(int i=0; i<cursor.getCount(); i++) {
                cursor.moveToNext(); //이걸 해줘야 다음 레코드로 넘어가게된다.
                /**
                 * 원문에선 age를 int로 구현했지만 이 코드에선 String으로 구현했고, 꼬이는 코드가 없도록 주의할 것!
                 */
                String name = cursor.getString(0); //첫번째 칼럼을 뽑아줌
                String age = cursor.getString(1);
                String phone = cursor.getString(2);
                println("#" + i + " -> " + name + ", " + age + ", " + phone);
            }
            cursor.close(); //cursor라는 것도 실제 데이터베이스 저장소를 접근하는 것이기 때문에 자원이 한정되어있다. 그러므로 웬만하면 마지막에 꼭 닫아줘야한다.
        }
    }

    public void println(String data) {
        textView.append(data + "\n");
    }




}
