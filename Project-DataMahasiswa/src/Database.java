// import library untuk koneksi ke database
    import java.sql.*;

    public class Database {
        // deklarasi variabel untuk koneksi dan statement SQL
        private Connection connection;
        private Statement statement;

        // konstruktor untuk menginisialisasi koneksi ke database
        public Database () {
            try {
                // membuat koneksi ke database dengan URL, username, dan password
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_mahasiswa", "root", "");
                // membuat statement untuk eksekusi query SQL
                statement = connection.createStatement();
            } catch(SQLException e) {
                // menangani kesalahan SQL dengan melempar runtime exception
                throw new RuntimeException(e);
            }
        }

        // method untuk eksekusi query SELECT
        public ResultSet selectQuery(String sql) {
            try {
                // eksekusi query SELECT
                statement.executeQuery(sql);
                // mengembalikan hasil query dalam bentuk ResultSet
                return statement.getResultSet();
            } catch(SQLException e) {
                // menangani kesalahan SQL dengan melempar runtime exception
                throw new RuntimeException(e);
            }
        }

        // method untuk eksekusi query INSERT, UPDATE, dan DELETE
        public int insertUpdateDeleteQuery(String sql) {
            try {
                // eksekusi query INSERT, UPDATE, atau DELETE
                return statement.executeUpdate(sql);
            } catch(SQLException e) {
                // menangani kesalahan SQL dengan melempar runtime exception
                throw new RuntimeException(e);
            }
        }

        // getter untuk mendapatkan statement
        public Statement getStatement() {
            return statement;
        }
    }