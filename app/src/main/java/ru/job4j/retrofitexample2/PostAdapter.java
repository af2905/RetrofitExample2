package ru.job4j.retrofitexample2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private boolean isButtonDelete = true;
    private static final String TAG = "log";

    PostAdapter(JsonPlaceHolderApi jsonPlaceHolderApi) {
        this.jsonPlaceHolderApi = jsonPlaceHolderApi;
    }

    private Post getPost(int position) {
        return posts.get(position);
    }

    void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(getItemCount() - 1);
    }

    private void removePostAt(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            posts.remove(location);
            notifyItemRemoved(location);
        }
    }

    private void updatePostAt(int location, Post post) {
        removePostAt(location);
        posts.add(location, post);
        notifyItemInserted(location);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_template, parent, false);
        EditText postText = view.findViewById(R.id.post_text);
        TextView postDate = view.findViewById(R.id.post_date);
        ImageView postEditImg = view.findViewById(R.id.post_edit_img);
        ImageView postDeleteImg = view.findViewById(R.id.post_delete_img);
        return new PostViewHolder(view, postText, postDate, postEditImg, postDeleteImg);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Post post = getPost(position);
        final PostViewHolder postViewHolder = (PostViewHolder) holder;
        postViewHolder.postText.setText(post.getText());
        Calendar calendar = Calendar.getInstance();
        postViewHolder.postDate.setText(Utils.getTime(calendar.getTimeInMillis()));
        postViewHolder.postDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonDelete) {
                    deletePost(post.getId(), holder.getLayoutPosition());
                } else {
                    post.setText(postViewHolder.postText.getText().toString().trim());
                    updatePost(post.getId(), post.getUserId(), post.getTitle(),
                            Utils.appendSpaces(post.getText()), holder.getLayoutPosition());
                    isButtonDelete = true;
                    postViewHolder.postText.setEnabled(false);
                    postViewHolder.postEditImg.setVisibility(View.VISIBLE);
                    postViewHolder.postText.setCursorVisible(false);
                    postViewHolder.postDeleteImg.setImageResource(R.drawable.ic_cancel_gray_24dp);
                }

            }
        });

        postViewHolder.postEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postViewHolder.postText.setEnabled(true);
                postViewHolder.postEditImg.setVisibility(View.INVISIBLE);
                postViewHolder.postText.setCursorVisible(true);
                isButtonDelete = false;
                postViewHolder.postDeleteImg.setImageResource(R.drawable.ic_check_green_24dp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    private void deletePost(int id, final int position) {
        Call<Void> call = jsonPlaceHolderApi.deletePost(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removePostAt(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void updatePost(final int id, final int userId, final String title,
                            final String text, final int position) {
        Call<Post> call = jsonPlaceHolderApi.getPost(id);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Post postResponse = response.body();
                    String updateText;
                    if (text != null) {
                        updateText = text;
                    } else {
                        updateText = postResponse.getText();
                    }
                    updatePostAt(position, new Post(id, userId, title, updateText));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        EditText postText;
        TextView postDate;
        ImageView postEditImg, postDeleteImg;

        PostViewHolder(@NonNull View itemView, EditText postText, TextView postDate,
                       ImageView postEditImg, ImageView postDeleteImg) {
            super(itemView);
            this.postText = postText;
            this.postDate = postDate;
            this.postEditImg = postEditImg;
            this.postDeleteImg = postDeleteImg;
        }
    }
}
