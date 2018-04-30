# Simple Pull Request job plugin for Jenkins

[![Join the chat at https://gitter.im/jenkinsci/simple-pull-request-job-plugin](https://badges.gitter.im/jenkinsci/simple-pull-request-job-plugin.svg)](https://gitter.im/jenkinsci/simple-pull-request-job-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

This project aims to develop a Job Plugin which can interact with Bitbucket Server, Bitbucket Cloud, and Github whenever a pull request is created or updated. Users should be able to configure job type using YAML file which will be placed in root directory of the Git repository being the subject of the PR.

Detect the presence of certain types of the report based on a conventional location, and automatically publish them. If the reports are not in a conventional location, users could specify the location using the YML file.
