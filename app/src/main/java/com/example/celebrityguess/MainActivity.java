package com.example.celebrityguess;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls= new ArrayList<String>();
    ArrayList<String> celebnames= new ArrayList<String>();

    int chosenceleb=0;
    int locationofCorrectAnswer=0;
    String [] answers= new String[4];

    ImageView imageView;

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationofCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct Answer",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Wrong It was " + celebnames.get(chosenceleb),Toast.LENGTH_SHORT).show();
        }
        CreateNewQuestion();
    }
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    public class DownloadTask extends AsyncTask<String, Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try{
                url=new URL(urls[0]);

                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data= reader.read();
                while(data!=-1){
                    char current =(char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

 public void CreateNewQuestion(){
     Random random= new Random();
     chosenceleb=random.nextInt(celebUrls.size());

     ImageDownloader imagetask= new ImageDownloader();
     Bitmap celebImage;

     try {
         celebImage=imagetask.execute(celebUrls.get(chosenceleb)).get();
     } catch (ExecutionException e) {
         throw new RuntimeException(e);
     } catch (InterruptedException e) {
         throw new RuntimeException(e);
     }
     imageView.setImageBitmap(celebImage);
     locationofCorrectAnswer=random.nextInt(4);
     int incorrectAnswerLocation;
     for(int i=0;i<4;i++){
         if(i==locationofCorrectAnswer){
             answers[i]=celebnames.get(chosenceleb);
         }else{
             incorrectAnswerLocation=random.nextInt(celebUrls.size());
             while(incorrectAnswerLocation==chosenceleb){
                 incorrectAnswerLocation=random.nextInt(celebUrls.size());
             }
             answers[i]=celebnames.get(incorrectAnswerLocation);

         }
     }

     button1.setText(answers[0]);
     button2.setText(answers[1]);
     button3.setText(answers[2]);
     button4.setText(answers[3]);
 }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView =(ImageView) findViewById(R.id.imageView);
        button1=(Button) findViewById(R.id.button1);
        button2=(Button) findViewById(R.id.button2);
        button3=(Button) findViewById(R.id.button3);
        button4=(Button) findViewById(R.id.button4);

        DownloadTask task= new DownloadTask();
        String result = null;

        try {
            result=task.execute("https://www.fabhotels.com/blog/monuments-in-india/").get();
            String[] splitresult=result.split("<p><strong> Old Goa Rd, Bainguinim, Goa</p>");

            Pattern p= Pattern.compile("\" src=\"(.*?)\"");
            Matcher m=p.matcher(splitresult[0]);

            while(m.find()){
                celebUrls.add(m.group(1));
            }

            p=Pattern.compile("jpg\" alt=\"(.*?)\"");
            m=p.matcher(splitresult[0]);

            while(m.find()){
                celebnames.add(m.group(1));
            }
           // Log.i("Contents of URL", result);

            Random random= new Random();
            chosenceleb=random.nextInt(celebUrls.size());

            ImageDownloader imagetask= new ImageDownloader();
            Bitmap celebImage;

            celebImage=imagetask.execute(celebUrls.get(chosenceleb)).get();
            imageView.setImageBitmap(celebImage);
            locationofCorrectAnswer=random.nextInt(4);
            int incorrectAnswerLocation;
            for(int i=0;i<4;i++){
                if(i==locationofCorrectAnswer){
                    answers[i]=celebnames.get(chosenceleb);
                }else{
                    incorrectAnswerLocation=random.nextInt(celebUrls.size());
                    while(incorrectAnswerLocation==chosenceleb){
                        incorrectAnswerLocation=random.nextInt(celebUrls.size());
                    }
                    answers[i]=celebnames.get(incorrectAnswerLocation);

                }
            }

            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}