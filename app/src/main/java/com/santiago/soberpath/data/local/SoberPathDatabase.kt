package com.santiago.soberpath.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.santiago.soberpath.data.local.dao.DailyCheckInDao
import com.santiago.soberpath.data.local.dao.HabitDao
import com.santiago.soberpath.data.local.dao.MilestoneDao
import com.santiago.soberpath.data.local.dao.MotivationReasonDao
import com.santiago.soberpath.data.local.entity.DailyCheckInEntity
import com.santiago.soberpath.data.local.entity.HabitEntity
import com.santiago.soberpath.data.local.entity.MilestoneEntity
import com.santiago.soberpath.data.local.entity.MotivationReasonEntity

@Database(
    entities = [
        HabitEntity::class,
        DailyCheckInEntity::class,
        MotivationReasonEntity::class,
        MilestoneEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SoberPathDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun motivationReasonDao(): MotivationReasonDao
    abstract fun milestoneDao(): MilestoneDao

    companion object {
        const val DATABASE_NAME = "soberpath.db"

        fun build(context: Context): SoberPathDatabase {
            return Room.databaseBuilder(
                context,
                SoberPathDatabase::class.java,
                DATABASE_NAME
            ).addCallback(MilestoneSeedCallback()).build()
        }
    }
}

private class MilestoneSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_1', '1 dia', 1, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_3', '3 dias', 3, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_7', '7 dias', 7, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_14', '14 dias', 14, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_30', '30 dias', 30, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_60', '60 dias', 60, 0)")
        db.execSQL("INSERT INTO milestones (id, title, daysRequired, achieved) VALUES ('ms_90', '90 dias', 90, 0)")
    }
}

