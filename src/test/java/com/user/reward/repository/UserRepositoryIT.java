package com.user.reward.repository;

import com.user.reward.model.Reward;
import com.user.reward.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Chaklader on 2019-04-11.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RewardRepository rewardRepository;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void findUsersWithRewards() {
        User user = new User();
        user.setName("John");
        user.setCountryName("Georgia");
        user.setCurrencyName("EUR");
        Reward reward = new Reward();
        reward.setUser(user);
        reward.setAmount(2);
        user.addReward(reward);
        user = userRepository.save(user);
        assertNotNull(user.getId());

        User user1 = new User();
        user1.setName("Maria");
        user1.setCountryName("USA");
        user1.setCurrencyName("USD");
        Reward reward1 = new Reward();
        reward1.setUser(user1);
        reward1.setAmount(4);
        user1.addReward(reward1);
        user1 = userRepository.save(user1);
        assertNotNull(user1.getId());

        assertEquals(2, userRepository.findUsersWithRewardHistory().get().size());
    }
}
