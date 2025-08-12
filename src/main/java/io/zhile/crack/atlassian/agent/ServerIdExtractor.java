package io.zhile.crack.atlassian.agent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to extract server ID from various Atlassian product configurations
 * @author The Augster
 * @version 1.0
 */
public class ServerIdExtractor {
    
    private static final String DEFAULT_SERVER_ID = "BXXX-XXXX-XXXX-XXXX";
    private static final Pattern SERVER_ID_PATTERN = Pattern.compile("([A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4})");
    
    /**
     * Extract server ID for the detected product
     * @param product The detected Atlassian product
     * @return The server ID or a default value if extraction fails
     */
    public static String extractServerId(ProductDetector.AtlassianProduct product) {
        System.out.println("atlassian-agent: Attempting to extract server ID for " + product.getDisplayName());
        
        String serverId;
        
        // Try multiple extraction strategies
        switch (product) {
            case JIRA:
                serverId = extractJiraServerId();
                break;
            case CONFLUENCE:
                serverId = extractConfluenceServerId();
                break;
            case BAMBOO:
                serverId = extractBambooServerId();
                break;
            case BITBUCKET:
                serverId = extractBitbucketServerId();
                break;
            case FISHEYE:
            case CRUCIBLE:
                serverId = extractFisheyeCrucibleServerId();
                break;
            case CROWD:
                serverId = extractCrowdServerId();
                break;
            default:
                serverId = extractGenericServerId();
                break;
        }
        
        if (isValidServerId(serverId)) {
            System.out.println("atlassian-agent: Server ID extracted: " + serverId);
            return serverId;
        }
        
        System.out.println("atlassian-agent: Unable to extract server ID, using default: " + DEFAULT_SERVER_ID);
        return DEFAULT_SERVER_ID;
    }
    
    /**
     * Extract server ID for JIRA
     */
    private static String extractJiraServerId() {
        // Try system property first
        String serverId = System.getProperty("jira.server.id");
        if (serverId != null) {
            return serverId;
        }
        
        // Try reading from JIRA home directory
        String jiraHome = System.getProperty("jira.home");
        if (jiraHome == null) {
            jiraHome = System.getenv("JIRA_HOME");
        }
        
        if (jiraHome != null) {
            // Check dbconfig.xml
            serverId = extractFromXmlFile(jiraHome + "/dbconfig.xml", "server-id");
            if (serverId != null) return serverId;
            
            // Check cluster.properties
            serverId = extractFromPropertiesFile(jiraHome + "/cluster.properties", "jira.node.id");
            if (serverId != null) return serverId;
        }
        
        return null;
    }
    
    /**
     * Extract server ID for Confluence
     */
    private static String extractConfluenceServerId() {
        // Try system property first
        String serverId = System.getProperty("confluence.server.id");
        if (serverId != null) {
            return serverId;
        }
        
        // Try reading from Confluence home directory
        String confluenceHome = System.getProperty("confluence.home");
        if (confluenceHome == null) {
            confluenceHome = System.getenv("CONFLUENCE_HOME");
        }
        
        if (confluenceHome != null) {
            // Check confluence.cfg.xml
            serverId = extractFromXmlFile(confluenceHome + "/confluence.cfg.xml", "server-id");
            if (serverId != null) return serverId;
            
            // Check cluster.properties
            serverId = extractFromPropertiesFile(confluenceHome + "/cluster.properties", "confluence.cluster.node.name");
            if (serverId != null) return serverId;
        }
        
        return null;
    }
    
    /**
     * Extract server ID for Bamboo
     */
    private static String extractBambooServerId() {
        String bambooHome = System.getProperty("bamboo.home");
        if (bambooHome == null) {
            bambooHome = System.getenv("BAMBOO_HOME");
        }
        
        if (bambooHome != null) {
            // Check bamboo.cfg.xml
            String serverId = extractFromXmlFile(bambooHome + "/bamboo.cfg.xml", "server-id");
            if (serverId != null) return serverId;
        }
        
        return null;
    }
    
    /**
     * Extract server ID for Bitbucket
     */
    private static String extractBitbucketServerId() {
        String bitbucketHome = System.getProperty("bitbucket.home");
        if (bitbucketHome == null) {
            bitbucketHome = System.getenv("BITBUCKET_HOME");
        }

        if (bitbucketHome != null) {
            // Check bitbucket.properties
            String serverId = extractFromPropertiesFile(bitbucketHome + "/shared/bitbucket.properties", "setup.displayName");
            if (serverId != null) return serverId;
        }

        return null;
    }

    /**
     * Extract server ID for FishEye/Crucible
     */
    private static String extractFisheyeCrucibleServerId() {
        String fisheyeHome = System.getProperty("fisheye.home");
        if (fisheyeHome == null) {
            fisheyeHome = System.getenv("FISHEYE_HOME");
        }
        
        if (fisheyeHome != null) {
            String serverId = extractFromXmlFile(fisheyeHome + "/config.xml", "server-id");
            if (serverId != null) return serverId;
        }
        
        return null;
    }
    
    /**
     * Extract server ID for Crowd
     */
    private static String extractCrowdServerId() {
        String crowdHome = System.getProperty("crowd.home");
        if (crowdHome == null) {
            crowdHome = System.getenv("CROWD_HOME");
        }

        if (crowdHome != null) {
            String serverId = extractFromXmlFile(crowdHome + "/shared/crowd.cfg.xml", "crowd.server.id");
            if (serverId != null) return serverId;
        }
        
        return null;
    }
    
    /**
     * Generic server ID extraction for unknown products
     */
    private static String extractGenericServerId() {
        // Try common system properties
        String serverId = System.getProperty("atlassian.server.id");
        if (serverId != null) return serverId;
        
        serverId = System.getProperty("server.id");
        if (serverId != null) return serverId;
        
        return null;
    }
    
    /**
     * Extract server ID from XML configuration file
     */
    private static String extractFromXmlFile(String filePath, String elementName) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(path));
            Pattern pattern = Pattern.compile("<.*?" + elementName + "[^>]*?>([^<]*?)</", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            
            if (matcher.find()) {
                String value = matcher.group(1).trim();
                if (isValidServerId(value)) {
                    return value;
                }
            }
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error reading XML file " + filePath + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extract server ID from properties file
     */
    public static String extractFromPropertiesFile(String filePath, String propertyName) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return null;
            }
            
            Properties props = new Properties();
            try (InputStream input = Files.newInputStream(path)) {
                props.load(input);
                String value = props.getProperty(propertyName);
                if (isValidServerId(value)) {
                    return value;
                }
            }
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error reading properties file " + filePath + ": " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Validate if the extracted value looks like a valid server ID
     */
    private static boolean isValidServerId(String serverId) {
        if (serverId == null || serverId.trim().isEmpty()) {
            return false;
        }
        
        // Check if it matches the typical server ID pattern
        Matcher matcher = SERVER_ID_PATTERN.matcher(serverId.trim());
        return matcher.find();
    }
}
