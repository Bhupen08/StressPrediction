# 🧠 Stress Level Prediction System

A machine learning-based Java application that predicts a user's stress level based on health and lifestyle data. It classifies users into **low** or **high** stress categories using a custom-built **Random Forest** and **Decision Tree** implementation—no external ML libraries. The project also generates personalized lifestyle recommendations and a visual radar chart comparison with average trends.


## 🚀 Features

- 📊 **Random Forest & Decision Tree classifiers** (custom implementations in Java)
- 🔍 Predict stress level from user inputs
- 💡 Generate personalized lifestyle recommendations
- 📈 Display user vs average health/lifestyle via radar chart (Python + matplotlib)
- 🧪 Modular unit and component tests included


## 🛠️ Technologies Used

- **Java** (core application logic, model training)
- **Python** (matplotlib for radar chart visualization)
- **matplotlib, pandas, scikit-learn** (for Python-based analysis)
- **Command-line interface (CLI)** for real-time predictions
- No third-party Java ML libraries used


## 📂 Project Structure
```
StressPrediction/
├── data/
│ └── trimmed_anxiety_dataset.csv # Cleaned and categorized input data
├── src/
│ ├── DataProcessorImpl.java
│ ├── DecisionTreeModel.java
│ ├── RandomForestModel.java
│ ├── StressPredictorImpl.java
│ ├── RecommendationEngineImpl.java
│ ├── GraphGeneratorImpl.java
│ ├── Main.java # Entry point with CLI loop
│ └── TestComponent.java / TestUnit.java
├── radar_chart.py # Python radar chart visualizer
└── README.md
```

## 🧠 How it Works

1. **Data Preprocessing**: `DataProcessorImpl.java`
   - Binarizes stress level: `1–5 → Low (0)`, `6–10 → High (1)`
   - Filters and normalizes input features

2. **Model Training**:
   - Random Forest: `25 trees`, `max depth = 10`, `max leaves = 30`
   - Decision Tree: `max depth = 20`, `max leaves = 100`

3. **Prediction**: `StressPredictorImpl.java` uses trained models to classify new inputs.

4. **Recommendations**: Compares user input with low-stress peers and suggests adjustments (excluding immutable fields like age).

5. **Radar Chart**: `GraphGeneratorImpl.java` outputs CSV and triggers `radar_chart.py` for visualization.


## 🧪 How to Run

### Compile,
```javac -d bin src/*.java```

### Run Main program,
```java -cp bin Main```

### Run Component Test program,
```java -cp bin TestComponent```

### Run Unit Test program,
```java -cp bin TestUnit```

### For verification of RandomForest prediction accuracy, we cross-check results with script using ML library from python.
```python3 verification.py```
