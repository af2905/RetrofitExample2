package ru.job4j.retrofitexample2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recycler;
    private PostAdapter adapter;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private TextInputEditText text;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts);
        text = findViewById(R.id.post_edit_text);
        MaterialButton send = findViewById(R.id.post_send);
        send.setOnClickListener(this);
        // result = findViewById(R.id.result);
        jsonPlaceHolderApi = JsonPlaceHolderApi.RETROFIT.create(JsonPlaceHolderApi.class);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(16);
        recycler.addItemDecoration(decoration);
        adapter = new PostAdapter(jsonPlaceHolderApi);
        recycler.setAdapter(adapter);

       /* createPost(100, "title1", "text1");
        createPostFormUrlEncoded(2, "title2", "text2");
        createPostMap();
        putPost(1, "newTitle1", "newText1");
        deletePost(2);
        getPost(3);
        updatePost(100, "changed", null);*/
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

    void updatePost(int userId, final String title, final String text) {
        Call<Post> call = jsonPlaceHolderApi.getPost(userId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Post postResponse = response.body();
                    String updateTitle, updateText;
                    if (title != null) {
                        updateTitle = title;
                    } else {
                        updateTitle = postResponse.getTitle();
                    }
                    if (text != null) {
                        updateText = text;
                    } else {
                        updateText = postResponse.getText();
                    }
                    String content = String.format(
                            "ID: %s \nuser ID: %s \nTitle: %s \nText: %s \n\n",
                            postResponse.getId(), postResponse.getUserId(),
                            updateTitle, updateText);
                    result.append(content);
                }

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }

    void createPostBasicMethod(Call<Post> call) {
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Post postResponse = response.body();
                    /*String content = String.format(
                            "ID: %s \nuser ID: %s \nTitle: %s \nText: %s \n\n",
                            postResponse.getId(), postResponse.getUserId(),
                            postResponse.getTitle(), postResponse.getText()
                    );*/
                    //result.append(content);
                    String content = String.format(
                            "ID: %s \nuser ID: %s \nTitle: %s \nText: %s \n\n",
                            postResponse.getId(), postResponse.getUserId(),
                            postResponse.getTitle(), postResponse.getText()
                    );
                    Integer id = postResponse.getId();
                    int userId = postResponse.getUserId();
                    String title = postResponse.getTitle();
                    String text = postResponse.getText();
                    adapter.addPost(new Post(id, userId, title, text));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        createPost(1, "new post", text.getText().toString());
        text.setText("");
    }
}
