package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import com.treecio.meetpoint.model.Place
import java.math.BigInteger
import java.security.SecureRandom
import java.sql.PreparedStatement

class User(
        id: Int,
        var name: String,
        var email: String,
        var origin: Place,
        val token: String
) : DatabaseObject(id) {

    override fun getTable(): String = "users"
    override fun getColumns(): Array<String> = arrayOf("name", "email", "origin", "token")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setString(1, name)
        stmt.setString(2, email)
        stmt.setString(3, origin.name)
        stmt.setString(4, token)
    }

    constructor(name: String, email: String, origin: Place) : this(-1, name, email, origin, generateToken()) {
    }

    companion object {
        fun querySel(selection: String): User? {
            val rs = DatabaseManager.getFromDatabase("users", selection)
            if (rs.next()) {
                return User(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        Place(rs.getString("origin")),
                        rs.getString("token"))
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
