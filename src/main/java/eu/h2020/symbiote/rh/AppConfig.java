package eu.h2020.symbiote.rh;

/**
 * Created by mateuszl on 30.09.2016.
 *
 * Note: to be used by components with MongoDB
 */

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
class AppConfig extends AbstractMongoConfiguration {
    
    @Value("${rh.mongo.uri:#{null}}")
    private String mongoUri;
    
    @Override
    protected String getDatabaseName() {
        return "symbiote-cloud-rh-database";
    }
    
    @Override
    public Mongo mongo() throws Exception {
        if (mongoUri != null) {
            return new MongoClient(new MongoClientURI(mongoUri));
        } else {
            return new MongoClient();
        }
    }

}