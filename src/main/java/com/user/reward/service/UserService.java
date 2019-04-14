package com.user.reward.service;

import com.user.reward.model.User;
import com.user.reward.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Created by Chaklader on 2019-04-10.
 */
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        return repository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public Optional<List<User>> findUsersWithRewardHistory() {
        return repository.findUsersWithRewardHistory();
    }

}
