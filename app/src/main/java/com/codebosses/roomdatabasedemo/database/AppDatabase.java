package com.codebosses.roomdatabasedemo.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codebosses.roomdatabasedemo.dao.TaskDao;
import com.codebosses.roomdatabasedemo.entity.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao getTaskDao();

}
