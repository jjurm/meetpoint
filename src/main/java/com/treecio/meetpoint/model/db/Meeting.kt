package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import java.sql.PreparedStatement
import java.util.*

class Meeting(id: Int,
              val name: String,
              val startDate: java.util.Date,
              val endDate: java.util.Date,
              val budget: Int,
              val prioBudget: Int,
              val prioHappiness: Int,
              val prioProductivity: Int,
              val offices: String
) : DatabaseObject(id) {

    override fun getTable(): String = "meetings"
    override fun getColumns(): Array<String> = arrayOf("name", "startDate", "endDate", "budget", "prio_budget", "prio_happiness", "prio_productivity", "offices")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setString(1, name)
        stmt.setLong(2, startDate.time)
        stmt.setLong(3, endDate.time)
        stmt.setInt(4, budget)
        stmt.setInt(5, prioBudget)
        stmt.setInt(6, prioHappiness)
        stmt.setInt(7, prioProductivity)
        stmt.setString(8, offices)
    }

    constructor(name: String, startDate: Date, endDate: Date, budget: Int, prioBudget: Int, prioHappiness: Int, prioProductivity: Int, offices: String) :
            this(-1, name, startDate, endDate, budget, prioBudget, prioHappiness, prioProductivity, offices)

    companion object {
        fun querySel(selection: String): Meeting? {
            val rs = DatabaseManager.getFromDatabase("meetings", selection)
            if (rs.next()) {
                return Meeting(rs.getInt("id"),
                        rs.getString("name"),
                        Date(rs.getLong("startDate")),
                        Date(rs.getLong("endDate")),
                        rs.getInt("budget"),
                        rs.getInt("prio_budget"),
                        rs.getInt("prio_happiness"),
                        rs.getInt("prio_productivity"),
                        rs.getString("offices")
                )
            }
            return null
        }

        fun query(id: Int): Meeting? {
            return querySel("id = " + id)
        }
    }
}