### configuration example 1

```yaml

configuration:
  # Changes will be pushed to target branch if build and tests were successful
  pushPROnSuccess: true

  # Trusted PR approvers
  prApprovers:
    - username1
    - username2
    - username3

```
### configuration example 2

```yaml

configuration:
  # Changes will not be pushed to target branch no matter what is the result of buld
  pushPROnSuccess: false

  # Trusted PR approvers
  prApprovers:
    - username1
    - username2
    - username3

```
