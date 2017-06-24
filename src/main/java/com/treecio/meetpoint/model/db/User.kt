package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import com.treecio.meetpoint.model.Place
import java.math.BigInteger
import java.security.SecureRandom
import java.sql.PreparedStatement
import java.sql.ResultSet

class User(
        id: Int,
        var meeting: Int,
        var name: String,
        var email: String,
        var origin: Place,
        val token: String
) : DatabaseObject(id) {

    override fun getTable(): String = "users"
    override fun getColumns(): Array<String> = arrayOf("meeting", "name", "email", "origin", "token")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setInt(1, meeting)
        stmt.setString(2, name)
        stmt.setString(3, email)
        stmt.setString(4, origin.name)
        stmt.setString(5, token)
    }

    constructor(meeting: Int, name: String, email: String, origin: Place) : this(-1, meeting, name, email, origin, generateToken())

    companion object {
        fun fromResultSet(rs: ResultSet): User {
            return User(rs.getInt("id"),
                    rs.getInt("meeting"),
                    rs.getString("name"),
                    rs.getString("email"),
                    Place(rs.getString("origin")),
                    rs.getString("token"))
        }
        fun querySel(selection: String): User? {
            val rs = DatabaseManager.getFromDatabase("users", selection)
            if (rs.next()) {
                return fromResultSet(rs)
            }
            return null
        }
        fun query(id: Int): User? {

            return querySel("id = $id")
        }
        fun query(token: String): User? {
            return querySel("token = '$token'")
        }
    }
}

var random = SecureRandom()

fun generateToken(): String {
    return BigInteger(130, random).toString(32)
}
