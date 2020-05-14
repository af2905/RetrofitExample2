package ru.job4j.retrofitexample2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean isDeleted;

    public PostAdapter(JsonPlaceHolderApi jsonPlaceHolderApi) {
        this.jsonPlaceHolderApi = jsonPlaceHolderApi;
    }

    public Post getPost(int position) {
        return posts.get(position);
    }

    public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removePost(int location) {
        if (location >= 0 && location <= getItemCount() - 1) {
            posts.remove(location);
            notifyItemRemoved(location);
        }
    }

    public void updatePost(int location) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_template, parent, false);
        TextView postText = view.findViewById(R.id.post_text);
        TextView postDate = view.findViewById(R.id.post_date);
        ImageView postEditImg = view.findViewById(R.id.post_edit_img);
        ImageView postDeleteImg = view.findViewById(R.id.post_delete_img);
        return new PostViewHolder(view, postText, postDate, postEditImg, postDeleteImg);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Post post = posts.get(position);
        final PostViewHolder postViewHolder = (PostViewHolder) holder;
        holder.itemView.setEnabled(true);
        postViewHolder.postText.setText(post.getText());
        Calendar calendar = Calendar.getInstance();
        postViewHolder.postDate.setText(Utils.getDate(calendar.getTimeInMillis()));
        postViewHolder.postDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(post.getId());
                if (isDeleted) {
                    removePost(postViewHolder.getAdapterPosition());
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    private void deletePost(int id) {
        Call<Void> call = jsonPlaceHolderApi.deletePost(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isDeleted = response.isSuccessful();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postText, postDate;
        ImageView postEditImg, postDeleteImg;

        PostViewHolder(@NonNull View itemView, TextView postText, TextView postDate,
                              ImageView postEditImg, ImageView postDeleteImg) {
            super(itemView);
            this.postText = postText;
            this.postDate = postDate;
            this.postEditImg = postEditImg;
            this.postDeleteImg = postDeleteImg;
        }
    }
}
