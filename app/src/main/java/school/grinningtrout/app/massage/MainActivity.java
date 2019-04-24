package school.grinningtrout.app.massage;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    EditText textView;
    ArrayList<String> array = new ArrayList<>();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference chiled = rootRef.child("waleed");
    DatabaseReference name = rootRef.child("names");
    FirebaseAuth auth;
    int i;

    Object value;
    boolean nointernt;


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        final Date currentTime = Calendar.getInstance().getTime();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;


        textView = findViewById(R.id.editText);
        Button button = findViewById(R.id.button2);
        Button signout = findViewById(R.id.button3);
        final ListView list = findViewById(R.id.list);
        list.setBackgroundColor(5);
        final ImageView imgView= findViewById(R.id.image);
        imgView.setAlpha(1f);

        textView.setTextColor(ColorStateList.valueOf(20));
        final View activityRootView = findViewById(R.id.root);
        final Snackbar snackbar= Snackbar.make(activityRootView, "No Internet", Snackbar.LENGTH_LONG);


        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(MainActivity.this, 200)
                        &&list.getAdapter().getCount() >5) { list.setY(height-3500f);}
                else if (heightDiff < dpToPx(MainActivity.this, 200)) { list.setY(0);}
            }
        });



        final CountDownTimer timer=new CountDownTimer(100,60) {
            @Override
            public void onTick(long millisUntilFinished) {
                i=i+1;
                if (value == null) {
                    imgView.setAlpha(1f);
                    imgView.setRotation(i*20);
                }
                if (!isInternetOn(MainActivity.this)) {
                    snackbar.show();
                    if (value == null) {
                        imgView.setAlpha(1f);
                        nointernt = true;
                    } else {
                        imgView.setAlpha(0f);
                        nointernt = false;
                    }
                }
                else if (isInternetOn(MainActivity.this))
                {
                if (value != null){
                    imgView.setAlpha(0f);
                    nointernt = false;}}
                    render();
            }
            @Override
            public void onFinish() {
                this.start();
            }
        } ;

         timer.start();

         signout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 auth.signOut();
                 Intent myIntent = new Intent(MainActivity.this, Signin.class);
                 startActivity(myIntent);
             }
         });


         if (nointernt){return;}

        if (auth.getCurrentUser()==null){
            Intent myIntent = new Intent(MainActivity.this, Signin.class);
            startActivity(myIntent);
            Toast.makeText(MainActivity.this,"you have to Sign In first",Toast.LENGTH_LONG).show();
        }



        if (auth.getCurrentUser()!=null){
            name.child(auth.getCurrentUser().getEmail()
                    .replace("@"," AT ")
                    .replace("."," DOT ")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    value=dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });}


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
        list.setAdapter(adapter);

//        if (auth.getCurrentUser().getEmail().
//                isEmpty()) {
//            user = "gust";
//        } else {
//            user = auth.getCurrentUser().getEmail();
//        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (value==null){
                    if (auth.getCurrentUser()==null){
                      return;
                    }
                  else if (auth.getCurrentUser().getEmail().isEmpty()){
                        Intent myIntent = new Intent(MainActivity.this, Signin.class);
                        startActivity(myIntent);
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:school.grinningtrout.app.massage"));
                        startActivity(intent);
                        return;
                    }
                    auth.getCurrentUser().delete();
                    Intent myIntent = new Intent(MainActivity.this, Signin.class);
                    startActivity(myIntent);
                    return;
                    }


                if (textView.getText().toString().isEmpty())
                {return;}
                if (!nointernt){
                chiled.push().setValue(DateFormat.format("dd/MM 'at' (h:mm a)",currentTime)+"\n \n "+
                        value.toString()+" : "+textView.getText() + " \n").addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e+"",Toast.LENGTH_SHORT).show();

                    }
                });
                textView.setText("");}
                if (nointernt){
                    Toast.makeText(MainActivity.this,"check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }

        });

        chiled.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                array.add(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
                list.getOverscrollHeader();
                list.smoothScrollToPosition(list.getMaxScrollAmount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                array.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public static boolean isInternetOn(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }





public void render(){

}









}
