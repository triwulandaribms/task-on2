package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;

import java.util.Optional;

abstract class AbstractService {

    Optional<Response<Object>> precondition(final Authentication authentication, User.Role role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(Response.unauthenticated());
        }
        if (authentication.id() == null || authentication.id() < 0 || role != authentication.role()) {
            return Optional.of(Response.unauthorized());
        }
        return Optional.empty();
    }

    Optional<Response<Object>> precondition(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(Response.unauthenticated());
        }
        if (authentication.id() == null || authentication.id() < 0 || authentication.role() == null) {
            return Optional.of(Response.unauthorized());
        }
        return Optional.empty();
    }
}
