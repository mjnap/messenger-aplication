package main;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class RelOfTwoClient {

    String  client1;
    String  client2;
    List<JSONObject> massageList;

    public RelOfTwoClient(String client1, String client2) {
        this.client1 = client1;
        this.client2 = client2;
        massageList = new ArrayList<>();
    }

    public int getSeq(){
        return massageList.size();
    }

    public void addMassage(JSONObject massage){
        massageList.add(massage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelOfTwoClient that = (RelOfTwoClient) o;
        return Objects.equals(client1, that.client1) &&
                Objects.equals(client2, that.client2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client1, client2);
    }
}
