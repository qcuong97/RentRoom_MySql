import android.util.Log
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Program to list databases in MySQL using Kotlin
 */
object MySqlConnection {

    private var conn: Connection? = null
    private var username = "root" // provide the username
    private var password = "" // provide the corresponding password

    fun executeInsert(query: String): IntArray? {
        val stmt: Statement? = conn?.createStatement()
        stmt?.addBatch(query)
        return stmt?.executeBatch()
    }

    fun executeUpdate(query: String): Int {
        val stmt: Statement? = conn?.createStatement()
        return stmt?.executeUpdate(query) ?: 0
    }

    fun executeMySQLQuery(query: String): ResultSet? {
        var resultset: ResultSet? = null
        val stmt: Statement?
        try {
            stmt = conn?.prepareStatement(query)
            if (stmt?.execute(query) == true) {
                resultset = stmt.resultSet
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
        return resultset
    }

    /**
     * This method makes a connection to MySQL Server
     * In this example, MySQL Server is running in the local host (so 127.0.0.1)
     * at the standard port 3306
     */
    fun getConnection() {
        val connectionProps = Properties()
        connectionProps["user"] = username
        connectionProps["password"] = password

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            if (conn == null || conn?.isClosed == true) {
                conn = DriverManager.getConnection(
                    "jdbc:mysql://10.0.2.2/appnet_tuanly",
                    connectionProps
                )
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
            Log.d("Cuong", ex.message.toString())
        } catch (ex: Exception) {
            Log.d("Cuong", ex.toString())
        }
    }
}