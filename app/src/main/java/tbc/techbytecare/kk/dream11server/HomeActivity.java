package tbc.techbytecare.kk.dream11server;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;
import tbc.techbytecare.kk.dream11server.Common.Common;
import tbc.techbytecare.kk.dream11server.Model.Banner;
import tbc.techbytecare.kk.dream11server.Model.Fixture;
import tbc.techbytecare.kk.dream11server.ViewHolder.BannerViewHolder;
import tbc.techbytecare.kk.dream11server.ViewHolder.FixtureViewHolder;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference fixtures;

    FloatingActionButton fabAddFixture;

    MaterialEditText edtName, edtTimeLeft,edtSeriesId;
    CircleImageView civFirstOpponent,civSecondOpponent;
    FButton btnSelect,btnUpload;

    FirebaseStorage storage;
    StorageReference storageReference;

    Fixture newFixture;
    Uri filePath;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Fixture, FixtureViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        fixtures = database.getReference("Fixtures");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fabAddFixture = findViewById(R.id.fabFixture);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fabAddFixture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFixture();
            }
        });
        loadFixture();
    }

    private void loadFixture() {
        FirebaseRecyclerOptions<Fixture> allFixture = new FirebaseRecyclerOptions.Builder<Fixture>()
                .setQuery(fixtures,Fixture.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Fixture, FixtureViewHolder>(allFixture) {
            @Override
            protected void onBindViewHolder(@NonNull FixtureViewHolder holder, int position, @NonNull Fixture model) {
                holder.txtSeriesName.setText(model.getSeriesName());
                holder.txtTimer.setText(model.getTimeLeft());

                Picasso.with(getBaseContext()).load(model.getFirstOpponent()).into(holder.imgFirstOpponent);
                Picasso.with(getBaseContext()).load(model.getSecondOpponent()).into(holder.imgSecondOpponent);
            }

            @NonNull
            @Override
            public FixtureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fixture_layout,null,false);

                return new FixtureViewHolder(itemView);
            }
        };
        adapter.startListening();

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)    {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showAddFixture() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Add New Banner");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();

        View add_fixture_layout = inflater.inflate(R.layout.add_fixture_layout,null);

        edtName = add_fixture_layout.findViewById(R.id.edtName);
        edtTimeLeft = add_fixture_layout.findViewById(R.id.edtTimeLeft);
        edtSeriesId = add_fixture_layout.findViewById(R.id.edtFixtureId);

        civFirstOpponent = add_fixture_layout.findViewById(R.id.civFirstOpponent);
        civSecondOpponent = add_fixture_layout.findViewById(R.id.civSecondOpponent);

        btnSelect = add_fixture_layout.findViewById(R.id.btnSelect);
        btnUpload = add_fixture_layout.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        civFirstOpponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFirstImage();
            }
        });

        civSecondOpponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseSecondImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        alertDialog.setView(add_fixture_layout);

        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                if (newFixture != null) {
                    fixtures.push()
                            .setValue(newFixture);
                }
                loadFixture();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                newFixture = null;
                loadFixture();
            }
        });
        alertDialog.show();
    }

    private void uploadPicture() {
        if (filePath != null)    {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    newFixture = new Fixture();
                                    newFixture.setId(edtSeriesId.getText().toString());
                                    newFixture.setSeriesName(edtName.getText().toString());
                                    newFixture.setTimeLeft(edtTimeLeft.getText().toString());
                                    newFixture.setFirstOpponent(uri.toString());
                                    newFixture.setSecondOpponent(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(HomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded "+progress+" %");
                        }
                    });
        }
    }

    private void chooseFirstImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    private void chooseSecondImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)  {

            filePath = data.getData();
            btnSelect.setText("Image Selected..");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
        }
        else if (id == R.id.nav_banner) {
            startActivity(new Intent(HomeActivity.this,BannerActivity.class));

        }
        else if (id == R.id.nav_fixture)    {
            showAddFixture();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
