package com.codebosses.roomdatabasedemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codebosses.roomdatabasedemo.adapter.TaskAdapter;
import com.codebosses.roomdatabasedemo.database.DatabaseClient;
import com.codebosses.roomdatabasedemo.databinding.ActivityMainBinding;
import com.codebosses.roomdatabasedemo.entity.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskAdapter.OnItemLongPressed {

    //    Android fields....
    private ActivityMainBinding activityMainBinding;
    private RecyclerView recyclerViewData;
    private FloatingActionButton floatingActionButtonAdd;
    private LayoutInflater layoutInflater;
    private AlertDialog addAlertDialog, updateAlertDialog;
    private EditText editTextAddTask, editTextAddDescription, editTextAddFinishBy;
    private EditText editTextUpdateTask, editTextUpdateDescription, editTextUpdateFinishBy;
    private CheckBox checkBoxFinished;

    //    Adapter fields....
    private TaskAdapter taskAdapter;

    //    Instance fields....
    private int updatePosition;
    private List<Task> taskList = new ArrayList<>();
    private ClickHandler clickHandler;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        Android fields initialization....
        recyclerViewData = activityMainBinding.recyclerViewData;
        floatingActionButtonAdd = activityMainBinding.floatingActionButtonAdd;

//        Setting layout manager for recycler view....
        recyclerViewData.setLayoutManager(new LinearLayoutManager(this));

//        Setting adapter for recycler view....
        taskAdapter = new TaskAdapter(this, taskList);
        taskAdapter.setOnItemLongPressed(this);
        recyclerViewData.setAdapter(taskAdapter);

//        Getting all tasks from room database....
        new GetAllDataTask().execute();

//        All event listeners....
        clickHandler = new ClickHandler();
        activityMainBinding.setClickHandler(clickHandler);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddTask:
                String task = editTextAddTask.getText().toString();
                String description = editTextAddDescription.getText().toString();
                String finishBy = editTextAddFinishBy.getText().toString();
                if (!TextUtils.isEmpty(task) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(finishBy)) {
                    if (addAlertDialog != null) {
                        addAlertDialog.dismiss();
                    }
                    Task taskData = new Task(task, description, finishBy, false);
                    taskList.add(taskData);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                    new AddDataTask().execute(taskData);
                } else {
                    Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonUpdateTask:
                if (updateAlertDialog != null) {
                    updateAlertDialog.dismiss();
                }
                String updatedTask = editTextUpdateTask.getText().toString();
                String updatedDescription = editTextUpdateDescription.getText().toString();
                String updatedFinishBy = editTextUpdateFinishBy.getText().toString();

                currentTask.setTask(updatedTask);
                currentTask.setDescription(updatedDescription);
                currentTask.setFinishBy(updatedFinishBy);
                currentTask.setFinished(checkBoxFinished.isChecked());
                new UpdateDataTask().execute(currentTask);

                taskAdapter.notifyItemChanged(updatePosition);
                break;
        }
    }

    //    TODO: Method to create dialog to add new task....
    private void createAddDataDialog() {
        AlertDialog.Builder addDialogBuilder = new AlertDialog.Builder(this);
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View addDialogView = layoutInflater.inflate(R.layout.dialog_add_data, null);

        editTextAddTask = addDialogView.findViewById(R.id.editTextTask);
        editTextAddDescription = addDialogView.findViewById(R.id.editTextDescription);
        editTextAddFinishBy = addDialogView.findViewById(R.id.editTextFinishBy);
        Button buttonAdd = addDialogView.findViewById(R.id.buttonAddTask);

        addDialogBuilder.setView(addDialogView);
        addAlertDialog = addDialogBuilder.create();
        addAlertDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        addAlertDialog.show();

        buttonAdd.setOnClickListener(this);
    }

    //    TODO: Method to update selected task....
    private void createUpdateDataDialog() {
        AlertDialog.Builder updateDialogBuilder = new AlertDialog.Builder(this);
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View updateDialogView = layoutInflater.inflate(R.layout.dialog_update_data, null);

        editTextUpdateTask = updateDialogView.findViewById(R.id.editTextTask);
        editTextUpdateDescription = updateDialogView.findViewById(R.id.editTextDescription);
        editTextUpdateFinishBy = updateDialogView.findViewById(R.id.editTextFinishBy);
        checkBoxFinished = updateDialogView.findViewById(R.id.checkBoxFinished);
        Button buttonUpdate = updateDialogView.findViewById(R.id.buttonUpdateTask);

        currentTask = taskList.get(updatePosition);
        editTextUpdateTask.setText(currentTask.getTask());
        editTextUpdateDescription.setText(currentTask.getDescription());
        editTextUpdateFinishBy.setText(currentTask.getFinishBy());
        checkBoxFinished.setChecked(currentTask.isFinished());

        updateDialogBuilder.setView(updateDialogView);
        updateAlertDialog = updateDialogBuilder.create();
        updateAlertDialog.getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.transparent));
        updateAlertDialog.show();

        buttonUpdate.setOnClickListener(this);
    }

    //    TODO: Override method trigger when long pressed from adapter on item....
    @Override
    public void onLongPress(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuDelete) {
                    new DeleteDataTask().execute(taskList.get(position));
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                } else if (item.getItemId() == R.id.menuUpdate) {
                    updatePosition = position;
                    createUpdateDataDialog();
                }
                return true;
            }
        });
        popupMenu.show();
    }

    //    TODO: Async task inner class to get all tasks on background thread....
    class GetAllDataTask extends AsyncTask<Void, Void, List<Task>> {

        @Override
        protected List<Task> doInBackground(Void... voids) {
            return DatabaseClient.getDatabaseClient(getApplicationContext()).getAppDatabase().getTaskDao().getAllTasks();
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            for (int i = 0; i < tasks.size(); i++) {
                taskList.add(tasks.get(i));
                taskAdapter.notifyItemInserted(i);
            }
        }
    }

    //    TODO: Async task inner class to add new tasks on background thread....
    class AddDataTask extends AsyncTask<Task, Void, Void> {

        @Override
        protected Void doInBackground(Task... tasks) {
            DatabaseClient.getDatabaseClient(getApplicationContext()).getAppDatabase().getTaskDao().insertTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    //    TODO: Async task inner class to delete tasks on background thread....
    class DeleteDataTask extends AsyncTask<Task, Void, Void> {

        @Override
        protected Void doInBackground(Task... tasks) {
            DatabaseClient.getDatabaseClient(getApplicationContext()).getAppDatabase().getTaskDao().deleteTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Deleted task successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    //    TODO: Async task inner class to update tasks on background thread....
    class UpdateDataTask extends AsyncTask<Task, Void, Void> {

        @Override
        protected Void doInBackground(Task... tasks) {
            DatabaseClient.getDatabaseClient(getApplicationContext()).getAppDatabase().getTaskDao().updateTask(tasks[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Data updated successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public class ClickHandler {

        public void onAddClick(View view) {
            createAddDataDialog();
        }

    }
}
