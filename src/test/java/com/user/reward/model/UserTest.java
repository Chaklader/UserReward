package com.user.reward.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Chaklader on 2019-04-11.
 */
public class UserTest {

    User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void getId() {
        Long id = 444L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    public void getName() {
        String name = "Maria";
        user.setName(name);
        assertEquals(name, user.getName());
    }


    @Test
    public void getCountryName() {
        String countryName = "USA";
        user.setCountryName(countryName);
        assertEquals(countryName, user.getCountryName());
    }

    @Test
    public void getCurrencyName() {
        String currencyName = "USD";
        user.setCurrencyName(currencyName);
        assertEquals(currencyName, user.getCurrencyName());
    }


    @Test
    public void getTotalSteps() {
        int total = 100000;
        user.setTotalSteps(total);
        assertEquals(total, user.getTotalSteps());
    }


    @Test
    public void getCurrentSteps() {
        int current = 100000;
        user.setCurrentSteps(current);
        assertEquals(current, user.getCurrentSteps());
    }


    @Test
    public void getRewardLists() {
        List<Reward> rewards = new ArrayList<>();
        rewards.add(new Reward());
        rewards.add(new Reward());
        user.setRewards(rewards);
        assertEquals(rewards, user.getRewards());
    }


    @Test
    public void equals1() {
        Long id = 1L;
        user.setId(id);
        User user2 = new User();
        user2.setId(id);
        assertEquals(user, user2);

    }

    @Test
    public void addReward() {
        Reward reward = new Reward();
        user.addReward(reward);
        assertEquals(1, user.getRewards().size());
    }
}
