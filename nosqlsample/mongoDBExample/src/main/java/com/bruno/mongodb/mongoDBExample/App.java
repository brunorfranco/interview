package com.bruno.mongodb.mongoDBExample;

import java.net.UnknownHostException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class App {
	public static void main(String[] args) {

		try {

			MongoClient mongo = new MongoClient("localhost", 27017);

			DB db = mongo.getDB("testdb");

			DBCollection table = db.getCollection("user");

			BasicDBObject document = new BasicDBObject();
			document.put("name", "bruno");
			document.put("age", 25);
			document.put("createdDate", new Date());
			table.insert(document);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", "bruno");

			DBCursor cursor = table.find(searchQuery);

			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}

			BasicDBObject query = new BasicDBObject();
			query.put("name", "bruno");

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("name", "bruno2");

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newDocument);

			table.update(query, updateObj);

			BasicDBObject searchQuery2 = new BasicDBObject().append("name",
					"bruno2");

			DBCursor cursor2 = table.find(searchQuery2);

			while (cursor2.hasNext()) {
				System.out.println(cursor2.next());
			}

			System.out.println("Done");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}
}
