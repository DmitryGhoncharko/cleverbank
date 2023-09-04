package com.example.mytask.dao;

import com.example.mytask.dto.UserDto;
import com.example.mytask.exception.DaoException;

import java.util.Optional;

public interface UserDao {
    UserDto createUser(UserDto userDto) throws DaoException;

    Optional<UserDto> findUserById(Long userId) throws DaoException;


}
