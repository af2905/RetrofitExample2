package ru.job4j.retrofitexample2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

        if (position % 2 != 0) {
            CardView cardView = holder.itemView.findViewById(R.id.card_view);
            cardView.setCardBackgroundColor(holder.itemView.getResources()
                    .getColor(R.color.colorBeige, holder.itemView.getContext().getTheme()));
        }

        postViewHolder.postDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isButtonDelete) {
                    deletePost(holder.itemView.getContext(), post.getId(), holder.getLayoutPosition());
                } else {
                    post.setText(Utils.appendSpaces(postViewHolder.postText.getText().toString().trim()));
                    updatePost(holder.itemView.getContext(), postViewHolder,
                            post, holder.getLayoutPosition());
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


    private void deletePost(final Context context, int id, final int position) {
        Call<Post> call = jsonPlaceHolderApi.deletePost(id);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    removePostAt(position);
                } else {
                    Toast.makeText(context.getApplicationContext(),
                            String.format("Error. Response status: %s", response.code()),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(),
                        String.format("Error. Response status: %s", t.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost(final Context context, final PostViewHolder postViewHolder,
                            final Post post, final int position) {
        Call<Post> call = jsonPlaceHolderApi.updatePost(post.getId(), post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    isButtonDelete = true;
                    postViewHolder.postText.setEnabled(false);
                    postViewHolder.postEditImg.setVisibility(View.VISIBLE);
                    postViewHolder.postText.setCursorVisible(false);
                    postViewHolder.postDeleteImg.setImageResource(R.drawable.ic_cancel_gray_24dp);
                    updatePostAt(position, post);
                } else {
                    Toast.makeText(context.getApplicationContext(),
                            String.format("Error. Response status: %s", response.code()),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(),
                        String.format("Error. Response status: %s", t.getMessage()),
                        Toast.LENGTH_SHORT).show();
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
