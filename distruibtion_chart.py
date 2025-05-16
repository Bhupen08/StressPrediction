import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load original dataset
df = pd.read_csv("anxiety_attack_dataset.csv")

# Binary categorize 'Stress level'
def map_stress_binary(val):
    return 1 if val >= 7 else 0

df['Stress level'] = df['Stress level'].apply(map_stress_binary)

# Drop ID if exists
if 'ID' in df.columns:
    df.drop(columns=['ID'], inplace=True)

# Convert Yes/No and boolean to 1/0
df.replace({'Yes': 1, 'No': 0, True: 1, False: 0}, inplace=True)

# Encode categorical columns: Gender, Occupation
def map_gender(val):
    val = str(val).lower()
    if "male" in val:
        return 0
    elif "female" in val:
        return 1
    return 2

def map_occupation(val):
    val = str(val).lower()
    if "doctor" in val or "engineer" in val or "teacher" in val:
        return 0
    elif "student" in val:
        return 1
    elif "unemployed" in val:
        return 3
    return 2

df['Gender'] = df['Gender'].apply(map_gender)
df['Occupation'] = df['Occupation'].apply(map_occupation)

# Filter only numeric columns for plotting
numeric_features = df.select_dtypes(include=['int64', 'float64']).columns.tolist()
numeric_features.remove("Stress level")  # We'll use this as y-axis

# Set plot style
sns.set(style="whitegrid")

# Create one stripplot per numeric feature
for feature in numeric_features:
    plt.figure(figsize=(7, 4))
    sns.stripplot(data=df, x=feature, y="Stress level", orient="h", jitter=0.25, alpha=0.4, palette="coolwarm")
    plt.title(f"{feature} Distribution by Stress Level (0 = Low, 1 = High)")
    plt.xlabel(feature)
    plt.ylabel("Stress level")
    plt.tight_layout()
    plt.savefig(f"original_dist_{feature.replace(' ', '_').lower()}.png")
    plt.show()
