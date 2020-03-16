# slack-client-java
A thin java wrapper around the Slack API

This slackclient is a module we use in one of our products. It has just the functionallity we need there (at the moment). We wrote it, because the old lib we used broke. So if it provides what you need, we are happy to share it with you!

# Functionality

## channels
- create a channel
- send a message to a channel
- archive a channel
- check if a channel exists
- get a channel id by the channels name
- set a channel topic
- invite users to channel

## users
- send a message to a user
- find a user by its mail-address
- invite user to a channel

# Install over Maven

```
<dependency>
  <groupId>biz.cosee</groupId>
  <artifactId>slack-client-java</artifactId>
  <version>1.0</version>
</dependency>
```
