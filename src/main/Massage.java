package main;

import org.json.simple.JSONObject;

public class Massage {

    private JSONObject massage;
    private MassageStatus status;

    public Massage(JSONObject massage, MassageStatus status) {
        this.massage = massage;
        this.status = status;
    }

    public JSONObject getMassage() {
        return massage;
    }

    public void setMassage(JSONObject massage) {
        this.massage = massage;
    }

    public MassageStatus getStatus() {
        return status;
    }

    public void setStatus(MassageStatus status) {
        this.status = status;
    }
}

enum MassageStatus{
    SEEN , UNSEEN , SENDER
}
