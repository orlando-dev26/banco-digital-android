package com.corporation_dev.user_microservice.repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import com.corporation_dev.user_microservice.entity.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Integer> {

}
