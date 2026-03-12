package com.example.healthpriority;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorDetailsActivity extends AppCompatActivity {

    private String[][] doctor_details1 = {
            {"Doctor Name: Dr. Anita Sharma", "Hospital Address: Baner", "Exp :10yrs", "Mobile No:9090909090", "500"},
            {"Doctor Name: Dr. Rajesh Patil", "Hospital Address: Aundh", "Exp :8yrs", "Mobile No:8080808080", "450"},
            {"Doctor Name: Dr. Meera Joshi", "Hospital Address: Wakad", "Exp :12yrs", "Mobile No:7070707070", "600"},
            {"Doctor Name: Dr. Alok Jain", "Hospital Address: Shivajinagar", "Exp :15yrs", "Mobile No:6060606060", "700"},
            {"Doctor Name: Dr. Kavita Rao", "Hospital Address: Kothrud", "Exp :9yrs", "Mobile No:5050505050", "400"}
    };

    private String[][] doctor_details2 = {
            {"Doctor Name: Dr. Sneha Rane", "Hospital Address: Camp", "Exp :5yrs", "Mobile No:9999999991", "300"},
            {"Doctor Name: Dr. Vikram Verma", "Hospital Address: Deccan", "Exp :7yrs", "Mobile No:9999999992", "350"},
            {"Doctor Name: Dr. Aarti Nair", "Hospital Address: Karve Nagar", "Exp :6yrs", "Mobile No:9999999993", "320"},
            {"Doctor Name: Dr. Rohan Mehta", "Hospital Address: Hadapsar", "Exp :9yrs", "Mobile No:9999999994", "380"},
            {"Doctor Name: Dr. Priya Desai", "Hospital Address: Kondhwa", "Exp :4yrs", "Mobile No:9999999995", "300"}
    };

    private String[][] doctor_details3 = {
            {"Doctor Name: Dr. Kunal Shah", "Hospital Address: Erandwane", "Exp :11yrs", "Mobile No:8888888881", "1000"},
            {"Doctor Name: Dr. Neha Bhosale", "Hospital Address: Pashan", "Exp :13yrs", "Mobile No:8888888882", "1200"},
            {"Doctor Name: Dr. Nitin Kulkarni", "Hospital Address: Bibwewadi", "Exp :10yrs", "Mobile No:8888888883", "950"},
            {"Doctor Name: Dr. Rachana Jagtap", "Hospital Address: Narhe", "Exp :14yrs", "Mobile No:8888888884", "1100"},
            {"Doctor Name: Dr. Tanmay Gokhale", "Hospital Address: Sinhagad Rd", "Exp :9yrs", "Mobile No:8888888885", "1050"}
    };

    private String[][] doctor_details4 = {
            {"Doctor Name: Dr. Suresh Kadam", "Hospital Address: Vishrantwadi", "Exp :6yrs", "Mobile No:7777777771", "400"},
            {"Doctor Name: Dr. Ayesha Khan", "Hospital Address: Swargate", "Exp :5yrs", "Mobile No:7777777772", "450"},
            {"Doctor Name: Dr. Ravi Jadhav", "Hospital Address: Fatima Nagar", "Exp :8yrs", "Mobile No:7777777773", "500"},
            {"Doctor Name: Dr. Manisha Kale", "Hospital Address: Bavdhan", "Exp :7yrs", "Mobile No:7777777774", "420"},
            {"Doctor Name: Dr. Ganesh Bhosale", "Hospital Address: Parvati", "Exp :9yrs", "Mobile No:7777777775", "480"}
    };

    private String[][] doctor_details5 = {
            {"Doctor Name: Dr. Anjali Thakur", "Hospital Address: Hinjewadi", "Exp :15yrs", "Mobile No:6666666661", "900"},
            {"Doctor Name: Dr. Abhay Dubey", "Hospital Address: Magarpatta", "Exp :18yrs", "Mobile No:6666666662", "950"},
            {"Doctor Name: Dr. Shreya Naik", "Hospital Address: Viman Nagar", "Exp :12yrs", "Mobile No:6666666663", "870"},
            {"Doctor Name: Dr. Mahesh Rane", "Hospital Address: Dhanori", "Exp :10yrs", "Mobile No:6666666664", "930"},
            {"Doctor Name: Dr. Ritu Malhotra", "Hospital Address: Kharadi", "Exp :14yrs", "Mobile No:6666666665", "910"}
    };

    String[][] doctor_details = {};
    TextView tv;
    Button btn;
    ArrayList<HashMap<String, String>> list;
    HashMap<String, String> item;
    SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_details);

        tv = findViewById(R.id.textViewDDTitle);
        btn = findViewById(R.id.buttonBack);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        tv.setText(title);

        if (title.equals("Family Physicians"))
            doctor_details = doctor_details1;
        else if (title.equals("Dietician"))
            doctor_details = doctor_details2;
        else if (title.equals("Surgeon"))
            doctor_details = doctor_details3;
        else if (title.equals("Dentist"))
            doctor_details = doctor_details4;
        else
            doctor_details = doctor_details5;

        btn.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        for (String[] doc : doctor_details) {
            item = new HashMap<>();
            item.put("line1", doc[0]);
            item.put("line2", doc[1]);
            item.put("line3", doc[2]);
            item.put("line4", doc[3]);
            item.put("line5", "Cons Fees: " + doc[4] + "/-");
            list.add(item);
        }

        sa = new SimpleAdapter(this, list,
                R.layout.multi_lines,
                new String[]{"line1", "line2", "line3", "line4", "line5"},
                new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e});

        ListView lst = findViewById(R.id.listViewDD);
        lst.setAdapter(sa);

        lst.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(DoctorDetailsActivity.this, BookAppointmentActivity.class);
            intent.putExtra("text1", title);
            intent.putExtra("text2", doctor_details[position][0]); // Name
            intent.putExtra("text3", doctor_details[position][1]); // Address
            intent.putExtra("text4", doctor_details[position][3]); // Mobile
            intent.putExtra("text5", doctor_details[position][4]); // Fee
            startActivity(intent);
        });
    }
}
