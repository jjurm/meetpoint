package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import java.sql.PreparedStatement
import java.util.*

class Meeting(id: Int,
                   val name: String,
                   val startDate: java.util.Date,
                   val endDate: java.util.Date): DatabaseObject(id) {

    override fun getTable(): String = "meetings"
    override fun getColumns(): Array<String> = arrayOf("name", "startDate", "endDate")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setString(1, name)
        stmt.setLong(2, startDate.time)
        stmt.setLong(3, endDate.time)
    }

    constructor(name: String, startDate: Date, endDate: Date) : this(-1, name, startDate, endDate)

    companion object {
        fun querySel(selection: String): Meeting? {
            val rs = DatabaseManager.getFromDatabase("meetings", selection)
            if (rs.next()) {
                return Meeting(rs.getInt("id"),
                        rs.getString("name"),
                        Date(rs.getLong("startDate")),
                        Date(rs.getLong("endDate"))
                )
            }
            return null
        }
        fun query(id: Int): Meeting? {
            return querySel("id = " + id)
        }
    }
}