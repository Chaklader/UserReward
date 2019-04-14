package com.user.reward.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Chaklader on 2019-04-11.
 */
public class RewardTest {

    Reward reward;

    @Before
    public void setUp() {
        reward = new Reward();
    }

    @Test
    public void getId() {
        Long id = 44L;
        reward.setId(id);
        assertEquals(id, reward.getId());
    }

    @Test
    public void getAmount() {
        double amount = 5.55;
        reward.setAmount(amount);
        assertEquals(amount, reward.getAmount(), 1e-9);
    }

    @Test
    public void getUser() {
        User user = new User();
        reward.setUser(user);
        assertEquals(user, reward.getUser());
    }

    @Test
    public void equals1() {
        Reward reward1 = new Reward();
        Long id = 555L;
        reward.setId(id);
        reward1.setId(id);
        assertEquals(reward1, reward);

    }
}
