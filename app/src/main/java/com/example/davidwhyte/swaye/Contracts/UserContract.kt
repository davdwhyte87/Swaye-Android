package com.example.davidwhyte.swaye.Contracts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class UserContract {
    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_UID = "uid"
        const val COLUMN_NAME_TOKEN="token"
        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE ${UserContract.UserEntry.TABLE_NAME} (" +
                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                        "${UserContract.UserEntry.COLUMN_NAME_NAME} TEXT," +
                        "${UserContract.UserEntry.COLUMN_NAME_UID} TEXT," +
                        "${UserContract.UserEntry.COLUMN_NAME_TOKEN} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${UserContract.UserEntry.TABLE_NAME}"

        class SwayeDbHelper(context: Context): SQLiteOpenHelper(context, Database_Name,null,1){
            override fun onCreate(p0: SQLiteDatabase?) {
                p0?.execSQL(SQL_CREATE_ENTRIES)
            }

            override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

            }

            companion object {
                const val Database_Name="swaye"
                const val Database_Version=1
            }
        }
    }
}