package dev.onebiteaidan.paperplugintemplate.DataManagement;

import dev.onebiteaidan.paperplugintemplate.DataManagement.Database;
import dev.onebiteaidan.paperplugintemplate.PaperPluginTemplate;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite implements Database {

    private final String filename;
    private Connection connection;

    public SQLite(String filename) {

        this.filename = filename;

        createIfDoesntExist(filename);
    }

    /**
     * Creates an SQLite database if one with filename doesn't already exist.
     * @param filename The filename for the database is in the format <name>.db
     */
    public void createIfDoesntExist(String filename) {
        // Check if the database file exists already
        File db = new File(PaperPluginTemplate.getPlugin(PaperPluginTemplate.class).getDataFolder(), filename);
        try {
            if (db.createNewFile()) {
                PaperPluginTemplate.getPlugin(PaperPluginTemplate.class).getLogger().info("WorldShop Has created a new SQLite Database File called '" + filename + "'");
            } else {
                PaperPluginTemplate.getPlugin(PaperPluginTemplate.class).getLogger().info("WorldShop found preexisting database " + filename);
            }
        } catch (IOException e) {
            PaperPluginTemplate.getPlugin(PaperPluginTemplate.class).getLogger().severe("WorldShop encountered an error while trying to create an SQLite Database with the name '"+ filename + "'!!!");
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + new File(PaperPluginTemplate.getPlugin(PaperPluginTemplate.class).getDataFolder(), filename));
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void disconnect() {
        try {
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    /**
     * This database query method is made to be SQL-injection safe.
     * There are limited data types here as an example but feel free to add more!
     */
    @Override
    public ResultSet query(String query, Object[] arguments, int[] types) {
        try {
            PreparedStatement ps = connection.prepareStatement(query);
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
                        ps.setString(i + 1, arguments[i].toString());
                        break;
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
            PreparedStatement ps = connection.prepareStatement(update);
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                    continue;
                }
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
                    case Types.NULL:
                        ps.setNull(i + 1, Types.NULL);
                    case Types.BLOB:
                        ps.setBytes(i + 1, ((ItemStack) arguments[i]).serializeAsBytes());
                        break;
                    case Types.VARCHAR:
                        ps.setString(i + 1, arguments[i].toString());
                        break;
                    default:
                        break;
                }
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String command) {
        try {
            PreparedStatement ps = connection.prepareStatement(command);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createExampleTable() {
        this.run(  // Storing items in mysql https://www.spigotmc.org/threads/ways-to-storage-a-inventory-to-a-database.547207/
                "CREATE TABLE IF NOT EXISTS example" +
                        "(" +
                        "example_id INTEGER PRIMARY KEY," +
                        "example_uuid varchar(36)," + // The length of a UUID will never be longer than 36 characters
                        "example_itemstack BLOB," +  // Itemstacks can be stored in the BLOB datatype after being converted to byte arrays
                        "example_timestamp BIGINT" +
                        ");"
        );
    }
}