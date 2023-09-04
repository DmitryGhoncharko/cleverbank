package com.example.mytask.dao;

import com.example.mytask.dto.UserDto;
import com.example.mytask.exception.DaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class PostgresUserDao implements UserDao {
    private static final String SQL_CREATE_USER = "insert into _user(user_first_name, user_last_name, user_family_name) values (?,?,?)";
    private static final String SQL_FIND_USER_BY_ID = "select user_id,user_first_name,user_last_name,user_family_name from _user where user_id = ?";
    private final Connection connection;

    @Override
    public UserDto createUser(UserDto userDto) throws DaoException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userDto.getFirstName());
            preparedStatement.setString(2, userDto.getLastName());
            preparedStatement.setString(3, userDto.getFamilyName());
            int rowsAdded = preparedStatement.executeUpdate();
            if (rowsAdded > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    UserDto userDtoForReturn = UserDto.builder()
                            .id(resultSet.getLong(1))
                            .firstName(userDto.getFirstName())
                            .lastName(userDto.getLastName())
                            .familyName(userDto.getFamilyName())
                            .build();
                    return userDtoForReturn;
                }
            }
        } catch (SQLException e) {
            log.error("Cannot create user", e);
            throw new DaoException("Cannot create user", e);
        }
        log.error("Cannot create user");
        throw new DaoException("Cannot create user");
    }

    @Override
    public Optional<UserDto> findUserById(Long userId) throws DaoException {
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_USER_BY_ID);
            preparedStatement.setLong(1,userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                UserDto userDtoForReturn = UserDto.builder()
                        .id(resultSet.getLong(1))
                        .firstName(resultSet.getString(2))
                        .lastName(resultSet.getString(3))
                        .familyName(resultSet.getString(4))
                        .build();
                return Optional.of(userDtoForReturn);
            }
        }catch (SQLException e){
            log.error("Cannot find user by id", e);
            throw new DaoException("Cannot find user by id", e);
        }
        return Optional.empty();
    }
}
