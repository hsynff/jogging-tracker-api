package com.jogging.tracker.repository;

import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.util.rsql.CustomRsqlVisitor;
import com.jogging.tracker.util.rsql.RsqlSearchOperation;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("classpath:sql/insert_user_active.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_whenEmailExists_thenReturnTrue() {
        final String email = "test.user@test.user";
        boolean exists = userRepository.existsByEmail(email);

        assertTrue(exists);
    }

    @Test
    void existsByEmail_whenEmailNotExists_thenReturnFalse() {
        final String email = "non.existing@email";
        boolean exists = userRepository.existsByEmail(email);

        assertFalse(exists);
    }


    @Test
    void findFirstByEmail_whenEmailExists_thenReturnUser() {
        final String email = "test.user@test.user";
        Optional<User> userOptional = userRepository.findFirstByEmail(email);

        assertTrue(userOptional.isPresent());

        User user = userOptional.get();

        assertEquals(email, user.getEmail());
    }

    @Test
    void findFirstByEmail_whenEmailNonExists_thenReturnEmptyOptional() {
        final String email = "non.existing@email";
        Optional<User> userOptional = userRepository.findFirstByEmail(email);

        assertFalse(userOptional.isPresent());
    }

    @Test
    @Sql("classpath:sql/insert_user_active_5.sql")
    void findAll_withRsql() {
        final String query = "(firstName eq David) and (failedLoginAttempts eq 2 or failedLoginAttempts eq 3) and role eq ROLE_USER";

        Set<ComparisonOperator> operators = Arrays.stream(RsqlSearchOperation.values())
                .map(RsqlSearchOperation::getOperator).collect(Collectors.toSet());

        Node rootNode = new RSQLParser(operators).parse(query);
        Specification<User> accept = rootNode.accept(new CustomRsqlVisitor<>());

        Page<User> all = userRepository.findAll(accept, PageRequest.of(0, 100));
        List<User> userList = all.getContent();

        assertFalse(all.isEmpty());
        assertEquals(2, userList.size());
        assertTrue(userList.stream().anyMatch(u -> u.getFailedLoginAttempts() == 2));
        assertTrue(userList.stream().anyMatch(u -> u.getFailedLoginAttempts() == 3));

        User user1 = userList.get(0);

        assertEquals(3, user1.getId());
        assertEquals("David", user1.getFirstName());
        assertEquals(User.Role.ROLE_USER, user1.getRole());

        User user2 = userList.get(1);

        assertEquals(6, user2.getId());
        assertEquals("David", user2.getFirstName());
        assertEquals(User.Role.ROLE_USER, user2.getRole());
    }

}