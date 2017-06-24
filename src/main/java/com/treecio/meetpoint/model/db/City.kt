package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import com.treecio.meetpoint.model.Coordinates
import java.sql.PreparedStatement
import java.sql.ResultSet

class City(
        id: Int,
        val country: String,
        val city: String,
        val accent: String,
        val population: Int,
        val coordinates: Coordinates,
        val timezone: String,
        val altitude: Double
) : DatabaseObject(id) {

    override fun getTable(): String = "cities"
    override fun getColumns(): Array<String> = arrayOf("country", "city", "accent", "population", "lat", "lon", "tz_id", "alt")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setString(1, country)
        stmt.setString(2, city)
        stmt.setString(3, accent)
        stmt.setInt(4, population)
        stmt.setDouble(5, coordinates.lat)
        stmt.setDouble(6, coordinates.lon)
        stmt.setString(7, timezone)
        stmt.setDouble(8, altitude)
    }

    companion object {
        fun fromResultSet(rs: ResultSet): City {
            return City(
                    rs.getInt("id"),
                    rs.getString("country"),
                    rs.getString("city"),
                    rs.getString("accent"),
                    rs.getInt("population"),
                    Coordinates(rs.getDouble("lat"), rs.getDouble("lon")),
                    rs.getString("tz_id"),
                    rs.getDouble("alt")
            )
        }
        fun querySel(selection: String): City? {
            val rs = DatabaseManager.getFromDatabase("cities", selection)
            if (rs.next()) {
                return fromResultSet(rs);
            }
            return null
        }
        fun query(id: Int): City? {
            return querySel("id = " + id)
        }
    }
}