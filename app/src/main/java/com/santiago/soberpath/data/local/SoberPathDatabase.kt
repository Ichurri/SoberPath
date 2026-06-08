package com.santiago.soberpath.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.santiago.soberpath.data.local.dao.DailyCheckInDao
import com.santiago.soberpath.data.local.dao.HabitDao
import com.santiago.soberpath.data.local.dao.MilestoneDao
import com.santiago.soberpath.data.local.dao.MotivationReasonDao
import com.santiago.soberpath.data.local.dao.RelapseDao
import com.santiago.soberpath.data.local.entity.DailyCheckInEntity
import com.santiago.soberpath.data.local.entity.HabitEntity
import com.santiago.soberpath.data.local.entity.MilestoneEntity
import com.santiago.soberpath.data.local.entity.MotivationReasonEntity
import com.santiago.soberpath.data.local.entity.RelapseEntity

@Database(
    entities = [
        HabitEntity::class,
        DailyCheckInEntity::class,
        MotivationReasonEntity::class,
        MilestoneEntity::class,
        RelapseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SoberPathDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun motivationReasonDao(): MotivationReasonDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun relapseDao(): RelapseDao

    companion object {
        const val DATABASE_NAME = "soberpath.db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS relapses (
                        id TEXT NOT NULL,
                        habitId TEXT NOT NULL,
                        relapseDate TEXT NOT NULL,
                        cravingLevel INTEGER NOT NULL,
                        trigger TEXT NOT NULL,
                        note TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_relapses_habitId 
                    ON relapses(habitId)
                    """.trimIndent()
                )
            }
        }

        fun build(context: Context): SoberPathDatabase {
            return Room.databaseBuilder(
                context,
                SoberPathDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .addCallback(MilestoneSeedCallback())
                .build()
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