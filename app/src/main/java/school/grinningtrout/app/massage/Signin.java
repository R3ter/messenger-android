package school.grinningtrout.app.massage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        final FirebaseAuth auth=FirebaseAuth.getInstance();
        final Snackbar snackbar= Snackbar.make(findViewById(R.id.root), "Signing In...", Snackbar.LENGTH_LONG);
        final SharedPreferences pre=getSharedPreferences("save",MODE_PRIVATE);
        final SharedPreferences.Editor editor =pre.edit();

        final EditText text=findViewById(R.id.editText2);
        final EditText pass=findViewById(R.id.editText3);
        text.setText(pre.getString("name",null));
        pass.setText(pre.getString("pass",null));
        Button signin=findViewById(R.id.button5);
        final Switch save=findViewById(R.id.switch1);

        boolean silent = pre.getBoolean("switchkey", false);
        save.setChecked(silent);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Signin.this, signup.class);
                startActivity(myIntent);
            }
        });

        Button button=findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().isEmpty()||pass.getText().toString().isEmpty()){
                    Toast.makeText(Signin.this,"Enter your email and password",Toast.LENGTH_LONG).show();
                    return;
                }
                snackbar.show();
                auth.signInWithEmailAndPassword(text.getText().toString(),pass.getText().toString()).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                snackbar.dismiss();
                                Toast.makeText(Signin.this,""+e.toString().replaceAll(":*,", "")
                                        ,Toast.LENGTH_LONG).show();
                            }
                        }
                ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        snackbar.dismiss();
                        if (save.isChecked()){
                            editor.putString("name",text.getText().toString()).apply();
                            editor.putString("pass",pass.getText().toString()).apply();
                            editor.commit();
                        }


                        Intent myIntent = new Intent(Signin.this, MainActivity.class);
                        startActivity(myIntent);


                    }
                });

            }
        });
        save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("switchkey",isChecked).apply();
                 if (!isChecked){
                    editor.putString("name",null).apply();
                    editor.putString("pass",null).apply();}
            }
        });
    }
}
