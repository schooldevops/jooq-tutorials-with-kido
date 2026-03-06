package com.example.mapper

import com.example.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    fun findByEmail(@Param("email") email: String): User?
}
