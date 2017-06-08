package pgapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pgapp.dto.DatabaseManagementDetailsObject;
import pgapp.service.DatabaseManagementService;

import java.util.List;

/**
 * @author Stefan Stan on 06.06.2017.
 */
@RestController
@RequestMapping("/pgapp")
public class DatabaseManagementAPI {

    @Autowired
    private DatabaseManagementService dbService;

    @GetMapping("/server")
    public List<String> getDBsNames() {
        return dbService.getDBsNames();
    }

    @PostMapping("/server")
    public DatabaseManagementDetailsObject changeDB(
            @RequestParam("dbServerName") String dbServerName,
            @RequestParam("action") String action
    ) {
        return dbService.changeDB(dbServerName, action);
    }

    @PostMapping("/basebackup")
    public DatabaseManagementDetailsObject basebackupDB(
            @RequestParam("dbServerName") String dbServerName
    ) {
        return dbService.basebackupDB(dbServerName);
    }

    @PostMapping("/pitr")
    public DatabaseManagementDetailsObject recoverDB(
            @RequestParam("dbServerName") String dbServerName,
            @RequestParam("dateTime") String dateTime
    ) {
        return dbService.recoverDB(dbServerName, dateTime, true);
    }
}
