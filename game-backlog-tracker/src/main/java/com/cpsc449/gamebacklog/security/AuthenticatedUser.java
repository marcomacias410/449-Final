package com.cpsc449.gamebacklog.security;

/**
 * Tiny principal stored in the SecurityContext after JWT validation.
 * Controllers pull the userId out of this — never from the request body.
 */
public record AuthenticatedUser(Long userId, String email) {
}
