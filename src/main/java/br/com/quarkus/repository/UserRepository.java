package br.com.quarkus.repository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public interface UserRepository {

    List<User> findAll();
    User find(String document);
    void update(User user);
    void remove(User user);
    void insert(User user);
    void insertMany(List<User> users);

}
