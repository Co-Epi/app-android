package org.coepi.android.cen;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//You can either provide `room.schemaLocation` annotation processor argument OR set exportSchema to false.
@Database(entities = {CEN.class, CENReport.class, CENKey.class}, version = 2, exportSchema=false )
public abstract class AppDatabase extends RoomDatabase {
    public abstract CENDao cenDao();
    public abstract CENReportDao cenReportDao();
    public abstract CENKeyDao cenkeyDao();
}
