package Models;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    Client client = new Client("javad","09217679934");

    @Test
    void saveMassage() {
        assertEquals(0, client.getRelOfOther().size());

        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals(1, client.getRelOfOther().size());
        assertEquals(1, client.getRelOfOther().get("ali").size());

        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals(1, client.getRelOfOther().size());
        assertEquals(2, client.getRelOfOther().get("ali").size());

        client.saveMassage("sara", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals(2, client.getRelOfOther().size());
        assertEquals(1, client.getRelOfOther().get("sara").size());
    }

    @Test
    void getSeq() {
        assertEquals(0, client.getSeq("ali"));

        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));
        client.saveMassage("sara", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals(2, client.getSeq("ali"));
        assertEquals(1, client.getSeq("sara"));
    }

    @Test
    void getChats() {
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.UNSEEN));
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals("[{\"unread_count\":1,\"name\":\"ali\"}]", client.getChats());

        client.getMessages("ali");

        assertEquals("[{\"unread_count\":0,\"name\":\"ali\"}]", client.getChats());
    }

    @Test
    void getMessages() {
        assertEquals("ERROR you have not interacted with this user before", client.getMessages("ali"));

        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.UNSEEN));
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"type\": \"text\", \"body\": \"Salam chetori?\"}"),
                MassageStatus.SENDER));

        assertEquals("OK [{\"type\":\"text\",\"body\":\"Salam chetori?\"},{\"type\":\"text\",\"body\":\"Salam chetori?\"}]",client.getMessages("ali"));
    }

    @Test
    void editMessage() {
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"from\":\"ali\" , \"type\": \"text\", \"body\": \"Salam chetori?\",\"seq\":1}"),
                MassageStatus.UNSEEN));
        client.saveMassage("ali", new Massage((JSONObject) JSONValue.parse("{\"from\":\"javad\" , \"type\": \"text\", \"body\": \"Salam chetori?\",\"seq\":2}"),
                MassageStatus.SENDER));

        assertEquals("OK edit done", client.editMessage("ali",2,"Hi",true));

        String newMassage = (String) client.getRelOfOther().get("ali").get(1).getMassage().get("body");
        assertEquals("Hi", newMassage);

        assertEquals("ERROR You are not the sender of this message", client.editMessage("ali",1,"Hi",true));

        newMassage = (String) client.getRelOfOther().get("ali").get(0).getMassage().get("body");
        assertNotEquals("Hi", newMassage);
    }
}