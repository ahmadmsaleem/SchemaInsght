# SchemaInsight

## What is SchemaInsight?

SchemaInsight is a data analysis tool designed to help you quickly understand and explore your CSV data. It provides a user-friendly interface to load, analyze, and visualize your data.

## Why use SchemaInsight?

* **Rapid Data Exploration:** Quickly load and visualize your CSV data.
* **Intelligent Data Type Detection:** Automatically detects data types for each column, saving you time and effort.
* **In-depth Data Analysis:** Analyze your data for null values, unique values, and duplicates.
* **Customizable Data Import:** Configure CSV import settings like delimiter, column count, skip rows, and more.
* **User-Friendly Interface:** A simple and intuitive interface for easy interaction.

## How to Use SchemaInsight

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/ahmadmsaleem/SchemaInsght.git](https://github.com/ahmadmsaleem/SchemaInsght.git)
   ```

**2. Build and Run:**
- **Build:**
  ```bash
  mvn clean package
  ```
- **Run:**
  ```bash
  java -jar target/SchemaInsight.jar
  ```

**3. Load Your CSV Data:**
- Click the "Upload" button in the sidebar.
- Select your CSV file.
- Configure import settings as needed.
- Click "Upload" to start the process.
  
**4. Explore Your Data:**

* **View Data:** The table view displays your data.
* **Analyze Data:** Use the "Table Info" sidebar to view:
    - Data types
    - And more (Very Soon)
* **Identify Issues:** Use the analysis tools to find null values, duplicates, and other anomalies.
* **Visualize Data:** (Future feature: Consider adding basic data visualization capabilities, e.g., histograms, bar charts)

**Contributing to SchemaInsight**

We welcome contributions to SchemaInsight. Feel free to fork the repository, make changes, and submit a pull request.

**License**

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## Future Enhancements

We plan to continue improving SchemaInsight with features like:

* Advanced data visualization capabilities
* Support for other data formats (e.g., JSON, Excel)
* Integration with data warehousing and ETL tools
* Machine learning-powered data insights

