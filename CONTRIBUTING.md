# Contributing to Snipping Tool++

We welcome anyone to help contribute to the future of Snipping Tool++ and will gladly review any pull requests.

## Development Environment

In order to build ST++ you will need to have the Oracle JDK 1.7 installed and the environment variable `JAVA_HOME` must be set to the root directory of its installation path.

1. To build, execute `gradlew jar`. 

You do not need gradle installed for this command to work. The first time it is ran, it will download a project specific version of gradle and place it in `.gradle` at the root of the project.

The first time you run this will take awhile as it must download gradle as well as the third party libraries. These are then cached so future builds will be quicker.

2. To run tests, execute `gradlew test`.

If you want a detailed coverage report, execute 'gradlew test jacoco`.

## Eclipse Setup

1. To generate eclipse project files, execute `gradlew eclipse`.

2. Import the project in to eclipse.

### Coding Style

If you plan on submitting a pull request, please format your code using our coding style.

1. Open the `Windows -> Preferences`

2. Under `Java -> Code Style -> Formatter` click on `Import...`

3. Browse to the `eclipse-format.xml` file at the root of the repository.

4. Save the settings and use `ctrl-shift-f` to format any added source.

In addition to using the eclipse formatter, please use `ctrl-shift-o` to organize the import statements and remove unneeded ones.

### Gradle plugin for eclipse

Although it is optional, it is recommended to install the gradle plugin for eclipse so you can run gradle tasks from within eclipse.


1. It is available through eclipse's `Help -> Install New Software..` dialog.

2. Use the download site `http://dist.springsource.com/release/TOOLS/gradle`

3. Check the `Gradle IDE` checkbox under the `Extensions / Gradle Integration` node.




