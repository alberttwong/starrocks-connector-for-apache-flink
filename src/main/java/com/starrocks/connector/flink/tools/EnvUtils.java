package com.starrocks.connector.flink.tools;

import org.apache.flink.runtime.util.EnvironmentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EnvUtils {

    private static class SRFCPropertiesHolder {

        private static final Logger logger = LoggerFactory.getLogger(SRFCPropertiesHolder.class);
        private static final Properties instance = loadProperties();
        public static Properties loadProperties() {
            Properties props = new Properties();
            try {
                props.load(SRFCPropertiesHolder.class.getClassLoader().getResourceAsStream("srfc.properties"));
            } catch (IOException e) {
                logger.warn("load srfc properties failed.", e);
            }
            return props;
        }
    }

    public static String getSRFCVersion() {
        return SRFCPropertiesHolder.instance.getProperty("srfc.version", "");
    }

    private static final class GitInformation {

        private static final Logger LOG = LoggerFactory.getLogger(GitInformation.class);
        private static final String PROP_FILE = "starrocks-connector-git.properties";
        private static final String UNKNOWN = "unknown";

        private static final GitInformation INSTANCE = new GitInformation();

        private String gitBuildTime = UNKNOWN;
        private String gitCommitId = UNKNOWN;
        private String gitCommitIdAbbrev = UNKNOWN;
        private String gitCommitTime = UNKNOWN;

        private String getProperty(Properties properties, String key, String defaultValue) {
            String value = properties.getProperty(key);
            if (value == null || value.charAt(0) == '$') {
                return defaultValue;
            }
            return value;
        }

        public GitInformation() {
            ClassLoader classLoader = EnvironmentInformation.class.getClassLoader();
            try (InputStream propFile = classLoader.getResourceAsStream(PROP_FILE)) {
                if (propFile != null) {
                    Properties properties = new Properties();
                    properties.load(propFile);
                    gitBuildTime = getProperty(properties, "git.build.time", UNKNOWN);
                    gitCommitId = getProperty(properties, "git.commit.id", UNKNOWN);
                    gitCommitIdAbbrev = getProperty(properties, "git.commit.id.abbrev", UNKNOWN);
                    gitCommitTime = getProperty(properties, "git.commit.time", UNKNOWN);
                }
            } catch (Exception e) {
                LOG.warn("Can't load git information, exception message: {}", e.getMessage());
            }
        }

        public String getGitBuildTime() {
            return gitBuildTime;
        }

        public String getGitCommitId() {
            return gitCommitId;
        }

        public String getGitCommitIdAbbrev() {
            return gitCommitIdAbbrev;
        }

        public String getGitCommitTime() {
            return gitCommitTime;
        }

        @Override
        public String toString() {
            return "GitInformation{" +
                    "gitBuildTime='" + gitBuildTime + '\'' +
                    ", gitCommitId='" + gitCommitId + '\'' +
                    ", gitCommitIdAbbrev='" + gitCommitIdAbbrev + '\'' +
                    ", gitCommitTime='" + gitCommitTime + '\'' +
                    '}';
        }
    }

    public static GitInformation getGitInformation() {
        return GitInformation.INSTANCE;
    }
}
