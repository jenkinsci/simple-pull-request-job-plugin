#### Jenkinsfile.yaml example with only a single simple stage

If we want only a simple single stage then we can just place below code to Jenkinsfile.yaml
and specify list of steps according to us. "Build" will be displayed as the name of this 
stage in Jenkins instance.

```yaml
agent: any

steps:
  - sh: "scripts/hello"
  - sh: "scripts/hello"
  - sh:
      script: "scripts/hello"

```

We still have option to define more sections like "configuration"
"environment", "post", etc.

```yaml
agent: any

configuration:
  pushPROnSuccess: true

environment:
  variables:
    variable_1: value_1
    variable_2: value_2
    
steps:
  - sh: "scripts/hello"
  - sh: "scripts/hello"
  - sh:
      script: "scripts/hello"
```
