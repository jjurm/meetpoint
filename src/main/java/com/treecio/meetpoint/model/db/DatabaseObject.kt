package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import org.apache.commons.lang3.StringUtils
import java.sql.PreparedStatement
import java.sql.Statement

abstract class DatabaseObject(var id: Int) {

    fun insert(): Int {
        val conn = DatabaseManager.getConnection()
        val stmt = conn.prepareStatement("INSERT INTO " + getTable() + " (" + getColumns().joinToString(separator = ",")
                + ") VALUES (" + StringUtils.repeat("?", ",", getColumns().size) + ")", Statement.RETURN_GENERATED_KEYS)
        bindValues(stmt)
        stmt.executeUpdate()
        val rs = stmt.generatedKeys
        if (rs.next()) {
            return rs.getInt(1)
        } else {
            return -1
        }
    }

    fun update() {
        val conn = DatabaseManager.getConnection()
        val stmt = conn.prepareStatement("UPDATE " + getTable() + " SET " + getColumns().joinToString(separator = " = ?, ", postfix = " = ?")
                + " WHERE id = " + id)
        bindValues(stmt)
        stmt.executeUpdate()
    }

    abstract fun getTable(): String
    abstract fun getColumns(): Array<String>
    abstract fun bindValues(stmt: PreparedStatement)

}
