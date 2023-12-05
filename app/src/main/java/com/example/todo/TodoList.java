package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TodoList extends Activity {


    FloatingActionButton add,history;
    ListView listView;
    SharedPreferences sh;
    String sImage;
    CircleImageView dp;
   private ArrayList<Tasks>todolist;
    private  CustomTodoAdapter adapter;
    BottomSheetDialog dialog;
    ImageView dateicon;
    boolean done=false;
    String id="",dt="";
    int hour=0,min=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        add=findViewById(R.id.add);
        sh = getSharedPreferences("imagedb", MODE_PRIVATE);
        dp=findViewById(R.id.dpimg1);
        sImage=sh.getString("image","");
        byte[] bytes= Base64.decode(sImage,Base64.DEFAULT);
         Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
         dp.setImageBitmap(bitmap);
         todolist=new ArrayList<>();
         listView=findViewById(R.id.flist);
         history=findViewById(R.id.history);
         adapter = new CustomTodoAdapter(this,todolist);
         listView.setAdapter(adapter);
         dialog = new BottomSheetDialog(this);
          NotificationChannel();

         history.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(TodoList.this, History.class));
             }
         });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createdialog("","");
                dialog.show();
             }
        });
    }
    protected void onStart(){
         super.onStart();
         loaddata();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();

    }

    public  void loaddata(){

           FirebaseDatabase.getInstance().getReference("taskList").addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  todolist.clear();
//                  System.out.println(snapshot.getChildrenCount());
                  for(DataSnapshot data:snapshot.getChildren()){
                      Tasks t= data.getValue(Tasks.class);
//                      System.out.println(t.tittle);
                      if(t.done==false) todolist.add(t);
                   }
                   Collections.sort(todolist, new Comparator<Tasks>() {
                       @Override
                       public int compare(Tasks tasks, Tasks t1) {
                           if(tasks.date<t1.date)return 1;
                           if(tasks.date>t1.date)return -1;
                           return 0;
                       }
                   });
                  adapter.notifyDataSetChanged();

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                  Toast.makeText(TodoList.this, "Couldn't found data", Toast.LENGTH_SHORT).show();
              }
          });


          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent,final View view, int position, long idd) {
                  System.out.println("pos = "+position);
                 id=todolist.get(position).id;
                 long dt=todolist.get(position).date;
                 SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm MM-dd-yyyy");
                  Date d = new Date(dt);
                  String dte =(simpleDateFormat.format(d));
                  createdialog(todolist.get(position).tittle,dte);
                  dialog.show();
              }
          });
         listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
             @Override
             public boolean onItemLongClick(AdapterView<?> parent,final View view, int position, long id) {
                 String Message="Delete or Mark_done ";
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
            delete(pos);
            adapter.notifyDataSetChanged();
            dialog.cancel();
        });
        alert.setNegativeButton("Mark", (DialogInterface.OnClickListener) (dialog, which) -> {
            Tasks t= new Tasks(todolist.get(pos).id,todolist.get(pos).tittle,todolist.get(pos).date,todolist.get(pos).done);
            t.done=true;
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("taskList");
            databaseReference.child(todolist.get(pos).id).setValue(t);
            adapter.notifyDataSetChanged();
             dialog.cancel();
        });
        AlertDialog art=alert.create();
        art.show();
    }
    public  void delete(int position){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("taskList");
        databaseReference.child(todolist.get(position).id).removeValue();
        adapter.notifyDataSetChanged();


     }
    public  void createdialog(String task,String date){
        View view = getLayoutInflater().inflate(R.layout.activity_create_task,null,false);
        dateicon = view.findViewById(R.id.dateimg);
        TextView dateView=view.findViewById(R.id.tvdate);
        EditText tittle = view.findViewById(R.id.ettask);
        if(task.length()>0)tittle.setText(task);
        if(date.length()>0)dateView.setText(date);
        Button save=view.findViewById(R.id.btnsave);
        task="";
        date="";
        final Calendar calendar =Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        dateicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener onTimeSetListener =  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String time=String.format(Locale.getDefault(),"%02d:%02d",hour,min);
                        dt=time+" "+dt;
                        dateView.setText(dt);
                    }
                };
                int style= android.app.AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(TodoList.this,style,onTimeSetListener,hour,min,true);
                timePickerDialog.setTitle("Set Time");
                timePickerDialog.show();


                DatePickerDialog dlog= new DatePickerDialog(TodoList.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {

                        month=month+1;
                        dt=month+"-"+dayofmonth+"-"+year;
                    }
                },year,month,day);
                dlog.show();

            }


        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String newTask=tittle.getText().toString().trim();
               String newDate=dateView.getText().toString().trim();
               String err="";
               if(!valid(newTask))err+="Invalid Task!!\n";
               long dms=0;
               SimpleDateFormat dt= new SimpleDateFormat("HH:mm MM-dd-yyyy");
               try {
                   Date d= dt.parse(newDate);
                   Date cdate= new Date();
                    dms=d.getTime();
                   if(d.compareTo(cdate)<0&&err.isEmpty())err+="Invalid date!!\n";

               }catch (Exception e){
                         err+="Invlid date format!!\n";
               }

               if(err.isEmpty()){
                      if(id.isEmpty())id+=System.currentTimeMillis();
                      Tasks t= new Tasks(id,newTask,dms,done);
                      DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("taskList");
                      databaseReference.child(id).setValue(t);
                      id="";
                      adapter.notifyDataSetChanged();
                      setNotification(dms);
                      dialog.cancel();

               }
               else {
                     showErrorDialog(err);
               }

            }
        });
        dialog.setContentView(view);

    }

    boolean valid(String task){
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
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setMessage(error);
        builder.setTitle("Error");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog alert=builder.create();
        alert.show();

    }

    private void NotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "toDo notification CHANNEL";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Notification", name, importance);
            channel.setDescription(description);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
    private  void setNotification(long time){
        Intent intent = new Intent(TodoList.this, MemoBroadcast.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE; // Choose FLAG_IMMUTABLE if it's more appropriate for your use case
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoList.this, 0, intent, flags);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent);
        }
    }

}