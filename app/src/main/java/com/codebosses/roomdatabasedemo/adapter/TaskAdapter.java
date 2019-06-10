package com.codebosses.roomdatabasedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codebosses.roomdatabasedemo.R;
import com.codebosses.roomdatabasedemo.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private Context context;
    private List<Task> taskList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnItemLongPressed onItemLongPressed;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.taskList = taskList;
    }

    public void setOnItemLongPressed(OnItemLongPressed onItemLongPressed) {
        this.onItemLongPressed = onItemLongPressed;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskHolder(layoutInflater.inflate(R.layout.row_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTask.setText(task.getTask());
        holder.textViewDescription.setText(task.getDescription());
        holder.textViewFinishBy.setText(task.getFinishBy());
        if (task.isFinished()) {
            holder.textViewStatus.setText("Completed");
        } else {
            holder.textViewStatus.setText("Pending");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        TextView textViewStatus, textViewTask, textViewDescription, textViewFinishBy;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewDescription = itemView.findViewById(R.id.textViewDesc);
            textViewFinishBy = itemView.findViewById(R.id.textViewFinishBy);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongPressed != null) {
                        onItemLongPressed.onLongPress(v, getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemLongPressed {
        void onLongPress(View view, int position);
    }
}
