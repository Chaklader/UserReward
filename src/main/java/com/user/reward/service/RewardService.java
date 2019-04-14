package com.user.reward.service;

import com.user.reward.model.Reward;
import com.user.reward.repository.RewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Chaklader on 2019-04-10.
 */
@Service
public class RewardService {

    private final RewardRepository repository;

    public RewardService(RewardRepository repository) {
        this.repository = repository;
    }

    /**
     * @param reward
     * @return saved Reward entity
     */
    @Transactional(rollbackFor = Exception.class)
    public Reward save(Reward reward) {
        return repository.save(reward);
    }

}
