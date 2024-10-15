package com.mehmettemiz.todoapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehmettemiz.todoapp.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoHolder> {

   ArrayList<TodoItem> todoItem;

   public TodoAdapter(ArrayList<TodoItem> todoItems) {
       this.todoItem = todoItems;
   }

    public void updateList(ArrayList<TodoItem> newList) {
        this.todoItem = newList;
        notifyDataSetChanged();
    }

    public  class TodoHolder extends RecyclerView.ViewHolder {
       private RecyclerRowBinding binding;

       public TodoHolder(RecyclerRowBinding binding) {
           super(binding.getRoot());
           this.binding = binding;
       }
   }

    @NonNull
    @Override
    public TodoAdapter.TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return  new TodoHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.TodoHolder holder, int position) {

       holder.binding.recyclerRowTitleView.setText(todoItem.get(holder.getAdapterPosition()).name);
       holder.binding.recyclerRowNoteView.setText(todoItem.get(holder.getAdapterPosition()).note);

       if (todoItem.get(holder.getAdapterPosition()).name.isEmpty()) {
           holder.binding.recyclerRowTitleView.setVisibility(View.GONE);
       }
       if (todoItem.get(holder.getAdapterPosition()).note.isEmpty()) {
           holder.binding.recyclerRowNoteView.setVisibility(View.GONE);
       }

holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
Intent intent = new Intent(holder.itemView.getContext(), TodoEditActivity.class);
intent.putExtra("info", "old");
intent.putExtra("todoId", todoItem.get(holder.getAdapterPosition()).id);

holder.itemView.getContext().startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return todoItem.size();
    }
}
