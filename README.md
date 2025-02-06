# **API Testing Project**

## **Overview**

This project is a collection of automated API tests written in Java using **RestAssured** for testing REST APIs. The tests are designed to validate the endpoints of a given API. The project also includes test scripts for various scenarios, such as checking valid page IDs, unauthorized access, and responses for components and incidents.

## **Technologies Used**

- **Java**: The main programming language used for writing test scripts.
- **RestAssured**: For making and validating HTTP requests.
- **TestNG**: For running the tests and managing test execution.
- **Maven**: For managing dependencies and building the project.
- **Aqua IDE**: Integrated Development Environment for writing and executing the tests.
- **Allure**: For generating test reports.


## **Setup Instructions**

### **Prerequisites**

- **Java**: Ensure that Java is installed on your machine.
  - You can check if Java is installed using:  
    `java -version`

- **Maven**: This project uses Maven to manage dependencies.
  - You can verify Maven installation with:  
    `mvn -v`

- **Aqua IDE** (Optional): You can use Aqua IDE for running and debugging tests if preferred.

### **Cloning the Repository**

1. Clone the repository using Git:

    ```bash
    git clone <repository_url>
    ```

2. Navigate to the project directory:

    ```bash
    cd <project_name>
    ```

### **Installing Dependencies**

1. Run Maven to download all dependencies:

    ```bash
    mvn clean install
    ```

This will download all required dependencies as specified in the `pom.xml` file.

## **Running the Tests**

### **TestNG Setup**
TestNG is used to run tests, and a test suite (`testng.xml`) is available to manage test execution. You can run the tests using Maven or directly from the IDE.

#### **Using Maven**:
You can run the tests with Maven using this command:

```bash
mvn test
```

### **Using Aqua IDE**

1. Open the project in **Aqua IDE**.  
2. Locate the test file or the **TestNG suite** (`testng.xml`).  
3. Right-click and choose **Run** to execute the tests.  

### **Individual Test Cases**  

You can run individual test cases directly. 

### **Test Output**  

After running the tests, the results will be saved in the following location:  

- **Allure Reports**: Generated in the `allure-results/` folder.  
  You can view the reports by running the following command:  

  ```bash
  allure serve allure-results/
  ```
### **Test Data Files**  

- **`response.json`**: A JSON file containing the list of page IDs returned by the `/v1/pages` API endpoint.  
- **`responseComponents.json`**: A JSON file containing components for each page returned by the `/v1/{pageId}/components` endpoint.  

These files are used in the tests to dynamically fetch **page IDs** and **component IDs** to ensure the tests run with up-to-date data.  


