package dev.onebiteaidan.paperplugintemplate.DataManagement;

import com.zaxxer.hikari.HikariDataSource;
import dev.onebiteaidan.paperplugintemplate.DataManagement.Config;
import dev.onebiteaidan.paperplugintemplate.DataManagement.Database;
import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class MySQL implements Database {

    private final String HOST = Config.getHost();
    private final int PORT = Config.getPort();
    private final String DATABASE = Config.getDatabase();
    private final String USERNAME = Config.getUsername();
    private final String PASSWORD = Config.getPassword();

    private HikariDataSource hikari;

    public void connect() {
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", HOST);
        hikari.addDataSourceProperty("port", PORT);
        hikari.addDataSourceProperty("databaseName", DATABASE);
        hikari.addDataSourceProperty("user", USERNAME);
        hikari.addDataSourceProperty("password", PASSWORD);
    }

    public boolean isConnected() {
        return hikari != null;
    }

    public void disconnect() {
        if (this.isConnected()) {
            hikari.close();
        }
    }

    public Connection getConnection() {
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This database query method is made to be SQL-injection safe.
     * There are limited data types here as an example but feel free to add more!
     */
    @Override
    public ResultSet query(String query, Object[] arguments, int[] types) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            for (int i = 0; i < arguments.length; i++) {
                switch (types[i]) {
                    case Types.INTEGER:
                        ps.setInt(i + 1, (int) arguments[i]);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(i + 1, (boolean) arguments[i]);
                        break;
                    case Types.BIGINT:
                        ps.setLong(i + 1, (long) arguments[i]);
                        break;
                    case Types.BLOB:
                        ps.setBytes(i + 1, ((ItemStack) arguments[i]).serializeAsBytes());
                        break;
                    case Types.VARCHAR:
                        ps.setString(i + 1, (String) arguments[i]);
                    default:
                        break;
                }
            }
            return ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void update(String update, Object[] arguments, int[] types) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(update);
            for (int i = 0; i < arguments.length; i++) {
                switch (types[i]) {
                    case Types.INTEGER:
                        ps.setInt(i + 1, (int) arguments[i]);
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(i + 1, (boolean) arguments[i]);
                        break;
                    case Types.BIGINT:
                        ps.setLong(i + 1, (long) arguments[i]);
                        break;
                    case Types.BLOB:
                        ps.setBytes(i + 1, ((ItemStack) arguments[i]).serializeAsBytes());
                        break;
                    case Types.VARCHAR:
                        ps.setString(i + 1, (String) arguments[i]);
                        break;
                    default:
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String command) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(command)) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createExampleTable() {
        this.run(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                "CREATE TABLE IF NOT EXISTS example" +
                        "(" +
                        "example_id int UNIQUE AUTO_INCREMENT," +
                        "example_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        "example_itemstack BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                        "example_timestamp BIGINT" +
                        ");"
        );
    }
}