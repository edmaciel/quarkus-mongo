package br.com.quarkus.resource;

import br.com.quarkus.infrastructure.utils.FileUtils;
import br.com.quarkus.repository.User;
import br.com.quarkus.repository.UserRepository;
import com.google.common.base.Splitter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Path("/user")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);


    @ConfigProperty(name = "path.directory")
    String directory;

    @ConfigProperty(name = "path.directory.read")
    String directoryRead;


    @Inject
    UserRepository userRepository;

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {

        List<User> users = userRepository.findAll();

        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    @Singleton
    public Response importFromFiles() {

        Function<String, User> mapToUser = getUserForLineFile();
        LOGGER.info(format("user %s", mapToUser));

        try (Stream<java.nio.file.Path> paths = Files.walk(Paths.get(directory))) {
            paths.filter(Files::isRegularFile)
                    .forEach( path -> {
                        try {
                            List<User> users = Files.lines(Paths.get(path.toString())).map(mapToUser).collect(Collectors.toList());
                            userRepository.insertMany(users);

                            // After read move file to new directory
                            FileUtils.move(path, directoryRead);
                        } catch (IOException e) {
                            LOGGER.error("Error read line file and convert to User");
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Error on read path files");
            return Response.status(500).build();
        }

        return Response.ok().build();
    }

    private Function<String, User> getUserForLineFile() {
        return line -> {
                User user = new User();

                List<String> userLine = Splitter.on(",").trimResults()
                        .omitEmptyStrings().splitToList(line);

                user.setCodeOrigin(Integer.parseInt(userLine.get(0)));
                user.setDocument(userLine.get(1));
                try {
                    user.setUpdateDate(new SimpleDateFormat("dd/MM/yyyy").parse(userLine.get(2)));
                } catch (ParseException e) {
                    LOGGER.error(format("Error to convert file date %s to user.updateDate", userLine.get(2)));
                }
                return user;
            };
    }

}