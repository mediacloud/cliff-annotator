FROM	centos:latest
RUN yum -y update; yum clean all; yum install -y crontabs wget tar apr-devel openssl-devel curl unzip wget

ENV JDK_VERSION 8u45
ENV JDK_BUILD_VERSION b14

RUN curl -LO "http://download.oracle.com/otn-pub/java/jdk/$JDK_VERSION-$JDK_BUILD_VERSION/jdk-$JDK_VERSION-linux-x64.rpm" -H 'Cookie: oraclelicense=accept-securebackup-cookie' && rpm -i jdk-$JDK_VERSION-linux-x64.rpm; rm -f jdk-$JDK_VERSION-linux-x64.rpm; yum clean all
ENV JAVA_HOME /usr/java/default

RUN cd /usr/local &&  wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.3.3/binaries/apache-maven-3.3.3-bin.tar.gz 
RUN cd /usr/local && tar -xzf apache-maven-3.3.3-bin.tar.gz && ln -s apache-maven-3.3.3 maven

RUN sed -i.bak 's/#networkaddress.cache.ttl=-1/networkaddress.cache.ttl=60/' /usr/java/default/jre/lib/security/java.security

ENV M2_HOME /usr/local/maven
ENV PATH ${M2_HOME}/bin:${PATH}

RUN mkdir -p /opt/java
RUN mkdir -p /data/log
VOLUME ["/data/log"]

VOLUME /etc/cliff2/IndexDirectory

ENV JAVA_OPTS "-Djava.libary.path=/usr/java/default/jre/lib -Xmx4g"

RUN yum install -y cmake swig gcc gcc-c++ bzip2 make
RUN wget https://github.com/mit-nlp/MITIE/archive/master.zip && unzip master.zip && rm master.zip && cd MITIE-master/mitielib/java && mkdir build && cd build && cmake .. && cmake --build . --config Release --target install

RUN cd MITIE-master/mitielib  && mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=$PWD/javamitie.jar -DgroupId=edu.mit.ll.mitie -DartifactId=mitie -Dversion=0.4 -Dpackaging=jar  
RUN cp MITIE-master/mitielib/libjavamitie.so $JAVA_HOME/jre/lib/.
RUN mkdir -p /etc/mitie/
RUN wget http://sourceforge.net/projects/mitie/files/binaries/MITIE-models-v0.2.tar.bz2 && tar -xjf MITIE-models-v0.2.tar.bz2 && rm MITIE-models-v0.2.tar.bz2

#cd MITIE-models && mv * /etc/mitie/. 

WORKDIR /tmp
RUN wget http://apache.mirrors.hoobly.com/tomcat/tomcat-7/v7.0.63/bin/apache-tomcat-7.0.63.tar.gz && tar xzf apache-tomcat-7.0.63.tar.gz && rm -rf apache-tomcat-7.0.63.tar.gz && mv apache-tomcat-7.0.63 /usr/local/tomcat7 && rm -rf /usr/local/tomcat7/webapps/examples && rm -rf /usr/local/tomcat7/webapps/manager && rm -rf /usr/local/tomcat7/webapps/docs 

#Build Me
ADD . /opt/java/.
WORKDIR /opt/java
RUN mvn  -Dmaven.test.skip=true -P mitie,\!stanford-ner clean install

RUN mv /opt/java/webapp/target/cliff-2.2.0.war /usr/local/tomcat7/webapps/ROOT.war

#Force a redeploy
RUN rm -rf /usr/local/tomcat7/webapps/ROOT

EXPOSE 8080
RUN mv /MITIE-models/english/ /etc/mitie/.
CMD /usr/local/tomcat7/bin/catalina.sh run 2>&1 | tee /data/log/tomcat.log
