package pgapp.dto;

/**
 * @author Stefan Stan on 06.06.2017.
 */
public class DatabaseManagementDetailsObject {

    private int status;

    public DatabaseManagementDetailsObject(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
