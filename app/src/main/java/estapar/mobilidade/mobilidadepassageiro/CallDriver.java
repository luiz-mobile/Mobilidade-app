package estapar.mobilidade.mobilidadepassageiro;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallDriver extends AppCompatActivity {
    CircleImageView imgAvatar;
    TextView tvName, tvPhone, tvRate;
    Button btnCallDriver;

    String driverID;
    LatLng lastLocation;

    IFCMService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_driver);
        mService=Common.getFCMService();

        imgAvatar=(CircleImageView)findViewById(R.id.imgAvatar);
        tvName=findViewById(R.id.tvDriverName);
        tvPhone=findViewById(R.id.tvPhone);
        tvRate=findViewById(R.id.tvRate);
        btnCallDriver=findViewById(R.id.btnCallDriver);

        if(getIntent()!=null){
            driverID=getIntent().getStringExtra("driverID");
            double lat=getIntent().getDoubleExtra("lat", 0.0);
            double lng=getIntent().getDoubleExtra("lng", 0.0);
            lastLocation=new LatLng(lat, lng);
            loadDriverInfo(driverID);
        }else finish();
        btnCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(driverID!=null && !driverID.isEmpty())
                    Common.sendRequestToDriver(Common.driverID, mService, getApplicationContext(),
                            new LatLng(lastLocation.latitude, lastLocation.longitude));
            }
        });
    }

    private void loadDriverInfo(String driverID) {
        FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                .child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);

                if((user != null ? user.getAvatarUrl ( ) : null) !=null &&
                        !TextUtils.isEmpty(user.getAvatarUrl()))
                    Picasso.get().load(user.getAvatarUrl()).into(imgAvatar);
                tvName.setText(user != null ? user.getName ( ) : null);
                tvPhone.setText(user != null ? user.getPhone ( ) : null);
                tvRate.setText(user != null ? user.getRates ( ) : null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
