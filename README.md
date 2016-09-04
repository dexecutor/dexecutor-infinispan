# dexecutor-infinispan
Dexecutor With Distributed Execution Engine (Infinispan)


Refer [wiki](https://github.com/dexecutor/dexecutor-core/wiki) or [Project Web site](https://dexecutor.github.io/) for more Details.

## License

Dexecutor is licensed under **Apache Software License, Version 2.0**.

## News

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
  <version>0.0.1</version>
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
