package com.uixs.config;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

@Component
public class MongoClientShutdownHook {
	private final MongoClient mongoClient;
	
	public MongoClientShutdownHook(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	
	@PreDestroy
	public void closeMongoClient() {
		if (mongoClient != null) {
			mongoClient.close();
			System.out.println("MongoClient connection closed successfully.");
		}
	}
	
}
