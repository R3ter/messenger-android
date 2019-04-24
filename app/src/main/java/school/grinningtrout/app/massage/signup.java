package school.grinningtrout.app.massage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class signup extends AppCompatActivity {
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("names");
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("passwords");
ArrayList<String> names;
int i=0;
     FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent myIntent = new Intent(signup.this, Signin.class);
        startActivity(myIntent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Snackbar snackbar= Snackbar.make(findViewById(R.id.root), "Signing Up Please WAit....", Snackbar.LENGTH_LONG);
        Button button=findViewById(R.id.button3);
        Button signin=findViewById(R.id.button5);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(signup.this, Signin.class);
                startActivity(myIntent);
            }
        });
        final EditText name=findViewById(R.id.editText2);
        final EditText email=findViewById(R.id.editText3);
        final EditText pass=findViewById(R.id.editText4);
        final EditText repass=findViewById(R.id.editText5);
        names=new ArrayList<>();

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator(); it.hasNext(); ){
                    i = i + 1;
                    names.add(it.next().getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (names.contains(name.getText().toString())){
                    Toast.makeText(signup.this,"Name is already taken",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!email.getText().toString().isEmpty()&&!pass.getText().toString().isEmpty()
                        &&!repass.getText().toString().isEmpty()&&!name.getText().toString().isEmpty()){

                    if (repass.getText().toString().equals(pass.getText().toString())){

                        if (name.getText().toString().length()<=4){
                            Toast.makeText(signup.this,"Name is too short",Toast.LENGTH_LONG).show();
                            return;
                        }
                    snackbar.show();



                mAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {snackbar.dismiss();
                                Toast.makeText(signup.this,"Failed "+e,Toast.LENGTH_LONG).show();

                            }
                        }
                ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        snackbar.dismiss();
                        rootRef.child(email.getText().toString().replace("@"," AT ")
                                .replace("."," DOT ")
                        ).setValue(name.getText().toString());

                        Toast.makeText(signup.this,"Sign Up Success",Toast.LENGTH_LONG).show();
                        root.child(email.getText().toString().replace("@"," AT ")
                                        .replace("."," DOT ")).setValue(pass.getText().toString());

                        Intent myIntent = new Intent(signup.this, Signin.class);
                        startActivity(myIntent);

                    }
                });

                }else if (!repass.getText().toString().equals(pass.getText().toString())){
                        Toast.makeText(signup.this,"Password does not match",Toast.LENGTH_LONG).show();

                    }}
                else
                {   Toast.makeText(signup.this,"Fill all blanks",Toast.LENGTH_LONG).show();}

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser()!=null){
        mAuth.signOut();}
        names.clear();
    }
}
