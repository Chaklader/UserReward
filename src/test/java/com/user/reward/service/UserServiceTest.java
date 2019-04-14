package com.user.reward.service;

import com.user.reward.model.Reward;
import com.user.reward.model.User;
import com.user.reward.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Chaklader on 2019-04-11.
 */
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    private UserService userService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void findById() {
        User user = new User();
        Long id = 45L;
        user.setId(id);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        User response = userService.findById(id).get();
        assertEquals(response, user);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void findAll() {

        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userRepository.findAll()).thenReturn(users);
        List<User> response = userService.findAll();
        assertEquals(response.size(), 1);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void save() {

        User user = new User();
        user.setId(45L);

        when(userRepository.save(any(User.class))).thenReturn(user);
        assertEquals(userService.save(user), user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void findUsersWithRewardHistory() {

        Long id = 45L;
        User user = new User();
        user.setId(id);
        Reward reward = new Reward();
        reward.setUser(user);
        user.addReward(reward);
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findUsersWithRewardHistory()).thenReturn(Optional.of(users));
        List<User> response = userService.findUsersWithRewardHistory().get();

        assertEquals(response.size(), 1);
        verify(userRepository, times(1)).findUsersWithRewardHistory();
    }
}
