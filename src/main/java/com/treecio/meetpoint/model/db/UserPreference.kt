package com.treecio.meetpoint.model.db

import com.treecio.meetpoint.db.DatabaseManager
import com.treecio.meetpoint.model.Preference
import java.sql.PreparedStatement

class UserPreference(id: Int, val user: Int, val preference: Int, val answer: String) : DatabaseObject(id) {

    override fun getTable(): String = "preferences"
    override fun getColumns(): Array<String> = arrayOf("user", "preference", "answer")
    override fun bindValues(stmt: PreparedStatement) {
        stmt.setInt(1, user)
        stmt.setInt(2, preference)
        stmt.setString(3, answer)
    }

    constructor(user: User, preference: Preference, answer: String): this(-1, user.id, preference.id, answer)

    fun getUser(): User? {
        return User.query(user)
    }
    fun getPreference(): Preference? {
        return Preference.fromId(preference)
    }

    companion object {
        fun querySel(selection: String): UserPreference? {
            val rs = DatabaseManager.getFromDatabase("users", selection)
            if (rs.next()) {
                return UserPreference(rs.getInt("id"),
                        rs.getInt("user"),
                        rs.getInt("preference"),
                        rs.getString("answer"))
            }
            return null
        }
        fun query(user: User, preference: Preference): UserPreference? {
            return querySel("user = ${user.id}")
        }
    }


}
