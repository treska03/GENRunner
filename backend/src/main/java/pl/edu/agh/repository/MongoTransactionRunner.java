package pl.edu.agh.repository;

import com.mongodb.MongoCommandException;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.MongoClient;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class MongoTransactionRunner {

    private MongoClient client;

    public MongoTransactionRunner(MongoClient client) {
        this.client = client;
    }

    public void runWithTransaction(Runnable commands) {
        Publisher<ClientSession> sessionPublisher = client.startSession();
        Mono.from(sessionPublisher).subscribe(session -> {
            try {
                session.startTransaction();
                commands.run();
                session.commitTransaction();
            } catch (MongoCommandException e) {
                session.abortTransaction();
                throw e;
            } finally {
                session.close();
            }
        });
    }
}
