### environment example with only environment variables 

```yaml
environment:
  variables:
    variable_1: value_1
    variable_2: value_2
```

### environment example with only credentials variables 

```yaml
environment:
  # Credentials contains only two fields. See pipeline file for how it will be used
  credentials:
    - credentialId : fileCredentialId
      variable : FILE

      # Username will be accessed by LOGIN_USR and Password will be accessed by LOGIN_PSW
    - credentialId : dummyGitRepo
      variable : LOGIN
```

### environment example

```yaml
environment:
  # Credentials contains only two fields. See pipeline file for how it will be used
  credentials:
    - credentialId : fileCredentialId
      variable : FILE

      # Username will be accessed by LOGIN_USR and Password will be accessed by LOGIN_PSW
    - credentialId : dummyGitRepo
      variable : LOGIN
```


