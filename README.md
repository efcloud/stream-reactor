![Actions Status](https://github.com/lensesio/stream-reactor/actions/workflows/build.yml/badge.svg)
[<img src="https://img.shields.io/badge/docs--orange.svg?"/>](https://docs.lenses.io/connectors/)

Join us on slack [![Alt text](images/slack.jpeg)](https://launchpass.com/lensesio)

# About this fork

This fork modifies the original build config to make the fat jars compatible with latest confluent cp-kafka-connect image.

The original build config uses slf4j 2.x that conflicts with confluent cp-kafka-connect who is using slf4j 1.x.

## How to build

```bash
export VERSION=5.0.1.1
sbt 'set test in assembly := {}' -java-home /Library/Java/JavaVirtualMachines/temurin-17.0.9/Contents/Home/  clean compile assembly
mv */target/libs/*.jar ./target/
```

# Lenses for Apache Kafka

Lenses offers SQL (for data browsing and Kafka Streams), Kafka Connect connector management, cluster monitoring and more.

You can find more on [lenses.io](http://www.lenses.io)

# Stream Reactor


![Alt text](images/streamreactor-logo.png)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flensesio%2Fstream-reactor.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Flensesio%2Fstream-reactor?ref=badge_shield)


A collection of components to build a real time ingestion pipeline.

## Kafka Compatibility
* Kafka 2.8 -> 3.3 (Confluent 2.8 -> 7.3) - Upcoming Stream Reactor 4.1.0
* Kafka 3.1 (Confluent 7.1) - Stream Reactor 4.0.0 (Kafka 3.1 Build)
* Kafka 2.8 (Confluent 6.2) - Stream Reactor 4.0.0 (Kafka 2.8 Build)
* Kafka 2.5 (Confluent 5.5) - Stream reactor 2.0.0+
* Kafka 2.0 -> 2.4 (Confluent 5.4) - Stream reactor 1.2.7

## DEPRECATION NOTICE

The following connectors have been deprecated and are no longer included in future releases:

* Kudu
* Hive

The following connectors have been deprecated and are no longer included in any release from 3.0.

* Blockchain
* Bloomberg
* Rethink
* VoltDB

The following connectors have been deprecated and are no longer included in any release from 4.0.

* Coap
* Hive 1.1

### Connectors

**Please take a moment and read the documentation and make sure the software prerequisites are met!!**

|Connector       | Type   | Description                                                                                   | Docs |
|----------------|--------|-----------------------------------------------------------------------------------------------|------|
| AWS S3         | Sink   | Copy data from Kafka to AWS S3.                                                     | [Docs](https://docs.lenses.io/4.0/integrations/connectors/stream-reactor/sinks/s3sinkconnector/)             |
| AzureDocumentDb| Sink   | Copy data from Kafka and Azure Document Db.                                          | [Docs](https://docs.lenses.io/connectors/sink/azuredocdb.html)             |
| Cassandra      | Source | Copy data from Cassandra to Kafka.                          | [Docs](https://docs.lenses.io/connectors/source/cassandra.html)            |
| *Cassandra     | Sink   | Certified DSE Cassandra, copy data from Kafka to Cassandra.   | [Docs](https://docs.lenses.io/connectors/sink/cassandra.html)              |
| Elastic 6      | Sink   | Copy data from Kafka to Elastic Search 6.x w. tcp or http      | [Docs](https://docs.lenses.io/connectors/sink/elastic6.html)               |
| FTP/HTTP       | Source | Copy data from FTP/HTTP to Kafka.                       | [Docs](https://docs.lenses.io/connectors/source/ftp.html)                  |
| Hazelcast      | Sink   | Copy data from Kafka to Hazelcast.                      | [Docs](https://docs.lenses.io/connectors/sink/hazelcast.html)              |
| HBase          | Sink   | Copy data from Kafka to HBase.                              | [Docs](https://docs.lenses.io/connectors/sink/hbase.html)                  |
| Hive           | Source | Copy data from Hive/HDFS to Kafka.                             | [Docs](https://docs.lenses.io/connectors/source/hive.html)                 |
| Hive           | Sink   | Copy data from Kafka to Hive/HDFS                            | [Docs](https://docs.lenses.io/connectors/sink/hive.html)                   |
| InfluxDb       | Sink   | Copy data from Kafka to InfluxDb.                                  | [Docs](https://docs.lenses.io/4.0/integrations/connectors/stream-reactor/sinks/influxsinkconnector/)|
| Kudu           | Sink   | Copy data from Kafka to Kudu.                                | [Docs](https://docs.lenses.io/connectors/sink/kudu.html)                 |
| JMS            | Source | Copy data from JMS topics/queues to Kafka.                                   | [Docs](https://docs.lenses.io/connectors/source/jms.html)                  |
| JMS            | Sink   | Copy data from Kafka to JMS.                                  | [Docs](https://docs.lenses.io/connectors/sink/jms.html)                    |
| MongoDB        | Sink   | Copy data from Kafka to MongoDB.                                    | [Docs](https://docs.lenses.io/connectors/sink/mongo.html)                  |
| MQTT           | Source | Copy data from MQTT to Kafka.                                   | [Docs](https://docs.lenses.io/connectors/source/mqtt.html)                 |
| MQTT           | Sink   | Copy data from Kafka to MQTT.                                     | [Docs](https://docs.lenses.io/connectors/sink/mqtt.html)                   |
| Pulsar         | Source | Copy data from Pulsar to Kafka.                               | [Docs](https://docs.lenses.io/connectors/source/pulsar.html)                 |
| Pulsar         | Sink   | Copy data from Kafka to Pulsar.                                 | [Docs](https://docs.lenses.io/connectors/sink/pulsar.html)                   |
| Redis          | Sink   | Copy data from Kafka to Redis.                              | [Docs](https://docs.lenses.io/connectors/sink/redis.html)                  |


## Release Notes

Please see the *[Stream Reactor Release Notes at Lenses Documentation](https://docs.lenses.io/4.3/integrations/connectors/sr-release-notes/)*.


### Building

To build:

```bash
sbt clean compile
```

To test:

```bash
sbt test
```

To create assemblies:

```bash
sbt assembly
```

To build a particular project:

```bash
sbt "project cassandra" compile
```

To test a particular project:

```bash
sbt "project cassandra" test
```

To create a jar of a particular project:

```bash
sbt "project cassandra" assembly
```

### Running E2E tests

If not already built, you must first build the connector archives:

```bash
sbt "project cassandra" assembly
sbt "project elastic6" assembly 
sbt "project mongodb" assembly
sbt "project redis" assembly
```

To run the tests:

```bash
sbt e2e:test
```

## Contributing

We'd love to accept your contributions! Please use GitHub pull requests: fork the repo, develop and test your code,
[semantically commit](http://karma-runner.github.io/1.0/dev/git-commit-msg.html) and submit a pull request. Thanks!


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flensesio%2Fstream-reactor.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Flensesio%2Fstream-reactor?ref=badge_large)

