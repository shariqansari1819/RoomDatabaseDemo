package com.codebosses.roomdatabasedemo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.codebosses.roomdatabasedemo.entity.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("select * from Task")
    List<Task> getAllTasks();

    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

}
