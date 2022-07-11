package other;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Serializable;

public class Massage implements Serializable {

    private String massage;
    private MassageStatus status;

    public Massage(JSONObject massage, MassageStatus status) {
        this.massage = massage.toString();
        this.status = status;
    }

    public JSONObject getMassage() {
        return (JSONObject) JSONValue.parse(massage);
    }

    public void setMassage(JSONObject massage) {
        this.massage = massage.toString();
    }

    public MassageStatus getStatus() {
        return status;
    }

    public void setStatus(MassageStatus status) {
        this.status = status;
    }
}

enum MassageStatus implements Serializable{
    SEEN , UNSEEN , SENDER
}
