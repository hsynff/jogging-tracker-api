package com.jogging.tracker.testUtils;

import com.jogging.tracker.model.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.*;


/**
 * Annotation for creating dummy authentication user with given role and id for testing.
 * */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithPrincipal.WithoutUserFactory.class)
public @interface WithPrincipal {

    User.Role role();
    long id() default 1;


    class WithoutUserFactory implements WithSecurityContextFactory<WithPrincipal> {

        @Override
        public SecurityContext createSecurityContext(WithPrincipal withPrincipal) {
            User userPrincipal = new User();
            userPrincipal.setId(withPrincipal.id());
            userPrincipal.setRole(withPrincipal.role());

            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null,
                    userPrincipal.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }
}
