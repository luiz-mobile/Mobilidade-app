package estapar.mobilidade.mobilidadepassageiro;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {

    Toolbar toolbar;
    Location location=null;
    private GoogleMap mMap;
    Marker riderMarket, destinationMarker;
    GoogleSignInAccount account;

    DatabaseReference drivers;
    GeoFire geoFire;

    private GoogleApiClient mGoogleApiClient;

    Double currentLat;
    Double currentLng;

    private static final int REQUEST_CODE_PERMISSION=100;
    private static final int PLAY_SERVICES_REQUEST_CODE=2001;

    SupportMapFragment mapFragment;

    ImageView imgExpandable;
    Button btnRequestPickup;
    BottomSheetRiderFragment bottomSheetRiderFragment;

    int radius=1; // km
    int distance=1;
    private static final int LIMIT=3;

    IFCMService ifcmService;

    DatabaseReference driversAvailable;

    PlaceAutocompleteFragment placeLocation, placeDestination;
    AutocompleteFilter typeFilter;
    String mPlaceLocation, mPlaceDestination;

    CircleImageView imgUser;
    TextView txRiderName, tvStars;

    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView carUberX, carUberBlack;
    boolean isUberX=false;

    //Gooogle
    private GoogleApiClient googleApiClient;
    //Facebook
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedInFacebook = accessToken != null && !accessToken.isExpired();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        verifyGoogleAccount();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ifcmService=Common.getFCMService();

        location=new Location(this, new locationListener() {
            @Override
            public void locationResponse(LocationResult response) {
                // Add a icon_marker in Sydney and move the camera
                currentLat=response.getLastLocation().getLatitude();
                currentLng=response.getLastLocation().getLongitude();
                Common.currenLocation=new LatLng(response.getLastLocation().getLatitude(), response.getLastLocation().getLongitude());
                if(mPlaceLocation==null) {
                    displayLocation();
                    driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
                    driversAvailable.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            loadAllAvailableDriver(new LatLng(currentLat, currentLng));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        carUberX=findViewById(R.id.selectedUberX);
        carUberBlack=findViewById(R.id.selectedUberBlack);

        carUberX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isToggle=!isUberX;
                isUberX=true;
                if(isToggle) {
                    carUberX.setImageResource(R.drawable.car_cui_select);
                    carUberBlack.setImageResource(R.drawable.car_vip);
                }
                mMap.clear();
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
            }
        });

        carUberBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isToggle=isUberX;
                isUberX=false;
                if(isToggle) {
                    carUberX.setImageResource(R.drawable.car_cui);
                    carUberBlack.setImageResource(R.drawable.car_vip_select);
                }
                mMap.clear();
                loadAllAvailableDriver(new LatLng(currentLat, currentLng));
            }
        });

        btnRequestPickup=findViewById(R.id.btnPickupRequest);
        btnRequestPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLat!=null && currentLng!=null) {
                    if (!Common.driverFound)
                        requestPickup(Common.userID);
                    else
                        Common.sendRequestToDriver(Common.driverID, ifcmService, getApplicationContext(), Common.currenLocation);
                }
            }
        });

        placeDestination=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.placeDestination);
        placeLocation=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.placeLocation);

        typeFilter=new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        placeLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlaceLocation=place.getAddress().toString();
                mMap.clear();

                riderMarket=mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker))
                        .title("Pickup Here"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
            }

            @Override
            public void onError(Status status) {

            }
        });
        placeDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlaceDestination=place.getAddress().toString();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_marker))
                        .title("Destino"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                BottomSheetRiderFragment mBottomSheet=BottomSheetRiderFragment.newInstance(mPlaceLocation, mPlaceDestination, false);
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }

            @Override
            public void onError(Status status) {

            }
        });

        setUpLocation();
        updateFirebaseToken();
    }

    public void initDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeaderView=navigationView.getHeaderView(0);
        TextView tvName=(TextView)navigationHeaderView.findViewById(R.id.tvRiderName);
        TextView tvStars=(TextView)findViewById(R.id.tvStars);
        CircleImageView imageAvatar=(CircleImageView) navigationHeaderView.findViewById(R.id.imgAvatar);

        tvName.setText(Common.currentUser.getName());
        if(Common.currentUser.getRates()!=null &&
                !TextUtils.isEmpty(Common.currentUser.getRates()))
            tvStars.setText(Common.currentUser.getRates());

        if(isLoggedInFacebook)
            Picasso.get().load("https://graph.facebook.com/" + Common.userID + "/picture?width=500&height=500").into(imageAvatar);
        else if(account!=null)
            Picasso.get().load(account.getPhotoUrl()).into(imageAvatar);
        if(Common.currentUser.getAvatarUrl()!=null &&
                !TextUtils.isEmpty(Common.currentUser.getAvatarUrl()))
            Picasso.get().load(Common.currentUser.getAvatarUrl()).into(imageAvatar);
    }

    private void loadUser(){
        FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl)
                .child(Common.userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.currentUser=dataSnapshot.getValue(User.class);
                        initDrawer();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void verifyGoogleAccount() {
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            handleSignInResult(result);
        }else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void updateFirebaseToken() {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        final DatabaseReference tokens=db.getReference(Common.token_tbl);

        final Token token=new Token(FirebaseInstanceId.getInstance().getToken());
        if(FirebaseAuth.getInstance().getUid()!=null) tokens.child(FirebaseAuth.getInstance().getUid()).setValue(token);
        else if(account!=null) tokens.child(account.getId()).setValue(token);
        else{
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String id = object.optString("id");
                            tokens.child(id).setValue(token);
                        }
                    });
            request.executeAsync();
        }
    }

    private void requestPickup(String uid) {
        DatabaseReference dbRequest=FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeofire=new GeoFire(dbRequest);
       mGeofire.setLocation(uid, new GeoLocation(Common.currenLocation.latitude, Common.currenLocation.longitude),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
        if (riderMarket.isVisible())riderMarket.remove();
        riderMarket=mMap.addMarker(new MarkerOptions().title(getResources().getString(R.string.pickup_here)).snippet("").position(new LatLng(Common.currenLocation.latitude, Common.currenLocation.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        riderMarket.showInfoWindow();
        btnRequestPickup.setText(getResources().getString(R.string.getting_uber));
        findDriver();
    }

    private void findDriver() {
        DatabaseReference driverLocation;
        if(isUberX)
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("UberX");
        else
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Uber Black");
        GeoFire geoFire=new GeoFire(driverLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(Common.currenLocation.latitude, Common.currenLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!Common.driverFound){
                    Common.driverFound=true;
                    Common.driverID=key;
                    btnRequestPickup.setText(getApplicationContext().getResources().getString(R.string.call_driver));
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!Common.driverFound && radius<LIMIT){
                    radius++;
                    findDriver();
                }else{
                    if(!Common.driverFound) {
                        Toast.makeText(Home.this, "Nenhum motorista disponível perto de você", Toast.LENGTH_SHORT).show();
                        btnRequestPickup.setText("Solicitar");
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id=item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent ( Home.this, Main2Activity.class );
            startActivity ( i );

         //   return true;
        }

        if (id == R.id.action_linhas_proximas_a_mimm) {

            Intent i = new Intent ( Home.this, Main2Activity.class );
            startActivity ( i );

            //   return true;
        }


        return super.onOptionsItemSelected(item);
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_trip_history:
                showTripHistory();
                break;

            // atualizar infomações
            case R.id.nav_updateInformation:
                showDialogUpdateInfo();
                break;
             // atualizar senha
            case R.id.nav_change_pwd:
                showDialogChangePwd();
                break;

            // sair quando logado
            case R.id.nav_signOut:
                // if(account!=null)
                signOut();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // obs esse codigo retirar depois



    /// chamar activity de historico
    private void showTripHistory() {
        Intent intent=new Intent(Home.this, TripHistory.class);
        startActivity(intent);
    }


    /// atualizar informações

    private void showDialogUpdateInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Atualizar Informações");
        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText etName = (MaterialEditText) layout_pwd.findViewById(R.id.etName);
        final MaterialEditText etPhone = (MaterialEditText) layout_pwd.findViewById(R.id.etPhone);
        final ImageView image_upload = (ImageView) layout_pwd.findViewById(R.id.imageUpload);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("ATUALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(name))
                    updateInfo.put("name", name);
                if(!TextUtils.isEmpty(phone))
                    updateInfo.put("phone",phone);
                DatabaseReference driverInformation = FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                driverInformation.child(Common.userID)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(Home.this,"Informação Atualizada!",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(Home.this,"Falha na atualização de informações!",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

      ///  permissão para acessar banco de dados
    @SuppressLint("InlinedApi")
    private void chooseImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, Common.PICK_IMAGE_REQUEST);
                        }else{
                            Toast.makeText(getApplicationContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri saveUri=data.getData();
            if(saveUri!=null){
                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Atualizando...");
                progressDialog.show();

                String imageName=UUID.randomUUID().toString();
                final StorageReference imageFolder=storageReference.child("images/"+imageName);

                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(Home.this, "Atualizar!", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String, Object> avatarUpdate=new HashMap<>();
                                        avatarUpdate.put("avatarUrl", uri.toString());


                                        DatabaseReference driverInformations=FirebaseDatabase.getInstance().getReference(Common.user_rider_tbl);
                                        driverInformations.child(Common.userID).updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(Home.this, "Atualizar!", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(Home.this, " Erro! ao  Atualizar", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded "+progress+"%");
                    }
                });
            }
        }
    }
      // sair quando conectado com google
    private void signOut() {
        if(account!=null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Home.this, "Não foi possível sair", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            // sair logado com faccebook

                    }else if(isLoggedInFacebook){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();

            // sair logado normal
        }else{
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            Common.userID=account.getId();
            loadUser();
        }else if(isLoggedInFacebook){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String id=object.optString("id");
                            Common.userID=id;
                            loadUser();
                        }
                    });
            request.executeAsync();
        }else{
            Common.userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadUser();
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_PERMISSION);
        }else{
            if (checkPlayServices()){
                buildGoogleApiClient();
                displayLocation();
            }
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST_CODE).show();
            else {
                Message.messageError(this, Errors.NOT_SUPPORT);
                finish();
            }
            return false;
        }
        return true;
    }

    private void displayLocation(){
        if (currentLat!=null && currentLng!=null){
            LatLng center=new LatLng(currentLat, currentLng);
            LatLng northSide=SphericalUtil.computeOffset(center, 100000, 0);
            LatLng southSide=SphericalUtil.computeOffset(center, 100000, 180);

            LatLngBounds bounds=LatLngBounds.builder()
                    .include(northSide)
                    .include(southSide)
                    .build();
            placeLocation.setBoundsBias(bounds);
            placeLocation.setFilter(typeFilter);

            placeDestination.setBoundsBias(bounds);
            placeDestination.setFilter(typeFilter);
            //presence system
            driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_tbl);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //if have change from drivers table, we will reload all drivers available
                    loadAllAvailableDriver(new LatLng(currentLat, currentLng));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            loadAllAvailableDriver(new LatLng(currentLat, currentLng));

        }else{
            Message.messageError(this, Errors.WITHOUT_LOCATION);
        }

    }

    private void loadAllAvailableDriver(final LatLng location) {
        mMap.clear();

        riderMarket = mMap.addMarker(new MarkerOptions().position(location)
                .title(getResources().getString(R.string.you))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));


        DatabaseReference driverLocation;
        if(isUberX)
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("UberX");
        else
            driverLocation=FirebaseDatabase.getInstance().getReference(Common.driver_tbl).child("Uber Black");
        GeoFire geoFire=new GeoFire(driverLocation);

        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User driver=dataSnapshot.getValue(User.class);
                        String name;
                        String phone;

                        if(driver.getName()!=null) name=driver.getName();
                        else name="not available";

                        if (driver.getPhone()!=null)phone="Phone: "+driver.getPhone();
                        else phone="Phone: none";


                        mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).flat(true)
                                .title(name).snippet("Driver ID: "+dataSnapshot.getKey()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance<=LIMIT){
                    distance++;
                    loadAllAvailableDriver(location);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(destinationMarker!=null)
                    destinationMarker.remove();
                destinationMarker=mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_destination_marker))
                        .title("Destino"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                BottomSheetRiderFragment mBottomSheet=BottomSheetRiderFragment.newInstance(String.format("%f,%f", currentLat, currentLng),
                        String.format("%f,%f",latLng.latitude, latLng.longitude), true);
                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
            }
        });
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    location.onRequestPermissionResult(requestCode, permissions, grantResults);
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        displayLocation();
                    }
                }
                break;
            case PLAY_SERVICES_REQUEST_CODE:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        location.stopUpdateLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        location.inicializeLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!marker.getTitle().equals("You")){
            Intent intent=new Intent(Home.this, CallDriver.class);
            String ID= marker.getSnippet().replace("Driver ID: ", "");
            intent.putExtra("driverID", ID);
            intent.putExtra("lat", currentLat);
            intent.putExtra("lng", currentLng);
            startActivity(intent);
        }
    }




    // codigos alterar a senha

    private void showDialogChangePwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder (Home.this);
        alertDialog.setTitle ("Mudar senha");


        LayoutInflater inflater = this.getLayoutInflater ( );
        View layout_pwd = inflater.inflate (R.layout.layout_change_pwd, null);

        final MaterialEditText edtPassword = ( MaterialEditText ) layout_pwd.findViewById (R.id.edtPassword);
        final MaterialEditText edtNewPassword = ( MaterialEditText ) layout_pwd.findViewById (R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = ( MaterialEditText ) layout_pwd.findViewById (R.id.edtRepetPassword);

        alertDialog.setView (layout_pwd);

        alertDialog.setPositiveButton ("Mudar senha", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog (Home.this);
                waitingDialog.show ( );

                if (edtNewPassword.getText ( ).toString ( ).equals (edtRepeatPassword.getText ( ).toString ( ))) {
                    String email = FirebaseAuth.getInstance ( ).getCurrentUser ( ).getEmail ( );

                    //Get auth credentials from the user for re-authentication.
                    //Example with only email
                    AuthCredential credential = EmailAuthProvider.getCredential (email, edtPassword.getText ( ).toString ( ));
                    FirebaseAuth.getInstance ( ).getCurrentUser ( )
                            .reauthenticate (credential)
                            .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful ( )) {
                                        FirebaseAuth.getInstance ( ).getCurrentUser ( )
                                                .updatePassword (edtRepeatPassword.getText ( ).toString ( ))
                                                .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful ( )) {
                                                            //update driver information password column
                                                            Map<String, Object> password = new HashMap<> ( );
                                                            password.put ("password", edtRepeatPassword.getText ( ).toString ( ));
                                                            DatabaseReference driverInformation = FirebaseDatabase.getInstance ( ).getReference (Common.user_driver_tbl);
                                                            driverInformation.child (Common.userID)
                                                                    .updateChildren (password)
                                                                    .addOnCompleteListener (new OnCompleteListener<Void> ( ) {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful ( ))
                                                                                Toast.makeText (Home.this, "A senha foi alterada!", Toast.LENGTH_SHORT).show ( );
                                                                            else
                                                                                Toast.makeText (Home.this, "A senha foi não alterada!", Toast.LENGTH_SHORT).show ( );
                                                                            waitingDialog.dismiss ( );

                                                                        }
                                                                    });

                                                        } else {
                                                            Toast.makeText (Home.this, "A senha não muda", Toast.LENGTH_SHORT).show ( );

                                                        }
                                                    }
                                                });

                                    } else {
                                        waitingDialog.dismiss ( );
                                        Toast.makeText (Home.this, "Senha antiga incorreta", Toast.LENGTH_SHORT).show ( );
                                    }

                                }
                            });

                } else {
                    waitingDialog.dismiss ( );
                    Toast.makeText (Home.this, "A senha não corresponde", Toast.LENGTH_SHORT).show ( );
                }


            }
        });

        alertDialog.setNegativeButton ("CANCELAR", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss ( );
            }
        });
        //show dialog
        alertDialog.show ( );

    }
}