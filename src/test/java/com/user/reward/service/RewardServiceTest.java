package com.user.reward.service;

import com.user.reward.model.Reward;
import com.user.reward.repository.RewardRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Chaklader on 2019-04-11.
 */
public class RewardServiceTest {

    RewardService rewardService;
    @Mock
    RewardRepository rewardRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rewardService = new RewardService(rewardRepository);
    }

    @Test
    public void save() {
        Reward reward = new Reward();

        reward.setId(45L);
        Mockito.when(rewardRepository.save(any(Reward.class))).thenReturn(reward);

        assertEquals(rewardService.save(reward), reward);
        verify(rewardRepository, times(1)).save(reward);
    }
}
