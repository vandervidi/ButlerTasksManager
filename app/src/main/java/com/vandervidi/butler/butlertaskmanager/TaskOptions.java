package com.vandervidi.butler.butlertaskmanager;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class TaskOptions extends Activity {
    private DBAdapter mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_options);
        //Get the task we want to edit via intent
        final Task task = (Task)getIntent().getSerializableExtra("taskToEdit");

       // Get imageViews references
       ImageButton imageDone = (ImageButton)findViewById(R.id.imageButtonDone);
       ImageButton imageLocation = (ImageButton) findViewById(R.id.imageButtonLocation);
       ImageButton imageEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
       ImageButton imageDelete = (ImageButton) findViewById(R.id.imageButtonDelete);

        //Edit navigate image button
        imageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task.getLat() != 0.0 && task.getLng()!= 0.0){
                    try
                    {
                        String url = "waze://?ll="+task.getLat()+","+task.getLng()+"&z=10";
                        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
                        startActivity( intent );
                    }
                    catch ( ActivityNotFoundException ex  )
                    {
                        Intent intent =
                                new Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze") );
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "You didnt set location for this task", Toast.LENGTH_LONG).show();

                }
            }
        });

        //Edit image button click event listener
        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(TaskOptions.this, EditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskToEdit",task);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //Delete image button click event listener
        imageDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mydb = new DBAdapter(TaskOptions.this);
                mydb.open();
                mydb.deleteRow(task.getId());
                mydb.close();
                finish();
                Intent intent = new Intent(TaskOptions.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Delete image button click event listener
        imageDone.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mydb = new DBAdapter(TaskOptions.this);
                mydb.open();
                mydb.deleteRow(task.getId());
                mydb.close();
                finish();
                Intent intent = new Intent(TaskOptions.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



}
