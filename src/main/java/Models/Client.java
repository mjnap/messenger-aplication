package Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(of = {"userName", "phoneNumber"})
@ToString(of = {"userName", "phoneNumber"})
public class Client implements Serializable {

    @Getter
    private String userName;
    @Getter
    private String phoneNumber;
    @Getter
    private Map<String, List<Massage>> relOfOther;
    public transient DataInputStream input;
    public transient DataOutputStream output;
    public ClientStatus status;

    public Client(String userName, String phoneNumber) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        relOfOther = new HashMap<>();
    }

    public void setConnection(Socket socket) throws IOException {
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void saveMassage(String receiver, Massage massage){
        try{
            List<Massage> newList = relOfOther.get(receiver);
            newList.add(massage);
            relOfOther.put(receiver, newList);
        }catch (NullPointerException e){
            relOfOther.put(receiver, new ArrayList<>(Arrays.asList(massage)));
        }
    }

    public int getSeq(String goalClient){
        try {
            return relOfOther.get(goalClient).size();
        }catch (NullPointerException e){
            return 0;
        }
    }

    /**
     * This method show unread messages
     * @return
     */
    public String getChats(){
        JSONArray jsonArray = new JSONArray();
        Set<String> otherUsers = relOfOther.keySet();
        for(String otherUser:otherUsers){
            List<Massage> massageList = relOfOther.get(otherUser);
            long unread = massageList.stream()
                                .filter(massage -> massage.getStatus().equals(MassageStatus.UNSEEN))
                                .count();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",otherUser);
            jsonObject.put("unread_count",unread);

            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    /**
     * This method get all messages that for a user
     * @param goalUser
     * @return
     */
    public String getMessages(String goalUser){
        if(relOfOther.containsKey(goalUser)){
            JSONArray jsonArray = new JSONArray();
            List<JSONObject> massageList = relOfOther.get(goalUser).stream()
                    .map(massage -> massage.getMassage())
                    .collect(Collectors.toList());

            List<Massage> massages = relOfOther.get(goalUser);
            for(int i=0; i<massages.size(); i++) {
                Massage massage = massages.get(i);
                if (massage.getStatus().equals(MassageStatus.UNSEEN)) {
                    massage.setStatus(MassageStatus.SEEN);
                    massages.set(i, massage);
                }
            }

            relOfOther.put(goalUser, massages);

            Collections.reverse(massageList);
            jsonArray.addAll(massageList);

            return "OK " + jsonArray.toString();
        }

        return "ERROR you have not interacted with this user before";
    }

    public String editMessage(String goalUser, long seq, String newBody, boolean isEditor){
        List<Massage> massageList = relOfOther.get(goalUser);
        JSONObject editedMassage = massageList.stream()
                                        .map(massage -> massage.getMassage())
                                        .filter(massage -> (long) massage.get("seq") == seq)
                                        .collect(Collectors.toList()).get(0);
        editedMassage.put("body",newBody);
        // the user who calls this function must also be the sender of the message
        if(isEditor){
            if(editedMassage.get("from").toString().equals(userName)){
                for(int i=0; i<massageList.size(); i++){
                    Massage tmpMassage = massageList.get(i);
                    long tmpSeq = (long) tmpMassage.getMassage().get("seq");
                    if(tmpSeq == seq) {
                        tmpMassage.setMassage(editedMassage);
                        massageList.set(i, tmpMassage);
                        break;
                    }
                }

                // update relOfOther
                relOfOther.put(goalUser, massageList);

                return "OK edit done";
            }

            return "ERROR You are not the sender of this message";
        }
        else {
            for(int i=0; i<massageList.size(); i++) {
                Massage tmpMassage = massageList.get(i);
                long tmpSeq = (long) tmpMassage.getMassage().get("seq");
                if (tmpSeq == seq) {
                    tmpMassage.setMassage(editedMassage);
                    massageList.set(i, tmpMassage);
                    break;
                }
            }

            // update relOfOther
            relOfOther.put(goalUser, massageList);

            return "OK edit done";
        }
    }
}

enum ClientStatus implements Serializable {
    ONLINE , OFFLINE
}