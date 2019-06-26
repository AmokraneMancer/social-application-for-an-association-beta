package com.smb116.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private RecyclerView blogListView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blogListView = (RecyclerView) findViewById(R.id.blog_list);
        blogListView.setHasFixedSize(true);
        blogListView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        FirebaseRecyclerOptions<Blog> options =
                new FirebaseRecyclerOptions.Builder<Blog>()
                        .setQuery(databaseReference, Blog.class)
                        .build();

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder blogViewHolder, int i, @NonNull Blog blog) {
                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setContent(blog.getContent());
                blogViewHolder.setImage(blog.getImageUrl());
            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_post, parent, false);

                return new BlogViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        blogListView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        private View itemView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void setTitle(String title){
            TextView titleView = (TextView) itemView.findViewById(R.id.post_title);
            titleView.setText(title);
        }

        public void setContent(String content){
            TextView contentView = (TextView) itemView.findViewById(R.id.post_content);
            contentView.setText(content);
        }

        //uses https://github.com/square/picasso

        public void setImage(String image){
            ImageView imageView = (ImageView) itemView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(imageView);
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add)
            startActivity(new Intent(this, PostActivity.class));

        if(item.getItemId() == R.id.action_logout)
            firebaseAuth.signOut();
        return super.onOptionsItemSelected(item);
    }
}
