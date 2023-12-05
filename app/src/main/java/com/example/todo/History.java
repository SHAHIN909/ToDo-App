package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class History extends AppCompatActivity {

    ListView listView;
    SharedPreferences sh;
    String sImage;
    CircleImageView dp;
    private ArrayList<Tasks> todolist;
    private  CustomTodoAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        sh = getSharedPreferences("imagedb", MODE_PRIVATE);
        dp=findViewById(R.id.dpimg2);
        sImage=sh.getString("image","");
        byte[] bytes= Base64.decode(sImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        dp.setImageBitmap(bitmap);
        todolist=new ArrayList<>();
        listView=findViewById(R.id.flist1);
        adapter = new CustomTodoAdapter(this,todolist);
        listView.setAdapter(adapter);

    }
    protected void onStart(){
         super.onStart();
         loaddata();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public  void loaddata(){
         FirebaseDatabase.getInstance().getReference("taskList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todolist.clear();

                for(DataSnapshot data:snapshot.getChildren()){
                    Tasks t= data.getValue(Tasks.class);
//                      System.out.println(t.tittle);
                        if(t.done==true) todolist.add(t);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(History.this, "Couldn't found data", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                String Message="Do you want to delete this task?";
                showDialog(Message,position);
                return true;
            }
        });


    }
    public  void showDialog(String message,int pos){
        AlertDialog.Builder alert = new  AlertDialog.Builder(this);
        alert.setTitle("Confirm");
        alert.setCancelable(true);

        alert.setMessage(message);
        alert.setPositiveButton("delete", (DialogInterface.OnClickListener) (dialog, which) -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList");
            databaseReference.child(todolist.get(pos).id).removeValue();
            adapter.notifyDataSetChanged();
            dialog.cancel();
        });
        alert.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {

            dialog.cancel();
        });
        AlertDialog art=alert.create();
        art.show();
    }
}