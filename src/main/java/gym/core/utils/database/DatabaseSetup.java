package gym.core.utils.database;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import co.aikar.idb.BukkitDB;
import gym.core.Core;

public class DatabaseSetup {
	
	private final Core main;
	
	public DatabaseSetup(final Core main) {
		this.main = main;
		this.setupHikariCP();
		this.setupDatabase();
	}
	
    private void setupDatabase() {
        if (this.main.connection != null) {
        	this.main.getMySQL().createPlayerManagerTableAsync();
            return;
        }
        System.out.println("WARNING enter valid database information (" + this.main.getHikariPath() + ") \n You will not be able to access many features");
    }
    
    private void setupHikariCP() {
        try {
            final HikariConfig config = new HikariConfig(this.main.getHikariPath());
            @SuppressWarnings("resource")
			final HikariDataSource ds = new HikariDataSource(config);
            final String passwd = (config.getDataSourceProperties().getProperty("password") == null) ? "" : config.getDataSourceProperties().getProperty("password");
            BukkitDB.createHikariDatabase(this.main, config.getDataSourceProperties().getProperty("user"), passwd, config.getDataSourceProperties().getProperty("databaseName"), config.getDataSourceProperties().getProperty("serverName") + ":" + config.getDataSourceProperties().getProperty("portNumber"));
            this.main.connection = ds.getConnection();
        }
        catch (SQLException e) {
            System.out.println("Error could not connect to SQL database.");
            e.printStackTrace();
        }
        System.out.println("Successfully connected to the SQL database.");
    }

}
