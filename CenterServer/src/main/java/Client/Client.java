package Client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class Client implements Serializable {

    private String phoneNumber;
    private String password;
    private String  ip;
}
