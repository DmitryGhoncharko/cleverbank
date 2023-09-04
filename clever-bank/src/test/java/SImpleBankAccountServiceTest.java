import com.example.mytask.connection.ConnectionPool;
import com.example.mytask.dao.BankAccountDao;
import com.example.mytask.dao.TransactionDao;
import com.example.mytask.dao.UserDao;
import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.BankDto;
import com.example.mytask.dto.CurrencyDto;
import com.example.mytask.dto.TransactionDto;
import com.example.mytask.dto.TransactionTypeDto;
import com.example.mytask.dto.UserDto;
import com.example.mytask.exception.DaoException;
import com.example.mytask.exception.ServiceException;
import com.example.mytask.service.SimpleBankAccountService;
import com.example.mytask.util.ConnectionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SImpleBankAccountServiceTest {
    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private UserDao userDao;

    @Mock
    private BankAccountDao bankAccountDao;

    @Mock
    private TransactionDao transactionDao;

    @BeforeAll
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBankAccount() throws DaoException, ServiceException {

        SimpleBankAccountService bankAccountService = new SimpleBankAccountService(connectionPool);
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .familyName("Smith")
                .build();
        BankAccountDto inputBankAccountDto = BankAccountDto.builder()
                .userDto(userDto)
                .dateCreated(new Timestamp(System.currentTimeMillis()))
                .currencyDto(new CurrencyDto(1L, "USD"))
                .balance(1000.0)
                .bankDto(new BankDto(1L, "Bank"))
                .accrualDate(new Timestamp(System.currentTimeMillis()))
                .build();
        when(ConnectionManager.getConnection(connectionPool)).thenReturn(connection);
        when(userDao.createUser(any(UserDto.class))).thenReturn(userDto);
        when(bankAccountDao.createBankAccount(any(BankAccountDto.class))).thenReturn(inputBankAccountDto);


        BankAccountDto resultBankAccountDto = bankAccountService.createBankAccount(inputBankAccountDto);


        assertNotNull(resultBankAccountDto);
        assertEquals(inputBankAccountDto.getUserDto().getId(), resultBankAccountDto.getUserDto().getId());
        assertEquals(inputBankAccountDto.getBalance(), resultBankAccountDto.getBalance(), 0.001);

    }

    @Test
    public void testAddBalance() throws DaoException, ServiceException {

        SimpleBankAccountService bankAccountService = new SimpleBankAccountService(connectionPool);
        BankAccountDto bankAccountDto = BankAccountDto.builder()
                .bankAccountId(1L)
                .userDto(UserDto.builder().id(1L).build())
                .dateCreated(new Timestamp(System.currentTimeMillis()))
                .currencyDto(new CurrencyDto(1L, "USD"))
                .balance(1000.0)
                .bankDto(new BankDto(1L, "Bank"))
                .accrualDate(new Timestamp(System.currentTimeMillis()))
                .build();
        double money = 500.0;
        TransactionDto expectedTransaction = TransactionDto.builder()
                .bankAccountDtoTo(bankAccountDto)
                .transactionTypeDto(TransactionTypeDto.builder().id(1L).name("ADD_BALANCE").build())
                .transactionDate(new Timestamp(System.currentTimeMillis()))
                .bankAccountDtoFrom(bankAccountDto)
                .money(money)
                .id(1L)
                .build();
        when(ConnectionManager.getConnection(connectionPool)).thenReturn(connection);
        when(transactionDao.createTransaction(any(TransactionDto.class))).thenReturn(expectedTransaction);
        when(bankAccountDao.findById(1L)).thenReturn(Optional.of(bankAccountDto));


        TransactionDto resultTransaction = bankAccountService.addBalance(bankAccountDto, money);


        assertNotNull(resultTransaction);
        assertEquals(expectedTransaction.getId(), resultTransaction.getId());
        assertEquals(expectedTransaction.getBankAccountDtoTo().getBankAccountId(), resultTransaction.getBankAccountDtoTo().getBankAccountId());
        assertEquals(expectedTransaction.getMoney(), resultTransaction.getMoney(), 0.001);

    }

    @Test
    public void testWithdrawBalance() throws DaoException, ServiceException {

        SimpleBankAccountService bankAccountService = new SimpleBankAccountService(connectionPool);
        BankAccountDto bankAccountDto = BankAccountDto.builder()
                .bankAccountId(1L)
                .userDto(UserDto.builder().id(1L).build())
                .dateCreated(new Timestamp(System.currentTimeMillis()))
                .currencyDto(new CurrencyDto(1L, "USD"))
                .balance(1000.0)
                .bankDto(new BankDto(1L, "Bank"))
                .accrualDate(new Timestamp(System.currentTimeMillis()))
                .build();
        double money = 500.0;
        TransactionDto expectedTransaction = TransactionDto.builder()
                .bankAccountDtoTo(bankAccountDto)
                .transactionTypeDto(TransactionTypeDto.builder().id(2L).name("WITHDRAW").build())
                .transactionDate(new Timestamp(System.currentTimeMillis()))
                .bankAccountDtoFrom(bankAccountDto)
                .money(money)
                .id(1L)
                .build();
        when(ConnectionManager.getConnection(connectionPool)).thenReturn(connection);
        when(transactionDao.createTransaction(any(TransactionDto.class))).thenReturn(expectedTransaction);
        when(bankAccountDao.findById(1L)).thenReturn(Optional.of(bankAccountDto));


        TransactionDto resultTransaction = bankAccountService.withdrawBalance(bankAccountDto, money);


        assertNotNull(resultTransaction);
        assertEquals(expectedTransaction.getId(), resultTransaction.getId());
        assertEquals(expectedTransaction.getBankAccountDtoFrom().getBankAccountId(), resultTransaction.getBankAccountDtoFrom().getBankAccountId());
        assertEquals(expectedTransaction.getMoney(), resultTransaction.getMoney(), 0.001);

    }

    @Test
    public void testBankAccountTransaction() throws DaoException, ServiceException {

        SimpleBankAccountService bankAccountService = new SimpleBankAccountService(connectionPool);
        BankAccountDto bankAccountDtoTo = BankAccountDto.builder()
                .bankAccountId(1L)
                .userDto(UserDto.builder().id(1L).build())
                .dateCreated(new Timestamp(System.currentTimeMillis()))
                .currencyDto(new CurrencyDto(1L, "USD"))
                .balance(1000.0)
                .bankDto(new BankDto(1L, "Bank"))
                .accrualDate(new Timestamp(System.currentTimeMillis()))
                .build();
        BankAccountDto bankAccountDtoFrom = BankAccountDto.builder()
                .bankAccountId(2L)
                .userDto(UserDto.builder().id(2L).build())
                .dateCreated(new Timestamp(System.currentTimeMillis()))
                .currencyDto(new CurrencyDto(1L, "USD"))
                .balance(2000.0)
                .bankDto(new BankDto(2L, "Bank"))
                .accrualDate(new Timestamp(System.currentTimeMillis()))
                .build();
        double money = 500.0;
        TransactionDto expectedTransaction = TransactionDto.builder()
                .bankAccountDtoTo(bankAccountDtoTo)
                .transactionTypeDto(TransactionTypeDto.builder().id(3L).name("TRANSACTION").build())
                .transactionDate(new Timestamp(System.currentTimeMillis()))
                .bankAccountDtoFrom(bankAccountDtoFrom)
                .money(money)
                .id(1L)
                .build();
        when(ConnectionManager.getConnection(connectionPool)).thenReturn(connection);
        when(transactionDao.createTransaction(any(TransactionDto.class))).thenReturn(expectedTransaction);
        when(bankAccountDao.findById(1L)).thenReturn(Optional.of(bankAccountDtoTo));
        when(bankAccountDao.findById(2L)).thenReturn(Optional.of(bankAccountDtoFrom));


        TransactionDto resultTransaction = bankAccountService.bankAccountTransaction(bankAccountDtoTo, bankAccountDtoFrom, money);


        assertNotNull(resultTransaction);
        assertEquals(expectedTransaction.getId(), resultTransaction.getId());
        assertEquals(expectedTransaction.getBankAccountDtoFrom().getBankAccountId(), resultTransaction.getBankAccountDtoFrom().getBankAccountId());
        assertEquals(expectedTransaction.getBankAccountDtoTo().getBankAccountId(), resultTransaction.getBankAccountDtoTo().getBankAccountId());
        assertEquals(expectedTransaction.getMoney(), resultTransaction.getMoney(), 0.001);

    }
}
