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

    void registerUser(UserRequest userRequest) {
        /** Czy te logi w ogóle widać??
         * Nie widać ich ani po odpaleniu aplikacji, ani przez Postmana*/
        log.info("Start user registration.");
        int years = Period.between(userRequest.birthDate(), LocalDate.now())
                .getYears();
        if (years >= 18) {
            User user = User.builder()
                    .firstName(userRequest.firstName())
                    .lastName(userRequest.lastName())
                    .birthDate(userRequest.birthDate())
                    .email(userRequest.email())
                    .address(userRequest.address())
                    .build();
        } else {
            throw new IllegalArgumentException("User have to be adult.");
        }
    }

    User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("There is no user with id #" + id + " in database."));
    }

    User updateUser(UserRequest userRequest, Long id) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("There is no user with id #" + id + " in database."));
        int years = Period.between(userRequest.birthDate(), LocalDate.now())
                .getYears();
        userToUpdate.setFirstName(userRequest.firstName());
        userToUpdate.setLastName(userRequest.lastName());
        if (years >= 18) {
            userToUpdate.setBirthDate(userRequest.birthDate());
        } else {
            log.info("NIE WIDAC LOGOW, NIE WIDAĆ LOGOW, NIE WIDAC LOGOW, NIE WIDAC LOGOW, NIE WIDAC LOGOW");
            throw new IllegalArgumentException("User have to be adult.");
        }
        userToUpdate.setEmail(userRequest.email());
        userToUpdate.setAddress(userRequest.address());
        return userRepository.save(userToUpdate);
    }

    void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
