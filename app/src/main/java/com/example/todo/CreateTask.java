package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateTask extends Activity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Tasks tasks;
    CircleImageView dp;


    Button save;
    SimpleDateFormat isvalid;
    String id="";
    long dms=0;
    boolean done=false;

    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private Notification.Builder builder;
    private final String channelId = "i.apps.notifications";
    private final String description = "Test notification";


     @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
//        save=findViewById(R.id.btnsave);
//        EditText task=findViewById(R.id.ettask);
////        EditText date=findViewById(R.id.etDate);
//
//         notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//         SharedPreferences sh = getSharedPreferences("imagedb", MODE_PRIVATE);
////         dp=findViewById(R.id.dpimg3);
//         String sImage=sh.getString("image","");
//         byte[] bytes= Base64.decode(sImage,Base64.DEFAULT);
//         Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//         dp.setImageBitmap(bitmap);
//
//
//         firebaseDatabase = FirebaseDatabase.getInstance();
//
//         Intent i=getIntent();
//         if (i.hasExtra("id")){
//            id=i.getStringExtra("id");
//            task.setText(i.getStringExtra("tittle"));
//            long d=i.getLongExtra("date",0);
//             isvalid = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//              try {
//                  Date dd= new Date(d);
//                  date.setText(""+isvalid.format(dd));
//
//              }catch (Exception ee){
//
//              }
//             done=i.getBooleanExtra("done",false);
//         }
//
//
//         save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String tsk=task.getText().toString().trim();
//                String dt=date.getText().toString().trim();
//
//                String err="";
//
//                isvalid = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//
//                try {
//                    Date valdat = isvalid.parse(dt);
//                    Date cdate = new Date();
//                    isvalid.format(cdate);
//                    dms = valdat.getTime();
//                    if (valdat.compareTo(cdate) < 0 && err.isEmpty()) {
//                        err += "Invalid date1!\n";
//                    }
//                } catch (ParseException e) {
//                    System.out.println(e);
//                    if ((err.isEmpty())) err += "Invalid date0!\n";
//                }
//                if(err.isEmpty()&&!valtask(tsk))err+="Invalid task";
//
//                if(err.isEmpty()){
//                    if(id.isEmpty()){
//                        id+=System.currentTimeMillis();
//                        storedata(id,tsk,dms,done);
//                    }
//                    else storedata(id,tsk,dms,done);
//                    startActivity(new Intent(CreateTask.this,TodoList.class));
//                }
//                else {
//                    showErrorDialog(err);
//                }
//
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void storedata(String id, String task, long dms, boolean done){
         tasks= new Tasks(id,task,dms,done);
         databaseReference = firebaseDatabase.getReference("taskList").child(id);
     databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 databaseReference.setValue(tasks);
                 Toast.makeText(CreateTask.this,"Value added",Toast.LENGTH_SHORT).show();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(CreateTask.this,"Failed to added",Toast.LENGTH_SHORT).show();

             }
         });
   }
 void updatedata(String id,String task,long dms,boolean done){

    }

    boolean valtask(String task){
        int len=task.length();
        if(len<4||len>16)return false;
        for(int i=0;i<len;i++){
              char c=task.charAt(i);
              if(c>='A'&&c<='Z')continue;
              else if(c>='a'&&c<='z')continue;
              else if(c==' ')continue;
              else if(c=='.')continue;
              else if(c=='#')continue;
              else if(c>='0'&&i<='9')continue;
              else if(c=='*')continue;
              else return false;

        }

         return true;
    }


    private void showErrorDialog(String error){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(error);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        AlertDialog alert=builder.create();
        alert.show();

    }
}