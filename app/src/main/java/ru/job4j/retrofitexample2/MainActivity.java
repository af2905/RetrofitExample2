package ru.job4j.retrofitexample2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recycler;
    private PostAdapter adapter;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private TextInputEditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts);
        initViewsAndSetClickListeners();
        initRetrofit();
        initRecycler();
    }

    void initViewsAndSetClickListeners() {
        text = findViewById(R.id.post_edit_text);
        MaterialButton send = findViewById(R.id.post_send);
        send.setOnClickListener(this);
    }

    void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        client.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    void initRecycler() {
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(8, 16));
        adapter = new PostAdapter(jsonPlaceHolderApi);
        recycler.setAdapter(adapter);
    }

    void createPost(int userId, String title, String text) {
        final Post post = new Post(userId, title, text);
        Call<Post> call = jsonPlaceHolderApi.createPost(post);
        createPostBasicMethod(call);
    }

    void createPostFormUrlEncoded(int userId, String title, String text) {
        Call<Post> call = jsonPlaceHolderApi.createPost(userId, title, text);
        createPostBasicMethod(call);
    }

    void createPostMap() {
        Map<String, String> fields = new HashMap<>();
        fields.put("userId", "3");
        fields.put("title", "title3");
        Call<Post> call = jsonPlaceHolderApi.createPost(fields);
        createPostBasicMethod(call);
    }

    void putPost(int userId, String title, String text) {
        final Post post = new Post(userId, title, text);
        Call<Post> call = jsonPlaceHolderApi.putPost(1, post);
        createPostBasicMethod(call);
    }

    void getPost(int id) {
        Call<Post> call = jsonPlaceHolderApi.getPost(id);
        createPostBasicMethod(call);
    }

    void createPostBasicMethod(Call<Post> call) {
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Post postResponse = response.body();
                    adapter.addPost(new Post(
                            postResponse.getId(),
                            postResponse.getUserId(),
                            postResponse.getTitle(),
                            Utils.appendSpaces(postResponse.getText())));
                    recycler.smoothScrollToPosition(recycler.getBottom());
                } else {
                    Toast.makeText(MainActivity.this,
                            String.format("Error. Response status: %s", response.code()),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        String.format("Error. Response status: %s", t.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        createPost(1, "new post", text.getText().toString().trim());
        text.setText(null);
    }
}
