package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import org.apache.commons.lang3.StringUtils
import java.sql.PreparedStatement

abstract class DatabaseObject(var id: Int) {

    fun insert() {
        val conn = DatabaseManager.getConnection();
        val stmt = conn.prepareStatement("INSERT INTO " + getTable() + " (" + getColumns().joinToString(separator = ",")
                + ") VALUES (" + StringUtils.repeat("?", ",", getColumns().size) + ")");
        bindValues(stmt)
        stmt.execute()
    }

    fun update() {
        val conn = DatabaseManager.getConnection();
        val stmt = conn.prepareStatement("UPDATE "+getTable()+" SET "+getColumns().joinToString(separator = ", ", postfix = " = ?"))
        bindValues(stmt)
        stmt.execute()
    }

    abstract fun getTable(): String
    abstract fun getColumns(): Array<String>
    abstract fun bindValues(stmt: PreparedStatement)

}
