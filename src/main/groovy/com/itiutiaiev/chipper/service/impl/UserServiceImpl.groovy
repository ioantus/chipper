package com.itiutiaiev.chipper.service.impl


import com.itiutiaiev.chipper.model.dto.UserDto
import com.itiutiaiev.chipper.model.dto.UserUpdateDto
import com.itiutiaiev.chipper.model.entity.User
import com.itiutiaiev.chipper.repository.UserRepository
import com.itiutiaiev.chipper.service.BaseService
import groovy.util.logging.Slf4j
import jakarta.annotation.Resource
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class UserServiceImpl implements BaseService<UserDto, User> {

    @Resource
    private UserRepository userRepository

    @Override
    UserDto toDto(User user) {
        UserDto userDto = new UserDto()
        copyPropertiesFromEntity(user, userDto)
        userDto
    }

    @Override
    User fromDto(UserDto userDto) {
        User user = new User()
        copyPropertiesFromDto(userDto, user)
        user
    }

    User create(UserDto userDto) {
        if (userRepository.findByEmailAndDeletedFalse(userDto.email).orElse(null)) {
            log.error "User with email ${userDto.email} already exists"
            throw new IllegalArgumentException("User with email ${userDto.email} already exists")
        }
        User user = fromDto(userDto)
        userRepository.save(user)
    }

    User update(UserUpdateDto userUpdateDto) {
        if (!userUpdateDto) {
            log.error "No data provided for user update"
            throw new IllegalArgumentException("No data provided for user update")
        } else if (!currentUser()) {
            log.error "User update failed, because user is not authenticated"
            throw new RuntimeException("User update failed, because user is not authenticated")
        }
        User user = findByEmail(currentUser())
        InvokerHelper.setProperties(user, userUpdateDto.properties)
        userRepository.save(user)
    }

    void changePassword(String password) {
        if (!password) {
            log.error "changePassword: new password is not provided"
            throw new IllegalArgumentException("changePassword: new password is not provided")
        } else if (!currentUser()) {
            log.error "Password change failed, because user is not authenticated"
            throw new RuntimeException("Password change failed, because user is not authenticated")
        }
        User user = findByEmail(currentUser())
        user.password = password
        userRepository.save(user)
    }

    void subscribe(String email) {
        if (!email) {
            log.error("subscribe: email is not provided")
            throw new IllegalArgumentException("subscribe: email is not provided")
        } else if (!currentUser()) {
            log.error "Subscribe failed, because user is not authenticated"
            throw new RuntimeException("Subscribe failed, because user is not authenticated")
        } else if (email == currentUser()) {
            log.error "User [$email] is trying to subscribe to itself"
            throw new RuntimeException("User [$email] is trying to subscribe to itself")
        }
        User subscriptionUser = findByEmail(email)
        User actionUser = findByEmail(currentUser())
        if (actionUser.subscriptions.contains(subscriptionUser.email) || subscriptionUser.subscribers.contains(actionUser.email)) {
            throw new IllegalArgumentException("Can't subscribe, because user [${actionUser.email}] is not subscribed to [${subscriptionUser.email}]")
        }
        actionUser.subscriptions.add(subscriptionUser.email)
        subscriptionUser.subscribers.add(actionUser.email)
        userRepository.save(actionUser)
        userRepository.save(subscriptionUser)
    }

    void unsubscribe(String email) {
        if (!email) {
            log.error "unsubscribe: email is not provided"
            throw new IllegalArgumentException("unsubscribe: email is not provided")
        } else if (!currentUser()) {
            log.error "Unsubscribe failed, because user is not authenticated"
            throw new RuntimeException("Unsubscribe failed, because user is not authenticated")
        }
        User subscriptionUser = findByEmail(email)
        User actionUser = findByEmail(currentUser())
        if (!actionUser.subscriptions.contains(subscriptionUser.email) || !subscriptionUser.subscribers.contains(actionUser.email)) {
            log.error "Can't unsubscribe, because user [${actionUser.email}] is not subscribed to [${subscriptionUser.email}]"
            throw new IllegalArgumentException("Can't unsubscribe, because user [${actionUser.email}] is not subscribed to [${subscriptionUser.email}]")
        }
        actionUser.subscriptions.remove(subscriptionUser.email)
        subscriptionUser.subscribers.remove(actionUser.email)
        userRepository.save(actionUser)
        userRepository.save(subscriptionUser)
    }

    void delete(String email) {
        User user = findByEmail(email)
        user.email = "${user.email}_DELETED_${UUID.randomUUID()}"
        user.deleted = true
        userRepository.save(user)
    }

    User findByEmail(String email) {
        def user = userRepository.findByEmailAndDeletedFalse(email).orElse(null)
        if (!user) {
            log.error "Can't find user `$email`"
            throw new UsernameNotFoundException("Can't find user `$email`")
        }
        user
    }

    static String currentUser() {
        SecurityContextHolder.context.authentication?.principal?.username
    }

}
