#### Jenkinsfile.yaml example with multiple stages

```yaml
stages:
  - name: satge1
    agent: any
    
    steps:
      - sh: "scripts/hello"
      - sh: "scripts/hello"
      - sh:
          script: "scripts/hello"
          
    post:
      failure:
        - sh: "scripts/hello"


  - name: stage2
    agent:
      docketImage: maven:3-alpine
      customWorkspace: path_to_workspace
      
    steps: 
      - echo: "Hello from stage2"
      
      post:
        success:
          echo: "stage2 was a success"
post:
  always:
    - sh: "Pipeline completed"
```

We can define all other sections also

```yaml
# same as defined in current version
agent:
  label: my_label
  customWorkspace: path_to_workspace
  dockerImage: maven:3-alpine
  args: -v /tmp:/tmp
  
  tools:
    maven : maven_3.0.1
    jdk : jdk8

configuration:
  pushPROnSuccess: true

  prApprovers:
    - username1
    - username2

reports:
  - location_of_report_1
  - location_of_report_2

environment:
  variables:
    variable_1: value_1
    variable_2: value_2

  credentials:
    - credentialId : fileCredentialId
      variable : FILE

    - credentialId : dummyGitRepo
      variable : LOGIN

stages:
  - name: satge1
    agent: any
    
    steps:
      - sh: "scripts/hello"
      - sh: "scripts/hello"
      - sh:
          script: "scripts/hello"
          
    post:
      failure:
        - sh: "scripts/hello"


  - name: stage2
    agent:
      docketImage: maven:3-alpine
      customWorkspace: path_to_workspace
      
    steps: 
      - echo: "Hello from stage2"
      
      post:
        success:
          echo: "stage2 was a success"
post:
  always:
    - sh: "Pipeline completed"
```