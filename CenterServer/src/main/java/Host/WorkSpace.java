package Host;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class WorkSpace implements Serializable {
    String name;
    Host host;
    int port;
}
