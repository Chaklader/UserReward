package com.user.reward.repository;

import com.user.reward.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Chaklader on 2019-04-10.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT * FROM userreward.user WHERE user.id IN (SELECT user_id FROM userreward.reward)", nativeQuery = true)
    Optional<List<User>> findUsersWithRewardHistory();
}
