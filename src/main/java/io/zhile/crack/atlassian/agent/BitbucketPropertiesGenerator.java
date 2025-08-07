package io.zhile.crack.atlassian.agent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Generates bitbucket.properties file for license automation only
 *
 * @author The Augster
 * @version 1.0
 */
public class BitbucketPropertiesGenerator {
    
    /**
     * Generate and write bitbucket.properties file for license automation only
     *
     * @param licenseCode The generated license code
     * @return true if file was created successfully, false otherwise
     */
    public static boolean generateLicensePropertiesFile(String licenseCode) {
        try {
            String bitbucketHome = getBitbucketHome();
            if (bitbucketHome == null) {
                System.out.println("atlassian-agent: Cannot determine Bitbucket home directory");
                return false;
            }
            
            // Create shared directory if it doesn't exist
            Path sharedDir = Paths.get(bitbucketHome, "shared");
            if (!Files.exists(sharedDir)) {
                Files.createDirectories(sharedDir);
                System.out.println("atlassian-agent: Created shared directory: " + sharedDir);
            }
            
            // Create bitbucket.properties file
            File propertiesFile = new File(sharedDir.toFile(), "bitbucket.properties");
            
            // Check if file already exists
            if (propertiesFile.exists()) {
                System.out.println("atlassian-agent: bitbucket.properties already exists, skipping generation");
                return false;
            }
            
            // Generate properties content - license only
            Properties props = new Properties();

            // Only set the license for automation
            props.setProperty("setup.license", licenseCode);
            
            // Write properties to file
            try (FileWriter writer = new FileWriter(propertiesFile)) {
                writer.write("# Bitbucket Server License Automation\n");
                writer.write("# Generated automatically by atlassian-agent\n");
                writer.write("# " + new java.util.Date() + "\n\n");

                // Write each property
                for (String key : props.stringPropertyNames()) {
                    writer.write(key + "=" + props.getProperty(key) + "\n");
                }
            }

            System.out.println("atlassian-agent: Created bitbucket.properties file: " + propertiesFile.getAbsolutePath());
            System.out.println("atlassian-agent: License automation configured - license will be applied automatically!");
            
            return true;
            
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error generating bitbucket.properties: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get Bitbucket home directory
     */
    private static String getBitbucketHome() {
        // Try system property first
        String home = System.getProperty("bitbucket.home");
        if (home != null && !home.trim().isEmpty()) {
            return home;
        }
        
        // Try environment variable
        home = System.getenv("BITBUCKET_HOME");
        if (home != null && !home.trim().isEmpty()) {
            return home;
        }
        
        // Try default location
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
            File defaultHome = new File(userHome, "bitbucket-home");
            if (defaultHome.exists() || defaultHome.mkdirs()) {
                return defaultHome.getAbsolutePath();
            }
        }
        
        return null;
    }
    

}
