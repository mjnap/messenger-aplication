package other;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Client {

    private String userName;
    private String phoneNumber;
    // Map <receiverUser , massages>
    private Map<String, List<Massage>> relOfOther;
    public DataInputStream input;
    public DataOutputStream output;
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

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public int getSeq(String otherClient){
        try {
            return relOfOther.get(otherClient).size();
        }catch (NullPointerException e){
            return 0;
        }
    }

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

    public String getMessages(String goalUser){
        JSONArray jsonArray = new JSONArray();
        List<JSONObject> massageList = relOfOther.get(goalUser).stream()
                                                        .map(massage -> massage.getMassage())
                                                        .collect(Collectors.toList());

        List<Massage> massages = relOfOther.get(goalUser);
        for(int i=0; i<massages.size(); i++)
            if(massages.get(i).getStatus().equals(MassageStatus.UNSEEN))
                massages.set(i, new Massage(massages.get(i).getMassage(), MassageStatus.SEEN));

        relOfOther.put(goalUser, massages);

        jsonArray.addAll(massageList);

        return jsonArray.toString();
    }

    @Override
    public String toString() {
        return "Client{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", relOfOther=" + relOfOther +
                ", input=" + input +
                ", output=" + output +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(userName, client.userName) &&
                Objects.equals(phoneNumber, client.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, phoneNumber);
    }
}
