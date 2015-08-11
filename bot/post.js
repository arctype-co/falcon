#!/usr/bin/env node

var querystring = require('querystring');
var https = require('https');

var slack_api_token = "xoxb-8938228928-ouR0Olm8ixpbTiOtoPA3OQqV"

function apiPost(api_call, api_params) {
  api_params.token = slack_api_token;
  var body = querystring.stringify(api_params);
  var params = {
    host: 'slack.com',
    port: 443,
    path: "/api/" + api_call,
    method: "POST",
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Content-Length': body.length
    }
  };
  var req = https.request(params, function(res) {
    res.on("data", function(content) {
      //process.stdout.write(content);
      //process.stdout.write("\n");
    });
  });

  req.on("error", function(error) {
    console.log(error);
  });
  req.write(body);
  req.end();
};

function userAlias(user) {
  switch(user) {
    case "sunry001":
      return "@sundbry";
  }
  return "@" + user;
}

function postDeployMessage(user, params) {
  var text = userAlias(user) + ": ./cluster $ `make " + params.trim() + "`";
  apiPost("chat.postMessage", {
    channel: "#deploys",
    username: "steve_jobs",
    text: text,
    icon_emoji: ":ghost:",
    as_user: false
  });
}

postDeployMessage(process.argv[2], process.argv[3]);
