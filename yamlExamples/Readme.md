## YAML format documentation

### Sections of "Jenkinsfile.yaml"

0. agent
0. configuration
0. reports
0. environment
0. steps
0. stages
0. post
0. artifactPublishingConfig
0. publishArtifacts
0. archiveArtifacts

### agent
Agent in "Jenkinfile.yaml" will be defined differently. We have below properties in agent

0. label
0. customWorkspace
0. dockerImage
0. alwaysPull
0. args
0. dockerfile
0. dir
0. reuseNode
0. tools

All parameters are same as 
[Declarative pipeline agent](https://jenkins.io/doc/book/pipeline/syntax/#agent) and here we have
one new parameters "dockerImage" and one parameter with different meaning "dockerfile". Also 
the function of "additionalBuildArgs" dockerfile agent type is served by "args" parameter only.

[See agent examples](AgentExamples.md)

Note:
1. dockerImage and dockerfile cannot be used simultaneously (obviously).
2. If you provide any extra parameter which will is not required like "reuseNode" without
"dockerImage" or "dockerfile" parameter then it will have no effect.

### configuration
This section has only two properties for now. They are:
0. pushPROnSuccess: This is a boolean property with default value false. On true the plugin 
will push the changes to the target branch if the build is success and all tests
passes else not. On the other hand on a false value no changes will not be pushed no matter what
is the build result or all tests passes or not.

2. prApprovers: This is a list of usernames of trusted approvers. If a user from this list approves 
a PR then only a build will be started else not. This function is not implemented yet.

[See configuration examples](ConfigurationExample.md)

