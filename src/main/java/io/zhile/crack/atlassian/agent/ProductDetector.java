package io.zhile.crack.atlassian.agent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to detect which Atlassian product is currently running
 * @author The Augster
 * @version 1.0
 */
public class ProductDetector {
    
    public enum AtlassianProduct {
        JIRA("jira", "JIRA Software"),
        CONFLUENCE("conf", "Confluence"),
        BAMBOO("bamboo", "Bamboo"),
        BITBUCKET("bitbucket", "Bitbucket"),
        FISHEYE("fisheye", "FishEye"),
        CRUCIBLE("crucible", "Crucible"),
        CROWD("crowd", "Crowd"),
        UNKNOWN("unknown", "Unknown Product");
        
        private final String productKey;
        private final String displayName;
        
        AtlassianProduct(String productKey, String displayName) {
            this.productKey = productKey;
            this.displayName = displayName;
        }
        
        public String getProductKey() {
            return productKey;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Detect the current Atlassian product using multiple strategies
     * @return The detected product or UNKNOWN if detection fails
     */
    public static AtlassianProduct detectProduct() {
        try {
            // Strategy 1: Check system properties for home directories
            AtlassianProduct product = detectBySystemProperties();
            if (product != AtlassianProduct.UNKNOWN) {
                System.out.println("atlassian-agent: Product detected via system properties: " + product.getDisplayName());
                return product;
            }
        
        // Strategy 2: Check environment variables
        product = detectByEnvironmentVariables();
        if (product != AtlassianProduct.UNKNOWN) {
            System.out.println("atlassian-agent: Product detected via environment variables: " + product.getDisplayName());
            return product;
        }
        
        // Strategy 3: Check classpath for product-specific classes
        product = detectByClasspath();
        if (product != AtlassianProduct.UNKNOWN) {
            System.out.println("atlassian-agent: Product detected via classpath analysis: " + product.getDisplayName());
            return product;
        }
        
        // Strategy 4: Check BIN_DIR environment variable (for Bitbucket)
        product = detectByBinDir();
        if (product != AtlassianProduct.UNKNOWN) {
            System.out.println("atlassian-agent: Product detected via BIN_DIR analysis: " + product.getDisplayName());
            return product;
        }
        
            System.out.println("atlassian-agent: Unable to detect product type, using fallback");
            return AtlassianProduct.UNKNOWN;
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error during product detection: " + e.getMessage());
            return AtlassianProduct.UNKNOWN;
        }
    }
    
    /**
     * Detect product by checking system properties for home directories
     */
    private static AtlassianProduct detectBySystemProperties() {
        if (System.getProperty("jira.home") != null) {
            return AtlassianProduct.JIRA;
        }
        if (System.getProperty("confluence.home") != null) {
            return AtlassianProduct.CONFLUENCE;
        }
        if (System.getProperty("bamboo.home") != null) {
            return AtlassianProduct.BAMBOO;
        }
        if (System.getProperty("bitbucket.home") != null) {
            return AtlassianProduct.BITBUCKET;
        }
        if (System.getProperty("fisheye.home") != null) {
            return AtlassianProduct.FISHEYE;
        }
        if (System.getProperty("crucible.home") != null) {
            return AtlassianProduct.CRUCIBLE;
        }
        if (System.getProperty("crowd.home") != null) {
            return AtlassianProduct.CROWD;
        }
        return AtlassianProduct.UNKNOWN;
    }
    
    /**
     * Detect product by checking environment variables
     */
    private static AtlassianProduct detectByEnvironmentVariables() {
        if (System.getenv("JIRA_HOME") != null) {
            return AtlassianProduct.JIRA;
        }
        if (System.getenv("CONFLUENCE_HOME") != null) {
            return AtlassianProduct.CONFLUENCE;
        }
        if (System.getenv("BAMBOO_HOME") != null) {
            return AtlassianProduct.BAMBOO;
        }
        if (System.getenv("BITBUCKET_HOME") != null) {
            return AtlassianProduct.BITBUCKET;
        }
        if (System.getenv("FISHEYE_HOME") != null) {
            return AtlassianProduct.FISHEYE;
        }
        if (System.getenv("CRUCIBLE_HOME") != null) {
            return AtlassianProduct.CRUCIBLE;
        }
        if (System.getenv("CROWD_HOME") != null) {
            return AtlassianProduct.CROWD;
        }
        return AtlassianProduct.UNKNOWN;
    }
    
    /**
     * Detect product by analyzing classpath for product-specific classes
     */
    private static AtlassianProduct detectByClasspath() {
        List<String> jiraClasses = Arrays.asList(
            "com.atlassian.jira.ComponentManager",
            "com.atlassian.jira.startup.JiraStartupLogger",
            "com.atlassian.jira.config.database.DatabaseConfigurationManager"
        );
        
        List<String> confluenceClasses = Arrays.asList(
            "com.atlassian.confluence.setup.ConfluenceConfigurationManager",
            "com.atlassian.confluence.core.ConfluenceActionSupport",
            "com.atlassian.confluence.spaces.SpaceManager"
        );
        
        List<String> bambooClasses = Arrays.asList(
            "com.atlassian.bamboo.configuration.ConfigurationMap",
            "com.atlassian.bamboo.build.BuildManager",
            "com.atlassian.bamboo.plan.PlanManager"
        );
        
        List<String> bitbucketClasses = Arrays.asList(
            "com.atlassian.bitbucket.repository.RepositoryService",
            "com.atlassian.bitbucket.project.ProjectService",
            "com.atlassian.bitbucket.server.ApplicationPropertiesService"
        );
        
        if (classesExist(jiraClasses)) {
            return AtlassianProduct.JIRA;
        }
        if (classesExist(confluenceClasses)) {
            return AtlassianProduct.CONFLUENCE;
        }
        if (classesExist(bambooClasses)) {
            return AtlassianProduct.BAMBOO;
        }
        if (classesExist(bitbucketClasses)) {
            return AtlassianProduct.BITBUCKET;
        }
        
        return AtlassianProduct.UNKNOWN;
    }
    
    /**
     * Detect product by analyzing BIN_DIR environment variable (useful for Bitbucket)
     */
    private static AtlassianProduct detectByBinDir() {
        String binDir = System.getenv("BIN_DIR");
        if (binDir != null) {
            if (binDir.contains("bitbucket")) {
                return AtlassianProduct.BITBUCKET;
            }
            if (binDir.contains("jira")) {
                return AtlassianProduct.JIRA;
            }
            if (binDir.contains("confluence")) {
                return AtlassianProduct.CONFLUENCE;
            }
            if (binDir.contains("bamboo")) {
                return AtlassianProduct.BAMBOO;
            }
        }
        return AtlassianProduct.UNKNOWN;
    }
    
    /**
     * Check if any of the specified classes exist in the classpath
     */
    private static boolean classesExist(List<String> classNames) {
        for (String className : classNames) {
            try {
                Class.forName(className);
                return true;
            } catch (ClassNotFoundException e) {
                // Continue checking other classes
            }
        }
        return false;
    }
}
