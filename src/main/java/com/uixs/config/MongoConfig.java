package com.uixs.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.StringUtils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	// @Value : application.properties 에 정의된 변수 값을 읽어와서
	// 아래 변수 값으로 주는 어노테이션

	// MongoDB Atlas URI에 인증 정보가 포함되어 있으므로 별도 username/password 불필요
	@Value("${spring.data.mongodb.database}")
	private String database;
	@Value("${spring.data.mongodb.uri}")
	private String uri;

	@Override
	protected String getDatabaseName() {
		return database;
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate((MongoClient) mongoClient(), database);
	}

	@Bean
	public MongoClient mongoClient() {
		MongoClient mongoClient = null;

		// String dbPassword = URLEncoder.encode(password, "UTF-8");
		// String dbUri = StringUtils.replace(uri, "<password>", dbPassword);
		ConnectionString connectionString = new ConnectionString(uri);
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.applyToConnectionPoolSettings(poolSettings -> poolSettings
						.minSize(5)
						.maxSize(300)
						.maxConnectionIdleTime(0, TimeUnit.MILLISECONDS))
				.applyToSocketSettings(socketSettings -> socketSettings
						.connectTimeout(1, TimeUnit.MINUTES)
						.readTimeout(1, TimeUnit.MINUTES))
				.build();

		mongoClient = MongoClients.create(mongoClientSettings);
		//
		//
		// MongoDatabase databases = mongoClient.getDatabase(database);
		//
		// MongoIterable<String> colls = databases.listCollectionNames();
		//
		// for (String tmp : colls) {
		// System.out.println(tmp);
		// }

		return mongoClient;
	}

}
