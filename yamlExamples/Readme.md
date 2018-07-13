## YAML format documentation

### Sections of "Jenkinsfile.yaml"

0. agent
0. configuration
0. reports
0. findbugs
0. environment
0. steps
0. stages
0. post
0. artifactPublishingConfig
0. publishArtifacts
0. archiveArtifacts

### agent
Agent in "Jenkinsfile.yaml" will be defined differently. We have below properties in agent

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
0. **pushPrOnSuccess**: This is a boolean property with default value false. On true the plugin 
will push the changes to the target branch if the build is success and all tests
passes else not. On the other hand on a false value no changes will not be pushed no matter what
is the build result or all tests passes or not.

2. **prApprovers**: This is a list of username of trusted approvers. If a user from this list approves 
a PR then only a build will be started else not. This function is not implemented yet.

[See configuration examples](ConfigurationExample.md)

### reports
##### Type: list of string

List of location path can be specified here in the [Ant glob syntax](http://ant.apache.org/manual/Types/fileset.html).
All these reports will be published at the end of build.

**Example** : "\*\*/build/test-reports/\*.xml"


### findbugs
##### Type: String

Location of findbugs reports can be specified here in the 
[Ant glob syntax](http://ant.apache.org/manual/Types/fileset.html).
These report(s) will be published at the end of build.

### environment
This section has two properties:
1. **variables :** Any environment variable that user wants to use in his/her scripts can be 
defined here and it will be available to all the scripts at the time of build.

2. **credentials :** Any credentials (already configured in Jenkins instance) can be used in 
all user scripts by **credentials** property.

[See environment example for more details](EnvironmentExample.md)

### stages
This section is used to declare multiple stages for the pipeline. For now this section 
supports below properties.

0. **name :** This will be shown in pipeline dashboard as the name of stage.
0. **agent :** Separate agent can be defined for a stage. Rules to define stage level agent
are same as [Declarative pipeline stage level agent](https://jenkins.io/doc/book/pipeline/syntax/#stage-level-agent-section).
0. **steps :** This enables us to write Jenkins supported steps. [See SimpleStageExample example for more details](SimpleStageExample.md)
for information to how to define steps in "Jenkinsfile.yaml"
0. **post :** [Same as post](#post)

### steps
If only a single stage is needs to be defined for a build the **steps** property can be used.
A list of Jenkins supported steps needs to be defined in this section.

'Build' will be displayed as the name of this stage in the pipeline UI.

[See SimpleStageExample example for more details](SimpleStageExample.md)

### post
This section enables us to run some steps according to the result the whole build or a stage depending
upon the location of the post section. This section supports all the conditions with same meaning 
like [here](https://jenkins.io/doc/book/pipeline/syntax/#post-conditions).

##### Syntax
post:
  <condition1>:
    - list of steps
  <condition1>:
    - list of steps

##### Example of post
```yaml
post:
  failure:
    - echo: "Build or stage is failed."
  success:
    - echo: "Build or stage was success."
  always:
    - echo: "I will run always."
```
### artifactPublishingConfig
This is a configuration section to publish / upload artifacts to a server. Following properties can
be configured:

0. **host :** Host address to which artifacts needs to be uploaded.
0. **user :** username of the host machine.
0. **credentialId :** Credential Id of the credential having a secrete file or text that is needed 
to communicate with remote server.

##### Example:

```yaml
artifactPublishingConfig:
    host: 192.32.52.12
    user: user53
    credentialId: SecretFileCredentialId
```

To indicate artifacts to be uploaded [publishArtifacts](#publishartifacts) is used.

### publishArtifacts
##### Type: List of HashMap with keys "*form*" and "*to*"

This section enables us to specify which artifact needs to be uploaded where.

##### Example:
```yaml
publishArtifacts:
    - from: location_artifact
      to: ~/upload_path_on_server
      
    - from: Jenkinsfile.yaml
      to: ~/archives

```

### archiveArtifacts
##### Type: List of string

This section enables us to provide a list of location of path in
[Ant glob syntax](http://ant.apache.org/manual/Types/fileset.html) to archive them.

##### Example:

```yaml
archiveArtifacts:
    - path_of_artifact_1
    - path_of_artifact_2
```