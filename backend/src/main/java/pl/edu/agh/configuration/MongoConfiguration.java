package pl.edu.agh.configuration;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.edu.agh.repository.MongoTransactionRunner;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String mongoUri) {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTransactionRunner mongoTransactionRunner(MongoClient mongoClient) {
        return new MongoTransactionRunner(mongoClient);
    }
}
