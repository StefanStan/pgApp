package pgapp.service;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pgapp.dto.DatabaseManagementDetailsObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Stan on 06.06.2017.
 */
@Service
@PropertySource(value = "classpath:pgapp/application.properties")
public class DatabaseManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagementService.class);

    @Autowired
    private Environment env;

    public List<String> getDBsNames() {
        List<String> result = new ArrayList<>();
        String serverPath = env.getProperty("serverScriptsPath");
        Arrays.stream(new File(serverPath).listFiles()).forEach((x) -> {
            if(!x.isDirectory()) {
                result.add(x.getName());
            }
        });
        return result;
    }

    public DatabaseManagementDetailsObject changeDB(String dbServerName, String action) {
        String serverPath = env.getProperty("serverScriptsPath");

        final List<String> baseCmds = new ArrayList<>();
        baseCmds.add(serverPath + dbServerName);
        baseCmds.add(action);
        final ProcessBuilder pb = new ProcessBuilder(baseCmds);

        try {
            final Process process = pb.start();

            final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF8"));
            String line = r.readLine();
            while (line != null) {
                LOGGER.info(line);
                line = r.readLine();
            }
            r.close();

            final int dcertExitCode = process.waitFor();
            LOGGER.info("Database " + dbServerName + " " + action + " process finished with exit code: " + dcertExitCode);
            if (dcertExitCode != 0) {
                throw new RuntimeException("Database " + dbServerName + " " + action + " process din not finished successfuly. Exit code: " + dcertExitCode);
            }
            return new DatabaseManagementDetailsObject(0, getDatabasePort(dbServerName));

        } catch (Exception t) {
            LOGGER.error("Error executing " + action + " on database " + dbServerName, t);
            throw new RuntimeException("Error executing " + action + " on database " + dbServerName + ".", t);
        }
    }

    public DatabaseManagementDetailsObject basebackupDB(String dbServerName) {
        String basebackupScriptsPath = env.getProperty("basebackupScriptsPath");

        String ip = "127.0.0.1";
        String port = getDatabasePort(dbServerName);

        final List<String> baseCmds = new ArrayList<>();
        baseCmds.add(basebackupScriptsPath + dbServerName);
        baseCmds.add(ip);
        baseCmds.add(port);
        final ProcessBuilder pb = new ProcessBuilder(baseCmds);

        try {
            final Process process = pb.start();

            final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF8"));
            String line = r.readLine();
            while (line != null) {
                LOGGER.info(line);
                line = r.readLine();
            }
            r.close();

            final int dcertExitCode = process.waitFor();
            LOGGER.info("Database " + dbServerName + "backup process with " + ip + " " + port +  " finished with exit code: " + dcertExitCode);
            if (dcertExitCode != 0) {
                throw new RuntimeException("Database " + dbServerName + "backup process with " + ip + " " + port + " din not finished successfuly. Exit code: " + dcertExitCode);
            }
            return new DatabaseManagementDetailsObject(0, null);

        } catch (Exception t) {
            LOGGER.error("Error executing backup on database " + dbServerName + " with " + ip + " " + port +  "", t);
            throw new RuntimeException("Error executing backup on database " + dbServerName + " with " + ip + " " + port +  "", t);
        }
    }

    public DatabaseManagementDetailsObject recoverDB(String dbServerName, String dateTime, boolean startDBServer) {
        String recoveryScriptsPath = env.getProperty("recoveryScriptsPath");

        final List<String> baseCmds = new ArrayList<>();
        baseCmds.add(recoveryScriptsPath + dbServerName);
        baseCmds.add(dateTime);
        baseCmds.add(String.valueOf(startDBServer));
        final ProcessBuilder pb = new ProcessBuilder(baseCmds);

        try {
            final Process process = pb.start();

            final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF8"));
            String line = r.readLine();
            while (line != null) {
                LOGGER.info(line);
                line = r.readLine();
            }
            r.close();

            final int dcertExitCode = process.waitFor();
            LOGGER.info("Database " + dbServerName + " point in time recovery at " + dateTime +  " finished with exit code: " + dcertExitCode);
            if (dcertExitCode != 0) {
                throw new RuntimeException("Database " + dbServerName + " point in time recovery at " + dateTime + " din not finished successfuly. Exit code: " + dcertExitCode);
            }
            return new DatabaseManagementDetailsObject(0, null);

        } catch (Exception t) {
            LOGGER.error("Error executing point in time recovery on database " + dbServerName + " from " + dateTime, t);
            throw new RuntimeException("Error executing point in time recovery on database " + dbServerName + " from " + dateTime, t);
        }
    }

    private String getDatabasePort(String dbServerName) {
        String pgdataPath = env.getProperty("pgdataPath");

        try {
            String postgresqlConfData = new String(Files.readAllBytes(Paths.get(pgdataPath + dbServerName + "/postgresql.conf")), StandardCharsets.UTF_8);

            Pattern pattern = Pattern.compile("port[ ]*=[ ]*[0-9]+");
            Matcher matcher = pattern.matcher(postgresqlConfData);
            if (matcher.find()) {
                return matcher.group(1).split("=")[1].trim();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
