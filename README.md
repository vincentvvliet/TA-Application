# Teaching Assistant Application

A Gradle Spring boot project for Teaching Assistant application using Microservice architecture for CSE2115 Software Engineering Methods.  

### Running 
`gradle bootRun`

### Testing
```
gradle test
```

To generate a coverage report:
```
gradle jacocoTestCoverageVerification
```


And
```
gradle jacocoTestReport
```
The coverage report is generated in: build/reports/jacoco/test/html, which does not get pushed to the repo. Open index.html in your browser to see the report. 

### Static analysis
```
gradle checkStyleMain
gradle checkStyleTest
gradle pmdMain
gradle pmdTest
```

# Contributors
- Vincent van Vliet
- Iarina Tudor
- Gijs van de Linde
- Andrei Tociu
- Ravi Snellenberg
- Pauline Hengst
