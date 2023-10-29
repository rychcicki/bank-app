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
    private final String userLessThan18YearsOldMessage = "User has to be adult.";

    User registerUser(UserRequest userRequest) {
        log.info("Start user's registration.");
        int years = Period.between(userRequest.getBirthDate(), LocalDate.now())
                .getYears();
        if (years >= 18) {
            User user = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .birthDate(userRequest.getBirthDate())
                    .email(userRequest.getEmail())
                    .address(userRequest.getAddress())
                    .build();
            userRepository.save(user);
            log.info("User's registration passed.");
            return user;
        } else {
            log.error("User's registration failed.");
            throw new IllegalArgumentException(userLessThan18YearsOldMessage);
        }
    }

    User getUser(Long id) throws UserNotFoundException {
        /** Nie wiem, o co chodziło Ci w tej uwadze, żeby to inaczej obsłużyć poprzez getById...
         *  Może to nieistotne już...*/
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        log.info("GetUser passed.");
        return user;
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
            throw new IllegalArgumentException(userLessThan18YearsOldMessage);
        }
        userToUpdate.setEmail(userRequest.getEmail());
        userToUpdate.setAddress(userRequest.getAddress());
        return userRepository.save(userToUpdate);
    }

    void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        log.info("User with id#" + id + " was deleted.");
    }
}
