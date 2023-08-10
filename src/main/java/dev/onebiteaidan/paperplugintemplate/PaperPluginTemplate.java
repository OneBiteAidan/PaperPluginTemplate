package dev.onebiteaidan.paperplugintemplate;

import dev.onebiteaidan.paperplugintemplate.DataManagement.Config;
import dev.onebiteaidan.paperplugintemplate.DataManagement.Database;
import dev.onebiteaidan.paperplugintemplate.DataManagement.MySQL;
import dev.onebiteaidan.paperplugintemplate.DataManagement.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class PaperPluginTemplate extends JavaPlugin {

    private static Config config;
    private static Database database;

    @Override
    public void onEnable() {
        // Checks if data folder exists
        if (!getDataFolder().exists()) {
            this.getLogger().info("Data folder for PaperPluginTemplate does not Exist. Creating now...");
            getDataFolder().mkdirs();
        }

        //Setting up Config
        config = new Config(this, this.getDataFolder(), "config", true, true);
        this.getLogger().info("Setting up the config.yml...");


        //Setting up database
        switch(Config.getDatabaseType()) {
            case "SQLite":
                database = new SQLite("shop.db");
                break;

            case "MySQL":
                database = new MySQL();
                break;

            default: // Disables the plugin if the database cannot be initialized.
                this.getLogger().severe("DATABASE COULD NOT BE INITIALIZED BECAUSE '" + Config.getDatabaseType() + "' IS AN INVALID DATABASE TYPE");
                this.onDisable();
        }

        try {
            database.connect();
        } catch (SQLException e) { // Disabled the plugin if the database throws exception while trying to connect.
            e.printStackTrace();
            this.getLogger().severe("ERROR THROWN WHILE CONNECTING TO THE DATABASE!");
            this.onDisable();
        }

        if (database.isConnected()) {
            this.getLogger().info("Connected to its database successfully!");

            // Initialize the example table if it doesn't exist
            database.createExampleTable();

        } else {
            this.getLogger().severe("UNABLE TO CONNECT TO THE DATABASE!");
            // Disables the plugin if the database doesn't connect.
            this.onDisable();
        }
    }

    // Config accessor
    public static Config getConfiguration() {
        return config;
    }

    // Database accessor
    public static Database getDatabase() {
        return database;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        database.disconnect();
    }
}
