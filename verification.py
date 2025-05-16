import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score

# Load the trimmed dataset
df = pd.read_csv("trimmed_anxiety_dataset.csv")

# Binary classification: 0 = low stress, 1 = high stress
def binarize_stress(val):
    return 0 if val <= 5 else 1

df["Stress level"] = df["Stress level"].apply(binarize_stress)

# Features and target
X = df.drop(columns=["Stress level"])
y = df["Stress level"]

# Split data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, shuffle=True)

# Class distribution
print("Class Distribution (Binary):")
print(y.value_counts())

# Train Random Forest
rf = RandomForestClassifier(n_estimators=25, max_depth=10, random_state=42)
rf.fit(X_train, y_train)
rf_preds = rf.predict(X_test)

# Train Decision Tree
dt = DecisionTreeClassifier(max_depth=20, random_state=42)
dt.fit(X_train, y_train)
dt_preds = dt.predict(X_test)

# Evaluation Function
def evaluate_model(name, y_true, y_pred):
    print(f"\n🔍 {name} Evaluation:")
    print("Confusion Matrix:")
    print(confusion_matrix(y_true, y_pred))
    print(f"Accuracy: {accuracy_score(y_true, y_pred) * 100:.2f}%")
    print("\nClassification Report:")
    print(classification_report(y_true, y_pred, digits=3))

# Run evaluations
evaluate_model("Random Forest", y_test, rf_preds)
