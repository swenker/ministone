__author__ = 'wenjusun'

from cassandra.cluster import Cluster

import logging

log = logging.getLogger()
log.setLevel('INFO')

class SimpleClient(object):
    session = None

    def connect(self, nodes):
        cluster = Cluster(nodes)
        metadata = cluster.metadata
        self.session = cluster.connect()
        log.info('Connected to cluster: ' + metadata.cluster_name)

        for host in metadata.all_hosts():
            log.info('Datacenter: %s; Host: %s; Rack: %s',
                host.datacenter, host.address, host.rack)

    def close(self):
        self.session.cluster.shutdown()
        log.info('Connection closed.')

    def create_schema(self):
            self.session.execute("""CREATE KEYSPACE simplex WITH replication = {'class':'SimpleStrategy', 'replication_factor':3};""")
            self.session.execute("""
                CREATE TABLE simplex.songs (
                    id uuid PRIMARY KEY,
                    title text,
                    album text,
                    artist text,
                    tags set<text>,
                    data blob
                );
            """)
            self.session.execute("""
                CREATE TABLE simplex.playlists (
                    id uuid,
                    title text,
                    album text,
                    artist text,
                    song_id uuid,
                    PRIMARY KEY (id, title, album, artist)
                );
            """)
            log.info('Simplex keyspace and schema created.')

def main():
    logging.basicConfig()
    client = SimpleClient()
    client.connect(['127.0.0.1'])
    client.close()

if __name__ == "__main__":
    main()