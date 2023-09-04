import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.connection.HikariCPConnectionPoolFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPConnectionPoolTest {
    private ConnectionPool connectionPool = new HikariCPConnectionPoolFactory().createPool();

    @Test
    public void getConnectionTest() throws SQLException {
        Connection connection = connectionPool.getConnection();
        Assertions.assertNotNull(connection);
    }
}
