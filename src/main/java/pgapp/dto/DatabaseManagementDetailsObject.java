package pgapp.dto;

/**
 * @author Stefan Stan on 06.06.2017.
 */
public class DatabaseManagementDetailsObject {

    private int operationStatus;
    private Object operationResult;

    public DatabaseManagementDetailsObject(int operationStatus, Object operationResult) {
        this.operationStatus = operationStatus;
        this.operationResult = operationResult;
    }

    public int getOperationStatus() {
        return this.operationStatus;
    }

    public void setOperationStatus(int operationStatus) {
        this.operationStatus = operationStatus;
    }

    public Object getOperationResult() {
        return this.operationResult;
    }

    public void setOperationResult(Object operationResult) {
        this.operationResult = operationResult;
    }
}
