package com.user.reward.repository;

import com.user.reward.model.Reward;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Chaklader on 2019-04-10.
 */
@Repository
public interface RewardRepository extends CrudRepository<Reward, Long> {

}
