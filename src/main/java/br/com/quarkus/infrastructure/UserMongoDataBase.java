package br.com.quarkus.infrastructure;

import br.com.quarkus.repository.User;
import br.com.quarkus.repository.UserRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class UserMongoDataBase implements UserRepository {

    @ConfigProperty(name = "mongodb.database")
    String dataBase;

    @Inject
    private MongoClient mongoClient;

    public UserMongoDataBase(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (MongoCursor<User> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                users.add(cursor.next());
            }
        }
        return users;
    }

    @Override
    public User find(String document) {
        Bson filter = eq("document", document);
        return (User) getCollection().find(filter);
    }

    @Override
    public void update(User user) {
        Bson filter = eq("document", user.getDocument());
        getCollection().findOneAndUpdate(filter, buildDocument(user));
    }

    @Override
    public void remove(User user) {
        BasicDBObject document = new BasicDBObject();
        document.put("document", user.getDocument());
        getCollection().deleteOne(document);
    }

    @Override
    public void insert(User user) {
        getCollection().insertOne(user);
    }

    @Override
    public void insertMany(List<User> users) {
        getCollection().insertMany(users);
    }

    private MongoCollection<User> getCollection(){
        return mongoClient.getDatabase(dataBase).getCollection("users", User.class);
    }

    private BasicDBObject buildDocument(User user){
        BasicDBObject document = new BasicDBObject();
        document.put("document", user.getDocument());
        document.put("updateDate", user.getUpdateDate());
        document.put("codeOrigin", user.getCodeOrigin());
        return document;
    }
}
