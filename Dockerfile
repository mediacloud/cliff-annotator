FROM debian:jessie

ENV MAVEN_VERSION 3.3.9
ENV MAVEN_HOME /usr/share/maven
ENV LANG C.UTF-8

WORKDIR /opt/java
RUN apt-get update; apt-get install -y unzip curl openssl tar wget

RUN \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get update  && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes oracle-java8-installer oracle-java8-set-default  && \
    \
    curl -kfsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn && \
    \
    echo "===> clean up..."  && \
    rm -rf /var/cache/oracle-jdk8-installer  && \
    apt-get clean  && \
    rm -rf /var/lib/apt/lists/*

#RUN sed -i.bak 's/#networkaddress.cache.ttl=-1/networkaddress.cache.ttl=60/' /usr/java/default/jre/lib/security/java.security

RUN mkdir -p /opt/java &&  mkdir -p /data/log

VOLUME ["/data/log"]
VOLUME /etc/cliff2/IndexDirectory

ENV JAVA_OPTS "-Djava.libary.path=/usr/lib/jvm/java-8-oracle/jre/lib -Xmx4g"
WORKDIR /opt/java
#CLIFF Specific stuff
RUN apt-get update; apt-get install -y cmake swig gcc g++ gfortran bzip2 make libopenblas-dev liblapack-dev && \
    wget https://github.com/mit-nlp/MITIE/archive/master.zip && unzip master.zip && rm master.zip && \
    cd MITIE-master/mitielib/java && mkdir build && cd build && cmake .. && cmake --build . --config Release --target install && \
    cd /opt/java/MITIE-master/mitielib && mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=$PWD/javamitie.jar -DgroupId=edu.mit.ll.mitie -DartifactId=mitie -Dversion=0.4 -Dpackaging=jar && \
    mv /opt/java/MITIE-master/mitielib/libjavamitie.so /usr/lib/jvm/java-8-oracle/jre/lib/. && \
    mkdir -p /etc/mitie/ && \
    cd /tmp && wget http://sourceforge.net/projects/mitie/files/binaries/MITIE-models-v0.2.tar.bz2 && tar -xjf MITIE-models-v0.2.tar.bz2 && rm MITIE-models-v0.2.tar.bz2 && \
    mv /tmp/MITIE-models/english/ /etc/mitie/. && \
    rm -rf /tmp/MITIE-models && \
    \
    cd /tmp && \
    wget https://s3.amazonaws.com/docker.sensorhub.eagle-ow.com/apache-tomcat-7.0.64.tar.gz && tar xzf apache-tomcat-7.0.64.tar.gz && \
    rm -rf apache-tomcat-7.0.64.tar.gz && mv apache-tomcat-7.0.64 /usr/local/tomcat7 && rm -rf /usr/local/tomcat7/webapps/examples && \
    rm -rf /usr/local/tomcat7/webapps/manager && rm -rf /usr/local/tomcat7/webapps/docs && \
    apt-get remove -y --auto-remove cmake swig gcc g++ gfortran bzip2 make

RUN chmod +x /usr/lib/jvm/java-8-oracle/jre/lib/libjavamitie.so
ENV LD_LIBRARY_PATH "/usr/lib/jvm/java-8-oracle/jre/lib"
#Build Me
ADD . /opt/java/.
RUN mvn -Dmaven.test.skip=true -P mitie,\!stanford-ner clean install && \
    mv /opt/java/webapp/target/cliff-*.war /usr/local/tomcat7/webapps/ROOT.war && \
    rm -rf /usr/local/tomcat7/webapps/ROOT && \
    rm -rf ~/.m2

EXPOSE 8080
CMD /usr/local/tomcat7/bin/catalina.sh run 2>&1 | tee /data/log/tomcat.log
