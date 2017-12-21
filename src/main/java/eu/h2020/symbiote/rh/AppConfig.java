package eu.h2020.symbiote.rh;

/**
 * Created by mateuszl on 30.09.2016.
 *
 * Note: to be used by components with MongoDB
 */

import com.mongodb.Mongo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
class AppConfig extends AbstractMongoConfiguration {

	private String mongoHost;
	
	public AppConfig(@Value("${spring.data.mongodb.host:localhost}") String mongoHost) {
		this.mongoHost = mongoHost;
	}

	@Override
    protected String getDatabaseName() {
        return "symbiote-cloud-rh-database";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new Mongo(mongoHost);
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.oreilly.springdata.mongodb";
    }

}