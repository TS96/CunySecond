package com.tareksaidee.cunysecond;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAccount extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private String userUID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference updateUserDatabaseRef;
    private ChildEventListener mChildEventListener;
    private MiniCourseAdapter currentCoursesAdapter;
    private MiniCourseAdapter historyAdapter;
    private TextView userNameView;
    private TextView userDOB;
    private TextView userAddress;
    private TextView userPhone;
    private TextView userGPA;
    private TextView userCredits;
    private TextView userMoneyDue;
    private TextView userMajor;
    private RecyclerView currentCoursesView;
    private RecyclerView pastCoursesView;
    private Student student;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        mFirebaseAuth = FirebaseAuth.getInstance();
        userNameView = (TextView) findViewById(R.id.user_name);
        userDOB = (TextView) findViewById(R.id.user_dob);
        userAddress = (TextView) findViewById(R.id.user_address);
        userPhone = (TextView) findViewById(R.id.user_phone);
        userGPA = (TextView) findViewById(R.id.user_gpa);
        userCredits = (TextView) findViewById(R.id.user_credits);
        userMoneyDue = (TextView) findViewById(R.id.user_money_due);
        userMajor = (TextView) findViewById(R.id.user_major);
        currentCoursesView = (RecyclerView) findViewById(R.id.student_current_courses);
        pastCoursesView = (RecyclerView) findViewById(R.id.student_course_history);
        userUID = mFirebaseAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");
        updateUserDatabaseRef = mFirebaseDatabase.getReference().child("users").child(mFirebaseAuth.getCurrentUser().getUid());
        currentCoursesAdapter = new MiniCourseAdapter(this);
        historyAdapter = new MiniCourseAdapter(this);
        pastCoursesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        currentCoursesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        currentCoursesView.setAdapter(currentCoursesAdapter);
        pastCoursesView.setAdapter(historyAdapter);

    }

    void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    student = dataSnapshot.getValue(Student.class);
                    userNameView.setText(student.getFirstName() + " " + student.getLastName());
                    userDOB.setText(student.getDOB());
                    userAddress.setText(student.getStreet() + "\n" + student.getCity()
                            + ", NY " + student.getZipcode());
                    userPhone.setText(student.getPhoneNumber());
                    userGPA.setText(student.getGPA() + "");
                    userCredits.setText(student.getTotalCredits() + "");
                    userMoneyDue.setText(student.getMoneyDue() + "");
                    userMajor.setText(student.getMajor());
                    if (student.getCurrentCourses() == null)
                        currentCoursesAdapter.setMiniCourses(new ArrayList<MiniCourse>());
                    else
                        currentCoursesAdapter.setMiniCourses(student.getCurrentCourses());
                    if (student.getCourseHistory() == null)
                        historyAdapter.setMiniCourses(new ArrayList<MiniCourse>());
                    else
                        historyAdapter.setMiniCourses(student.getCourseHistory());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    student = dataSnapshot.getValue(Student.class);
                    userNameView.setText(student.getFirstName() + " " + student.getLastName());
                    userDOB.setText(student.getDOB());
                    userAddress.setText(student.getStreet() + "\n" + student.getCity()
                            + ", NY " + student.getZipcode());
                    userPhone.setText(student.getPhoneNumber());
                    userGPA.setText(student.getGPA() + "");
                    userCredits.setText(student.getTotalCredits() + "");
                    userMoneyDue.setText(student.getMoneyDue() + "");
                    userMajor.setText(student.getMajor());
                    if (student.getCurrentCourses() == null)
                        currentCoursesAdapter.setMiniCourses(new ArrayList<MiniCourse>());
                    else
                        currentCoursesAdapter.setMiniCourses(student.getCurrentCourses());
                    if (student.getCourseHistory() == null)
                        historyAdapter.setMiniCourses(new ArrayList<MiniCourse>());
                    else
                        historyAdapter.setMiniCourses(student.getCourseHistory());
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mUserDatabaseReference.orderByKey().equalTo(userUID).addChildEventListener(mChildEventListener);
        }
    }

    public void declareMajor(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Declare Major");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setTitle("Enter Major Name");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                student.setMajor(input.getText().toString());
                updateUserDatabaseRef.setValue(student);
                Toast.makeText(input.getContext(), "Major Updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachReadListener();
        historyAdapter.clear();
        currentCoursesAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    void detachReadListener() {
        if (mChildEventListener != null) {
            mUserDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
