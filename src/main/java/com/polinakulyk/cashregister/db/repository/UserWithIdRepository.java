package com.polinakulyk.cashregister.db.repository;

import com.polinakulyk.cashregister.db.entity.UserWithId;
import org.springframework.data.repository.CrudRepository;

public interface UserWithIdRepository extends CrudRepository<UserWithId, String> {
}
