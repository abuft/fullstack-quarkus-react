package com.example.fullstack.user;

import com.example.fullstack.project.Project;
import com.example.fullstack.task.Task;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.ObjectNotFoundException;

import java.util.List;

@ApplicationScoped
public class UserService {

    private final JsonWebToken jwt;

    @Inject
    public UserService(JsonWebToken jwt) {
        this.jwt = jwt;
    }

    public static boolean matches(User user, String password) {
        return BcryptUtil.matches(password, user.password);
    }

    @WithTransaction
    public Uni<User> findById(long id) {
        return User.<User>findById(id)
                .onItem()
                .ifNull()
                .failWith(
                        () -> new ObjectNotFoundException(id, "User")
                );
    }

    @WithTransaction
    public Uni<User> findByName(String name) {
        // find("name", name) is the short of the query
        // find("from User u where u.name = ?1", name)
        return User.<User>find("name", name).firstResult();
    }

    public Uni<List<User>> list() {
        return User.listAll();
    }

    @WithTransaction // may be not needed? why?
    public Uni<User> create(User user) {
        user.password = BcryptUtil.bcryptHash(user.password);
        return user.persistAndFlush();
    }

    @WithTransaction
    public Uni<User> update(User user) {
        return findById(user.id)
                .chain(u -> {
                    user.setPassword(u.password);
                    return User.getSession();
                })
                .chain(s -> s.merge(user));
    }

    @WithTransaction
    public Uni<Void> delete(long id) {
        return findById(id)
                .chain(u -> Uni.combine().all().unis(
                                Task.delete("user.id", u.id),
                                Project.delete("user.id", u.id)).asTuple()
                        .chain(t -> u.delete()));
    }

    public Uni<User> getCurrentUser() {
        return findByName(jwt.getName());
    }

    @WithTransaction
    public Uni<User> changePassword(String currentPassword, String newPassword) {
        return getCurrentUser()
                .chain(u -> {
                    if (!matches(u, currentPassword)) {
                        throw new ClientErrorException("Current password does not match", Response.Status.CONFLICT);
                    }
                    u.setPassword(BcryptUtil.bcryptHash(newPassword));
                    return u.persistAndFlush();
                });
    }
}
