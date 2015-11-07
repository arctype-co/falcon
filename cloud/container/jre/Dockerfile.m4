FROM __REPOSITORY__/base

#  Authorship
MAINTAINER ryan.sundberg@gmail.com

RUN apt-get update -q -q
# Install java
RUN apt-get install -y openjdk-7-jre
