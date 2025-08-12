package io.zhile.crack.atlassian.agent;

import io.zhile.crack.atlassian.keygen.Encoder;
import io.zhile.crack.atlassian.license.LicenseProperty;
import io.zhile.crack.atlassian.license.products.*;

/**
 * Automatic license generator that detects product and generates appropriate licenses
 * @author The Augster
 * @version 1.0
 */
public class AutoLicenseGenerator {
    
    private static final String DEFAULT_EMAIL = "admin@example.com";
    private static final String DEFAULT_NAME = "Administrator";
    private static final String DEFAULT_ORGANIZATION = "Example Organization";
    
    private static boolean licenseGenerated = false;
    
    /**
     * Generate and optionally apply a license for the detected product
     * @return true if license was successfully generated, false otherwise
     */
    public static boolean generateLicense() {
        // Check if auto-generation is disabled
        if (Boolean.parseBoolean(getConfigValue("ATLASSIAN_AGENT_DISABLE_AUTO_LICENSE", "false"))) {
            System.out.println("atlassian-agent: Automatic license generation is disabled");
            return false;
        }

        // Prevent multiple license generations
        if (licenseGenerated) {
            System.out.println("atlassian-agent: License already generated, skipping");
            return true;
        }
        
        try {
            // Detect the product
            ProductDetector.AtlassianProduct product = ProductDetector.detectProduct();
            if (product == ProductDetector.AtlassianProduct.UNKNOWN) {
                System.out.println("atlassian-agent: Cannot generate license for unknown product");
                System.out.println("atlassian-agent: You can manually generate a license using the command line interface");
                System.out.println("atlassian-agent: Example: java -jar atlassian-agent.jar -p jira -s YOUR_SERVER_ID -m admin@example.com -o \"Your Organization\"");
                printConfigurationHelp();
                return false;
            }

            // Check if we're in setup mode and apply license proactively
            if (isInSetupMode(product)) {
                System.out.println("atlassian-agent: Detected setup mode, applying license proactively...");
                return applyLicenseProactively(product);
            }

            // Extract server ID
            String serverId = ServerIdExtractor.extractServerId(product);

            // Generate license
            String licenseCode = generateLicenseForProduct(product, serverId);
            if (licenseCode != null && !licenseCode.isEmpty()) {
                System.out.println("atlassian-agent: License generated successfully for " + product.getDisplayName());
                System.out.println("atlassian-agent: Server ID: " + serverId);
                System.out.println("atlassian-agent: License Code:");
                System.out.println(licenseCode);
                System.out.println("atlassian-agent: ========================================");
                
                licenseGenerated = true;
                return true;
            } else {
                System.out.println("atlassian-agent: Failed to generate license for " + product.getDisplayName());
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error during automatic license generation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate license for a specific product
     */
    public static String generateLicenseForProduct(ProductDetector.AtlassianProduct product, String serverId) {
        try {
            LicenseProperty licenseProperty = createLicenseProperty(product, serverId);
            if (licenseProperty == null) {
                return null;
            }
            
            licenseProperty.init();
            return Encoder.encode(licenseProperty.toString());
            
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error generating license: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create appropriate license property object based on product type
     */
    private static LicenseProperty createLicenseProperty(ProductDetector.AtlassianProduct product, String serverId) {
        String contactName = getConfigValue("ATLASSIAN_LICENSE_NAME", DEFAULT_NAME);
        String contactEmail = getConfigValue("ATLASSIAN_LICENSE_EMAIL", DEFAULT_EMAIL);
        String organization = getConfigValue("ATLASSIAN_LICENSE_ORG", DEFAULT_ORGANIZATION);
        boolean dataCenter = Boolean.parseBoolean(getConfigValue("ATLASSIAN_LICENSE_DATACENTER", "false"));
        
        switch (product) {
            case JIRA:
                return new JIRASoftware(contactName, contactEmail, serverId, organization, dataCenter);
                
            case CONFLUENCE:
                return new Confluence(contactName, contactEmail, serverId, organization, dataCenter);
                
            case BAMBOO:
                return new Bamboo(contactName, contactEmail, serverId, organization, dataCenter);

            case BITBUCKET:
                return new Bitbucket(contactName, contactEmail, serverId, organization, dataCenter);
                
            case FISHEYE:
                return new FishEye(contactName, contactEmail, serverId, organization, dataCenter);

            case CRUCIBLE:
                return new Crucible(contactName, contactEmail, serverId, organization, dataCenter);

            case CROWD:
                return new Crowd(contactName, contactEmail, serverId, organization, dataCenter);
                
            default:
                System.out.println("atlassian-agent: Unsupported product for license generation: " + product);
                return null;
        }
    }
    
    /**
     * Get configuration value from environment variables or system properties with fallback
     */
    private static String getConfigValue(String key, String defaultValue) {
        // Try environment variable first
        String value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        
        // Try system property
        value = System.getProperty(key.toLowerCase().replace('_', '.'));
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        
        return defaultValue;
    }
    
    /**
     * Reset the license generation flag (useful for testing)
     */
    public static void resetLicenseGeneration() {
        licenseGenerated = false;
    }
    
    /**
     * Check if license has been generated
     */
    public static boolean isLicenseGenerated() {
        return licenseGenerated;
    }

    /**
     * Print configuration help for users
     */
    public static void printConfigurationHelp() {
        System.out.println("atlassian-agent: Configuration Options (Environment Variables or System Properties):");
        System.out.println("  ATLASSIAN_AGENT_DISABLE_AUTO_LICENSE=true    - Disable automatic license generation");
        System.out.println("  ATLASSIAN_LICENSE_NAME=<name>                - License holder name (default: Administrator)");
        System.out.println("  ATLASSIAN_LICENSE_EMAIL=<email>              - License holder email (default: admin@example.com)");
        System.out.println("  ATLASSIAN_LICENSE_ORG=<organization>         - Organization name (default: Example Organization)");
        System.out.println("  ATLASSIAN_LICENSE_DATACENTER=true            - Generate Data Center license (default: false)");
    }

    /**
     * Check if the application is in setup mode
     */
    private static boolean isInSetupMode(ProductDetector.AtlassianProduct product) {
        try {
            switch (product) {
                case BITBUCKET:
                    return isBitbucketInSetupMode();
                case JIRA:
                    return isJiraInSetupMode();
                case CONFLUENCE:
                    return isConfluenceInSetupMode();
                default:
                    return false;
            }
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error checking setup mode: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if Bitbucket is in setup mode
     */
    private static boolean isBitbucketInSetupMode() {
        try {
            // Check if setup is complete by looking for setup-related system properties or files
            String bitbucketHome = System.getProperty("bitbucket.home");
            if (bitbucketHome == null) {
                bitbucketHome = System.getenv("BITBUCKET_HOME");
            }

            // If no home directory is set, we're likely in setup
            if (bitbucketHome == null) {
                return true;
            }

            // Check if setup is complete by looking for configuration files
            java.io.File setupCompleteFile = new java.io.File(bitbucketHome + "/shared/bitbucket.properties");
            if (!setupCompleteFile.exists()) {
                System.out.println("atlassian-agent: Bitbucket setup not complete - no bitbucket.properties found");
                return true;
            }

            // Additional check: look for setup-related classes in the call stack
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                if (className.contains("setup") || className.contains("Setup") ||
                    className.contains("wizard") || className.contains("Wizard") ||
                    className.contains("license") || className.contains("License")) {
                    System.out.println("atlassian-agent: Detected setup-related class in call stack: " + className);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            System.out.println("atlassian-agent: Error checking Bitbucket setup mode: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if JIRA is in setup mode
     */
    private static boolean isJiraInSetupMode() {
        // Similar logic for JIRA - placeholder for now
        return false;
    }

    /**
     * Check if Confluence is in setup mode
     */
    private static boolean isConfluenceInSetupMode() {
        // Similar logic for Confluence - placeholder for now
        return false;
    }

    /**
     * Apply license proactively during setup
     */
    private static boolean applyLicenseProactively(ProductDetector.AtlassianProduct product) {
        try {
            // Extract server ID (use default if not available)
            String serverId = ServerIdExtractor.extractServerId(product);
            if (serverId == null) {
                System.out.println("atlassian-agent: Cannot extract server ID, using default for proactive license application");
                serverId = "BXXX-XXXX-XXXX-XXXX";
            }

            // Generate license
            String licenseCode = generateLicenseForProduct(product, serverId);
            if (licenseCode == null) {
                System.out.println("atlassian-agent: Failed to generate license for proactive application");
                return false;
            }

            System.out.println("atlassian-agent: Generated license for proactive application:");
            System.out.println("atlassian-agent: Server ID: " + serverId);
            System.out.println("atlassian-agent: License Code:");
            System.out.println(licenseCode);
            System.out.println("atlassian-agent: ========================================");

            // For Bitbucket, create properties file for license automation
            if (product == ProductDetector.AtlassianProduct.BITBUCKET) {
                boolean propertiesCreated = BitbucketPropertiesGenerator.generateLicensePropertiesFile(licenseCode);
                if (propertiesCreated) {
                    System.out.println("atlassian-agent: License automation configured successfully!");
                }
            }

            // License generated successfully, will be applied via verification hooks
            System.out.println("atlassian-agent: License generated successfully, will be applied via verification hooks");
            licenseGenerated = true;
            return true;

        } catch (Exception e) {
            System.out.println("atlassian-agent: Error during proactive license application: " + e.getMessage());
            return false;
        }
    }




}
