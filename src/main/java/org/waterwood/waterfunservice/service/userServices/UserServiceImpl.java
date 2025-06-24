package org.waterwood.waterfunservice.service.userServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.PasswordUtil;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUserByRole(String role) {
        return List.of();
    }

    @Override
    public void activateUser(long id) {

    }

    @Override
    public void deactivateUser(long id) {

    }

    @Override
    public void suspendUser(long id) {

    }

    @Override
    public void deleteUser(long id) {

    }

    @Override
    public void ChangeUserRole(long userId, long roleId) {

    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }
}
