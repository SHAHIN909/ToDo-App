package com.example.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CustomTodoAdapter extends ArrayAdapter<Tasks> {

    private final Context context;
    private final ArrayList<Tasks> events;

    public CustomTodoAdapter(@NonNull Context context, @NonNull ArrayList<Tasks> events) {
        super(context, -1, events);
        this.context = context;
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.cardview, parent, false);
        CheckBox check= rowView.findViewById(R.id.task);
        TextView date=rowView.findViewById(R.id.time);
         Tasks e = events.get(position);

        try {
            SimpleDateFormat simple = new SimpleDateFormat("HH:mm MMM dd, yyyy");
            Date d = new Date(e.date);
            date.setText(simple.format(d));
        }catch (Exception ee){
             System.out.println(ee);
        }
        if(e.done==true)check.setChecked(true);
        check.setText(e.tittle);
         return rowView;
    }
    @Override
    public void notifyDataSetChanged() {
        //do your sorting here
        Collections.sort(events, new Comparator<Tasks>() {
            @Override
            public int compare(Tasks tasks, Tasks t1) {
                if(tasks.date<t1.date)return -1;
                if(tasks.date>t1.date)return 1;
                return 0;
            }
        });
        super.notifyDataSetChanged();
    }
}
