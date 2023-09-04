package com.example.mytask.servlet;

import com.example.mytask.dto.BankAccountDto;
import com.example.mytask.dto.BankDto;
import com.example.mytask.dto.CurrencyDto;
import com.example.mytask.dto.TransactType;
import com.example.mytask.dto.TransactionDto;
import com.example.mytask.dto.TransactionTypeDto;
import com.example.mytask.dto.UserDto;
import com.example.mytask.exception.ServiceException;
import com.example.mytask.exception.ValidationFailedException;
import com.example.mytask.quartz.SheduleJob;
import com.example.mytask.service.BankAccountService;
import com.example.mytask.service.ServiceFactory;
import com.example.mytask.service.SimpleServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class MainServlet extends HttpServlet {

    private final ServiceFactory serviceFactory = new SimpleServiceFactory();
    private final BankAccountService bankAccountService = serviceFactory.createBankAccount();
    private Scheduler scheduler;
    @Override
    public void init() throws ServletException {
        super.init();

        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            JobDetail jobDetail = JobBuilder.newJob(SheduleJob.class)
                    .withIdentity("dataUpdateJob", "group1")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("dataUpdateTrigger", "group1")
                    .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(1))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if("create-bank-account".equals(action)){
            Long bankAccountId = Long.parseLong(req.getParameter("bankAccountId"));
            Long userId = Long.parseLong(req.getParameter("userId"));
            Timestamp dateCreated = Timestamp.valueOf(req.getParameter("dateCreated"));
            Long currencyId = Long.parseLong(req.getParameter("currencyId"));
            double balance = Double.parseDouble(req.getParameter("balance"));
            Long bankId = Long.parseLong(req.getParameter("bankId"));
            Timestamp accrualDate = Timestamp.valueOf(req.getParameter("accrualDate"));

            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String familyName = req.getParameter("familyName");

            UserDto userDto = UserDto.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .familyName(familyName)
                    .build();

            CurrencyDto currencyDto = CurrencyDto.builder()
                    .id(currencyId)
                    .name(req.getParameter("currencyName"))
                    .build();

            BankDto bankDto = BankDto.builder()
                    .id(bankId)
                    .name(req.getParameter("bankName"))
                    .build();

            BankAccountDto bankAccountDto = BankAccountDto.builder()
                    .bankAccountId(bankAccountId)
                    .userDto(userDto)
                    .dateCreated(dateCreated)
                    .currencyDto(currencyDto)
                    .balance(balance)
                    .bankDto(bankDto)
                    .accrualDate(accrualDate)
                    .build();
            BankAccountDto bankAccountDtoAfterSave = null;
            try {
               bankAccountDtoAfterSave = bankAccountService.createBankAccount(bankAccountDto);
            } catch (ServiceException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = gson.toJson(bankAccountDtoAfterSave);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(json);
        }else if("add-balance".equals(action)){
            Long bankAccountId = Long.parseLong(req.getParameter("bankAccountId"));
            Long userId = Long.parseLong(req.getParameter("userId"));
            Timestamp dateCreated = Timestamp.valueOf(req.getParameter("dateCreated"));
            Long currencyId = Long.parseLong(req.getParameter("currencyId"));
            double balance = Double.parseDouble(req.getParameter("balance"));
            Long bankId = Long.parseLong(req.getParameter("bankId"));
            Timestamp accrualDate = Timestamp.valueOf(req.getParameter("accrualDate"));

            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String familyName = req.getParameter("familyName");
            String money = req.getParameter("money");
            UserDto userDto = UserDto.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .familyName(familyName)
                    .build();

            CurrencyDto currencyDto = CurrencyDto.builder()
                    .id(currencyId)
                    .name(req.getParameter("currencyName"))
                    .build();

            BankDto bankDto = BankDto.builder()
                    .id(bankId)
                    .name(req.getParameter("bankName"))
                    .build();

            BankAccountDto bankAccountDto = BankAccountDto.builder()
                    .bankAccountId(bankAccountId)
                    .userDto(userDto)
                    .dateCreated(dateCreated)
                    .currencyDto(currencyDto)
                    .balance(balance)
                    .bankDto(bankDto)
                    .accrualDate(accrualDate)
                    .build();
            TransactionDto transactionDto = null;
            try {
               transactionDto = bankAccountService.addBalance(bankAccountDto,Double.parseDouble(money));
            } catch (ServiceException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = gson.toJson(transactionDto);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(json);
        }else if("withdraw-balance".equals(action)){
            Long bankAccountId = Long.parseLong(req.getParameter("bankAccountId"));
            Long userId = Long.parseLong(req.getParameter("userId"));
            Timestamp dateCreated = Timestamp.valueOf(req.getParameter("dateCreated"));
            Long currencyId = Long.parseLong(req.getParameter("currencyId"));
            double balance = Double.parseDouble(req.getParameter("balance"));
            Long bankId = Long.parseLong(req.getParameter("bankId"));
            Timestamp accrualDate = Timestamp.valueOf(req.getParameter("accrualDate"));

            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String familyName = req.getParameter("familyName");
            String money = req.getParameter("money");
            UserDto userDto = UserDto.builder()
                    .id(userId)
                    .firstName(firstName)
                    .lastName(lastName)
                    .familyName(familyName)
                    .build();

            CurrencyDto currencyDto = CurrencyDto.builder()
                    .id(currencyId)
                    .name(req.getParameter("currencyName"))
                    .build();

            BankDto bankDto = BankDto.builder()
                    .id(bankId)
                    .name(req.getParameter("bankName"))
                    .build();

            BankAccountDto bankAccountDto = BankAccountDto.builder()
                    .bankAccountId(bankAccountId)
                    .userDto(userDto)
                    .dateCreated(dateCreated)
                    .currencyDto(currencyDto)
                    .balance(balance)
                    .bankDto(bankDto)
                    .accrualDate(accrualDate)
                    .build();
            TransactionDto transactionDto = null;
            try {
                transactionDto = bankAccountService.withdrawBalance(bankAccountDto,Double.parseDouble(money));
            } catch (ServiceException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = gson.toJson(transactionDto);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(json);
        }else if("transaction".equals(action)){
            Long bankAccountIdTo = Long.parseLong(req.getParameter("bankAccountIdTo"));
            Long userIdTo = Long.parseLong(req.getParameter("userIdTo"));
            Timestamp dateCreatedTo = Timestamp.valueOf(req.getParameter("dateCreatedTo"));
            Long currencyIdTo = Long.parseLong(req.getParameter("currencyIdTo"));
            double balanceTo = Double.parseDouble(req.getParameter("balanceTo"));
            Long bankIdTo = Long.parseLong(req.getParameter("bankIdTo"));
            Timestamp accrualDateTo = Timestamp.valueOf(req.getParameter("accrualDateTo"));

            String firstNameTo = req.getParameter("firstNameTo");
            String lastNameTo = req.getParameter("lastNameTo");
            String familyNameTo = req.getParameter("familyNameTo");
            String money = req.getParameter("money");
            UserDto userDtoTo = UserDto.builder()
                    .id(userIdTo)
                    .firstName(firstNameTo)
                    .lastName(lastNameTo)
                    .familyName(familyNameTo)
                    .build();

            CurrencyDto currencyDtoTo = CurrencyDto.builder()
                    .id(currencyIdTo)
                    .name(req.getParameter("currencyNameTo"))
                    .build();

            BankDto bankDtoTo = BankDto.builder()
                    .id(bankIdTo)
                    .name(req.getParameter("bankNameTo"))
                    .build();

            BankAccountDto bankAccountDtoTo = BankAccountDto.builder()
                    .bankAccountId(bankAccountIdTo)
                    .userDto(userDtoTo)
                    .dateCreated(dateCreatedTo)
                    .currencyDto(currencyDtoTo)
                    .balance(balanceTo)
                    .bankDto(bankDtoTo)
                    .accrualDate(accrualDateTo)
                    .build();
            Long bankAccountIdFrom = Long.parseLong(req.getParameter("bankAccountIdFrom"));
            Long userIdFrom = Long.parseLong(req.getParameter("userIdFrom"));
            Timestamp dateCreatedFrom = Timestamp.valueOf(req.getParameter("dateCreatedFrom"));
            Long currencyIdFrom = Long.parseLong(req.getParameter("currencyIdFrom"));
            double balanceFrom = Double.parseDouble(req.getParameter("balanceFrom"));
            Long bankIdFrom = Long.parseLong(req.getParameter("bankIdFrom"));
            Timestamp accrualDateFrom = Timestamp.valueOf(req.getParameter("accrualDateFrom"));

            String firstNameFrom = req.getParameter("firstNameFrom");
            String lastNameFrom = req.getParameter("lastNameFrom");
            String familyNameFrom = req.getParameter("familyNameFrom");
            UserDto userDtoFrom = UserDto.builder()
                    .id(userIdFrom)
                    .firstName(firstNameFrom)
                    .lastName(lastNameFrom)
                    .familyName(familyNameFrom)
                    .build();

            CurrencyDto currencyDtoFrom = CurrencyDto.builder()
                    .id(currencyIdFrom)
                    .name(req.getParameter("currencyNameFrom"))
                    .build();

            BankDto bankDtoFrom = BankDto.builder()
                    .id(bankIdFrom)
                    .name(req.getParameter("bankNameFrom"))
                    .build();
            BankAccountDto bankAccountDtoFrom = BankAccountDto.builder()
                    .bankAccountId(bankAccountIdFrom)
                    .userDto(userDtoFrom)
                    .dateCreated(dateCreatedFrom)
                    .currencyDto(currencyDtoFrom)
                    .balance(balanceFrom)
                    .bankDto(bankDtoFrom)
                    .accrualDate(accrualDateFrom)
                    .build();
            TransactionDto transactionDto = null;
            try {
                transactionDto = bankAccountService.bankAccountTransaction(bankAccountDtoTo,bankAccountDtoFrom,Double.parseDouble(money));
            } catch (ServiceException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = gson.toJson(transactionDto);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(json);
        }
    }

}
