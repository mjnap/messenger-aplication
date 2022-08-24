import DataRepository.LoadData;
import DataRepository.SaveData;
import Models.Host;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.*;

class LoadDataTest {

    static Host host;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        host = Mockito.mock(Host.class);
        new SaveData().save(host);
    }

    @SneakyThrows
    @Test
    void loadHost() {
        assertNotNull(LoadData.loadHost());
    }

    @SneakyThrows
    @Test
    void loadWorkspaces() {
        assertNotNull(LoadData.loadWorkspaces());
    }
}