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
      # If credentialId belongs to a secret text or secret file the the secret text or
      # contents of secret file will be available in $FILE environment variable
    - credentialId : fileCredentialId
      variable : FILE

      # If credentialId belongs to username and password type credential then 
      # Username can be accessed by LOGIN_USR and Password can be accessed by LOGIN_PSW
    - credentialId : dummyGitRepo
      variable : LOGIN
```

### environment example

```yaml
environment:
  variables:
    variable_1: value_1
    variable_2: value_2
    
  # Credentials contains only two fields. See pipeline file for how it will be used
  credentials:
    - credentialId : fileCredentialId
      variable : FILE

      # Username will be accessed by LOGIN_USR and Password will be accessed by LOGIN_PSW
    - credentialId : dummyGitRepo
      variable : LOGIN
```


