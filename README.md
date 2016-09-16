# dexecutor-infinispan

[![Build Status](https://travis-ci.org/dexecutor/dexecutor-infinispan.svg?branch=master)](https://travis-ci.org/dexecutor/dexecutor-infinispan)
[![Coverage Status](https://coveralls.io/repos/github/dexecutor/dexecutor-infinispan/badge.svg?branch=master)](https://coveralls.io/github/dexecutor/dexecutor-infinispan?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/57cbf2b469d949002f38dd5a/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57cbf2b469d949002f38dd5a)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.dexecutor/dexecutor-infinispan/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.dexecutor/dexecutor-infinispan)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


Dexecutor With Distributed Execution Engine (Infinispan)


Refer [wiki](https://github.com/dexecutor/dexecutor-infinispan/wiki) or [Project Web site](https://dexecutor.github.io/) for more Details.

## License

Dexecutor is licensed under **Apache Software License, Version 2.0**.

## News

* Version **0.0.3** released on 09/16/2016.
* Version **0.0.2** released on 09/10/2016.
* Version **0.0.1** released on 09/04/2016.

## Maven Repository

Dexecutor is deployed at sonatypes open source maven repository. You may use the following repository configuration (if you are interested in snapshots)

```xml
<repositories>
     <repository>
         <id>dexecutor-snapshots</id>
         <snapshots>
             <enabled>true</enabled>
         </snapshots>
         <url>https://oss.sonatype.org/content/groups/public/</url>
     </repository>
</repositories>
```
This repositories releases will be synched to maven central on a regular basis. Snapshots remain at sonatype.

Alternatively you can  pull Dexecutor from the central maven repository, just add these to your pom.xml file:
```xml
<dependency>
  <groupId>com.github.dexecutor</groupId>
  <artifactId>dexecutor-infinispan</artifactId>
  <version>0.0.3</version>
</dependency>
```

## BUILDING from the sources

As it is maven project, buidling is just a matter of executing the following in your console:

	mvn package

This will produce the dexecutor-infinispan-VERSION.jar file under the target directory.

## Support
If you need help using Dexecutor feel free to drop an email or create an issue in github.com (preferred)

## Contributions
To help Dexecutor development you are encouraged to provide 
* Suggestion/feedback/Issue
* pull requests for new features

[![View My profile on LinkedIn](https://static.licdn.com/scds/common/u/img/webpromo/btn_viewmy_160x33.png)](https://in.linkedin.com/pub/nadeem-mohammad/17/411/21)
