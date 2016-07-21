package com.moto.bigdata.dss.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wenjusun on 3/28/2016.
 */
public class CassandraStore {

    private static Cluster cluster;
    private static Session session;

/*    public static void main1(String[] args) {

        try {

            cluster = Cluster.builder().addContactPoints(InetAddress.getLocalHost()).build();

            session = cluster.connect("mykeyspace");

            CassandraOperations cassandraOps = new CassandraTemplate(session);

            cassandraOps.insert(new LogEntry(10001, "/cas/login", 40));

            Select s = QueryBuilder.select().from("api_requests");
            s.where(QueryBuilder.eq("id", 10001));

            System.out.println(cassandraOps..queryForObject(s, Person.class).getId());

            cassandraOps.truncate("person");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }*/

    public static void main(String args[]) {
        CassandraStore cassandraStore = new CassandraStore();
        cassandraStore.connect();
        cassandraStore.createSchema();
//        cassandraStore.loadData();
        cassandraStore.saveData();
        cassandraStore.querySchema();
        cassandraStore.close();
    }

    public void connect() {
        String cassandraServer = "centos.blurdev.com";
        cassandraServer = "localhost";
        List<String> cassandraServers = new ArrayList<String>();
        cassandraServers.add("centos.blurdev.com");
        cassandraServers.add("s001.blurdev.com");
        cassandraServers.add("s002.blurdev.com");
//        cluster = Cluster.builder().addContactPoints(cassandraServers.get(0),cassandraServers.get(1),cassandraServers.get(2)).build();
        cluster = Cluster.builder().addContactPoints(cassandraServer).build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
    }

    public void close() {
        if (session != null) session.close();
        if (cluster != null) cluster.close();
    }

    public void createSchema() {
        session.execute("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};");
        System.out.println("creating table now....");
        try {
            session.execute(
                    "CREATE TABLE IF NOT EXISTS simplex.songs (" +
                            "id uuid PRIMARY KEY," +
                            "title text," +
                            "album text," +
                            "artist text," +
                            "tags set<text>," +
                            "data blob" +
                            ");");
            session.execute(
                    "CREATE TABLE IF NOT EXISTS simplex.playlists (" +
                            "id uuid," +
                            "title text," +
                            "album text, " +
                            "artist text," +
                            "song_id uuid," +
                            "PRIMARY KEY (id, title, album, artist)" +
                            ");");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData(){
        PreparedStatement pstmt = session.prepare("INSERT INTO simplex.songs (id, title, album, artist, tags) VALUES(?,?,?,?,?)");

        BatchStatement batchStatement = new BatchStatement();
        Set<String> tags1 = new HashSet<String>();
        tags1.add("time");
        tags1.add("rock");
        batchStatement.add(pstmt.bind(UUIDs.random(),"title 1","albums 1","Lady GAGa",tags1));

        tags1 = new HashSet<String>();
        tags1.add("fashion");
        tags1.add("rural");
        batchStatement.add(pstmt.bind(UUIDs.random(),"title 2","albums 2","Lady GAGa",tags1));

        session.execute(batchStatement);

    }
    public void loadData() {
        session.execute(
                "INSERT INTO simplex.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'," +
                        "{'jazz', '2013'})" +
                        ";");
        session.execute(
                "INSERT INTO simplex.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'" +
                        ");");
    }

    public void querySchema() {
        ResultSet results = session.execute("SELECT * FROM simplex.playlists WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;");
         results = session.execute("SELECT * FROM simplex.playlists");
        System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title", "album", "artist",
                "-------------------------------+-----------------------+--------------------"));
        for (Row row : results) {
            System.out.println(String.format("%-30s\t%-20s\t%-20s", row.getString("title"),
                    row.getString("album"), row.getString("artist")));
        }
        System.out.println();
    }

}
