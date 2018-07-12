### Docker image example 1

```yaml
agent:
  label: my_label
  customWorkspace: path_to_workspace
  dockerImage: maven:3-alpine
  args: -v /tmp:/tmp
  alwaysPull: false
```

### Docker image example 2

```yaml
agent:
  dockerImage: maven:3-alpine
  args: -v /tmp:/tmp
  alwaysPull: true
```

### Docker file example 1

```yaml
agent:
  label: my_label
  customWorkspace: path_to_workspace
  dockerfile: my_dockerfile
  args: dockerfile arguments
  dir: directory_location
```

### Docker file example 2

```yaml
agent:
  dockerfile: my_dockerfile
  args: dockerfile arguments
```

### any agent example

```yaml
agent: any
```

### none agent example

```yaml
agent: none
```

### Agent with tools example 1

```yaml
agent:
  label: my_label
  customWorkspace: path_to_workspace
  
  tools:
    maven : maven_3.0.1
    jdk : jdk8
```

### Agent with tools example 2

Here agent will be treated just like 'any agent example'

```yaml
agent:
  tools:
    maven : maven_3.0.1
    jdk : jdk8
```
