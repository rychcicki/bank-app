package com.example.bank.userRegister;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    User registerUser(UserRequest userRequest) {
        log.info("Start user registration.");
        int years = Period.between(userRequest.getBirthDate(), LocalDate.now())
                .getYears();
        if (years >= 18) {  //komentarz dla mnie-doczytaj o walidacji
            User user = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .birthDate(userRequest.getBirthDate())
                    .email(userRequest.getEmail())
                    .address(userRequest.getAddress())
                    .build();
            userRepository.save(user);
            return user;
        } else {
            throw new IllegalArgumentException("User have to be adult.");
        }
    }

    User getUser(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    User updateUser(UserRequest userRequest, Long id) throws UserNotFoundException {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        int years = Period.between(userRequest.getBirthDate(), LocalDate.now())
                .getYears();
        userToUpdate.setFirstName(userRequest.getFirstName());
        userToUpdate.setLastName(userRequest.getLastName());
        if (years >= 18) {
            userToUpdate.setBirthDate(userRequest.getBirthDate());
        } else {
            throw new IllegalArgumentException("User have to be adult.");
        }
        userToUpdate.setEmail(userRequest.getEmail());
        userToUpdate.setAddress(userRequest.getAddress());
        return userRepository.save(userToUpdate);
    }

    void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
